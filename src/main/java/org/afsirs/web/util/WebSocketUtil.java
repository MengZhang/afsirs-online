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
    
//    public static void sendMsg(Session receiver, String message) {
//        try {
//            receiver.getRemote().sendString(message);
//        } catch (IOException ex) {
//            ex.printStackTrace(System.err);
//        }
//    }
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status, String message) {
        return sendMsg(receiver, action, status.getStatusCode(), message);
    }
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status) {
        return sendMsg(receiver, action, status.getStatusCode(), "");
    }
    
    private static boolean sendMsg(Session receiver, WSAction action, int status, String message) {
        WebSocketMsg msg = new WebSocketMsg(action, status);
        if (message != null && !message.isEmpty()) {
            msg.setMessage(message);
        }
        return sendMsg(receiver, msg);
    }
    
    public static boolean sendMsg(Session receiver, WSAction action, WSStatus status, JSONObject messages) {
        return sendMsg(receiver, action, status.getStatusCode(), messages);
    }
    
    private static boolean sendMsg(Session receiver, WSAction action, int status, JSONObject messages) {
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
