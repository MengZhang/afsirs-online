package org.afsirs.web.view;

import java.util.HashSet;
import java.util.Map;
import org.afsirs.module.Soil;
import org.afsirs.web.dao.SoilDataDAO;
import org.afsirs.web.dao.bean.SoilData;
import org.afsirs.web.util.DataUtil;
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
        HashSet<String> mukeys = new HashSet();
        if (soilId == null || (soil = SoilDataDAO.find(soilId)) == null) {
            soil = new SoilData();
            soil.setUser_id(request.queryParams("user"));
            soil.setPolygon_info(request.queryParams("json"));
            soil.setSoil_unit_name(request.queryParams("unit"));
            soil.setTotalArea(request.queryParams("area"));
        } else {
            for (Soil soilData : soil.getSoils()) {
                mukeys.add(soilData.getSOILSERIESKEY());
            }
        }

        attributes.put("zoom", request.queryParams("zoom"));
        attributes.put("soilData", soil);
        attributes.put("checked_mukeys", mukeys.toString().substring(1, mukeys.toString().length() - 1));
        attributes.put("climateCityList", DataUtil.getClimateCityList());
        attributes.put("rainfallCityList", DataUtil.getRainfallCityList());

        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.DataTools.SOILMAP));
    }
    
    public static String getWthSheetPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
//        String soilId = request.queryParams("soil_id");
//        SoilData soil;
//        HashSet<String> mukeys = new HashSet();
//        if (soilId == null || (soil = SoilDataDAO.find(soilId)) == null) {
//            soil = new SoilData();
//            soil.setUser_id(request.queryParams("user"));
//            soil.setPolygon_info(request.queryParams("json"));
//            soil.setSoil_unit_name(request.queryParams("unit"));
//            soil.setTotalArea(request.queryParams("area"));
//        } else {
//            for (Soil soilData : soil.getSoils()) {
//                mukeys.add(soilData.getSOILSERIESKEY());
//            }
//        }

//        attributes.put("zoom", request.queryParams("zoom"));
//        attributes.put("soilData", soil);
//        attributes.put("checked_mukeys", mukeys.toString().substring(1, mukeys.toString().length() - 1));
//        attributes.put("climateCityList", DataUtil.getClimateCityList());
//        attributes.put("rainfallCityList", DataUtil.getRainfallCityList());

        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.DataTools.WTHSHEET));
    }
    
}
