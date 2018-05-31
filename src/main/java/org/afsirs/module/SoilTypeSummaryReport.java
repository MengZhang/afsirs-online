package org.afsirs.module;

import lombok.Getter;
import lombok.Setter;
import org.afsirs.module.util.JSONObject;

/**
 *
 * @author Meng Zhang
 */
public class SoilTypeSummaryReport extends SummaryReport {
    @Getter @Setter private String soilSeriesName;
    @Getter @Setter private String soilSeriesKey;

    public SoilTypeSummaryReport(Soil soil) {
        super(soil.getCOMPKEY(), soil.getSNAME(), soil.getSoilSymbolNum(), soil.getSoilTypeArea());
        this.soilSeriesKey = soil.getSOILSERIESKEY();
        this.soilSeriesName = soil.getSERIESNAME();
    }

    public SoilTypeSummaryReport(JSONObject data) {
        super(
                data.getOrBlank("cokey"),
                data.getOrBlank("soilName"),
                data.getOrBlank("musym"),
                data.getAsDouble("compArea")
        );
        this.soilSeriesKey = data.getOrBlank("mukey");
        this.soilSeriesName = data.getOrBlank("mukeyName");
    }
    
    @Override
    public JSONObject toJsonData() {
        JSONObject data = super.toJsonData();
        data.put("mukey", soilSeriesKey);
        data.put("mukeyName", soilSeriesName);
        return data;
    }
}
