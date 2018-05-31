package org.afsirs.web.controller;

import org.afsirs.web.dao.WorkerDAO;
import org.afsirs.web.dao.bean.WebSocketMsg;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Login;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Logout;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Renew;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.UnknownAct;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Login_Failed;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Renew_Failed;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Success;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.UnknownRet;
import org.afsirs.module.util.JSONArray;
import org.afsirs.module.util.JSONObject;
import org.afsirs.web.util.WebSocketUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author Meng Zhang
 */
@WebSocket
public class WorkerWSController {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        WorkerDAO.disconnect(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        WebSocketMsg wsmsg = new WebSocketMsg(message);
        if (wsmsg.isResponse()) {

        } else {
            switch (wsmsg.getAction()) {
                case Login:
                    String hash = WorkerDAO.authenticate(wsmsg.getMsg(), user);
                    if (hash != null) {
                        if (!WebSocketUtil.sendMsg(user, Login, Success, new JSONObject()
                                .put("hash", hash)
                                .put("workers", new JSONArray().putAll(WorkerDAO.getAllWorkerIds()))
                        )) {
                            WorkerDAO.disconnect(hash);
                            WebSocketUtil.sendMsg(user, Login, Login_Failed);
                        }
                    } else {
                        WebSocketUtil.sendMsg(user, Login, Login_Failed);
                    }
                    break;
                case Renew:
                    if (!WorkerDAO.renew(wsmsg.getHash(), user)) {
                        WebSocketUtil.sendMsg(user, Renew, Renew_Failed);
                    } else {
                        WebSocketUtil.sendMsg(user, Renew, Success);
                    }
                    break;
                case Logout:
                    WorkerDAO.disconnect(wsmsg.getHash());
                    WebSocketUtil.sendMsg(user, Logout, Success, new JSONObject()
                                .put("workers", new JSONArray().putAll(WorkerDAO.getAllWorkerIds())));
                    break;
                default:
                    WebSocketUtil.sendMsg(user, UnknownAct, UnknownRet);
            }
        }
    }

}
