package org.afsirs.web.view;

import java.util.Map;
import org.afsirs.web.util.Path;
import static org.afsirs.web.Main.LOG;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

public class ViewUtil {

    public static void setCommonParam(Request request, Map<String, Object> attributes) {
//        model.put("msg", new MessageBundle(getSessionLocale(request)));
        attributes.put("currentUser", getSessionVar(request, "currentUser"));
        if (!attributes.containsKey("operation_result")) {
            attributes.put("operation_result", "");
        }
        attributes.put("WebPath", Path.Web.class); // Access application URLs from templates
//        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }

    public static String getIndexPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        fillDefValue(attributes,
                new String[]{"username", "message"},
                new Object[]{"world", "Hello"});
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.INDEX));

    }

    public static String getRegisterPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.REGISTER));
    }

    public static String getLoginPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.LOGIN));
    }

    public static String getUploadPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.UPLOAD));
    }
//
//    public static Route notAcceptable = (Request request, Response response) -> {
//        response.status(HttpStatus.NOT_ACCEPTABLE_406);
//        return new MessageBundle(getSessionLocale(request)).get("ERROR_406_NOT_ACCEPTABLE");
//    };
//
//    public static Route notFound = (Request request, Response response) -> {
//        response.status(HttpStatus.NOT_FOUND_404);
//        return render(request, new HashMap<>(), Path.Template.NOT_FOUND);
//    };
//
//    private static VelocityTemplateEngine strictVelocityEngine() {
//        VelocityEngine configuredEngine = new VelocityEngine();
//        configuredEngine.setProperty("runtime.references.strict", true);
//        configuredEngine.setProperty("resource.loader", "class");
//        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        return new VelocityTemplateEngine(configuredEngine);
//    }

    public static void fillDefValue(Map<String, Object> attributes, String[] requiredAttrs, Object... defVals) {
        if (defVals.length < requiredAttrs.length) {
            Object defVal = "";
            if (defVals.length > 0) {
                defVal = defVals[0];
            }
            for (String key : requiredAttrs) {
                if (attributes.get(key) == null) {
                    LOG.debug("Detect missing value for {}", key);
                    attributes.put(key, defVal);
                }
            }
        } else {
            for (int i = 0; i < requiredAttrs.length; i++) {
                if (attributes.get(requiredAttrs[i]) == null) {
                    attributes.put(requiredAttrs[i], defVals[i]);
                }
            }
        }
    }

    public static boolean isLogined(Request request) {
        String currentUser = getSessionVar(request, "currentUser", "");
        return !currentUser.isEmpty();
    }

    public static String getSessionVar(Request request, String varName, String defVal) {
        String ret = request.session().attribute(varName);
        if (ret == null) {
            ret = defVal;
        }
        return ret;
    }

    public static String getSessionVar(Request request, String varName) {
        return getSessionVar(request, varName, null);
    }
    
    public static String getUserID(Request request) {
        return getSessionVar(request, "currentUser", "");
    }
}
