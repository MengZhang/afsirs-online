package org.afsirs.web.util;

import java.util.Map;

/**
 *
 * @author Meng Zhang
 */
public class JSONObject extends org.json.simple.JSONObject {
    
    public JSONObject() {
        super();
    }
    
    public JSONObject(Map m) {
        super(m);
    }
    
    public String getOrBlank(String key) {
        return (String) super.getOrDefault(key, "");
    }
    
    public String getOrDefault(String key, String def) {
        return (String) super.getOrDefault(key, def);
    }
}
