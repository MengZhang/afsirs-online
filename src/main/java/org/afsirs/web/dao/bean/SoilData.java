package org.afsirs.web.dao.bean;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import lombok.Data;
import org.afsirs.module.Soil;
import org.afsirs.module.UserInput;
import org.afsirs.module.util.JSONArray;
import org.afsirs.module.util.JSONObject;
import org.afsirs.module.util.JsonUtil;
import org.afsirs.web.util.DataUtil;

/**
 *
 * @author Meng Zhang
 */
@Data
public class SoilData {
    
    private String soil_id;
    private String user_id;
    private String soil_source;
    private String soil_unit_name;
    private LinkedHashSet<String> dbSoilNames;
    private ArrayList<Soil> soils;
    private String soil_json;
    private String polygon_info;
    private String polygon_loc_info;
    private String latitude;
    private String longitude;
    private String plantedArea;
    private String totalArea;
    private String zoom = "9";
    
    private static final HashFunction hf = Hashing.sha256();

    public void setSoil_unit_name(String soil_unit_name) {
        if (soil_unit_name != null) {
            this.soil_unit_name = soil_unit_name.trim();
        }
    }
    
    public String getSoil_unit_name() {
        if (this.soil_unit_name != null) {
            return this.soil_unit_name.trim();
        } else {
            return null;
        }
    }
    
    public void setDbSoilNames(LinkedHashSet<String> names) {
        if (names != null) {
            dbSoilNames = new LinkedHashSet<>();
            dbSoilNames.addAll(names);
        } else {
            dbSoilNames = null;
        }
    }
    
    public void setDbSoilNames(String[] names) {
        if (names != null) {
            dbSoilNames = new LinkedHashSet<>();
            dbSoilNames.addAll(Arrays.asList(names));
        } else {
            dbSoilNames = null;
        }
    }

    public static SoilData readFromJson(File jsonFile) {
        JSONObject data = JsonUtil.parseFrom(jsonFile);
        return readFromJson(data);
    }

    public static SoilData readFromJson(String json) {
        JSONObject data = JsonUtil.parseFrom(json);
        return readFromJson(data);
    }
    
    public static SoilData readFromJson(JSONObject data) {
        SoilData ret = new SoilData();
        ret.setSoil_id(data.getObjId());
        ret.setUser_id(data.getOrDefault("user_id", ""));

        String soilSource = data.getOrDefault("soil_source", null);
        ret.setSoil_source(soilSource);
        ret.setSoil_unit_name(data.getOrBlank("soil_unit_name"));
//        ret.setWater_hold_capacity(data.getOrBlank("water_hold_capacity"));
        ret.setTotalArea(data.getOrBlank("total_area"));
        ret.setPlantedArea(data.getOrBlank("planted_area"));
        ret.setSoil_json(data.getObjArr("soils").toJSONString());
        JSONArray polygonInfo = data.getObjArr("polygon");
        if (!polygonInfo.isEmpty()) {
            ret.setPolygon_info(polygonInfo.getObj(0).toJSONString());
        }
        JSONArray polygonLocInfo = data.getObjArr("afsirs");
        if (polygonLocInfo.isEmpty()) {
            polygonLocInfo = data.getObjArr("asfirs");
        }
        if (!polygonLocInfo.isEmpty()) {
            JSONObject locInfo = polygonLocInfo.getObj(0);
            ret.setPolygon_loc_info(locInfo.toJSONString());
            if (locInfo.containsKey("lat") && locInfo.containsKey("long")) {
                ret.setLatitude(locInfo.getOrBlank("lat"));
                ret.setLongitude(locInfo.getOrBlank("long"));
            }
        }
//        ArrayList<Soil> soils = DataUtil.toSoils(data, ret.getWater_hold_capacity());
        ArrayList<Soil> soils = DataUtil.toSoils(data);
        ret.setSoils(soils);
        if ("DB".equals(soilSource)) {
            String[] soilNames = new String[soils.size()];
            for (int i = 0; i < soils.size(); i++) {
                soilNames[i] = soils.get(i).getSNAME();
            }
            ret.setDbSoilNames(soilNames);
        }
        if (ret.getTotalArea() != null && !ret.getTotalArea().isEmpty()) {
            try {
                long zoomVal = 17 - Math.round(Math.log(Double.parseDouble(ret.getTotalArea()))/Math.log(8));
                if (zoomVal > 17) {
                    zoomVal = 17;
                } else if (zoomVal < 1) {
                    zoomVal = 1;
                }
                ret.setZoom(zoomVal + "");
            } catch (Exception ex) {
            }
        }

        return ret;
    }
    
    public UserInput toAFSIRSInputSoilData() {
        UserInput input = new UserInput();

        input.setSoilSource(soil_source);
        input.setUNIT(soil_unit_name);
        input.setSoils(soils);
        input.setMapArea(new BigDecimal(totalArea).doubleValue());
        input.setPlantedAcres(new BigDecimal(plantedArea).doubleValue());
        input.setPolygonInfo(polygon_info);
        input.setPolygonLocInfo(polygon_loc_info);

        return input;
    }
    
    public HashCode getHash() {
        Hasher hasher = hf.newHasher();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                if (field.getType().getName().equals(String.class.getName()) &&
                        !fieldName.equalsIgnoreCase("soil_id") &&
                        !fieldName.equalsIgnoreCase("zoom")) {
                    String val = (String) this.getClass().getMethod("get" + fieldName.substring(0 ,1).toUpperCase() + fieldName.substring(1)).invoke(this);
                    hasher.putBytes((fieldName + val).getBytes("UTF-8"));
                }
            }
            if (soils != null) {
                for (Soil soil : soils) {
                    hasher.putBytes((soil.getSNAME() + soil.getCOMPKEY()).getBytes());
                }
            }
        } catch (UnsupportedEncodingException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.err);
        }
        return hasher.hash();
    }
    
    public boolean compare(SoilData soilData) {
//    private String soil_unit_name;
//    private LinkedHashSet<String> dbSoilNames;
//    private ArrayList<Soil> soils;
//    private String polygon_info;
//    private String polygon_loc_info;
//    private String latitude;
//    private String longitude;
//    private String plantedArea;
//    private String totalArea;
        if ((user_id == null && soilData.getUser_id() != null) || !user_id.equals(soilData.getUser_id())) {
            return false;
        } else if ((soil_source == null && soilData.getSoil_source() != null) || !soil_source.equals(soilData.getSoil_source())) {
            return false;
        } else if ((soil_unit_name == null && soilData.getSoil_unit_name() != null) || !soil_unit_name.equals(soilData.getSoil_unit_name())) {
            return false;
        } else if ((polygon_info == null && soilData.getPolygon_info() != null) || !polygon_info.equals(soilData.getPolygon_info())) {
            return false;
        } else if ((polygon_loc_info == null && soilData.getPolygon_loc_info() != null) || !polygon_loc_info.equals(soilData.getPolygon_loc_info())) {
            return false;
        } else if ((totalArea == null && soilData.getTotalArea() != null) || !totalArea.equals(soilData.getTotalArea())) {
            return false;
        } else if ((plantedArea == null && soilData.getPlantedArea() != null) || !plantedArea.equals(soilData.getPlantedArea())) {
            return false;
        } else if ((soils == null && soilData.getSoils() != null) || soils.size() != soilData.getSoils().size()) {
            return false;
        } else {
            for (int i = 0; i < soils.size(); i++) {
                Soil soil = soils.get(i);
                Soil soil2 = soilData.getSoils().get(i);
//                soil.
            }
        }
        return true;
    }
}
