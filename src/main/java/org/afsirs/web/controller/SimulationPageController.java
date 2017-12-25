package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.SimulationViewUtil.getAfsirsResultPage;
import org.afsirs.web.view.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author Meng Zhang
 */
public class SimulationPageController {

    public static Route serveAfsirsPage = new Route() {

        @Override
        public Object handle(Request request, Response response) {
            LOG.info("Serve AFSIRS Page");
            Map<String, Object> attributes = new HashMap<>();
            if (!ViewUtil.isLogined(request)) {
                response.redirect(Path.Web.LOGIN);
                return ViewUtil.getLoginPage(request, attributes);
            }
            String userId = ViewUtil.getUserID(request);
            String permitId = request.queryParams("permit_id");
            WaterUsePermit permit = WaterUsePermitDAO.find(permitId, userId);
            if (permit == null) {
                attributes.put("operation_result", "Failed");
            } else {
                attributes.put("afsirs_input", permit.toAFSIRSInputData(userId));
            }
            return getAfsirsResultPage(request, attributes);
        }
    };

    public static Route handleAfsirsPost = new Route() {

        @Override
        public Object handle(Request request, Response response) {
            LOG.info("Handle AFSIRS Post");
            Map<String, Object> attributes = new HashMap<>();
            if (!ViewUtil.isLogined(request)) {
                response.redirect(Path.Web.LOGIN);
                return ViewUtil.getLoginPage(request, attributes);
            }
            String userId = ViewUtil.getUserID(request);
            String permitId = request.queryParams("permit_id");
            WaterUsePermit permit = WaterUsePermitDAO.find(permitId, userId);
            if (permit == null) {
                attributes.put("operation_result", "Failed");
            } else {
                attributes.put("afsirs_input", permit.toAFSIRSInputData(userId));
            }
            return getAfsirsResultPage(request, attributes);
        }
    };
}
