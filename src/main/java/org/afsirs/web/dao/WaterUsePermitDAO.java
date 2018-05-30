package org.afsirs.web.dao;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import lombok.Data;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.DBUtil.AFSIRSCollection;
import static org.afsirs.web.util.DBUtil.getConnection;
import org.afsirs.web.util.MongoDBHandler;
import org.afsirs.web.util.Path;
import org.bson.Document;
import org.eclipse.jetty.util.ConcurrentHashSet;

/**
 *
 * @author Meng Zhang
 */
public class WaterUsePermitDAO {

    private static final ConcurrentHashSet<WUPKey> permitIds = syncUserRecords();

    @Data
    public static class WUPKey {

        private String userId;
        private String permitId;

        private WUPKey(String userId, String permitId) {
            this.userId = userId;
            this.permitId = permitId;
        }
    }

    public static ConcurrentHashSet<WUPKey> syncUserRecords() {
        ConcurrentHashSet<WUPKey> ret = new ConcurrentHashSet();
        return ret;
    }

    public static ArrayList<WaterUsePermit> list(String userId) {
        ArrayList<WaterUsePermit> ret = new ArrayList<>();
        ArrayList<Document> dbRetArr;
        if (UserDAO.isAdmin(userId)) {
            dbRetArr = MongoDBHandler.list(getConnection(AFSIRSCollection.WaterUsePermit));
        } else {
            dbRetArr = MongoDBHandler.search(
                getConnection(AFSIRSCollection.WaterUsePermit),
                new Document("user_id", userId));
        }
        
        for (Document data : dbRetArr) {
            ret.add(WaterUsePermit.readFromJson(data.toJson()));
        }
        return ret;
//            return listPermits(userId);
    }

    public static WaterUsePermit find(String id, String userId) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Document dbRet = MongoDBHandler.find(getConnection(AFSIRSCollection.WaterUsePermit),
                MongoDBHandler.getFindCritia(
                        new String[]{"permit_id", "user_id"},
                        new String[]{id, userId}));
        if (dbRet != null) {
            return WaterUsePermit.readFromJson(dbRet.toJson());
        } else {
            return null;
        }
    }

    public static boolean add(WaterUsePermit permit, String currentUser) {
        WUPKey pk = new WUPKey(currentUser, permit.getPermit_id());
//        if (permitIds.contains(pk)) {
//            return false;
//        }
        String json = AFSIRSModule.savePermitJson(permit.toAFSIRSInputData(currentUser));
        if (json != null && !json.isEmpty()) {
            try {
                Document data = Document.parse(json);
                data.put("user_id", currentUser);
                boolean ret = MongoDBHandler.add(getConnection(AFSIRSCollection.WaterUsePermit), data);
                if (ret) {
                    permitIds.add(pk);
                }
                return ret;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean update(WaterUsePermit permit, String currentUser) {
        String json = AFSIRSModule.savePermitJson(permit.toAFSIRSInputData(currentUser));
        if (json != null && !json.isEmpty()) {
            try {
                Document data = Document.parse(json);
                data.put("user_id", currentUser);
                return MongoDBHandler.replace(getConnection(AFSIRSCollection.WaterUsePermit),
                        MongoDBHandler.getFindCritia(
                                new String[]{"permit_id", "user_id"},
                                new String[]{permit.getPermit_id(), currentUser}),
                        data) != null;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        } else {
            return false;
        }
    }

    public static File getOutputFile(String userId, String permitId, String fileType) {

        File outDir = Path.Folder.getUserWaterUsePermitOutputDir(userId);
        String fileName;

        if (fileType == null || fileType.isEmpty()) {
            fileName = permitId + "-Summary.pdf";
        } else if ("text".equalsIgnoreCase(fileType)) {
            fileName = permitId + ".txt";
        } else if ("pdf".equalsIgnoreCase(fileType)) {
            fileName = permitId + "-Summary.pdf";
        } else if ("excel".equalsIgnoreCase(fileType)) {
            fileName = permitId + "-Summary.xlsx";
        } else if ("calcExcel".equalsIgnoreCase(fileType)) {
            fileName = permitId + "-Cal.xlsx";
        } else {
            fileName = permitId + "-Summary.pdf";
        }
        return Paths.get(outDir.getPath(), fileName).toFile();
    }
}
