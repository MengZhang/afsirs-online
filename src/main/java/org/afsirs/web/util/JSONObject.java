package org.afsirs.web.util;

import java.math.BigDecimal;
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
    
    public JSONObject(org.json.simple.JSONObject o) {
        super(o);
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
    
    public Double getAsDouble(String key, int round) {
        String read = getOrBlank(key);
        if (!read.isEmpty()) {
            return new BigDecimal(read).setScale(round, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return null;
    }
    
    public Double getAsDouble(String key) {
        String read = getOrBlank(key);
        if (!read.isEmpty()) {
            return new BigDecimal(read).doubleValue();
        }
        return null;
    }
}
