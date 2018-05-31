package org.afsirs.web.dao.bean;

import lombok.Data;
import lombok.Getter;
import org.afsirs.module.util.JSONObject;
import org.afsirs.module.util.JsonUtil;

/**
 *
 * @author Meng Zhang
 */
@Data
public class WebSocketMsg {

    public enum WSAction {

        Login,
        Logout,
        Renew,
        Simulation,
        UnknownAct
    }

    public enum WSStatus {

        Success(200),
        Login_Failed(601),
        Renew_Failed(602),
        Error(900),
        UnknownRet(0);

        private final int code;

        private WSStatus(int code) {
            this.code = code;
        }

        public int getStatusCode() {
            return this.code;
        }
    }

    @Getter private JSONObject msg;
    @Getter private boolean response;
    @Getter private int status;
    @Getter private WSAction action;
    @Getter private String hash;
    @Getter private String message;

    private static final String ACTION = "action";
    private static final String STATUS = "status";
    private static final String HASH = "hash";
    private static final String MESSAGE = "message";

    public WebSocketMsg(String msg) {
        this.msg = JsonUtil.parseFrom(msg);
        init();
    }

    public WebSocketMsg(JSONObject msg) {
        this.msg = msg;
        init();
    }

    public WebSocketMsg(WSAction action, WSStatus status) {
        this.action = action;
        this.status = status.getStatusCode();
        this.msg = new JSONObject()
                .put(ACTION, action.toString())
                .put(STATUS, status.getStatusCode());
    }

    private void init() {
        try {
            action = Enum.valueOf(WSAction.class, msg.getOrBlank(ACTION));
        } catch (Exception e) {
            action = WSAction.UnknownAct;
        }
        try {
            status = Integer.parseInt(msg.getOrBlank(STATUS));
        } catch (Exception e) {
            status = 0;
        }
        response = status > 0;
        hash = msg.getOrBlank(HASH);
        message = msg.getOrBlank(MESSAGE);
    }
    
    public void setMessage(String message) {
        this.message = message;
        this.msg.put(MESSAGE, message);
    }
}
