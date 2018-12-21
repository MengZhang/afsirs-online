package org.afsirs.web.dao;

import com.google.common.hash.HashCode;
import com.mongodb.client.model.Projections;
import java.util.ArrayList;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.web.dao.bean.SoilData;
import org.afsirs.web.util.DBUtil.AFSIRSCollection;
import static org.afsirs.web.util.DBUtil.getConnection;
import org.afsirs.web.util.MongoDBHandler;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Meng Zhang
 */
public class SoilDataDAO {

    private static final String[] listParams = {"_id", "user_id", "soil_unit_name", "soil_source", "afsirs", "planted_area", "total_area"};

    public static ArrayList<SoilData> list(String userId) {
        ArrayList<SoilData> ret = new ArrayList<>();
        ArrayList<Document> dbRetArr;
        if (UserDAO.isAdmin(userId)) {
            dbRetArr = MongoDBHandler.list(
                    getConnection(AFSIRSCollection.SoilData),
                    Projections.include(listParams));
        } else {
            dbRetArr = MongoDBHandler.search(
                    getConnection(AFSIRSCollection.SoilData),
                    new Document("user_id", userId),
                    Projections.include(listParams));
        }

        for (Document data : dbRetArr) {
            ret.add(SoilData.readFromJson(data.toJson()));
        }
        return ret;
    }

    public static SoilData find(String unitName, String userId) {
        if (unitName == null || unitName.isEmpty()) {
            return null;
        }
        Document dbRet = MongoDBHandler.find(getConnection(AFSIRSCollection.SoilData),
                MongoDBHandler.getFindCritia(
                        new String[]{"soil_unit_name", "user_id"},
                        new String[]{unitName, userId}));
        if (dbRet != null) {
            return SoilData.readFromJson(dbRet.toJson());
        } else {
            return null;
        }
    }

    public static SoilData find(String id) {
        return find(new ObjectId(id));
    }

    public static SoilData find(ObjectId id) {
        if (id == null) {
            return null;
        }
        Document dbRet = MongoDBHandler.find(
                getConnection(AFSIRSCollection.SoilData),
                new Document("_id", id));
        if (dbRet != null) {
            return SoilData.readFromJson(dbRet.toJson());
        } else {
            return null;
        }
    }

    public static ObjectId getId(HashCode hash) {
        if (hash == null) {
            return null;
        }
        Document dbRet = MongoDBHandler.find(getConnection(AFSIRSCollection.SoilData),
                MongoDBHandler.getFindCritia(
                        new String[]{"data_hash"},
                        new String[]{hash.toString()}),
                Projections.include("_id"));
        if (dbRet != null) {
            return dbRet.getObjectId("_id");
        } else {
            return null;
        }
    }

    public static ObjectId getId(String unitName, String userId) {
        if (unitName == null || unitName.isEmpty()) {
            return null;
        }
        Document dbRet = MongoDBHandler.find(getConnection(AFSIRSCollection.SoilData),
                MongoDBHandler.getFindCritia(
                        new String[]{"soil_unit_name", "user_id"},
                        new String[]{unitName, userId}),
                Projections.include("_id"));
        if (dbRet != null) {
            return dbRet.getObjectId("_id");
        } else {
            return null;
        }
    }

    public static ObjectId add(SoilData soil, String currentUser) {
        String json = AFSIRSModule.saveSoilDataJson(soil.toAFSIRSInputSoilData());
        if (json != null && !json.isEmpty()) {
            try {
                String soilIdLoc = soil.getSoil_id();
                String unitName = soil.getSoil_unit_name();
                if (soilIdLoc != null && !soilIdLoc.isEmpty()) {
                    ObjectId ret = getId(unitName, currentUser);
                    if (ret != null && soilIdLoc.equals(ret.toString())) {
                        return ret;
                    }
                }
                HashCode hash = soil.getHash();
                unitName = unitName.replaceAll("((?<!_)__\\(\\d+\\))?$", "");
                Document data = Document.parse(json);
                data.put("user_id", currentUser);
                data.put("data_hash", hash.toString());
                int count = 0;
                while (!MongoDBHandler.add(getConnection(AFSIRSCollection.SoilData), data)) {
                    if (count < 1) {
                        count = 1;
                        for (SoilData soilRet : list(currentUser)) {
                            if (soilRet.getSoil_unit_name().matches("((?<!_)__\\(\\d+\\))?$") &&
                                    soilRet.getSoil_unit_name().startsWith(unitName)) {
                                count++;
                            }
                        }
                    } else if (count > 100) {
                        break; // TODO
                    }
                    soil.setSoil_unit_name(unitName + "__(" + count + ")");
                    count++;
                    data.put("soil_unit_name", soil.getSoil_unit_name());
                    hash = soil.getHash();
                    data.put("data_hash", hash.toString());
                }
                
                return getId(hash);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean update(SoilData soil, String currentUser) {
        String json = AFSIRSModule.saveSoilDataJson(soil.toAFSIRSInputSoilData());
        if (json != null && !json.isEmpty()) {
            try {
                Document data = Document.parse(json);
                data.put("user_id", currentUser);
                data.put("data_hash", soil.getHash().toString());
                return MongoDBHandler.replace(getConnection(AFSIRSCollection.SoilData),
                        MongoDBHandler.getFindCritia(
                                new String[]{"soil_unit_name", "user_id"},
                                new String[]{soil.getSoil_unit_name(), currentUser}),
                        data) != null;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        } else {
            return false;
        }
    }
    
    public static boolean delete(String id, String currentUser) {
        return delete(new ObjectId(id), currentUser);
    }

    public static boolean delete(ObjectId id, String currentUser) {
        try {
            return MongoDBHandler.delete(getConnection(AFSIRSCollection.SoilData),
                    new Document("_id", id).append("user_id", currentUser));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return false;
        }
    }
}
