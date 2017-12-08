package org.afsirs.web.util;

import lombok.*;

public class Path {

    // The @Getter methods are needed in order to access
    public static class Web {
        @Getter public static final String INDEX = "/index";
        @Getter public static final String REGISTER = "/register";
        @Getter public static final String LOGIN = "/login";
        @Getter public static final String LOGOUT = "/logout";
        @Getter public static final String UPLOAD = "/upload";
        
        public static class Simulation {
            private static final String PACKAGE = "/" + Simulation.class.getSimpleName().toLowerCase();
            @Getter public static final String AFSIRS = PACKAGE + "/afsirs";
        }
        
        public static class Data {
            private static final String PACKAGE = "/" + Data.class.getSimpleName().toLowerCase();
            @Getter public static final String SOILMAP = PACKAGE + "/soilmap";
        }
    }
    
    public static class Template {
        public final static String INDEX = "index.ftl";
        public final static String REGISTER = "register.ftl";
        public final static String LOGIN = "login.ftl";
        public final static String UPLOAD = "upload.ftl";
        public static final String NOT_FOUND = "notFound.ftl";
        
        public static class Simulation {
            private static final String PACKAGE = Simulation.class.getSimpleName().toLowerCase();
            public static final String AFSIRS = PACKAGE + "/afsirs.ftl";
            public static final String AFSIRS_RESULT = PACKAGE + "/afsirs_result.ftl";
        }
        
        public static class Data {
            private static final String PACKAGE = Data.class.getSimpleName().toLowerCase();
            public static final String SOILMAP = PACKAGE + "/soilmap.ftl";
        }
    }
    
    public static class Folder {
        public final static String WORKING = "working";
    }

}
