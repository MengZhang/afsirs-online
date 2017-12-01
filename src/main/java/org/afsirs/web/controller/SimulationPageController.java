package org.afsirs.web.controller;

import java.util.HashMap;
import java.util.Map;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.SimulationViewUtil.getAfsirsPage;
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

    public static Route serveAfsirsPage = (Request request, Response response) -> {
        LOG.info("Serve AFSIRS Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return getAfsirsPage(request, attributes);
    };

    public static Route handleAfsirsPost = (Request request, Response response) -> {
        LOG.info("Handle AFSIRS Post");
        Map<String, Object> attributes = new HashMap<>();
        return getAfsirsResultPage(request, attributes);
    };
    
    
}
