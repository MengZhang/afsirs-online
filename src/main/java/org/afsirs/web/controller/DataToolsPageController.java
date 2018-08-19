package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.util.Path;
import org.afsirs.web.view.DataToolsViewUtil;
import org.afsirs.web.view.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author Meng Zhang
 */
public class DataToolsPageController {

    public static Route serveSoilMapPage = (Request request, Response response) -> {
        LOG.info("Serve Soil Map Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return DataToolsViewUtil.getSoilMapPage(request, attributes);
    };

    public static Route serveSoilMapPage2 = (Request request, Response response) -> {
        LOG.info("Serve Soil Map Page2");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return DataToolsViewUtil.getSoilMapPage(request, attributes);
    };
}
