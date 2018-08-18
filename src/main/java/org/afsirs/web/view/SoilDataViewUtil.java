package org.afsirs.web.view;

import java.util.Map;
import org.afsirs.module.AFSIRSModule;
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
public class SoilDataViewUtil {

    public static String getListPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        attributes.put("soils", SoilDataDAO.list((String) attributes.get("currentUser")));
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.SoilData.LIST));
    }
    
    public static boolean getSoilDataSaveResponse(Request request, Map<String, Object> attributes) {
        SoilData soil = SoilData.readFromJson(request.queryParams("data"));
        soil.setSoil_unit_name(request.queryParams("soil_unit_name"));
        soil.setSoil_source(request.queryParams("soil_source"));
        soil.setPlantedArea(request.queryParams("plantedArea"));
        soil.setTotalArea(request.queryParams("total_area"));
        String updateFlg = request.queryParams("update_flg");
        if (updateFlg.equalsIgnoreCase("true")) {
            return SoilDataDAO.update(soil, ViewUtil.getUserID(request));
        } else {
            return SoilDataDAO.add(soil, ViewUtil.getUserID(request)) != null;
        }
    }
    
    public static String getSoilDataFindResponse(Request request, Map<String, Object> attributes) {
        SoilData ret = SoilDataDAO.find(request.queryParams("soil_id"));
        if (ret != null) {
            return AFSIRSModule.saveSoilDataJson(ret.toAFSIRSInputSoilData());
        } else {
            return "";
        }
    }
}
