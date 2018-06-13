package org.afsirs.web.view;

import java.util.Map;
import org.afsirs.web.dao.SoilDataDAO;
import org.afsirs.web.dao.bean.SoilData;
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
        String soilId = request.queryParams("soil_id");
        SoilData soil;
        if (soilId == null || (soil = SoilDataDAO.find(soilId)) == null) {
            soil = new SoilData();
            soil.setUser_id((String) attributes.get("currentUser"));
            soil.setPolygon_info(request.queryParams("json"));
            soil.setSoil_unit_name(request.queryParams("unit"));
            soil.setTotalArea(request.queryParams("area"));
        }
        attributes.put("zoom", request.queryParams("zoom"));
        attributes.put("soilData", soil);
        
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.DataTools.SOILMAP));
    }
    
}
