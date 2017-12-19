package org.afsirs.web.view;

import java.util.Map;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.ViewUtil.setCommonParam;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

/**
 *
 * @author Meng Zhang
 */
public class DataToolsViewUtil {
    
    public static String getSoilMapPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.DataTools.SOILMAP));
    }
    
}
