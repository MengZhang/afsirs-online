package org.afsirs.web.dao.bean;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import lombok.Data;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.module.Irrigation;
import org.afsirs.module.Soil;
import org.afsirs.module.UserInput;
import org.afsirs.module.util.JSONArray;
import org.afsirs.module.util.JSONObject;
import org.afsirs.module.util.JsonUtil;
import org.afsirs.web.util.DataUtil;
import org.afsirs.web.util.DataUtil.CropData;
import org.afsirs.web.util.DataUtil.CropDataAnnual;
import org.afsirs.web.util.DataUtil.CropDataPerennial;
import org.afsirs.web.util.Path;
import spark.Request;

/**
 *
 * @author Meng Zhang
 */
@Data
public class WaterUsePermit {

    // Site Information
    private String permit_id;
    private String user_id;
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
    private String soil_unit_name;
    private LinkedHashSet<String> dbSoilNames;
    private ArrayList<Soil> soils;
    private String soil_json;
    private String polygon_info;
    private String polygon_loc_info;
    private String water_hold_capacity;
    private String latitude;
    private String longitude;
    private String plantedArea;
    private String totalArea;

    // Climate
    private String et_loc;
    private String rain_loc;
    private String et_nearest_flg;
    private String rain_nearest_flg;

    // Coefficent
    private String coefficent_type;
    // Coefficent for Annual Crop
    private String dzn;
    private String dzx;
    private String akc3;
    private String akc4;
    private String f1;
    private String f2;
    private String f3;
    private String f4;
    private String ald1;
    private String ald2;
    private String ald3;
    private String ald4;
    // Coefficent for Perennial Crop
    private String drzirr;
    private String drztot;
    private ArrayList<String> akcArr;
    private ArrayList<String> aldpArr;
    private String hgt;

    public void setPermit_id(String permit_id) {
        if (permit_id != null) {
            this.permit_id = permit_id.trim();
        }
    }
    
    public String getPermit_id() {
        if (this.permit_id != null) {
            return this.permit_id.trim();
        } else {
            return null;
        }
    }
    
    public void setDbSoilNames(String[] names) {
        dbSoilNames = new LinkedHashSet<>();
        dbSoilNames.addAll(Arrays.asList(names));
    }

    public void setHgt(String hgt) {
        if (hgt == null || hgt.isEmpty()) {
            return;
        }
        this.hgt = hgt;
        BigDecimal hgtVal = new BigDecimal(hgt).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal hgtValInch = hgtVal.multiply(new BigDecimal(12)).setScale(2, BigDecimal.ROUND_HALF_UP);
        water_table_depth = hgtValInch.toString();
        drzirr = hgtValInch.add(new BigDecimal(-6)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        drztot = hgtValInch.add((new BigDecimal(12)).divide(hgtVal, 2, BigDecimal.ROUND_HALF_UP)).toString();
        if (aldpArr == null) {
            return;
        }
        for (int i = 0; i < aldpArr.size(); i++) {
            if (new BigDecimal(aldpArr.get(i)).compareTo(new BigDecimal("0.5")) <= 0) {
                aldpArr.set(i, "0.5");
            }
        }

    }

    public void setCropData(CropData input) {
        if (input instanceof CropDataAnnual) {
            CropDataAnnual data = (CropDataAnnual) input;
            this.setDzn(data.getDZN() + "");
            this.setDzx(data.getDZX() + "");
            this.setAkc3(data.getAKC3() + "");
            this.setAkc4(data.getAKC4() + "");
            this.setF1(data.getF()[0] + "");
            this.setF2(data.getF()[1] + "");
            this.setF3(data.getF()[2] + "");
            this.setF4(data.getF()[3] + "");
            this.setAld1(data.getALD()[0] + "");
            this.setAld2(data.getALD()[1] + "");
            this.setAld3(data.getALD()[2] + "");
            this.setAld4(data.getALD()[3] + "");
        } else if (input instanceof CropDataPerennial) {
            CropDataPerennial data = (CropDataPerennial) input;
            this.setDrzirr(data.getDRZIRR() + "");
            this.setDrztot(data.getDRZTOT() + "");
            this.setAkcArr(toStringArray(data.getAKC()));
            this.setAldpArr(toStringArray(data.getALDP()));

        }
    }

    private static ArrayList<String> toStringArray(double[] values) {
        ArrayList arr = new ArrayList();
        for (double val : values) {
            arr.add(val + "");
        }
        return arr;
    }

    public static WaterUsePermit readFromRequest(Request request) {
        WaterUsePermit ret = new WaterUsePermit();
        ret.setPermit_id(request.queryParams("permit_id"));
        ret.setOwner_name(request.queryParams("owner_name"));
        String cropType = request.queryParams("crop_type");
        ret.setCrop_type(cropType);
        if (cropType != null && !cropType.isEmpty()) {
            if (cropType.equals("annual")) {
                ret.setCrop_name(request.queryParams("crop_name_annual"));
            } else {
                ret.setCrop_name(request.queryParams("crop_name_perennial"));
            }
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
        ret.setSoil_unit_name(request.queryParams("soil_unit_name"));
        ret.setWater_hold_capacity(request.queryParams("water_hold_capacity"));
        ret.setTotalArea(request.queryParams("total_area"));
        if (ret.getTotalArea() == null) {
            ret.setTotalArea(request.queryParams("planted_area"));
        }
        ret.setPlantedArea(request.queryParams("planted_area"));
        ret.setSoil_json(request.queryParams("soil_file_json"));
        if (soilSource.equalsIgnoreCase("DB")) {
            ret.setDbSoilNames(request.queryParamsValues("soil_type_db"));
            ArrayList<Soil> soils = DataUtil.readSoils(ret.getDbSoilNames());
            String[] pcts = request.queryParamsValues("db_soil_type_pct");
            BigDecimal plantedArea = new BigDecimal(ret.getPlantedArea());
            for (int i = 0; i < soils.size(); i++) {
                Soil soil = soils.get(i);
                BigDecimal area = plantedArea.multiply(new BigDecimal(pcts[i])).divide(new BigDecimal(100));
                soil.setSoilTypeArea(area.setScale(3).doubleValue());
                soil.setSoilTypePct(100);
            }
            ret.setSoils(soils);
        } else if (soilSource.equalsIgnoreCase("MAP")) {
            String jsonStr = ret.getSoil_json();
            ret.setSoils(DataUtil.toSoils(jsonStr));
            ret.setPolygon_info(request.queryParams("polygon_info"));
            String locInfo = request.queryParams("polygon_loc_info");
            ret.setPolygon_loc_info(locInfo);
            JSONObject data = JsonUtil.parseFrom(locInfo);
            ret.setLatitude(data.getOrBlank("lat"));
            ret.setLongitude(data.getOrBlank("long"));
//            JSONObject data = JsonUtil.parseFrom(jsonStr);
//            List<Map> asfirs = (List) data.get("asfirs");
//            if (asfirs == null) {
//                return null;
//            }
//            String longi = null;
//            String lat = null;
//            String totArea = null;
//            for (Map node : asfirs) {
//                longi = node.get("long").toString();
//                lat = node.get("lat").toString();
//                totArea = node.get("TotalArea").toString();
            // TODO for multiple polygons
//            }
//            ret.setLatitude(lat);
//            ret.setLongitude(longi);
//            ret.setTotalArea(request.queryParams("total_area")); // TODO
//            ret.setTotalArea(totArea);
        }

        ret.setEt_loc(calculateNearestStation(request.queryParams("et_loc"),
                request.queryParams("et_nearest_flg"), "CLIMATE", ret));
        ret.setRain_loc(calculateNearestStation(request.queryParams("rain_loc"),
                request.queryParams("rain_nearest_flg"), "RAIN", ret));
        ret.setEt_nearest_flg(request.queryParams("et_nearest_flg"));
        ret.setRain_nearest_flg(request.queryParams("rain_nearest_flg"));

        String coeffType = request.queryParams("coefficent_type");
        ret.setCoefficent_type(coeffType);
        if ("default".equalsIgnoreCase(coeffType)) {
            if (cropType != null && !cropType.isEmpty()) {
                if (cropType.equals("annual")) {
                    CropData cropData = DataUtil.getCropDataAnnual().get(ret.getCrop_name()).cloneData();
                    ret.setCropData(cropData);
                } else {
                    CropData cropData = DataUtil.getCropDataPerennial().get(ret.getCrop_name()).cloneData();
                    ret.setCropData(cropData);
                    ret.setHgt(request.queryParams("hgt"));
                }
            }
        } else {
            if (cropType != null && !cropType.isEmpty()) {
                if (cropType.equals("annual")) {
                    ret.setDzn(request.queryParams("dzn"));
                    ret.setDzx(request.queryParams("dzx"));
                    ret.setAkc3(request.queryParams("akc3"));
                    ret.setAkc4(request.queryParams("akc4"));
                    ret.setF1(request.queryParams("f1"));
                    ret.setF2(request.queryParams("f2"));
                    ret.setF3(request.queryParams("f3"));
                    ret.setF4(request.queryParams("f4"));
                    ret.setAld1(request.queryParams("ald1"));
                    ret.setAld2(request.queryParams("ald2"));
                    ret.setAld3(request.queryParams("ald3"));
                    ret.setAld4(request.queryParams("ald4"));
                } else {
                    ret.setDrzirr(request.queryParams("drzirr"));
                    ret.setDrztot(request.queryParams("drztot"));
                    ret.setAkcArr(JsonUtil.parseFrom(request.queryParams("akc_arr")).getArr());
                    ret.setAldpArr(JsonUtil.parseFrom(request.queryParams("aldp_arr")).getArr());
                    ret.setHgt(request.queryParams("hgt"));
                }
            }
        }
        return ret;
    }

    private static String calculateNearestStation(String loc, String flg, String type, WaterUsePermit permit) {
        if ("true".equalsIgnoreCase(flg)) {
            String jsonStr = permit.getPolygon_loc_info();
            loc = DataUtil.calculateNearestStation(type, jsonStr);
        }
        return loc;
    }

    public static WaterUsePermit readFromJson(File jsonFile) {
        JSONObject data = JsonUtil.parseFrom(jsonFile);
        return readFromJson(data);
    }

    public static WaterUsePermit readFromJson(String json) {
        JSONObject data = JsonUtil.parseFrom(json);
        return readFromJson(data);
    }

    public static WaterUsePermit readFromJson(JSONObject data) {
        WaterUsePermit ret = new WaterUsePermit();
        ret.setPermit_id(data.getOrDefault("permit_id", ""));
        ret.setUser_id(data.getOrDefault("user_id", ""));
        ret.setOwner_name(data.getOrDefault("owner_name", data.getOrBlank("output_name")));
        ret.setCrop_type(readCropType(data));
        ret.setCrop_name(data.getOrDefault("crop_name", ""));
        ret.setBeg_date_month(data.getOrBlank("beg_date_month"));
        ret.setBeg_date_day(data.getOrBlank("beg_date_day"));
        ret.setEnd_date_month(data.getOrBlank("end_date_month"));
        ret.setEnd_date_day(data.getOrBlank("end_date_day"));
        ret.setDescription(data.getOrBlank("description"));

        ret.setIrr_type(data.getOrBlank("irr_type"));
        ret.setIrr_option(data.getOrDefault("irr_option", null));
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

        String soilSource = data.getOrDefault("soil_source", null);
        ret.setSoil_source(soilSource);
        ret.setSoil_unit_name(data.getOrBlank("soil_unit_name"));
        ret.setWater_hold_capacity(data.getOrBlank("water_hold_capacity"));
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
        ArrayList<Soil> soils = DataUtil.toSoils(data, ret.getWater_hold_capacity());
        if ("DB".equals(soilSource)) {
            String[] soilNames = new String[soils.size()];
            for (int i = 0; i < soils.size(); i++) {
                soilNames[i] = soils.get(i).getSNAME();
            }
            ret.setDbSoilNames(soilNames);
            
            ArrayList<Soil> dbSoils = DataUtil.readSoils(ret.getDbSoilNames());
            for (int i = 0; i < soils.size() && i < dbSoils.size(); i++) {
                Soil dbSoil = dbSoils.get(i);
                soils.get(i).setValues(ret.getWater_hold_capacity(), dbSoil.getWCL(), dbSoil.getWCU(), dbSoil.getDU(), new String[3]);
            }
        }
        ret.setSoils(soils);

        ret.setEt_loc(data.getOrBlank("et_loc"));
        ret.setRain_loc(data.getOrBlank("rain_loc"));
        ret.setEt_nearest_flg(data.getOrBlank("et_nearest_flg"));
        ret.setRain_nearest_flg(data.getOrBlank("rain_nearest_flg"));

        ret.setCoefficent_type(data.getOrDefault("coefficent_type", null));
        ret.setDzn(data.getOrBlank("dzn"));
        ret.setDzx(data.getOrBlank("dzx"));
        ret.setAkc3(data.getOrBlank("akc3"));
        ret.setAkc4(data.getOrBlank("akc4"));
        ret.setF1(data.getOrBlank("f1"));
        ret.setF2(data.getOrBlank("f2"));
        ret.setF3(data.getOrBlank("f3"));
        ret.setF4(data.getOrBlank("f4"));
        ret.setAld1(data.getOrBlank("ald1"));
        ret.setAld2(data.getOrBlank("ald2"));
        ret.setAld3(data.getOrBlank("ald3"));
        ret.setAld4(data.getOrBlank("ald4"));

        ret.setDrzirr(data.getOrBlank("drzirr"));
        ret.setDrztot(data.getOrBlank("drztot"));
        ret.setAkcArr(data.getArr("akc"));
        ret.setAldpArr((ArrayList) data.getArr("aldp"));
        if ((AFSIRSModule.IRCRFL + "").equals(ret.getIrr_type())) {
            ret.setHgt(data.getOrBlank("hgt"));
        }

        return ret;
    }

    public UserInput toAFSIRSInputData(String userId) {
        UserInput input = new UserInput();
        input.setSITE(permit_id, Path.Folder.getUserWaterUsePermitOutputDir(userId).getPath());
        input.setOWNER(owner_name);
        input.setCropType(crop_type);
//        input.setCropName(crop_name);
        input.setCropData(DataUtil.getCropIndexCode(crop_type, crop_name), crop_name);
        if (crop_type.equals("annual")) {
            input.setIrrigationSeason(beg_date_month, beg_date_day, end_date_month, end_date_day);
        } else {
            input.setIrrigationSeason(1, 1, 12, 31);
        }

        input.setCodes(2, 0);

        input.setIrrOption(irr_option);
        input.setIDCODE(irr_depth_type, irr_depth);
        input.setIrrigationSystem(irr_type, soil_surface_irr, et_extracted, irr_efficiency,
                DataUtil.getIRSysNameList().get(Integer.parseInt(irr_type)));
//        input.setIVERS(ir_dat);
        input.setDWT(Double.parseDouble(water_table_depth));

        input.setSoilSource(soil_source);
        input.setUNIT(soil_unit_name);
        input.setSoils(soils);
        input.setWATERHOLDINGCAPACITY(water_hold_capacity);
        input.setMapArea(new BigDecimal(totalArea).doubleValue());
        input.setPlantedAcres(new BigDecimal(plantedArea).doubleValue());
        input.setPolygonInfo(polygon_info);
        input.setPolygonLocInfo(polygon_loc_info);

        input.setWeather(DataUtil.toWeather(et_loc, rain_loc));
        input.setRAIN_FLG(rain_nearest_flg);
        input.setCLIM_FLG(et_nearest_flg);

        input.setCoefficentType(coefficent_type);
        if (crop_type != null && !crop_type.isEmpty()) {
            if (crop_type.equals("annual")) {
                input.setDCOEFAnnual(
                        new BigDecimal(dzn).doubleValue(),
                        new BigDecimal(dzx).doubleValue(),
                        new double[]{new BigDecimal(f1).doubleValue(),
                            new BigDecimal(f2).doubleValue(),
                            new BigDecimal(f3).doubleValue(),
                            new BigDecimal(f4).doubleValue()},
                        new double[]{new BigDecimal(ald1).doubleValue(),
                            new BigDecimal(ald2).doubleValue(),
                            new BigDecimal(ald3).doubleValue(),
                            new BigDecimal(ald4).doubleValue()});
                input.setAKC34(
                        new BigDecimal(akc3).doubleValue(),
                        new BigDecimal(akc4).doubleValue());
            } else {
                input.setDCOEFPerennial(
                        new BigDecimal(drzirr).doubleValue(),
                        new BigDecimal(drztot).doubleValue(),
                        akcArr.stream().mapToDouble(Double::parseDouble).toArray(),
                        aldpArr.stream().mapToDouble(Double::parseDouble).toArray());
                if ((AFSIRSModule.IRCRFL + "").equals(irr_type)) {
                    input.setHGT(new BigDecimal(hgt).doubleValue());
                }
            }
        }

        return input;
    }

    public static UserInput setDeviation(UserInput input, WaterUsePermit permit) {

        // Climate
        if (!permit.getEt_loc().equals(DataUtil.calculateNearestStation("CLIMATE", permit.getPolygon_loc_info()))) {
            input.addDeviation("RainfallLocation", permit.getEt_loc());
        }
        if (!permit.getRain_loc().equals(DataUtil.calculateNearestStation("RAIN", permit.getPolygon_loc_info()))) {
            input.addDeviation("Climate Location", permit.getRain_loc());
        }

        // General
        if (input.isPerennialCrop()) {
            input.addDeviation("Crop Type", permit.getCrop_type());
        } else {
            CropDataAnnual crop = (CropDataAnnual) DataUtil.getCropDataAnnual().get(permit.getCrop_name());
            int max = crop.getMaxDays();
            int min = crop.getMinDays();
            long days;
            if (input.getMO1() > input.getMON() || (input.getMO1() == input.getMON() && input.getDAY1() > input.getDAYN())) {
                days = ChronoUnit.DAYS.between(
                        LocalDate.of(2001, Month.of(input.getMO1()), input.getDAY1()),
                        LocalDate.of(2002, Month.of(input.getMON()), input.getDAYN()));
            } else {
                days = ChronoUnit.DAYS.between(
                        LocalDate.of(2001, Month.of(input.getMO1()), input.getDAY1()),
                        LocalDate.of(2001, Month.of(input.getMON()), input.getDAYN()));
            }
            if (days > max || days < min) {
                MonthDay start = MonthDay.of(input.getMO1(), input.getDAY1());
                MonthDay end = MonthDay.of(input.getMON(), input.getDAYN());
                input.addDeviation("Date Range", start.format(DateTimeFormatter.ofPattern("MM-dd")) + " ~ " + end.format(DateTimeFormatter.ofPattern("MM-dd")) + " which is outside the normal range");
            }
        }

        // Irrigation
        if (input.getIrrigationSystem() != 0) {
            input.addDeviation("Irrigation system", DataUtil.getIRSysNameList().get(input.getIrrigationSystem()));
        }
        if (input.isNetCalc()) {
            input.addDeviation("Irrigation Requirement", permit.getIrr_option());
        }
        switch (permit.getIrr_depth_type()) {
            case "1":
                input.addDeviation("Irrigation water depths", "Fixed water depth per application");
                break;
            case "2":
                input.addDeviation("Irrigation water depths", "Deficit Irrigation");
                break;
            case "3":
                input.addDeviation("Irrigation water depths", "none");
                break;
        }
        Irrigation irSys = DataUtil.getIRSysList().get(input.getIrrigationSystem());
        if (irSys.getEff() != input.getIEFF()) {
            input.addDeviation("Irrigation application efficiency", input.getIEFF() + "");
        }
        if (irSys.getArea() != input.getARZI()) {
            input.addDeviation("Fraction of soil surface irrigated", input.getARZI() + "");
        }
        if (irSys.getEx() != input.getEXIR()) {
            input.addDeviation("Fraction of ET extracted", input.getEXIR() + "");
        }
        if (irSys.getDwt() != input.getDWT()) {
            input.addDeviation("Depth of water table", input.getDWT() + "");
        }

        // Soil
        if (input.getPlantedAcres() != input.getMapArea()) {
            input.addDeviation("Planted area", input.getPlantedAcres() + "");
        }
        if (!input.getWATERHOLDINGCAPACITY().equals("Average")) {
            input.addDeviation("Water hold capacity", input.getWATERHOLDINGCAPACITY());
        }
//1074:  input.addDeviation("Soil Series name", soilNameText.toString());
//1082:  input.addDeviation("Soil Texture", soilTextureText.toString());
//1368:  input.addDeviation("Input data from", "User defined");
//1409:  input.addDeviation("Input data from", "Soil Database");

        // Annual Decoefficient
        if (input.isPerennialCrop()) {
            CropDataPerennial crop = (CropDataPerennial) DataUtil.getCropDataPerennial().get(permit.getCrop_name());
            if (input.getDRZIRR() != crop.getDRZIRR()) {
                input.addDeviation("Irrigated root zone depth", input.getDRZIRR() + "");
            }
            if (input.getDRZTOT() != crop.getDRZTOT()) {
                input.addDeviation("Total crop root zone depth", input.getDRZTOT() + "");
            }
            if (input.getHGT() != crop.getHGT()) {
                input.addDeviation("Height of Crown flood system", input.getHGT() + "");
            }
            for (int i = 0; i < 12; i++) {
                if (input.getAKC()[i] != crop.getAKC()[i]) {
                    input.addDeviation("Crop water use coefficients for " + Month.of(i + 1).name(), input.getAKC()[i] + "");
                }
                if (input.getALDP()[i] != crop.getALDP()[i]) {
                    input.addDeviation("Allowable soil water depletions for " + Month.of(i + 1).name(), input.getALDP()[i] + "");
                }
            }

        } else {
            CropDataAnnual crop = (CropDataAnnual) DataUtil.getCropDataAnnual().get(permit.getCrop_name());
            if (input.getDZN() != crop.getDZN()) {
                input.addDeviation("Intial Irrigated root zone depth", input.getDZN() + "");
            }
            if (input.getDZX() != crop.getDZX()) {
                input.addDeviation("Maximum Irrigated root zone depth", input.getDZX() + "");
            }
            if (input.getAKC3() != crop.getAKC3()) {
                input.addDeviation("Crop water usage stage 3 coefficient", input.getAKC3() + "");
            }
            if (input.getAKC4() != crop.getAKC4()) {
                input.addDeviation("Crop water usage stage 4 coefficient", input.getAKC4() + "");
            }
            for (int i = 0; i < 4; i++) {
                if (input.getF()[i] != crop.getF()[i]) {
                    input.addDeviation("Fraction of growing season stage " + (i + 1), input.getF()[i] + "");
                }
                if (input.getALD()[i] != crop.getALD()[i]) {
                    input.addDeviation("Allowable soil water depletion stage " + (i + 1), input.getALD()[i] + "");
                }
            }
        }

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
