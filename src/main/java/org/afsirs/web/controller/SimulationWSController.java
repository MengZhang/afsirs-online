package org.afsirs.web.controller;

import java.io.File;
import org.afsirs.web.dao.SimulationDAO;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Login;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Logout;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Renew;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.UnknownAct;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Login_Failed;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Renew_Failed;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Success;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.UnknownRet;
import org.afsirs.web.dao.bean.WebSocketSimStatusMsg;
import org.afsirs.web.util.Path;
import org.afsirs.web.util.WebSocketUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author mike
 */
@WebSocket
public class SimulationWSController {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        WebSocketSimStatusMsg wsmsg = new WebSocketSimStatusMsg(message);
        if (wsmsg.isResponse()) {

        } else {
            switch (wsmsg.getAction()) {
                case Login:
                    boolean ret = SimulationDAO.registerSession(user, wsmsg.getUserId(), wsmsg.getPermitId());
                    if (ret) {
                        if (!WebSocketUtil.sendMsg(user, Login, Success)) {
                            SimulationDAO.disconnect(user);
                            WebSocketUtil.sendMsg(user, Login, Login_Failed);
                        }
                    } else {
                        // Check if result json is existed
                        File json = Path.Folder.getUserWaterUsePermitOutputJsonFile(wsmsg.getUserId(), wsmsg.getPermitId());
                        if (json.exists()) {
                            WebSocketUtil.sendMsg(user, new WebSocketSimStatusMsg(100));
                        } else {
                            WebSocketUtil.sendMsg(user, Login, Login_Failed);
                        }
                    }
                    break;
                case Renew:
                    if (!SimulationDAO.renew(user, wsmsg.getUserId(), wsmsg.getPermitId())) {
                        WebSocketUtil.sendMsg(user, Renew, Renew_Failed);
                    } else {
                        WebSocketUtil.sendMsg(user, Renew, Success);
                    }
                    break;
                case Logout:
                    SimulationDAO.disconnect(user);
                    WebSocketUtil.sendMsg(user, Logout, Success);
                    break;
                default:
                    WebSocketUtil.sendMsg(user, UnknownAct, UnknownRet);
            }
        }
    }
}
