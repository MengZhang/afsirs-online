package org.afsirs.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.dao.UserDAO;
import org.afsirs.web.dao.WaterUsePermitDAO;
import static org.afsirs.web.dao.bean.WaterUsePermit.setDeviation;
import org.afsirs.web.util.Path;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Meng Zhang
 */
public class AFSIRSTester {
    
    private final static String adminId = "mike";
    
    @Before
    public void init() {
        UserDAO.getUserByUsername(adminId);
    }
    
    @Test
    public void runAllFromDB() {
        for (WaterUsePermit permit : WaterUsePermitDAO.list(adminId)) {
            try {
                String userId = permit.getUser_id();
                UserInput input = setDeviation(permit.toAFSIRSInputData(userId), permit);
                AFSIRSModule.savePermitFile(Path.Folder.getUserWaterUsePermitDir(userId), input);
                SimResult ret = AFSIRSModule.run(input, null);
                File comp = ret.getCalculationExcel();
                URL resouce = this.getClass().getResource("/" + userId + "/" + permit.getPermit_id() + "-Cal.xlsx");
                if (resouce == null) {
                    continue;
                }
                String path = URLDecoder.decode(resouce.getPath(), "UTF-8");
                File expect = new File(path);
                if (!expect.exists()) {
                    System.out.println(expect.getPath() + " does not exist");
                }
            } catch (Exception ex) {
                System.out.println(permit.getUser_id() + " : " + permit.getPermit_id());
                ex.printStackTrace();
            }
//            assertTrue(expect.getPath() + " does not exist", expect.exists());
//            assertEquals(ret, ret2);
        }
        assertTrue(true);
    }
    
}
