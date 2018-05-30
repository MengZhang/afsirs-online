package org.afsirs.web.dao.bean;

import java.util.Collection;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.afsirs.module.Soil;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author Meng Zhang
 */
public class Simulation {

    @Getter
    private SimulationKey key;
    @Getter
    @Setter
    private Session session;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    HashMap<String, Integer> soilStatus = new HashMap();

    public Simulation(String userName, String permitId) {
        this.key = new SimulationKey(userName, permitId);
    }

    public Simulation(WaterUsePermit permit) {
        this.key = new SimulationKey(permit.getUser_id(), permit.getPermit_id());
        this.addSoils(permit.getSoils());
    }
    
    public String getUserName() {
        return key.getUserName();
    }
    
    public String getPermitId() {
        return key.getPermitId();
    }

    public void addSoil(String soilId) {
        if (!soilStatus.containsKey(soilId)) {
            soilStatus.put(soilId, 0);
        }
    }

    public final void addSoils(Collection<Soil> soils) {
        for (Soil soil : soils) {
            addSoil(soil.getCOMPKEY());
        }
    }

    public int getReadyCount() {
        int cnt = 0;
        for (int ret : soilStatus.values()) {
            if (ret > 0) {
                cnt++;
            }
        }
        return cnt;
    }

    public String getProcessString() {
        return getReadyCount() + "/" + soilStatus.values().size();
    }

    public int getProcessPct() {
        return getReadyCount() * 100 / soilStatus.values().size();
    }

    public void setReady(String soilId) {
        if (soilStatus.containsKey(soilId)) {
            soilStatus.put(soilId, 1);
        }
    }

    public boolean isReady(String soilId) {
        if (soilStatus.containsKey(soilId)) {
            return soilStatus.get(soilId) > 0;
        } else {
            return false;
        }
    }

    public boolean isReady() {
        int cnt = 0;
        for (int status : soilStatus.values()) {
            cnt += status;
        }
        return cnt == soilStatus.values().size();
    }

    @Data
    public static class SimulationKey {

        private String userName;
        private String permitId;

        public SimulationKey(String userName, String permitId) {
            this.userName = userName;
            this.permitId = permitId;
        }

        public SimulationKey(WaterUsePermit permit) {
            this.userName = permit.getUser_id();
            this.permitId = permit.getPermit_id();
        }
    }

}
