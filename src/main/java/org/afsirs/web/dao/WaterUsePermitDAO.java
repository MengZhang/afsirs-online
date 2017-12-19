package org.afsirs.web.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.Path;
import static org.afsirs.web.util.Path.Folder.getUserWaterUsePermitDir;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.jetty.util.ConcurrentHashSet;
//import org.ifdc.web.dao.bean.Project;
//import org.ifdc.web.util.DBUtil;
//import org.ifdc.web.util.JsonUtil;

/**
 *
 * @author Meng Zhang
 */
public class WaterUsePermitDAO {

//    private static final ConcurrentHashSet<String> projectNames = syncRecords("project");

//    public static ConcurrentHashSet<String> syncRecords(String dataType) {
//        ConcurrentHashSet<String> ret = new ConcurrentHashSet();
//
//        try {
//            Client client = ClientBuilder.newClient();
//            WebTarget service = client.target(DBUtil.getDBBaseURI());
//            Response response = service.path("me").path(dataType).path("listname").request().get();
//            if (response.getStatus() == 200) {
//                String resJson = response.readEntity(String.class);
//                if (!resJson.trim().equals("")) {
//                    ObjectMapper mapper = new ObjectMapper();
//                    ret.addAll(mapper.readValue(resJson, ArrayList.class));
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return ret;
//    }
    
    public static ArrayList<WaterUsePermit> list(String userId) {
        try {
//            Client client = ClientBuilder.newClient();
//            WebTarget service = client.target(DBUtil.getDBBaseURI());
//            Response response = service.path("me").path("project").path("list")
//                    .request().get();
//            if (response.getStatus() == 200) {
//                String json = response.readEntity(String.class);
//                return JsonUtil.toObject(json, new TypeReference<ArrayList<HashMap>>() {});
//            }
            
            return listPermits(userId);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return new ArrayList();
    }
    
    private static ArrayList<WaterUsePermit> listPermits(String userId) {
        ArrayList<WaterUsePermit> ret = new ArrayList();
        File permitDir = getUserWaterUsePermitDir(userId);
        for (File file : permitDir.listFiles((FileFilter) new WildcardFileFilter("*.json"))) {
            ret.add(WaterUsePermit.readFromJson(file));
        }
        return ret;
    }
    
//    public static boolean isNameExist(String permitName) {
//        return permitName != null && !permitName.isEmpty() && projectNames.contains(permitName);
//    }
    
//    public static List<String> listNames() {
//        return Arrays.asList(projectNames.toArray(new String[]{}));
//    }
    
//    public static HashMap find(String id) {
//        if (id == null || id.isEmpty()) {
//            return new HashMap();
//        }
//        try {
//            Client client = ClientBuilder.newClient();
//            WebTarget service = client.target(DBUtil.getDBBaseURI());
//            Response response = service.path("me").path("project").path("find")
//                    .queryParam("id", id)
//                    .request().get();
//            if (response.getStatus() == 200) {
//                String json = response.readEntity(String.class);
//                return JsonUtil.toMap(json);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return new HashMap();
//    }

    public static boolean add(WaterUsePermit permit, String currentUser) {
        File dir = Path.Folder.getUserWaterUsePermitDir(currentUser);
        return AFSIRSModule.savePermitFile(dir, permit.toAFSIRSInputData());
    }
    
//    public static boolean add(Project project) {
//        String name = project.getName();
//        String description = project.getDescription();
//        if (name == null || name.isEmpty()) {
//            return false;
//        } else {
//            try {
//                Client client = ClientBuilder.newClient();
//                WebTarget service = client.target(DBUtil.getDBBaseURI());
//                Response response = service.path("me").path("project").path("add")
//                        .queryParam("name", name)
//                        .queryParam("description", description)
//                        .request().get();
//                if (response.getStatus() == 200) {
//                    projectNames.add(name);
//                    String id = response.readEntity(String.class);
//                    if (id != null && !id.equals("-1") && !id.equals("-2")) {
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            return false;
//        }
//    }
}
