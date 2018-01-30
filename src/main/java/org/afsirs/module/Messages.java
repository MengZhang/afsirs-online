package org.afsirs.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author rohit
 * @author Meng Zhang
 */
public class Messages {
    
    public final static String EOL = "\r\n";
    
//    public static final int MAX_VERSION = 6;
//    public static final int MIN_VERSION = 2;
//    public static final int SUB_MIN_VERSION = 7;
    public static final String VERSION = initVersionStr();
    public static final String BUILD_TS = initBuildTimeStamp();
    
    public static String initVersionStr() {
        Properties versionProperties = new Properties();
        try (InputStream versionFile = Messages.class.getClassLoader().getResourceAsStream("product.properties")) {
            versionProperties.load(versionFile);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
            StringBuilder qv = new StringBuilder();
            String buildType = versionProperties.getProperty("product.buildtype");
            String buildVerion = versionProperties.getProperty("product.version");
            if (buildType.equalsIgnoreCase("dev")) {
                buildVerion = buildVerion.replaceAll("-SNAPSHOT", "");
            }
            qv.append(buildVerion);
            qv.append(" ").append(versionProperties.getProperty("product.buildversion"));
            return qv.toString();
    }
    
    public static String initBuildTimeStamp() {
        Properties versionProperties = new Properties();
        try (InputStream versionFile = Messages.class.getClassLoader().getResourceAsStream("product.properties")) {
            versionProperties.load(versionFile);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        String str = versionProperties.getProperty("product.buildts");
        if (str.length() == 13) {
            str = str.substring(0 ,4) + "/" + str.substring(4,6) + "/" + str.substring(6,8) + str.substring(8,11) + ":" + str.substring(11);
        }
        return str;
    }
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getBuildTS() {
        return BUILD_TS;
    }
    
    
    public static final String AFSIRS_ERROR = "Fewer than three years have non-zero IRR values, \n" +
            "the least squares curve fitting procedure cannot be used";
    public static final String TABLE_HEADER [] = {"", "Jan",  "Feb",  "Mar",  "Apr",  "May",  "Jun",  "Jul", "Aug",  "Sep",  "Oct",  "Nov",  "Dec",  "Total"};
    public static final String TABLE_HEADER_EXCEL [] = {"", "Jan",  "Feb",  "Mar",  "Apr",  "May",  "Jun",  "Jul", "Aug",  "Sep",  "Oct",  "Nov",  "Dec",  "Total", "Peak Monthly", "Annual Daily"};
    /*public static final String DOC_HEADER [] = {
                        "AGRICULTURAL",
                        "FIELD",
                        "SCALE",
                        "IRRIGATION",
                        "REQUIREMENTS",
                        "SIMULATION",
                        "MODEL",
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION,
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS",
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.",
                        "PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS",
                        "ARE CALCULATED USING HISTORICAL WEATHER DATA BASES",
                        "FOR NINE FLORIDA LOCATIONS.",
                        "INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN",
                        "IN THE AFSIRS MODEL USER'S GUIDE.",
                        "DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS",
                        "AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL.",
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION,
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS",
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.",
                        " ",
                        " "
    };
    
    
    public static final String DOC_HEADER_EXCEL = 
                        "AGRICULTURAL" + EOL + 
                        "FIELD"+ EOL +
                        "SCALE"+ EOL +
                        "IRRIGATION"+ EOL +
                        "REQUIREMENTS"+ EOL +
                        "SIMULATION" + EOL +
                        "MODEL"+ EOL +
                        "AFSIRS MODEL: INTERACTIVE VERSION " + Messages.MAX_VERSION+"."+Messages.MIN_VERSION+ EOL +
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS"+ EOL +
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS."+ EOL +
                        "PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS"+ EOL +
                        "ARE CALCULATED USING HISTORICAL WEATHER DATA BASES"+ EOL +
                        "FOR NINE FLORIDA LOCATIONS."+ EOL +
                        "INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN"+ EOL +
                        "IN THE AFSIRS MODEL USER'S GUIDE."+ EOL +
                        "DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS"+ EOL +
                        "AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL."+ EOL +
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION+ EOL +
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS"+ EOL +
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS."+ EOL +
                        " "+ EOL +
                        " ";*/
    
    public static final String DOC_HEADER [] = {
                        "AFSIRS MODEL: INTERACTIVE VERSION "+ getVersion(),
                        " ",
                        " "
    };
    
    
    public static final String DOC_HEADER_EXCEL = 
                        "AFSIRS MODEL: INTERACTIVE VERSION " + getVersion() + EOL +EOL +
                        " ";
    
    
    public static final String USER_DETAILS [] = {
                        "Owner Name : "  ,
                        "Permit ID : ",
                        "Map Name : ",
                        "Crop : ",
                        "Irrigation Method : ",
                        "Crop Simulation Start Date : ",
                        "Crop Simulation End Date : ",
                        "Period of Record : ",
                        //"End Year : ",
                        "Planted Area (ACRES): ",
                        "Site Area (ACRES):",
                        "ET Station : ",
                        "Rainfall Station : ",
                        "Irrigation Option: ",
                        "Irrigation efficiency : ",
                        "Soil Surface Irrigated : ",
                        //"Root Zone Depth(in) :",
                        "Selected Soil Area :",
                        "Depth of Water Table :",
                        "KC :",
                        "Water Holding Capacity :"
    };
    
        
    public static final String USER_DETAILS_EXCEL [] = {
                        "Owner"  ,
                        "Site",
                        "Unit",
                        "Crop",
                        "Irrigation Method",
                        "Simulation Start Date",
                        "Simulation End Date",
                        "Area (ACRES)",
                        "Climate Station"
    };
    
    
    public static final String FOOTNOTE [] = {
                        "Avg Irrigation Requirement refers to 50% Probablity."  ,
    };
    public static final String INFO_TYPES [] = { "Mean Irrigation(Inches)",
                                                 "Mean Irrigation(Weighted Inches)",
                                                 "2-In-10 Irrigation(Inches)",
                                                 "2-In-10 Irrigation(Weighted Inches)",
                                                 "1-In-10 Irrigation(Inches)",
                                                 "1-In-10 Irrigation(Weighted Inches)",
                                                 "Net Mean Irrigation(Million Gallons)",
                                                 "Gross Mean Irrigation(Million Gallons)",
                                                 "Net 2-In-10 Irrigation(Million Gallons)",
                                                 "Gross 2-In-10 Irrigation(Million Gallons)",
                                                 "Net 1-In-10 Irrigation(Million Gallons)",
                                                 "Gross 1-In-10 Irrigation(Million Gallons)",
                                                 "Net Irrigation Weighted Average(Million Gallons)",
                                                 "Gross Irrigation Weighted Average(Million Gallons)"
    };
}