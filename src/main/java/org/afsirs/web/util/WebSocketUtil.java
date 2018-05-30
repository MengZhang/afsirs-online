package org.afsirs.web.util;

import java.io.IOException;
import org.afsirs.module.util.JSONObject;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.dao.bean.WebSocketMsg;
import org.afsirs.web.dao.bean.WebSocketMsg.WSAction;
import org.afsirs.web.dao.bean.WebSocketMsg.WSStatus;
import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author Meng Zhang
 */
public class WebSocketUtil {
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status) {
        return sendMsg(receiver, action, status, "");
    }
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status, String message) {
        WebSocketMsg msg = new WebSocketMsg(action, status);
        if (message != null && !message.isEmpty()) {
            msg.setMessage(message);
        }
        return sendMsg(receiver, msg);
    }
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status, JSONObject messages) {
        WebSocketMsg msg = new WebSocketMsg(action, status);
        if (messages != null) {
            msg.getMsg().putAll(messages);
        }
        return sendMsg(receiver, msg);
    }
    
    public static boolean sendMsg(Session receiver, WebSocketMsg msg) {
        try {
            receiver.getRemote().sendString(String.valueOf(msg.getMsg()));
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        return true;
    }
}
