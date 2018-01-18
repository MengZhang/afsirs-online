package org.afsirs.web.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.afsirs.web.dao.bean.User;
import org.afsirs.web.util.DBUtil;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    private static final ConcurrentHashMap<String, User> users = syncUserRecords();

    public static ConcurrentHashMap<String, User> syncUserRecords() {
        ConcurrentHashMap<String, User> ret = new ConcurrentHashMap();
        return ret;
    }

    public static User getUserByUsername(String userName) {
        if (users.containsKey(userName)) {
            return users.get(userName);
        } else {
            String json;
            try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {

                MongoDatabase database = mongoClient.getDatabase(DBUtil.AFSIRS_DB);
                MongoCollection<Document> collection = database.getCollection(DBUtil.AFSIRS_USER_COLLECTION);
                Document ret = collection.find(new Document("userName", userName)).first();
                if (ret == null) {
                    json = "";
                } else {
                    json = ret.toJson();
                }
            }

            try {
                User ret = null;
                if (!json.trim().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    ret = mapper.readValue(json, User.class);
                    users.put(userName, ret);
                }
                return ret;

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        return null;
    }

    public static boolean registerUser(User user) {
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            return false;
        }
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        try (MongoClient mongoClient = new MongoClient(DBUtil.getDBURI());) {
     
            MongoDatabase database = mongoClient.getDatabase(DBUtil.AFSIRS_DB);
            MongoCollection<Document> collection = database.getCollection(DBUtil.AFSIRS_USER_COLLECTION);
//            if (collection.find(new Document("userName", userName)).first() == null) {
//                return false;
//            }
            collection.insertOne(
                    new Document("userName", user.getUserName())
                    .append("salt", salt)
                    .append("hashedPassword", hashedPassword)
                    .append("userRank", "regular")); //TODO
             
            return true;
        }
    }

    public static Collection<User> getAllUserNames() {
        return users.values();
    }
}
