package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.util.DataUtil;
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

    public static Route serveWthSheetPage = (Request request, Response response) -> {
        LOG.info("Serve Weathe Sheet Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        String etLoc = request.queryParams("et_loc");
        String rainLoc = request.queryParams("rain_loc");
        attributes.put("wthData", DataUtil.getCombinedWeatherData(etLoc, rainLoc));
        attributes.put("et_loc", etLoc);
        attributes.put("rain_loc", rainLoc);
        return DataToolsViewUtil.getWthSheetPage(request, attributes);
    };
}
