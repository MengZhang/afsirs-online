package org.afsirs.web.dao.bean;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.afsirs.module.Soil;
import org.afsirs.module.UserInput;
import org.afsirs.web.util.DataUtil;
import org.afsirs.web.util.JSONObject;
import org.afsirs.web.util.JsonUtil;
import spark.Request;

/**
 *
 * @author Meng Zhang
 */
@Data
public class WaterUsePermit {

    // Site Information
    private String permit_id;
    private String owner_name;
    private String crop_type;
    private String crop_name;
    private String beg_date_month;
    private String beg_date_day;
    private String end_date_month;
    private String end_date_day;
    private String description;

    // Irrigation
    private String irr_type;
    private String irr_option;
    private String irr_depth_type;
    private String irr_depth;
//    private String ir_dat;
    private String irr_efficiency;
    private String soil_surface_irr;
    private String et_extracted;
    private String water_table_depth;

    // Soil
    private String soil_source;
    private LinkedHashSet<String> dbSoilNames;
    private ArrayList<Soil> soils;
    private String mapSoilJsonFile;
    private String water_hold_capacity;
    private String latitude;
    private String longitude;
    private String totalArea;

    // Climate
    private String et_loc;
    private String rain_loc;

    public void setDbSoilNames(String[] names) {
        dbSoilNames = new LinkedHashSet<>();
        dbSoilNames.addAll(Arrays.asList(names));
    }

    public static WaterUsePermit readFromRequest(Request request) {
        WaterUsePermit ret = new WaterUsePermit();
        ret.setPermit_id(request.queryParams("permit_id"));
        ret.setOwner_name(request.queryParams("owner_name"));
        String cropType = request.queryParams("crop_type");
        ret.setCrop_type(cropType);
        if (cropType.equals("annual")) {
            ret.setCrop_name(request.queryParams("crop_name_annual"));
        } else {
            ret.setCrop_name(request.queryParams("crop_name_perennial"));
        }
        ret.setBeg_date_month(request.queryParams("beg_date_month"));
        ret.setBeg_date_day(request.queryParams("beg_date_day"));
        ret.setEnd_date_month(request.queryParams("end_date_month"));
        ret.setEnd_date_day(request.queryParams("end_date_day"));
        ret.setEnd_date_day(request.queryParams("end_date_day"));

        ret.setIrr_type(request.queryParams("irr_type"));
        ret.setIrr_option(request.queryParams("irr_option"));
        ret.setIrr_depth_type(request.queryParams("irr_depth_type"));
        ret.setIrr_depth(request.queryParams("irr_depth"));
//        ret.setIr_dat(request.queryParams("ir_dat"));
        ret.setIrr_efficiency(request.queryParams("irr_efficiency"));
        ret.setSoil_surface_irr(request.queryParams("soil_surface_irr"));
        ret.setEt_extracted(request.queryParams("et_extracted"));
        ret.setWater_table_depth(request.queryParams("water_table_depth"));

        String soilSource = request.queryParams("soil_source");
        ret.setSoil_source(soilSource);
        ret.setWater_hold_capacity(request.queryParams("water_hold_capacity"));
        if (soilSource.equalsIgnoreCase("DB")) {
            ret.setDbSoilNames(request.queryParamsValues("soil_type_db"));
            ret.setSoils(DataUtil.readSoils(ret.getDbSoilNames()));
            ret.setTotalArea(request.queryParams("total_area"));
        } else if (soilSource.equalsIgnoreCase("MAP")) {
            String jsonStr = request.queryParams("soil_file_json");
            ret.setSoils(DataUtil.toSoils(jsonStr));
            JSONObject data = JsonUtil.parseFrom(jsonStr);
            List<Map> asfirs = (List) data.get("asfirs");
            if (asfirs == null) {
                return null;
            }
            String longi = null;
            String lat = null;
//            String totArea = null;
            for (Map node : asfirs) {
                longi = node.get("long").toString();
                lat = node.get("lat").toString();
//                totArea = node.get("TotalArea").toString();
                // TODO for multiple polygons
            }
            ret.setLatitude(lat);
            ret.setLongitude(longi);
            ret.setTotalArea(request.queryParams("total_area")); // TODO
//            ret.setTotalArea(totArea);
        }

        ret.setEt_loc(calculateNearestStation(request.queryParams("et_loc"), "CLIMATE", ret));
        ret.setRain_loc(calculateNearestStation(request.queryParams("rain_loc"), "RAIN", ret));

        return ret;
    }

    private static String calculateNearestStation(String loc, String type, WaterUsePermit permit) {
        if ("Nearest Station".equalsIgnoreCase(loc)) {
            loc = DataUtil.calculateNearestStation(type, permit.getLatitude(), permit.getLongitude());
        }
        return loc;
    }

    public static WaterUsePermit readFromJson(File jsonFile) {
        WaterUsePermit ret = new WaterUsePermit();
        JSONObject data = JsonUtil.parseFrom(jsonFile);
        ret.setPermit_id(data.getOrDefault("permit_id", ""));
        ret.setOwner_name(data.getOrDefault("owner_name", data.getOrBlank("output_name")));
        ret.setCrop_type(readCropType(data));
        ret.setCrop_name(data.getOrDefault("crop_name", ""));
        ret.setBeg_date_month(data.getOrBlank("beg_date_month"));
        ret.setBeg_date_day(data.getOrBlank("beg_date_day"));
        ret.setEnd_date_month(data.getOrBlank("end_date_month"));
        ret.setEnd_date_day(data.getOrBlank("end_date_day"));
        ret.setDescription(data.getOrBlank("description"));

        ret.setIrr_type(data.getOrBlank("irr_type"));
        ret.setIrr_option(data.getOrBlank("irr_option"));
        String irrDepthType = data.getOrBlank("irr_depth_type");
        if (irrDepthType.isEmpty()) {
            irrDepthType = data.getOrBlank("irr_depth");
        } else {
            ret.setIrr_depth(data.getOrBlank("irr_depth"));
        }
        ret.setIrr_depth_type(irrDepthType);
//        ret.setIr_dat(readIrDat(data));
        ret.setIrr_efficiency(data.getOrBlank("irr_efficiency"));
        ret.setSoil_surface_irr(data.getOrBlank("soil_surface_irr"));
        ret.setEt_extracted(data.getOrBlank("et_extracted"));
        ret.setWater_table_depth(data.getOrBlank("water_table_depth"));

        ret.setSoil_source(data.getOrBlank("soil_source"));
        ret.setWater_hold_capacity(data.getOrBlank("water_hold_capacity"));
        ret.setTotalArea(data.getOrBlank("planted_area"));
        // TODO handel soil data

        ret.setEt_loc(data.getOrBlank("et_loc"));
        ret.setRain_loc(data.getOrBlank("rain_loc"));

        return ret;
    }

    public UserInput toAFSIRSInputData(String userId) {
        UserInput input = new UserInput();
        input.setSITE(permit_id);
        input.setOWNER(owner_name, Path.Folder.getUserWaterUsePermitOutputDir(userId).getPath());
        input.setCropType(crop_type);
//        input.setCropName(crop_name);
        input.setCropData(DataUtil.getCropIndexCode(crop_type, crop_name), crop_name);
        if (crop_type.equals("annual")) {
            input.setIrrigationSeason(beg_date_month, beg_date_day, end_date_month, end_date_day);
        } else {
            input.setIrrigationSeason(1, 1, 12, 31);
        }

        input.setIrrOption(irr_option);
        input.setIDCODE(irr_depth_type, irr_depth);
        input.setIrrigationSystem(irr_type, soil_surface_irr, et_extracted, irr_efficiency,
                DataUtil.getIRSysNameList().get(Integer.parseInt(irr_type) - 1));
//        input.setIVERS(ir_dat);
        input.setDWT(Double.parseDouble(water_table_depth));

        input.setSoilSource(soil_source);
        input.setSoils(soils);
        input.setWATERHOLDINGCAPACITY(water_hold_capacity);
        input.setPlantedAcres(new BigDecimal(totalArea).doubleValue());

        input.setWeather(DataUtil.toWeather(et_loc, rain_loc));

        return input;
    }

    private static String readCropType(JSONObject data) {
        Object isPerennial = data.get("crop_type");
        if (isPerennial == null) {
            return "";
        } else if (isPerennial instanceof Boolean) {
            if ((Boolean) isPerennial) {
                return "perennial";
            } else {
                return "annual";
            }
        } else if (isPerennial instanceof String) {
            return (String) isPerennial;
        } else {
            return "";
        }
    }

//    private static String readIrDat(JSONObject data) {
//        Object isIrDat = data.get("ir_dat");
//        if (isIrDat == null) {
//            return "";
//        } else if (isIrDat instanceof Boolean) {
//            if ((Boolean) isIrDat) {
//                return "true";
//            } else {
//                return "";
//            }
//        } else if (isIrDat instanceof String) {
//            return (String) isIrDat;
//        } else {
//            return "";
//        }
//    }
}
