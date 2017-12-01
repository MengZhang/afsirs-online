package org.afsirs.web.dao.bean;

import java.util.HashMap;

/**
 *
 * @author Meng Zhang
 */
public class User extends HashMap<String, Object>{
    
    public User() {
        super();
    }
    
    public User(String userName, String password) {
        super();
        this.put("userName", userName);
        this.put("password", password);
    }
    
    public String getSalt() {
        return (String) this.get("salt");
    }
    public String getHashedPassword() {
        return (String) this.get("hashedPassword");
    }
    public String getUserName() {
        return (String) this.get("userName");
    }
}
