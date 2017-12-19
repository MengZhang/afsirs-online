package org.afsirs.web.dao.bean;

import java.io.File;
import lombok.Data;
import org.afsirs.module.UserInput;
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
        
        return ret;
    }

    public static WaterUsePermit readFromJson(File jsonFile) {
        WaterUsePermit ret = new WaterUsePermit();
        JSONObject data = JsonUtil.parseFrom(jsonFile);
        ret.setPermit_id(data.getOrDefault("permit_id", ""));
        ret.setOwner_name(data.getOrBlank("owner_name"));
        ret.setCrop_type(readCropType(data));
        ret.setBeg_date_month(data.getOrBlank("beg_date_month"));
        ret.setBeg_date_day(data.getOrBlank("beg_date_day"));
        ret.setEnd_date_month(data.getOrBlank("end_date_month"));
        ret.setEnd_date_day(data.getOrBlank("end_date_day"));
        ret.setDescription(data.getOrBlank("description"));
        return ret;
    }
    
    public UserInput toAFSIRSInputData() {
        UserInput input = new UserInput();
        input.setSITE(permit_id);
        input.setOWNER(owner_name);
        input.setCropType(crop_type);
        input.setCropName(crop_name);
        input.setIrrigationSeason(beg_date_month, beg_date_day, end_date_month, end_date_day);
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
}
