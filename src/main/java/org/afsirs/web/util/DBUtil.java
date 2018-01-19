package org.afsirs.web.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author Meng Zhang
 */
public class DBUtil {
    
    private static MongoClient mongoClient;
    protected final static String DEF_SKIP = "0";
    protected final static String DEF_LIMIT = Integer.MAX_VALUE + "";
    
    public final static String AFSIRS_DB = "afsirs_db";
    public final static String AFSIRS_USER_COLLECTION = "users";
    public final static String AFSIRS_WUP_COLLECTION = "water_use_permit";
    
    public enum AFSIRSCollection {
        
        User("users"),
        WaterUserPermit("water_use_permit");

        private final String name;

        private AFSIRSCollection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public static MongoClientURI getDBURI() {
        // Give your DB path here
        String dbPath = "mongodb://mikecomic:Mike0105@cluster0-shard-00-00-upixo.mongodb.net:27017,cluster0-shard-00-01-upixo.mongodb.net:27017,cluster0-shard-00-02-upixo.mongodb.net:27017/mydb?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin";
        MongoClientURI uri = new MongoClientURI(dbPath);
        return uri;
    }
    
    public static MongoCollection<Document> getConnection(AFSIRSCollection collection) {
        return getConnection(collection.toString());
    }
    
    public static MongoCollection<Document> getConnection(String collectionName) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(DBUtil.getDBURI());
        }
        return mongoClient.getDatabase(AFSIRS_DB).getCollection(collectionName);
    }
}
