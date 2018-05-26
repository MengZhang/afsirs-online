package org.afsirs.web.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.afsirs.web.dao.bean.Worker;
import org.afsirs.web.util.DBUtil;
import static org.afsirs.web.util.DBUtil.getConnection;
import org.afsirs.web.util.JSONObject;
import org.afsirs.web.util.MongoDBHandler;
import org.bson.Document;
import org.eclipse.jetty.websocket.api.Session;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Meng Zhang
 */
public class WorkerDAO {
    

    private static final ConcurrentHashMap<String, Worker> workers = syncWorkerRecords();
    private static final ConcurrentHashMap<String, Worker> workerHashMap = syncWorkerHashMap();

    public static ConcurrentHashMap<String, Worker> syncWorkerRecords() {
        ConcurrentHashMap<String, Worker> ret = new ConcurrentHashMap();
        return ret;
    }
    
    public static ConcurrentHashMap<String, Worker> syncWorkerHashMap() {
        ConcurrentHashMap<String, Worker> ret = new ConcurrentHashMap();
        return ret;
    }

    public static Worker getWorkerById(String id) {
        if (workers.containsKey(id)) {
            return workers.get(id);
        } else {
            try {
                Document data = MongoDBHandler.find(getConnection(DBUtil.AFSIRSCollection.User), new Document("userName", id));
                if (data == null) {
                    return null;
                } else {
                    String json = data.toJson();
                    Worker ret = null;
                    if (!json.trim().isEmpty()) {
                        ObjectMapper mapper = new ObjectMapper();
                        ret = mapper.readValue(json, Worker.class);
                        workers.put(id, ret);
                    }
                    return ret;
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        return null;
    }

//    public static boolean registerWorker(Worker user) {
//        String password = user.getPassword();
//        if (password == null || password.isEmpty()) {
//            return false;
//        }
//        String salt = BCrypt.gensalt();
//        String hashedPassword = BCrypt.hashpw(password, salt);
//        MongoDBHandler.add(getConnection(DBUtil.AFSIRSCollection.User),
//                new Document("userName", user.getWorkerId())
//                .append("salt", salt)
//                .append("hashedPassword", hashedPassword)
//                .append("userRank", "regular")); //TODO
//        return true;
//    }

    public static Set<String> getAllWorkerIds() {
        return workers.keySet();
    }
    
    public static String authenticate(JSONObject jsonMsg, Session session) {
        String workerId = jsonMsg.getOrBlank("worker_id");
        String password = jsonMsg.getOrBlank("password");
        if (workerId.isEmpty() || password.isEmpty()) {
            return null;
        }
        Worker worker = getWorkerById(workerId);
        if (worker == null) {
            return null;
        }
        String hashedPassword = BCrypt.hashpw(password, worker.getSalt());
        
        if (hashedPassword.equals(worker.getHashedPassword())) {
            String hash = BCrypt.gensalt();
            worker.setWorkerSession(session);
            workerHashMap.put(hash, worker);
            return hash;
        } else {
            return null;
        }
    }
    
    public static boolean renew(String hash, Session session) {
        if (workerHashMap.containsKey(hash)) {
            workerHashMap.get(hash).setWorkerSession(session);
            return true;
        } else {
            return false;
        }
    }
    
    public static void disconnect(Session session) {
        for (Worker worker : workerHashMap.values()) {
            if (worker.getWorkerSession().equals(session)) {
                worker.removeWorkerSession();
            }
        }
    }
    
    public static boolean disconnect(String hash) {
        Worker worker = workerHashMap.remove(hash);
        if (worker != null) {
            worker.removeWorkerSession();
            return true;
        } else {
            return false;
        }
    }
}
