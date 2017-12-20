package org.afsirs.module.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 *
 * @author Meng Zhang
 */
public class Util {

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

        JsonNode asfirs = root.path("asfirs");
        if (asfirs == null) {
//            JPanel inputFilePanel = new JPanel();
//            JOptionPane.showMessageDialog(inputFilePanel, "The JSON file \"" +jsonName+"\" is not valid!", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String longi = null;
        String lat = null;
        for (JsonNode node : asfirs) {
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
}
