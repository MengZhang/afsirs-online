package org.afsirs.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.afsirs.module.Irrigation;
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

    private static ArrayList<String> readCropList(String type) {
        ArrayList<String> ret = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("./Data/crop.dat")));
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
            BufferedReader br = new BufferedReader(new FileReader(new File("./Data/ir.dat")));
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
        try (BufferedReader br = new BufferedReader(new FileReader(new File("./Data/soil.dat")))) {
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

    public HashMap<String, Object> toMap(Object data) throws JsonProcessingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        String reqJson = mapper.writeValueAsString(data);
        HashMap<String, Object> ret = mapper.readValue(reqJson, HashMap.class);
        return ret;
    }

    public static String revisePath(String path) {
        if (!path.trim().equals("")) {
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
}
