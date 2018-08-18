package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.util.Path;
import org.afsirs.web.view.SoilDataViewUtil;
import org.afsirs.web.view.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author Meng Zhang
 */
public class SoilDataPageController {

    public static Route serveSoilDataListPage = (Request request, Response response) -> {
        LOG.info("Serve Soil Data List Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return SoilDataViewUtil.getListPage(request, attributes);
    };

    public static Route handleSoilDataSavePost = (Request request, Response response) -> {
        LOG.info("Handle Soil Data Save Post");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return SoilDataViewUtil.getSoilDataSaveResponse(request, attributes);
    };
}
