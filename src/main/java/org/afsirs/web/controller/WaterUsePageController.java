package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.WaterUsePermitViewUtil.getCreatePage;
import static org.afsirs.web.view.WaterUsePermitViewUtil.getDetailPage;
import static org.afsirs.web.view.WaterUsePermitViewUtil.getListPage;
import org.afsirs.web.view.ViewUtil;;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author Meng Zhang
 */
public class WaterUsePageController {

    public static Route serveCreatePage = (Request request, Response response) -> {
        LOG.info("Serve Create Project Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return getCreatePage(request, attributes);
    };

    public static Route serveListPage = (Request request, Response response) -> {
        LOG.info("Serve List Project Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return getListPage(request, attributes);
    };

    public static Route handleCreatePost = (Request request, Response response) -> {
        LOG.info("Handle Create Project Post");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        WaterUsePermit permit = WaterUsePermit.readFromRequest(request);
        attributes.put("permit", permit);
        if (WaterUsePermitDAO.add(permit, ViewUtil.getUserID(request))) {
            return getListPage(request, attributes);
        } else {
            attributes.put("operation_result", "Failed");
            return getCreatePage(request, attributes);
        }
    };

    public static Route serveDetailPage = (Request request, Response response) -> {
        LOG.info("Serve Project Detail Page");
        Map<String, Object> attributes = new HashMap<>();
        
        String id = request.queryParams("id");
//        HashMap ret = ProjectDAO.find(id);
//        if (ret.isEmpty()) {
//            attributes.put("operation_result", "Failed");
//        } else {
//            ret.remove("_id");
//            attributes.put("id", id);
//            attributes.putAll(ret);
//        }
        return getDetailPage(request, attributes);
    };
    
    
}
