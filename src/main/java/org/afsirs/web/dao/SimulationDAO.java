package org.afsirs.web.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.module.SimResult;
import org.afsirs.module.Soil;
import org.afsirs.module.UserInput;
import org.afsirs.web.dao.bean.Simulation;
import org.afsirs.web.dao.bean.Simulation.SimulationKey;
import org.afsirs.web.dao.bean.WaterUsePermit;
import static org.afsirs.web.dao.bean.WaterUsePermit.setDeviation;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Simulation;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Error;
import org.afsirs.web.dao.bean.WebSocketSimStatusMsg;
import org.afsirs.web.util.Path;
import org.afsirs.web.util.WebSocketUtil;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author Meng Zhang
 */
public class SimulationDAO {
    
    private static final ConcurrentHashMap<SimulationKey, Simulation> simulations = new ConcurrentHashMap();
    private static final ConcurrentHashMap<Session, Simulation> simulationRefs = new ConcurrentHashMap();
    private static final ExecutorService simExecutor = Executors.newCachedThreadPool();

    public static Simulation getSimulation(String userId, String permitId) {
        return getSimulation(new SimulationKey(userId, permitId));
    }
    
    public static Simulation getSimulation(WaterUsePermit permit) {
        return getSimulation(new SimulationKey(permit));
    }
    
    public static Simulation getSimulation(SimulationKey key) {
        return simulations.get(key);
    }
    
    public static boolean registerSession(Session session, String userId, String permitId) {
        
        if (!simulationRefs.containsKey(session)) {
            try {
                Simulation simulation = getSimulation(userId, permitId);
                if (simulation != null) {
                    simulationRefs.put(session, simulation);
                    simulation.setSession(session);
                } else {
                    return false;
                }
                return true;
//                Document data = MongoDBHandler.find(getConnection(DBUtil.AFSIRSCollection.User), new Document("userName", id));
//                if (data == null) {
//                    return null;
//                } else {
//                    String json = data.toJson();
//                    Worker ret = null;
//                    if (!json.trim().isEmpty()) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        ret = mapper.readValue(json, Worker.class);
//                        workers.put(id, ret);
//                    }
//                    return ret;
//                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        } else {
            return true;
        }
    }
    
    public static boolean renew(Session session, String userId, String permitId) {
        return renew(session, new SimulationKey(userId, permitId));
    }
    
    public static boolean renew(Session session, WaterUsePermit permit) {
        return renew(session, new SimulationKey(permit));
    }
    
    public static boolean renew(Session session, SimulationKey key) {
        Simulation simulation = getSimulation(key);
        if (simulation != null) {
            if (simulation.getSession() != null) {
                simulationRefs.remove(simulation.getSession());
            }
            simulationRefs.put(session, simulation);
            simulation.setSession(session);
            return true;
        } else {
            return false;
        }
    }
    
    public static void disconnect(Session session) {
        Simulation sim = simulationRefs.remove(session);
        if (sim != null) {
            sim.setSession(null);
        }
    }
    
    public static void addSimulation(WaterUsePermit permit) {
        Simulation sim = new Simulation(permit);
        if (!simulations.containsKey(sim.getKey())) {
            simulations.put(sim.getKey(), sim);
            simExecutor.submit(new SimulationRunner(permit, sim));
        }
    }
    
    public static void finishSimulation(Simulation simulation) {
        if (simulation.isReady()) {
            simulations.remove(simulation.getKey());
        }
    }
    
    public static class SimulationRunner implements Callable<File> {
        
        private final WaterUsePermit permit;
        private final Simulation simulation;
        
        public SimulationRunner(WaterUsePermit permit, Simulation simulation) {
            this.permit = permit;
            this.simulation = simulation;
        }

        @Override
        public File call() throws IOException {
            
            // Run simulation
            String userId = permit.getUser_id();
            File json = Path.Folder.getUserWaterUsePermitOutputJsonFile(userId, permit.getPermit_id());
            if (json.exists()) {
                json.delete();
            }
            UserInput input = setDeviation(permit.toAFSIRSInputData(userId), permit);
//            SimResult simRetOrg = AFSIRSModule.run(input);
            SimResult simRetOrg = new SimResult();
            Session session;
            boolean append = false;
            for (int i = 0; i < input.getSoils().size(); i++) {
                if (input.getSoils().get(i).getNL() <= 0) {
                    continue;
                }
                SimResult tmp = AFSIRSModule.run(input, i, append);
                simRetOrg.addSoilTypeSummaryReport(tmp.getSoilTypeSummaryList().get(0));
                simRetOrg.setTotalMonth(tmp.getTotalMonth());
                simulation.setReady(input.getSoils().get(i).getCOMPKEY());
                session = simulation.getSession();
                if (session != null && session.isOpen()) {
                    WebSocketUtil.sendMsg(session, new WebSocketSimStatusMsg(simulation.getProcessPct()));
                }
                append = true;
            }
            
            // Generate JSON result file
            try (FileWriter writer = new FileWriter(json)) {
                writer.write(simRetOrg.toJson());
                writer.flush();
            }
            
            // Callback
//            for (Soil soil : permit.getSoils()) {
//                simulation.setReady(soil.getCOMPKEY());
//            }
            session = simulation.getSession();
            if (session != null && session.isOpen()) {
                if (simulation.isReady()) {
                    WebSocketUtil.sendMsg(session, new WebSocketSimStatusMsg(simulation.getProcessPct()));
                } else {
                    WebSocketUtil.sendMsg(session, new WebSocketSimStatusMsg(Simulation, Error));
                }
            }
            finishSimulation(simulation);
            return json;
        }
        
    }
}
