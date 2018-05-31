package org.afsirs.web.dao.bean;

import lombok.Getter;
import org.afsirs.module.util.JSONObject;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSAction.Simulation;
import static org.afsirs.web.dao.bean.WebSocketMsg.WSStatus.Success;

/**
 *
 * @author Meng Zhang
 */
public class WebSocketSimStatusMsg extends WebSocketMsg {
    
    @Getter String userId;
    @Getter String permitId;
    @Getter int progressPct;
    
    private static final String USER_ID = "user_id";
    private static final String PERMIT_ID = "permit_id";
    private static final String PROGRESS_PCT = "progress_pct";
    
    public WebSocketSimStatusMsg(String msg) {
        super(msg);
        init();
    }

    public WebSocketSimStatusMsg(JSONObject msg) {
        super(msg);
        init();
    }
    
    public WebSocketSimStatusMsg(WSAction action, WSStatus status) {
        super(action, status);
    }
    
    public WebSocketSimStatusMsg(int prograssPct) {
        this(Simulation, Success);
        setProgressPct(prograssPct);
        
    }

    private void init() {
        userId = super.getMsg().getOrBlank(USER_ID);
        permitId = super.getMsg().getOrBlank(PERMIT_ID);
    }
    
    public final void setProgressPct(int progressPct) {
        this.progressPct = progressPct;
        this.getMsg().put(PROGRESS_PCT, progressPct);
    }
}
