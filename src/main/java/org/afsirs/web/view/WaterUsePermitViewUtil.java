package org.afsirs.web.view;

import java.util.ArrayList;
import java.util.Map;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.WaterUsePermit;
import org.afsirs.web.util.DataUtil;
import static org.afsirs.web.util.DataUtil.getCropList;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.ViewUtil.setCommonParam;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

/**
 *
 * @author Meng Zhang
 */
public class WaterUsePermitViewUtil {
    
    protected static void setWaterUsePermitCommonParam(Request request, Map<String, Object> attributes) {
//        if (!attributes.containsKey("xxx") && attributes.containsKey("xxx")) {
//            attributes.put("xxx", attributes.get("xxx"));
//        }
    }
    
    public static String getCreatePage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setWaterUsePermitCommonParam(request, attributes);
        if (!attributes.containsKey("permit")) {
            attributes.put("permit", new WaterUsePermit());
        }
        attributes.put("cropListAnnual", getCropList("ANNUAL"));
        attributes.put("cropListPerennial", getCropList("PERENNIAL"));
        attributes.put("irSysList", DataUtil.getIRSysList());
        
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.WaterUse.CREATE));
    }
    
    public static String getListPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setWaterUsePermitCommonParam(request, attributes);
        attributes.put("permits", WaterUsePermitDAO.list((String) attributes.get("currentUser")));
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.WaterUse.LIST));
    }
    
    public static String getDetailPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setWaterUsePermitCommonParam(request, attributes);
//        attributes.put("xxx", xxxDAO.search((String) attributes.get("xxx")));
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.WaterUse.DETAIL));
    }
    
    
    public static String getSearchPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setWaterUsePermitCommonParam(request, attributes);
//        attributes.put("xxx", xxxDAO.listNames());
        if (!attributes.containsKey("results")) {
            attributes.put("results", new ArrayList());
        }
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.WaterUse.CREATE));
    }
}
