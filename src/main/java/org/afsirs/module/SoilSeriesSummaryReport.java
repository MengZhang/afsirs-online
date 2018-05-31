package org.afsirs.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import static org.afsirs.module.util.Util.summaryReportComparetor2;
import static org.afsirs.module.util.Util.isSorted2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SoilSeriesSummaryReport extends SummaryReport {

    private static final Logger LOG = LoggerFactory.getLogger(SoilSeriesSummaryReport.class);

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final LinkedHashMap<String, SoilTypeSummaryReport> reports = new LinkedHashMap<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ArrayList<SoilTypeSummaryReport> sortedReports = new ArrayList<>();

    private SoilTypeSummaryReport curReport;

    public SoilSeriesSummaryReport(SoilTypeSummaryReport firstSoilTypeReport) {
        super(firstSoilTypeReport.getSoilSeriesKey(), firstSoilTypeReport.getSoilSeriesName(), firstSoilTypeReport.getSoilSymbolNum());
        addSoilTypeSummaryReport(firstSoilTypeReport);
    }

    public SoilSeriesSummaryReport(Soil soil) {
        super(soil.getSOILSERIESKEY(), soil.getSERIESNAME(), soil.getSoilSymbolNum());
        addSoilTypeSummaryReport(new SoilTypeSummaryReport(soil));
    }

    public void setCurReport(SoilTypeSummaryReport report) {
        String soilTypeKey = report.getSoilKey();
        if (!reports.containsKey(soilTypeKey)) {
            addSoilTypeSummaryReport(report);
        }
        this.curReport = reports.get(soilTypeKey);
    }

    public void setCurReport(Soil soil) {
        String soilTypeKey = soil.getCOMPKEY();
        if (!reports.containsKey(soilTypeKey)) {
            addSoilTypeSummaryReport(new SoilTypeSummaryReport(soil));
        }
        this.curReport = reports.get(soilTypeKey);
    }

    public final void addSoilTypeSummaryReport(SoilTypeSummaryReport report) {
        if (report == null) {
            return;
        }
        this.curReport = report;
        updateStatistics(report);
        reports.put(report.getSoilKey(), report);
    }

    public SoilTypeSummaryReport getSoilTypeSummaryReport(Soil soil) {
        SoilTypeSummaryReport ret;
        String soilKey = soil.getCOMPKEY();
        if (reports.containsKey(soilKey)) {
            ret = reports.get(soilKey);
        } else {
            ret = new SoilTypeSummaryReport(soil);
            addSoilTypeSummaryReport(ret);
        }
        this.curReport = ret;
        return ret;
    }

    public ArrayList<SoilTypeSummaryReport> getSoilTypeSummaryReportList() {
        if (sortedReports.size() < reports.size()) {
            sortedReports = new ArrayList(reports.values());
            Collections.sort(sortedReports, summaryReportComparetor2);
        } else if (!isSorted2(sortedReports)) {
            Collections.sort(sortedReports, summaryReportComparetor2);
        }
        return sortedReports;
    }

    public void addSoilArea(double soilArea) {
        this.setSoilArea(this.getSoilArea() + soilArea);
    }

    private void updateStatistics(SoilTypeSummaryReport report) {
        String soilKey = report.getSoilKey();
        if (reports.containsKey(soilKey)) {
            LOG.warn("Detect repeated soil type [{}] under soil series [{}]", report.getSoilName(), report.getSoilSeriesName());
            this.addSoilArea(-reports.get(soilKey).getSoilArea());
        }
        this.addSoilArea(report.getSoilArea());
    }

    @Override
    public void setTotalRainFall(int month, double rainFall) throws IllegalArgumentException {
        double orgVal = curReport.getTotalRainFallByMonth(month);
        double diff = rainFall - orgVal;
        curReport.setTotalRainFall(month, rainFall);
        rainFall = super.getTotalRainFallByMonth(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTotalRainFall(month, rainFall);
    }

    @Override
    public void setTotalEvaporation(int month, double evaporation) throws IllegalArgumentException {
        double orgVal = curReport.getTotalEvaporationByMonth(month);
        double diff = evaporation - orgVal;
        curReport.setTotalEvaporation(month, evaporation);
        evaporation = super.getTotalEvaporationByMonth(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTotalEvaporation(month, evaporation);
    }

    @Override
    public void setPeakMonthlyEvaporation(int month, double evaporation) {
        double orgVal = curReport.getPeakEvaporationByMonth(month);
        double diff = evaporation - orgVal;
        if (diff > 0) {
            curReport.setPeakMonthlyEvaporation(month, evaporation);
            evaporation = super.getPeakEvaporationByMonth(month) + diff * curReport.getSoilArea() / this.getSoilArea();
            super.setPeakMonthlyEvaporation(month, evaporation);
        }
    }

    @Override
    public void setEvaporationCrop(int month, double evaporation) throws IllegalArgumentException {
        double orgVal = curReport.getEvaporationCropByMonth(month);
        double diff = evaporation - orgVal;
        curReport.setEvaporationCrop(month, evaporation);
        evaporation = super.getEvaporationCropByMonth(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setEvaporationCrop(month, evaporation);
    }

    @Override
    public void setPeakMonthlyEvaporationCrop(int month, double evaporation) {
        double orgVal = curReport.getPeakEvaporationCropByMonth(month);
        double diff = evaporation - orgVal;
        if (diff > 0) {
            curReport.setPeakMonthlyEvaporationCrop(month, evaporation);
            evaporation = super.getPeakEvaporationCropByMonth(month) + diff * curReport.getSoilArea() / this.getSoilArea();
            super.setPeakMonthlyEvaporationCrop(month, evaporation);
        }
    }

    @Override
    public void addTotalIrrigationRequiredByMonth(int month, double irrigationRequired) {
        curReport.addTotalIrrigationRequiredByMonth(month, irrigationRequired);
        irrigationRequired = irrigationRequired * curReport.getSoilArea() / this.getSoilArea();
        super.addTotalIrrigationRequiredByMonth(month, irrigationRequired);
    }

    @Override
    public void setAverageIrrigationRequired(int month, double irrigationRequired) {
        double orgVal = curReport.getAverageIrrigationRequired(month);
        double diff = irrigationRequired - orgVal;
        curReport.setAverageIrrigationRequired(month, irrigationRequired);
        irrigationRequired = super.getAverageIrrigationRequired(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setAverageIrrigationRequired(month, irrigationRequired);
    }

    @Override
    public void setTwoin10IrrigationRequired(int month, double irrigationRequired) {
        double orgVal = curReport.getTwoin10IrrigationRequired(month);
        double diff = irrigationRequired - orgVal;
        curReport.setTwoin10IrrigationRequired(month, irrigationRequired);
        irrigationRequired = super.getTwoin10IrrigationRequired(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTwoin10IrrigationRequired(month, irrigationRequired);
    }

    @Override
    public void setOnein10IrrigationRequired(int month, double irrigationRequired) {
        double orgVal = curReport.getOnein10IrrigationRequired(month);
        double diff = irrigationRequired - orgVal;
        curReport.setOnein10IrrigationRequired(month, irrigationRequired);
        irrigationRequired = super.getOnein10IrrigationRequired(month) + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setOnein10IrrigationRequired(month, irrigationRequired);
    }

    @Override
    public void setWeightedAvgIrrRequired(int month, double val) {
        curReport.setWeightedAvgIrrRequired(month, val);
        super.setWeightedAvgIrrRequired(month, val);
    }

    @Override
    public void setWeighted2In10IrrRequired(int month, double val) {
        curReport.setWeighted2In10IrrRequired(month, val);
        super.setWeighted2In10IrrRequired(month, val);
    }

    @Override
    public void setWeighted1In10IrrRequired(int month, double val) {
        curReport.setWeighted1In10IrrRequired(month, val);
        super.setWeighted1In10IrrRequired(month, val);
    }

    /**
     * @param totalTwoinTen the totalTwoinTen to set
     */
    @Override
    public void setTotalTwoinTen(double totalTwoinTen) {
        double orgVal = curReport.getTotalTwoinTen();
        double diff = totalTwoinTen - orgVal;
        curReport.setTotalTwoinTen(totalTwoinTen);
        totalTwoinTen = super.getTotalTwoinTen() + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTotalTwoinTen(totalTwoinTen);
    }

    /**
     * @param totalOneinTen the totalOneinTen to set
     */
    @Override
    public void setTotalOneinTen(double totalOneinTen) {
        double orgVal = curReport.getTotalOneinTen();
        double diff = totalOneinTen - orgVal;
        curReport.setTotalOneinTen(totalOneinTen);
        totalOneinTen = super.getTotalOneinTen() + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTotalOneinTen(totalOneinTen);
    }

    /**
     * @param totalAvgIrr the totalAvgIrr to set
     */
    @Override
    public void setTotalAvgIrr(double totalAvgIrr) {
        double orgVal = curReport.getTotalAvgIrr();
        double diff = totalAvgIrr - orgVal;
        curReport.setTotalAvgIrr(totalAvgIrr);
        totalAvgIrr = super.getTotalAvgIrr() + diff * curReport.getSoilArea() / this.getSoilArea();
        super.setTotalAvgIrr(totalAvgIrr);
    }

}
