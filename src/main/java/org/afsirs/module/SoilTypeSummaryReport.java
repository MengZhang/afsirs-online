package org.afsirs.module;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Meng Zhang
 */
public class SoilTypeSummaryReport extends SummaryReport {
    @Getter @Setter private String soilSeriesName;
    @Getter @Setter private String soilSeriesKey;
    
    public SoilTypeSummaryReport(Soil soil) {
        super(soil.getCOMPKEY(), soil.getSNAME(), soil.getSoilTypeArea());
        this.soilSeriesKey = soil.getSOILSERIESKEY();
        this.soilSeriesName = soil.getSERIESNAME();
    }
    
}
