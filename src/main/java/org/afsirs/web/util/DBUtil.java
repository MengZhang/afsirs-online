package org.afsirs.web.util;

import com.mongodb.MongoClientURI;

/**
 *
 * @author Meng Zhang
 */
public class DBUtil {
    
//    public static String getDBBaseURI() {
//        return "https://sample-api2.herokuapp.com/rest";
////        return "http://localhost:8080/ifdc-db-api/rest/";
//    }
    
    public static String AFSIRS_DB = "afsirs_db";
    public static String AFSIRS_USER_COLLECTION = "users";
    public static String AFSIRS_WUP_COLLECTION = "water_use_permit";
    
    public static MongoClientURI getDBURI() {
        // Give your DB path here
        String dbPath = "mongodb://mikecomic:Mike0105@cluster0-shard-00-00-upixo.mongodb.net:27017,cluster0-shard-00-01-upixo.mongodb.net:27017,cluster0-shard-00-02-upixo.mongodb.net:27017/mydb?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin";
        MongoClientURI uri = new MongoClientURI(dbPath);
        return uri;
    }
    
//    public static ArrayList<String> get
}
