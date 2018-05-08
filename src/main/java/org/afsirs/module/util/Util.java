package org.afsirs.module.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.afsirs.module.SoilTypeSummaryReport;
import org.afsirs.module.SummaryReport;
import org.afsirs.module.UserInput;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author Meng Zhang
 */
public class Util {

    public static final String SOIL_MAP_BASE_URL = "/SoilMap/";

    public static String calculateNearestStation(String type, File jsonFile) throws IOException {
//        String station;
        String finalFile = null;
        //Hiranava Das: 27 Sep 2016: Set total Area
//        File latestFile = null;jsonFile
        //String siteName = utils.getSITE();
        //String unitName = utils.getUNIT();

//        String home = System.getProperty("user.home");
//        File dir = new File("Maps/");
//        //Hiranava Das:23 Jun 2017: 
//        String jsonName = dir + "//" + soilFileListCombo.getSelectedItem()+".json";
//        //System.out.println("File name " +jsonName);
//        latestFile = new File(jsonName);
        // Hiranava Das: 27 Sep 2016: To store total area
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        JsonNode afsirs = root.path("afsirs");
        if (afsirs == null || afsirs.isMissingNode()) {
//            JPanel inputFilePanel = new JPanel();
//            JOptionPane.showMessageDialog(inputFilePanel, "The JSON file \"" +jsonName+"\" is not valid!", "Warning", JOptionPane.WARNING_MESSAGE);
            afsirs = root.path("asfirs");
        }
        if (afsirs == null) {
            return null;
        }
        String longi = null;
        String lat = null;
        for (JsonNode node : afsirs) {
            longi = node.path("long").toString();
            lat = node.path("lat").toString();

        }
        if (longi == null || lat == null) {
//            JPanel inputFilePanel = new JPanel();
//            JOptionPane.showMessageDialog(inputFilePanel, "The JSON file " +jsonName+" is not valid!", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double longitude = Double.parseDouble(longi);
        double latitude = Double.parseDouble(lat);
        //System.out.println("longitude and lat :" +longitude +" " +latitude);
        BufferedReader br;
        if (type.equals("RAIN")) {
            br = new BufferedReader(new InputStreamReader(Util.class.getClass().getResourceAsStream("/data2/LongLatRAIN.txt")));
        } else {
            br = new BufferedReader(new InputStreamReader(Util.class.getClass().getResourceAsStream("/data2/LongLatCLIM.txt")));
        }
        String line;
        double shortestDistance = 9999999999.00;
        try {
            while ((line = br.readLine()) != null) {
                String city = line.split(",")[0];
                //System.out.println("City " +city);
                String Long = line.split(",")[1];
                double longCity = Double.parseDouble(Long);
                String Lat = line.split(",")[2];
                double latCity = Double.parseDouble(Lat);
                String fileName = line.split(",")[3];
                double distance = distance(latitude, latCity, longitude, longCity, 0.0, 0.0);
                //System.out.println(" distance :" + distance);
                if (shortestDistance > distance) {
                    shortestDistance = distance;
                    finalFile = fileName + "," + city;
                    //utils.setCLIMATESTATION(city);
                    //utils.setRAINFALLSTATION(city);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Station :" +finalFile);
        return finalFile;
    }

    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static File[] getListOfDataFiles() {

//        if (dataFiles != null) {
//            return dataFiles;
//        }
        String home = System.getProperty("user.home");
        File dir = new File(home + "/Downloads");

        //String siteName = utils.getSITE();
        //System.out.println (siteName);
        FileFilter fileFilter = new WildcardFileFilter("*.json*");
        File[] files = dir.listFiles(fileFilter);

        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }

        return files;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double[][] deepCopy(double[][] original) {
        double[][] ret = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            ret[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return ret;
    }

    public static URL getSoilMapUrl(String siteName, String unitName) throws MalformedURLException {
        return getSoilMapUrl(siteName, unitName, null, null, null);
    }

    public static URL getSoilMapUrl(String siteName, String unitName, String longitude, String latitude) throws MalformedURLException {
        return getSoilMapUrl(siteName, unitName, longitude, latitude, null);
    }

    public static URL getSoilMapUrl(String siteName, String unitName, String jsonStr) throws MalformedURLException {
        return getSoilMapUrl(siteName, unitName, null, null, jsonStr);
    }

    public static URL getSoilMapUrl(UserInput input) throws MalformedURLException {
        return getSoilMapUrl(input.getSITE(), input.getUNIT(), null, null, input.getPolygonInfo());
    }

    public static URL getSoilMapUrl(String siteName, String unitName, String longitude, String latitude, String jsonStr) throws MalformedURLException {
        StringBuilder sb = new StringBuilder();
        if (siteName != null && !siteName.isEmpty()) {
            sb.append("&site=").append(siteName);
        }
        if (unitName != null && !unitName.isEmpty()) {
            sb.append("&unit=").append(unitName);
        }
        if (longitude != null && !longitude.isEmpty()) {
            sb.append("&long=").append(longitude);
        }
        if (latitude != null && !latitude.isEmpty()) {
            sb.append("&lat=").append(latitude);
        }
        if (jsonStr != null && !jsonStr.isEmpty() && jsonStr.contains("polygon")) {
            String[] jsonParts = jsonStr.split("polygon");
            String content = jsonParts[1];
            content = content.substring(3);
            int len = content.length();
            content = content.substring(0, len - 2);
            try {
                sb.append("&json=").append(URLEncoder.encode(content, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String params = sb.toString();
        if (!params.isEmpty()) {
            sb = new StringBuilder();
            sb.append(SOIL_MAP_BASE_URL).append("?").append(params.substring(1)).append("#");
            return new URL(sb.toString());
        } else {
            return new URL(SOIL_MAP_BASE_URL);
        }

    }

    public static Comparator getSummaryReportComparetor() {
        return new Comparator<SummaryReport>() {

            @Override
            public int compare(SummaryReport o1, SummaryReport o2) {
                double compare = o2.getTotalAvgIrr() - o1.getTotalAvgIrr();
                if (compare > 0) {
                    return 1;
                } else if (compare < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    public static Comparator getSummaryReportComparetor2() {
        return new Comparator<SummaryReport>() {

            @Override
            public int compare(SummaryReport o1, SummaryReport o2) {
                double compare = o2.getSoilArea()- o1.getSoilArea();
                if (compare > 0) {
                    return 1;
                } else if (compare < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    public static boolean isSorted(ArrayList<? extends SummaryReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return true;
        }
        for (int i = 1; i < reports.size(); i++) {
            if (reports.get(i - 1).getTotalAvgIrr() < reports.get(i).getTotalAvgIrr()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSorted2(ArrayList<? extends SummaryReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return true;
        }
        for (int i = 1; i < reports.size(); i++) {
            if (reports.get(i - 1).getSoilArea() < reports.get(i).getSoilArea()) {
                return false;
            }
        }
        return true;
    }
}
