package org.afsirs.module;

import java.util.HashMap;
import static org.afsirs.module.AFSIRSTester.runPermit;
import org.afsirs.web.dao.SoilDataDAO;
import org.afsirs.web.dao.UserDAO;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.SoilData;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.DBUtil.AFSIRSCollection;
import static org.afsirs.web.util.DBUtil.getConnection;
import org.afsirs.web.util.MongoDBHandler;
import org.bson.types.ObjectId;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Meng Zhang
 */
public class AFSIRSSoilDataTester {

    private final static String adminId = "mike";

    @Before
    public void init() {
        UserDAO.getUserByUsername(adminId);
    }

//    @Test
    public void checkAllSoilData() {
        HashMap<String, SoilData> soilDB = new HashMap();
        for (WaterUsePermit permit : WaterUsePermitDAO.listFull(adminId)) {
            
            SoilData soilData = SoilData.readFromJson(AFSIRSModule.savePermitJson(permit.toAFSIRSInputData(permit.getUser_id())));
            String hash = soilData.getHash().toString();

            String unit = permit.getSoil_unit_name().trim();
            if (soilDB.containsKey(hash)) {
                
                System.out.println("Repeated unit name [" + unit + "] for " + permit.getUser_id() + ":" + permit.getPermit_id());// + " <> " + soilDB.get(unit).getUser_id() + ":" + soilDB.get(unit).getPermit_id());
                if (permit.getSoil_unit_name().equalsIgnoreCase("Pomona")) {
                    System.out.println("Find it");
                }
                permit.setSoilData(soilDB.get(hash));
                runPermit(permit);

            } else {
                soilDB.put(hash, soilData);
            }
        }
        assertTrue(true);
    }
    
    @Test
    public void checkAllSoilData2() {
        for (WaterUsePermit permit : WaterUsePermitDAO.listFull(adminId)) {
            if (permit.getPermit_id().equalsIgnoreCase("testDB001")) {
                System.out.println("Find it");
            }
            SoilData soilData = SoilDataDAO.find(permit.getSoil_id());
            SoilData soilDataOrg = SoilData.readFromJson(AFSIRSModule.savePermitJson(permit.toAFSIRSInputData(permit.getUser_id())));
            soilDataOrg.setUser_id(permit.getUser_id());
            
            assertTrue(permit.getUser_id() + "\t" + permit.getPermit_id(),soilData.getUser_id().equals(permit.getUser_id()));
            assertTrue(permit.getUser_id() + "\t" + permit.getPermit_id(),soilData.getSoil_unit_name().equals(permit.getSoil_unit_name()));
            assertTrue(permit.getUser_id() + "\t" + permit.getPermit_id(),soilData.getHash().toString().equals(soilDataOrg.getHash().toString()));
            
            permit.setSoilData(soilData);        
            runPermit(permit);
        }
        
        assertTrue(true);
    }
    
//    @Test
    public void createSoilDataDB() {
        HashMap<String, SoilData> soilHashDB = new HashMap();
        HashMap<String, SoilData> soilUnitDB = new HashMap();
        HashMap<String, ObjectId> soilIdDB = new HashMap();
        for (WaterUsePermit permit : WaterUsePermitDAO.listFull(adminId)) {
            
            if ("8099test2B".equals(permit.getPermit_id())) {
                System.out.println("Find it");
            }
            
            SoilData soilData = SoilData.readFromJson(AFSIRSModule.savePermitJson(permit.toAFSIRSInputData(permit.getUser_id())));
            soilData.setUser_id(permit.getUser_id());
            String hash = soilData.getHash().toString();
            String unitKey = soilData.getSoil_unit_name() + ":" + soilData.getUser_id();
            SoilData retSoil;

            String unit = permit.getSoil_unit_name().trim();
            ObjectId retId;
            if (soilHashDB.containsKey(hash)) {
                
                System.out.println("Repeated data for [" + unit + "] by \t" + permit.getUser_id() + "\t" + permit.getPermit_id());// + " <> " + soilDB.get(unit).getUser_id() + ":" + soilDB.get(unit).getPermit_id());
                
                retId = SoilDataDAO.add(soilData, soilData.getUser_id());
                assertTrue(retId.toString().equals(soilIdDB.get(hash).toString()));

            } else if (soilUnitDB.containsKey(unitKey)) {
//                assertTrue(false);
                System.out.println("Repeated unit for [" + unit + "] by \t" + permit.getUser_id() + "\t" + permit.getPermit_id());
                
                retId = SoilDataDAO.add(soilData, soilData.getUser_id());
                retSoil = SoilDataDAO.find(retId);
                String retHash = retSoil.getHash().toString();
                String retUnit = retSoil.getSoil_unit_name();
                soilHashDB.put(retHash, soilData);
                soilUnitDB.put(retUnit + ":" + soilData.getUser_id(), soilData);
                soilIdDB.put(retHash, retId);
                
                assertTrue(!hash.equals(retHash));
                assertTrue(!unit.equals(retUnit));
                assertTrue(!retId.toString().equals(soilUnitDB.get(unitKey).toString()));
                
                unit = retUnit;
                hash = retHash;
                
            } else {
                
                retId = SoilDataDAO.add(soilData, soilData.getUser_id());
                
                soilHashDB.put(hash, soilData);
                soilUnitDB.put(unitKey, soilData);
                soilIdDB.put(hash, retId);
                
                assertTrue(retId != null);
            }
            
            if (WaterUsePermitDAO.update(permit, permit.getUser_id(), retId, unit)) {
                WaterUsePermit retPermit = WaterUsePermitDAO.find(permit.getPermit_id(), permit.getUser_id());
                assertTrue(retPermit != null);
                SoilData tmp = SoilData.readFromJson(AFSIRSModule.savePermitJson(retPermit.toAFSIRSInputData(retPermit.getUser_id())));
                tmp.setUser_id(retPermit.getUser_id());
                tmp.setSoil_unit_name(unit);
                if (!tmp.getHash().toString().equals(hash))
                assertTrue(permit.getUser_id() + "\t" + permit.getPermit_id(), tmp.getHash().toString().equals(hash));
            } else {
                assertTrue(permit.getUser_id() + "\t" + permit.getPermit_id() + "\t failed", false);
            }
        }
    }

//    @Test
    public void cleanSoilDataDB() {
        MongoDBHandler.clean(getConnection(AFSIRSCollection.SoilData));
        assertTrue(true);
    }
    
//    @Test
//    public void findSB() {
//        WaterUsePermitDAO.findSB(adminId);
//        assertTrue(true);
//    }
}
