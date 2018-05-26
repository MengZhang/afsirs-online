package org.afsirs.web.util;

import java.util.Collection;

/**
 *
 * @author Meng Zhang
 */
public class JSONArray extends org.json.simple.JSONArray {
    
    public JSONArray put(Object e) {
        super.add(e);
        return this;
    }
    
    public JSONArray putAll(Collection c) {
        super.addAll(c);
        return this;
    }
}
