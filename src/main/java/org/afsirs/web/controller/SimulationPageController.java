package org.afsirs.web.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.dao.SimulationDAO;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.SimulationViewUtil.getAfsirsDownloadResponse;
import static org.afsirs.web.view.SimulationViewUtil.getAfsirsResultLoadingPage;
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
            LOG.info("Serve AFSIRS result Page");
            Map<String, Object> attributes = new HashMap<>();
            if (!ViewUtil.isLogined(request)) {
                response.redirect(Path.Web.LOGIN);
                return ViewUtil.getLoginPage(request, attributes);
            }
            return getAfsirsResultPage(request, attributes);
        }
    };

//    public static Route handleAfsirsPost = new Route() {
//
//        @Override
//        public Object handle(Request request, Response response) {
//            LOG.info("Handle AFSIRS Post");
//            Map<String, Object> attributes = new HashMap<>();
//            if (!ViewUtil.isLogined(request)) {
//                response.redirect(Path.Web.LOGIN);
//                return ViewUtil.getLoginPage(request, attributes);
//            }
//            String userId = ViewUtil.getUserID(request);
//            String permitId = request.queryParams("permit_id");
//            WaterUsePermit permit = WaterUsePermitDAO.find(permitId, userId);
//            if (permit == null) {
//                attributes.put("operation_result", "Failed");
//            } else {
//                attributes.put("afsirs_input", setDeviation(permit.toAFSIRSInputData(userId), permit));
//                attributes.put("user_id", userId);
//            }
//            return getAfsirsResultPage(request, attributes);
//        }
//    };

    public static Route serveAfsirsLoadPage = new Route() {

        @Override
        public Object handle(Request request, Response response) {
            LOG.info("Serve AFSIRS Loading Page");
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
                SimulationDAO.addSimulation(permit);
            }
            return getAfsirsResultLoadingPage(request, attributes);
        }
    };

    public static Route serveDownloadRequest = new Route() {

        @Override
        public Object handle(Request request, Response response) {
            LOG.info("Handle AFSIRS Result Download Post");
            Map<String, Object> attributes = new HashMap<>();
            if (!ViewUtil.isLogined(request)) {
                response.redirect(Path.Web.LOGIN);
                return ViewUtil.getLoginPage(request, attributes);
            }
            String userId = ViewUtil.getUserID(request);
            String permitId = request.queryParams("permit_id");
            String fileType = request.queryParams("file_type");
            File downloandFile = WaterUsePermitDAO.getOutputFile(userId, permitId, fileType);
            if (downloandFile.exists()) {
                return getAfsirsDownloadResponse(response, downloandFile);
            } else {
                response.status(404);
                return "FILE NOT FOUND";
            }
        }
    };
}
