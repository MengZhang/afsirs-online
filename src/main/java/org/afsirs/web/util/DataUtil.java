package org.afsirs.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.afsirs.module.Irrigation;
import org.afsirs.module.Soil;
import org.afsirs.module.Weather;
import org.afsirs.module.util.Util;
import static org.afsirs.module.util.Util.round;
import static org.afsirs.web.Main.LOG;

/**
 *
 * @author Meng Zhang
 */
public class DataUtil {

    private final static ArrayList<String> CROP_LIST_ANNUAL = readCropList("ANNUAL");
    private final static ArrayList<String> CROP_LIST_PERENNIAL = readCropList("PERENNIAL");
    private final static ArrayList<Irrigation> IR_SYS_LIST = new ArrayList();
    private final static ArrayList<String> IR_NAME_LIST = readIrrigationList();
    private final static LinkedHashSet<String> SOILTYPE_DB_NAME_LIST = readSoilData();
    private final static LinkedHashMap<String, WeatherData> CLIMATE_DATA_LIST = readWeatherData("CLIMLIST.txt");
    private final static LinkedHashMap<String, WeatherData> RAINFALL_DATA_LIST = readWeatherData("RAINLIST.txt");

    @Data
    public static class WeatherData {

        private double[][] data = new double[64][365];
        private String location;
        private int startYear;
        private int endYear;
    }

    public static ArrayList<String> getCropList(String type) {
        if (type != null) {
            switch (type) {
                case "ANNUAL":
                    return CROP_LIST_ANNUAL;
                case "PERENNIAL":
                    return CROP_LIST_PERENNIAL;
                default:
                    return new ArrayList();
            }
        } else {
            return new ArrayList();
        }
    }

    public static ArrayList<String> getIRSysNameList() {
        return IR_NAME_LIST;
    }

    public static ArrayList<Irrigation> getIRSysList() {
        return IR_SYS_LIST;
    }

    public static LinkedHashSet<String> getSoilTypeDBNameList() {
        return SOILTYPE_DB_NAME_LIST;
    }

    public static ArrayList<String> getClimateCityList() {
        return new ArrayList(CLIMATE_DATA_LIST.keySet());
    }

    public static WeatherData getClimateData(String etLoc) {
        return CLIMATE_DATA_LIST.get(etLoc);
    }

    public static ArrayList<String> getRainfallCityList() {
        return new ArrayList(RAINFALL_DATA_LIST.keySet());
    }

    public static WeatherData getRainfallData(String rainLoc) {
        return RAINFALL_DATA_LIST.get(rainLoc);
    }

    private static ArrayList<String> readCropList(String type) {
        ArrayList<String> ret = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(Path.Folder.getDataFile("crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(type)) {
                    break;
                }
            }
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.length() < 14) {
                    break;
                }
                String crop;
                if (type.equals("ANNUAL")) {
                    crop = line.substring(0, 13).trim();
                } else {
                    crop = line.substring(0, 14).trim();
                }
                if (crop.length() < 1) {
                    break;
                }
                ret.add(crop);
                if (type.equals("PERENNIAL")) {
                    br.readLine();
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    private static ArrayList<String> readIrrigationList() {
        ArrayList<String> ret = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(Path.Folder.getDataFile("ir.dat")));
            String line = br.readLine();
            String[] parts = line.split(" ");
            int i = 0;
            while (i < parts.length) {
                if (parts[i].length() > 0) {
                    break;
                }
                i++;
            }
            int n = Integer.parseInt(parts[i].trim());

            br.readLine();
            i = 0;
            while (i < n) {
                line = br.readLine();
                parts = line.split("  ");
                Irrigation irr = new Irrigation();
                irr.setCode(Integer.parseInt(parts[1].trim()));
                irr.setEff(Double.parseDouble(parts[2]));
                irr.setArea(Double.parseDouble(parts[3]));
                irr.setEx(Double.parseDouble(parts[4]));
                irr.setDwt(Double.parseDouble(parts[5]));
                irr.setSys(parts[6]);
                IR_SYS_LIST.add(irr);

                ret.add(irr.getSys());
                i++;
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    private static LinkedHashSet<String> readSoilData() {
        LinkedHashSet<String> ret = new LinkedHashSet();
        try (BufferedReader br = new BufferedReader(new FileReader(Path.Folder.getDataFile("soil.dat")))) {
            String line;
            br.readLine(); //Ignore first line
            line = br.readLine();
            int start = 0;
            while (line.charAt(start) == ' ') {
                start++;
            }
            int end = start;
            while (line.charAt(end) != ' ') {
                end++;
            }
            br.readLine();//Ignore Line

            int N = Integer.parseInt(line.substring(start, end));

            for (int i = 0; i < N; i++) {
                line = br.readLine();
                String item = line.substring(4, 24).trim() + "    ";

                String[] parts = line.substring(24).split(" ");
                int k = 0;
                for (String x : parts) {
                    if (x.length() < 1) {
                        continue;
                    }
                    k++;
                    item += x + "    ";
                }
                if (ret.contains(item)) {
                    LOG.warn("[{}] is repeated! Please check soil.dat file!", item);
                } else {
                    ret.add(item);
                }
                br.readLine();//Ignore next line
            }
            br.close();

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    private static LinkedHashMap<String, WeatherData> readWeatherData(String fileName) {
        LinkedHashMap<String, WeatherData> ret = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Path.Folder.getDataFile(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String city = line.split(" ")[0];
                String file = line.split(" ")[1];
                ret.put(city, readWeatherFile(Path.Folder.getDataFile(file)));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    private static WeatherData readWeatherFile(File file) {
        WeatherData weather = new WeatherData();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            weather.setLocation(line.split(" ")[0]);
            double[][] data = weather.getData();
            line = br.readLine();
            String year = line.trim().split("\\s+")[0];
            int totalYears = Integer.parseInt(year);
            int cyear = 0;
            for (int j = 0; j < totalYears; j++) {
                int iYear = Integer.parseInt(br.readLine());
                int k = 0;
                int l = 0;
                if (j == 0) {
                    weather.setStartYear(iYear);
                }

                while (l < 365) {

                    String[] parts = br.readLine().split(" ");

                    for (String x : parts) {

                        if (x.length() > 0) {
                            data[cyear][k] = Double.parseDouble(x);
                            k++;
                        }
                        if (x.length() > 0) {
                            l++;
                        }
                    }
                }
                cyear++;
                weather.setEndYear(iYear);
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
        return weather;
    }

    public static ArrayList<Soil> readSoils(Set<String> dbSoilNames) {
        ArrayList<Soil> ret = new ArrayList();
        //TODO
        return ret;
    }

    public static ArrayList<Soil> toSoils(String jsonStr) {
        JSONObject data = JsonUtil.parseFrom(jsonStr);
        return toSoils(data);
    }

    public static ArrayList<Soil> toSoils(File file) {
        JSONObject data = JsonUtil.parseFrom(file);
        return toSoils(data);
    }

    public static ArrayList<Soil> toSoils(JSONObject data) {
        ArrayList<Soil> ret = new ArrayList<>();
        List<Map> soils = (List) data.get("soils");
//        int row = 0;
//        int whcIndex = waterholdcapacityBox.getSelectedIndex();
//        ArrayList<Soil> soilList = new ArrayList<>();
        for (Map soil : soils) {

            String soilSeriesName = soil.get("mukeyName").toString();
            String soilSeriesKey = soil.get("mukey").toString();
            String soilName = soil.get("soilName").toString();
            String compKey = soil.get("cokey").toString();
            String soilTypeArea = soil.get("compArea").toString();
            List<Map> soilLayers = (List) soil.get("soilLayer");

            int nl = 0;
            double[] wc = new double[6];
            double[] wcl = new double[6];
            double[] wcu = new double[6];
            double[] du = new double[6];
            String[] txt = new String[3];

            for (Map soilLayer : soilLayers) {
                //System.out.println ("NL we are looking for: " + NL);
                wcu[nl] = new BigDecimal(soilLayer.get("sldul").toString()).doubleValue() / 100.00;
                du[nl] = new BigDecimal(soilLayer.get("sllb").toString()).doubleValue() * 0.39370;
                du[nl] = round(du[nl], 3);
                wcl[nl] = new BigDecimal(soilLayer.get("slll").toString()).doubleValue() / 100.00;

//                if (whcIndex == 0) {
//                    wc[nl] = wcl[nl];
//                } else if (whcIndex == 2) {
//                    wc[nl] = wcu[nl];
//                } else {
//                    wc[nl] = 0.5 * (wcl[nl] + wcu[nl]);
//                }
                wc[nl] = round(wc[nl], 3);
                nl++;
            }
            // soilSeriesKey
            Soil soilData = new Soil(soilName, soilSeriesKey, compKey, soilSeriesName, nl);
            soilData.setValues("Average", wcl, wcu, du, txt);

            if (soilTypeArea != null) {
                soilData.setSoilTypeArea(Double.valueOf(soilTypeArea));
            } else {
                soilData.setSoilTypeArea(0.0);
            }
            ret.add(soilData);
        }
        return ret;
    }

    public static Weather toWeather(String etLoc, String rainLoc) {
        return toWeather(CLIMATE_DATA_LIST.get(etLoc), RAINFALL_DATA_LIST.get(rainLoc));
    }

    public static Weather toWeather(WeatherData etData, WeatherData rainData) {
        Weather weather = new Weather();
        int startYear = Math.max(etData.getStartYear(), rainData.getStartYear());
        int endYear = Math.min(etData.getEndYear(), rainData.getEndYear());
        weather.setStartYear(startYear);
        weather.setEndYear(endYear);
        weather.setNYR(endYear - startYear + 1);

        weather.setETLoc(etData.getLocation());
        weather.setETP(etData.getData());

        weather.setRainLoc(rainData.getLocation());
        weather.setRAIN(rainData.getData());

        return weather;
    }

    // save uploaded file to new location
    public static void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        revisePath(uploadedFileLocation);
        File dir = new File(uploadedFileLocation);
        LOG.info("File was wrote to {} ...", dir.getAbsolutePath());
        try (OutputStream out = new FileOutputStream(dir)) {

            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            LOG.info("File was wrote to {} done!", dir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

    }

//    public HashMap<String, Object> toMap(Object data) throws JsonProcessingException, IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//        String reqJson = mapper.writeValueAsString(data);
//        HashMap<String, Object> ret = mapper.readValue(reqJson, HashMap.class);
//        return ret;
//    }

    public static String revisePath(String path) {
        if (!path.trim().isEmpty()) {
            File f = new File(path);
            if (!f.isDirectory()) {
                f = f.getParentFile();
                path = f.getPath();
            }
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
        }
        return path;
    }
    
    public static String calculateNearestStation(String type, String jsonStr) {
        JSONObject data = JsonUtil.parseFrom(jsonStr);
        List<Map> asfirs = (List) data.get("asfirs");
        if (asfirs == null) {
            return null;
        }
        String longi = null;
        String lat = null;
        for (Map node : asfirs) {
            longi = node.get("long").toString();
            lat = node.get("lat").toString();
            // TODO for multiple polygons
        }
        if (longi == null || lat == null) {
            return null;
        }
        return calculateNearestStation(type, lat, longi);
    }

    public static String calculateNearestStation(String type, String lat, String longi) {

        String finalFile = null;
        
        double longitude = Double.parseDouble(longi);
        double latitude = Double.parseDouble(lat);
        //System.out.println("longitude and lat :" +longitude +" " +latitude);
        try {
            BufferedReader br;
            if (type.equals("RAIN")) {
                br = new BufferedReader(new FileReader(Path.Folder.getDataFile("LongLatRAIN.txt")));
            } else {
                br = new BufferedReader(new FileReader(Path.Folder.getDataFile("LongLatCLIM.txt")));
            }
            String line;
            double shortestDistance = 9999999999.00;
            while ((line = br.readLine()) != null) {
                String city = line.split(",")[0];
                //System.out.println("City " +city);
                String Long = line.split(",")[1];
                double longCity = Double.parseDouble(Long);
                String Lat = line.split(",")[2];
                double latCity = Double.parseDouble(Lat);
//                String fileName = line.split(",")[3];
                double distance = distance(latitude, latCity, longitude, longCity, 0.0, 0.0);
                //System.out.println(" distance :" + distance);
                if (shortestDistance > distance) {
                    shortestDistance = distance;
                    finalFile = city;
                }
            }
            br.close();
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
}
