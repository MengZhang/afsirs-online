package org.afsirs.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.LinkedHashMap;
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

    private final static LinkedHashMap<String, CropData> CROP_LIST_ANNUAL = readCropList("ANNUAL");
    private final static LinkedHashMap<String, CropData> CROP_LIST_PERENNIAL = readCropList("PERENNIAL");
    private final static ArrayList<Irrigation> IR_SYS_LIST = new ArrayList();
    private final static ArrayList<String> IR_NAME_LIST = readIrrigationList();
    private final static LinkedHashMap<String, Soil> SOILTYPE_DB_DATA_LIST = readSoilData();
    private final static LinkedHashMap<String, WeatherData> CLIMATE_DATA_LIST = readWeatherData("CLIMLIST.txt");
    private final static LinkedHashMap<String, WeatherData> RAINFALL_DATA_LIST = readWeatherData("RAINLIST.txt");
    private final static String LAST_BUILD_TS = readLastBuildTS();

    @Data
    public static class WeatherData {

        private double[][] data = new double[64][365];
        private String location;
        private int startYear;
        private int endYear;

        public WeatherData cloneData() {
            WeatherData ret = new WeatherData();
            ret.setLocation(location);
            ret.setStartYear(startYear);
            ret.setEndYear(endYear);
            ret.setData(Util.deepCopy(data));
            return ret;
        }
    }

    public interface CropData {

        public CropData cloneData();
    }

    @Data
    public static class CropDataAnnual implements CropData {

        private String cropName;
        private double DZN, DZX;
        private double AKC3, AKC4;
        private double[] F = new double[4];
        private double[] ALD = new double[4];
        private int maxDays;
        private int minDays;
        private int defDays;

        public double[] getFR() {
            return F;
        }

        public CropDataAnnual(String cropName) {
            this.cropName = cropName;
        }

        @Override
        public CropDataAnnual cloneData() {
            CropDataAnnual ret = new CropDataAnnual(cropName);
            ret.setDZN(DZN);
            ret.setDZX(DZX);
            ret.setAKC3(AKC3);
            ret.setAKC4(AKC4);
            ret.setF(Arrays.copyOf(F, F.length));
            ret.setALD(Arrays.copyOf(ALD, ALD.length));
            return ret;
        }
    }

    @Data
    public static class CropDataPerennial implements CropData {

        private String cropName;
        private double DRZIRR, DRZTOT;
        private double[] AKC = new double[12];
        private double[] ALDP = new double[12];
        private double HGT;

        public CropDataPerennial(String cropName) {
            this.cropName = cropName;
        }

        @Override
        public CropDataPerennial cloneData() {
            CropDataPerennial ret = new CropDataPerennial(cropName);
            ret.setDRZIRR(DRZIRR);
            ret.setDRZTOT(DRZTOT);
            ret.setHGT(HGT);
            ret.setAKC(Arrays.copyOf(AKC, AKC.length));
            ret.setALDP(Arrays.copyOf(ALDP, ALDP.length));
            return ret;
        }
    }

    public static ArrayList<String> getCropList(String type) {
        if (type != null) {
            switch (type) {
                case "ANNUAL":
                    return new ArrayList(CROP_LIST_ANNUAL.keySet());
                case "PERENNIAL":
                    return new ArrayList(CROP_LIST_PERENNIAL.keySet());
                default:
                    return new ArrayList();
            }
        } else {
            return new ArrayList();
        }
    }

    public static int getCropIndexCode(String type, String cropName) {
        if ("ANNUAL".equalsIgnoreCase(type)) {
            return new ArrayList(CROP_LIST_ANNUAL.keySet()).indexOf(cropName);
        } else if ("PERENNIAL".equalsIgnoreCase(type)) {
            return new ArrayList(CROP_LIST_PERENNIAL.keySet()).indexOf(cropName);
        }
        return -1;
    }

    public static ArrayList<String> getIRSysNameList() {
        return IR_NAME_LIST;
    }

    public static ArrayList<Irrigation> getIRSysList() {
        return IR_SYS_LIST;
    }

    public static Set<String> getSoilTypeDBNameList() {
        return SOILTYPE_DB_DATA_LIST.keySet();
    }

    public static ArrayList<String> getClimateCityList() {
        return new ArrayList(CLIMATE_DATA_LIST.keySet());
    }

    public static WeatherData getClimateData(String etLoc) {
        return CLIMATE_DATA_LIST.get(etLoc).cloneData();
    }

    public static ArrayList<String> getRainfallCityList() {
        return new ArrayList(RAINFALL_DATA_LIST.keySet());
    }

    public static WeatherData getRainfallData(String rainLoc) {
        return RAINFALL_DATA_LIST.get(rainLoc).cloneData();
    }

    public static LinkedHashMap<String, CropData> getCropDataAnnual() {
        return CROP_LIST_ANNUAL;
    }

    public static LinkedHashMap<String, CropData> getCropDataPerennial() {
        return CROP_LIST_PERENNIAL;
    }

    public static String getLastBuildTS() {
        return LAST_BUILD_TS;
    }

    private static String readLastBuildTS() {
//        try (InputStream versionFile = DataUtil.class.getClassLoader().getResourceAsStream(Path.Folder.PROPERTY_FILE)) {
//            Properties versionProperties = new Properties();
//            versionProperties.load(versionFile);
//            versionFile.close();
//            return versionProperties.getProperty("product.buildts");
//        } catch (IOException ex) {
//            LOG.error("Unable to load version information, will use system time instead.");
//            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static LinkedHashMap<String, CropData> readCropList(String type) {
        LinkedHashMap<String, CropData> ret = new LinkedHashMap();
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
                CropData cropData;
                if (type.equals("ANNUAL")) {
                    crop = line.substring(0, 13).trim();
                    cropData = readCropDataAnnual(line, crop);
                } else {
                    crop = line.substring(0, 14).trim();
                    cropData = readCropDataPerennial(line, br.readLine(), crop);
                }
                if (crop.length() < 1) {
                    break;
                }
                ret.put(crop, cropData);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return ret;
    }

    private static CropDataAnnual readCropDataAnnual(String line, String cropName) {
        CropDataAnnual ret = new CropDataAnnual(cropName);
        if (line.length() < 12) {
            return ret;
        }
        String data = line.substring(12).trim();
        String[] arr = data.split("\\s+");
        int i = 0;
        for (String str : arr) {
            if (str.length() < 1) {
                continue;
            }
            if (i == 0) {
                ret.setDZN(new BigDecimal(str).doubleValue());
            } else if (i == 1) {
                ret.setDZX(new BigDecimal(str).doubleValue());
            } else if (i == 2) {
                ret.setAKC3(new BigDecimal(str).doubleValue());
            } else if (i == 3) {
                ret.setAKC4(new BigDecimal(str).doubleValue());
            } else if (i < 8) {
                ret.getF()[i - 4] = new BigDecimal(str).doubleValue();
            } else if (i < 12) {
                ret.getALD()[i - 8] = new BigDecimal(str).doubleValue();
            } else if (i == 12) {
                ret.setMinDays(new BigDecimal(str).intValue());
            } else if (i == 13) {
                ret.setMaxDays(new BigDecimal(str).intValue());
            } else if (i == 14) {
                ret.setDefDays(new BigDecimal(str).intValue());
            }
            i++;
        }
        return ret;
    }

    private static CropDataPerennial readCropDataPerennial(String line1, String line2, String cropName) {
        CropDataPerennial ret = new CropDataPerennial(cropName);
        String data = line1.substring(14);
        String[] arr = data.split(" ");
        int i = 0;
        for (String str : arr) {
            ret.getAKC()[i] = new BigDecimal(str).doubleValue();
            i++;
        }
        data = line2.substring(8);
        if (data.charAt(0) == ' ') {
            data = data.substring(1);
        }
        arr = data.split(" ");
        i = 0;
        for (String str : arr) {
            if (str.length() < 1) {
                continue;
            }
            if (i == 0) {
                ret.setDRZIRR(new BigDecimal(str).doubleValue());
            } else if (i == 1) {
                ret.setDRZTOT(new BigDecimal(str).doubleValue());
            } else {
                ret.getALDP()[i - 2] = new BigDecimal(str).doubleValue();
            }
            i++;
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

    private static LinkedHashMap<String, Soil> readSoilData() {
        LinkedHashMap<String, Soil> ret = new LinkedHashMap();
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
            String item = "";

            for (int i = 0; i < N; i++) {
                line = br.readLine();
                if (line == null || line.isEmpty()) {
                    System.out.println("detect empty line after " + item);
                    continue;
                }
                item = line.substring(4, 24).trim() + "    ";

                String[] parts = line.substring(24).split(" ");
//                int k = 0;
                for (String x : parts) {
                    if (x.length() < 1) {
                        continue;
                    }
//                    k++;
                    item += x + "    ";
                }

                String line2 = br.readLine();//Read data line
                int NL = Integer.parseInt(line2.substring(0, 1));
                String idxStr = String.format("DB%04d", i);
                Soil soil = new Soil(i, item, idxStr, idxStr, item, NL);
                soil.setSoilSymbolNum(idxStr);
                int k = 0;
                int j = 1;
                while (line2.charAt(j) == ' ') {
                    j++;
                }
                line2 = line2.substring(j);
                parts = line2.split(" ");
                ArrayList<Double> wc = new ArrayList();
                ArrayList<Double> wcl = new ArrayList();
                ArrayList<Double> wcu = new ArrayList();
                ArrayList<Double> du = new ArrayList();
                String[] txt = new String[3];

                while (k < NL) {
                    if (parts.length == 0 || parts[0].isEmpty()) {
                        System.out.println(item + " Style broken");
                        k++;
                        continue;
                    } else {
                        boolean errFlg = false;
                        for (String part : parts) {
                            if (part.isEmpty()) {
                                System.out.println(item + " Style broken");
                                k++;
                                errFlg = true;
                                break;
                            }
                        }
                        if (errFlg) {
                            continue;
                        }
                    }
                    String[] fields = parts[k].split("[.]");
//                    System.out.print(item + " Fields : ");
//                    for (String field : fields) {
//                        System.out.print(field + " ");
//                    }
//                    System.out.println();
//                    System.out.println("Fields : " + fields[0] + " " + fields[1] + " " + fields[2]);
                    du.add(Double.parseDouble(fields[0]));
                    wcl.add(Double.parseDouble(fields[1]) / 100.0);
                    wcu.add(Double.parseDouble(fields[2]) / 100.0);
                    wc.add(new BigDecimal(wcl.get(k)).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                    k++;
                }
                soil.setValues(wc, wcl, wcu, du, txt);

                if (ret.containsKey(item)) {
                    LOG.warn("[{}] is repeated! Please check soil.dat file!", item);
                } else {
                    ret.put(item, soil);
                }
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
        for (String soilName : dbSoilNames) {
            ret.add(SOILTYPE_DB_DATA_LIST.get(soilName).cloneData());
        }
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
        return toSoils(data, "Average");
    }

    public static ArrayList<Soil> toSoils(JSONObject data, String WHC) {
        ArrayList<Soil> ret = new ArrayList();
        ArrayList<org.json.simple.JSONObject> soilArr = (ArrayList) data.getOrDefault("soils", new ArrayList());
        String soilVersion = data.getOrBlank("version");
        for (org.json.simple.JSONObject soilJS : soilArr) {
            JSONObject soilJ = new JSONObject(soilJS);
            String soilSeriesName = soilJ.getOrBlank("mukeyName");
            String soilSeriesKey = soilJ.getOrBlank("mukey");
            String soilSymbolNum = soilJ.getOrBlank("musym");

            String soilName = soilJ.getOrBlank("soilName");
            String compKey = soilJ.getOrBlank("cokey");

            String soilTypeArea = soilJ.getOrBlank("compArea");
            String soilTypePct = soilJ.getOrBlank("comppct_r");

            ArrayList<org.json.simple.JSONObject> soilLayersNodes = (ArrayList) soilJ.getOrDefault("soilLayer", new ArrayList());

            int nl = 0;
            double[] wc = new double[soilLayersNodes.size()];
            double[] wcl = new double[soilLayersNodes.size()];
            double[] wcu = new double[soilLayersNodes.size()];
            double[] du = new double[soilLayersNodes.size()];
            String[] txt = new String[3];

            for (org.json.simple.JSONObject nodeJS : soilLayersNodes) {
                //System.out.println ("NL we are looking for: " + NL);
                JSONObject node = new JSONObject(nodeJS);
                wcu[nl] = node.getAsDouble("sldul");
                du[nl] = node.getAsDouble("sllb");
                du[nl] = round(du[nl], 3);
                wcl[nl] = node.getAsDouble("slll");

                if (WHC.equalsIgnoreCase("Minimum")) {
                    wc[nl] = wcl[nl];
                } else if (WHC.equalsIgnoreCase("Maximum")) {
                    wc[nl] = wcu[nl];
                } else {
                    wc[nl] = 0.5 * (wcl[nl] + wcu[nl]);
                }

                wc[nl] = Util.round(wc[nl], 3);
                nl++;
            }

            Soil soil = new Soil(soilName, soilSeriesKey, compKey, soilSeriesName, soilSymbolNum, nl);
            soil.setValues(wc, wcl, wcu, du, txt);

            if (!soilTypeArea.isEmpty()) {
                soil.setSoilTypeArea(Double.valueOf(soilTypeArea));
            } else {
                soil.setSoilTypeArea(0.0);
            }
            if (!soilTypePct.isEmpty()) {
                soil.setSoilTypePct(Integer.valueOf(soilTypePct));
            }
            soil.setVersion(soilVersion);
            ret.add(soil);
        }
        return ret;
    }

    public static Weather toWeather(String etLoc, String rainLoc) {
        return toWeather(getClimateData(etLoc), getRainfallData(rainLoc));
    }

    public static Weather toWeather(WeatherData etData, WeatherData rainData) {

        int startYear = Math.max(etData.getStartYear(), rainData.getStartYear());
        int endYear = Math.min(etData.getEndYear(), rainData.getEndYear());
        Weather weather = new Weather(startYear, endYear);

        weather.setETLoc(etData.getLocation());
        weather.setETP(etData.getData(), etData.getStartYear(), etData.getEndYear());

        weather.setRainLoc(rainData.getLocation());
        weather.setRAIN(rainData.getData(), rainData.getStartYear(), rainData.getEndYear());

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

    public static String getTotalArea(String jsonStr) {
        JSONObject data = JsonUtil.parseFrom(jsonStr);
        List<Map> afsirs = (List) data.get("afsirs");
        if (afsirs == null) {
            afsirs = (List) data.get("asfirs");
        }
        if (afsirs == null) {
            return null;
        }
        String ret = "0";
        for (Map node : afsirs) {
            ret = (String) node.get("TotalArea");
            if (ret != null) {
                break;
            }
        }
        return ret;
    }

    public static String calculateNearestStation(String type, String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return "";
        }
        JSONObject data = JsonUtil.parseFrom(jsonStr);
        return calculateNearestStation(type, data);
    }

    public static String calculateNearestStation(String type, JSONObject data) {
        String longi = data.getOrBlank("long");
        String lat = data.getOrBlank("lat");
        if (longi.isEmpty() || lat.isEmpty()) {
            return null;
        }
        return calculateNearestStation(type, lat, longi);
    }

//    public static String calculateNearestStation(String type, JSONObject data) {
//        List<Map> afsirs = (List) data.get("afsirs");
//        if (afsirs == null) {
//            afsirs = (List) data.get("asfirs");
//        }
//        if (afsirs == null) {
//            return null;
//        }
//        String longi = null;
//        String lat = null;
//        for (Map node : afsirs) {
//            longi = node.get("long").toString();
//            lat = node.get("lat").toString();
//            // TODO for multiple polygons
//        }
//        if (longi == null || lat == null) {
//            return null;
//        }
//        return calculateNearestStation(type, lat, longi);
//    }

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
