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
        return (String) getOrDefault(key, "");
    }
    
    public String getOrDefault(String key, String def) {
        Object val = super.getOrDefault(key, def);
        if (val == null) {
            return def;
        } else {
            return val.toString();
        }
    }
}
