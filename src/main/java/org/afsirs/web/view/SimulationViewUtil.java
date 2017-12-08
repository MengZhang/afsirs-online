package org.afsirs.web.view;

import java.io.File;
import java.util.Map;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.module.SimResult;
import org.afsirs.module.UserInput;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.ViewUtil.setCommonParam;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

/**
 *
 * @author Meng Zhang
 */
public class SimulationViewUtil {
    
    protected static void setSimulationCommonParam(Request request, Map<String, Object> attributes) {
        // Set common attribute in this function and always call this one in the particular view function
//        if (!attributes.containsKey("name") && attributes.containsKey("project_name")) {
//            attributes.put("name", attributes.get("project_name"));
//        }
    }
    
    public static String getAfsirsPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS));
    }
    
    public static String getAfsirsResultPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
        File out = new File("worksing/userid");
        SimResult ret = AFSIRSModule.run((UserInput) attributes.get("user_input"), out);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS_RESULT));
    }
}
