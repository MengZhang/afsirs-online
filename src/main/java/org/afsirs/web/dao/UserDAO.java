package org.afsirs.web.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoWriteException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.afsirs.web.dao.bean.User;
import org.afsirs.web.util.DBUtil.AFSIRSCollection;
import static org.afsirs.web.util.DBUtil.getConnection;
import org.afsirs.web.util.MongoDBHandler;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    private static final ConcurrentHashMap<String, User> users = syncUserRecords();

    public static ConcurrentHashMap<String, User> syncUserRecords() {
        ConcurrentHashMap<String, User> ret = new ConcurrentHashMap();
        return ret;
    }

    public static boolean isAdmin(String userId) {
        if (users.containsKey(userId)) {
            return users.get(userId).isAdmin();
        } else {
            return false;
        }
    }

    public static User getUserByUsername(String userName) {
        if (users.containsKey(userName)) {
            return users.get(userName);
        } else {
            try {
                Document data = MongoDBHandler.find(getConnection(AFSIRSCollection.User), new Document("userName", userName));
                if (data == null) {
                    return null;
                } else {
                    String json = data.toJson();
                    User ret = null;
                    if (!json.trim().isEmpty()) {
                        ObjectMapper mapper = new ObjectMapper();
                        ret = mapper.readValue(json, User.class);
                        users.put(userName, ret);
                    }
                    return ret;
                }
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
        try {
            return MongoDBHandler.add(getConnection(AFSIRSCollection.User),
                    new Document("userName", user.getUserName())
                    .append("salt", salt)
                    .append("hashedPassword", hashedPassword)
                    .append("userRank", "regular")); //TODO
        } catch (MongoWriteException ex) {
            return false;
        }
    }

    public static Collection<User> getAllUserNames() {
        return users.values();
    }
}
