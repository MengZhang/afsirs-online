package org.afsirs.web.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import static org.afsirs.web.Main.LOG;
import org.afsirs.web.util.DataUtil;
import org.afsirs.web.util.Path;
import org.afsirs.web.view.ViewUtil;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public class PageController {

    public static Route serveIndexPage = (Request request, Response response) -> {
        LOG.info("Serve Index Page");
        Map<String, Object> attributes = new HashMap<>();
        String username = request.session().attribute("currentUser");
        attributes.put("username", username);
        return ViewUtil.getIndexPage(request, attributes);
    };

    public static TemplateViewRoute serveNotFoundPage = (Request request, Response response) -> {
//        LOG.info("Serve Not Found Page");
        Map<String, Object> attributes = new HashMap<>();
        return new ModelAndView(attributes, Path.Template.NOT_FOUND);
    };

    public static Route serveRegisterPage = (Request request, Response response) -> {
        LOG.info("Serve Register Page");
        Map<String, Object> attributes = new HashMap<>();
        return ViewUtil.getRegisterPage(request, attributes);
    };

    public static Route handleRegisterPost = (Request request, Response response) -> {
        LOG.info("Handle Register Post");
        Map<String, Object> attributes = new HashMap<>();
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        if (!UserController.register(username, password)) {
            attributes.put("operation_result", "Failed");
            return ViewUtil.getRegisterPage(request, attributes);
        }
        request.session().attribute("currentUser", username);
//        if (getQueryLoginRedirect(request) != null) {
//            response.redirect(getQueryLoginRedirect(request));
//        }
        response.redirect(Path.Web.INDEX);
        
        return ViewUtil.getIndexPage(request, attributes);
    };

    public static Route serveLoginPage = (Request request, Response response) -> {
        LOG.info("Serve Login Page");
        Map<String, Object> attributes = new HashMap<>();
        return ViewUtil.getLoginPage(request, attributes);
    };

    public static Route handleLoginPost = (Request request, Response response) -> {
        LOG.info("Handle Login Post");
        Map<String, Object> attributes = new HashMap<>();
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        if (!UserController.authenticate(username, password)) {
            attributes.put("operation_result", "Failed");
            return ViewUtil.getLoginPage(request, attributes);
        }
        request.session().attribute("currentUser", username);
//        if (getQueryLoginRedirect(request) != null) {
//            response.redirect(getQueryLoginRedirect(request));
//        }
        response.redirect(Path.Web.INDEX);
        
        return ViewUtil.getIndexPage(request, attributes);
    };

    public static Route handleLogoutRequest = (Request request, Response response) -> {
        LOG.info("Handle Logout Request");
        Map<String, Object> attributes = new HashMap<>();
        String username = request.session().attribute("currentUser");
        request.session().removeAttribute("currentUser");
        request.session().attribute("loggedOut", true);
        attributes.put("message", "Goodbye");
        attributes.put("username", username);
        response.redirect(Path.Web.INDEX);
        return ViewUtil.getIndexPage(request, attributes);
    };

    public static Route serveUploadPage = (Request request, Response response) -> {
        LOG.info("Serve Upload Page");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        return ViewUtil.getUploadPage(request, attributes);
    };

    public static Route handleUploadPost = (Request request, Response response) -> {
        LOG.info("Handle Upload Post");
        Map<String, Object> attributes = new HashMap<>();
        if (!ViewUtil.isLogined(request)) {
            response.redirect(Path.Web.LOGIN);
            return ViewUtil.getLoginPage(request, attributes);
        }
        
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        Part file = request.raw().getPart("file");
        String fileName = file.getSubmittedFileName();
        LOG.info(fileName);
        try (InputStream is = file.getInputStream()) {
            DataUtil.writeToFile(is, "upload/" + fileName);
        }
        
        return ViewUtil.getUploadPage(request, attributes);
    };
}
