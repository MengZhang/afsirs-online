package org.afsirs.web.dao.bean;

import java.util.HashMap;
import org.afsirs.module.util.JSONObject;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author Meng Zhang
 */
public class Worker  extends HashMap<String, Object>{
    
    public Worker() {
        super();
    }
    
    public Worker(JSONObject msg) {
        this(msg.getOrBlank("worker_id"),
             msg.getOrBlank("password"));
    }
    
    public Worker(String id, String password) {
        super();
        this.put("worker_id", id);
        this.put("password", password);
    }
    
    public String getSalt() {
        return (String) this.get("salt");
    }
    public String getPassword() {
        return (String) this.get("password");
    }
    public String getHashedPassword() {
        return (String) this.get("hashedPassword");
    }
    public String getWorkerId() {
        return (String) this.get("worker_id");
    }
    
//    public String putWorkerHash(String hash) {
//        return (String) this.put("worker_hash", hash);
//    }
//    
//    public String getWorkerHash() {
//        return (String) this.get("worker_hash");
//    }
    
    public Session setWorkerSession(Session user) {
        if (user == null) {
            return removeWorkerSession();
        }
        return (Session) this.put("worker_session", user);
    }
    
    public Session removeWorkerSession() {
        return (Session) this.remove("worker_session");
    }
    
    public Session getWorkerSession() {
        return (Session) this.get("worker_session");
    }
    
    public boolean isAlive() {
        Session session = getWorkerSession();
        return session != null && session.isOpen();
    }
}