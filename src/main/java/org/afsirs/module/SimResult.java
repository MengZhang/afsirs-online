package org.afsirs.module;

import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import static org.afsirs.module.util.Util.getSummaryReportComparetor;
import static org.afsirs.module.util.Util.isSorted;

/**
 * The container class for simulation result
 *
 * @author Meng Zhang
 */
@Data
public class SimResult {

    private double totalArea = 0.0;
    private double plantedArea = 0.0;
    private double[][] RAIN;

    // ArrayList for Soilnames with negative value error
    private ArrayList<String> soilNames = new ArrayList<>();

    // ArrayList to Hold all the data
    private ArrayList<PDAT> allSoilInfo = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final LinkedHashMap<String, SoilSeriesSummaryReport> summaryList = new LinkedHashMap<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ArrayList<SoilSeriesSummaryReport> sortedSummaryList = new ArrayList<>();

    private File outFile, summaryFile, summaryFileExcel, calculationExcel;

    public void addSoilTypeSummaryReport(SoilTypeSummaryReport report) {
        String soilSeriesKey = report.getSoilSeriesKey();
        if (summaryList.containsKey(soilSeriesKey)) {
            summaryList.get(soilSeriesKey).addSoilTypeSummaryReport(report);
        } else {
            summaryList.put(soilSeriesKey, new SoilSeriesSummaryReport(report));
        }
    }

    public ArrayList<SoilSeriesSummaryReport> getSummaryList() {
        if (sortedSummaryList.size() < summaryList.size()) {
            sortedSummaryList = getSoilSeriesSummaryList();
            Collections.sort(sortedSummaryList, getSummaryReportComparetor());
        } else if (!isSorted(sortedSummaryList)) {
            Collections.sort(sortedSummaryList, getSummaryReportComparetor());
        }
        return sortedSummaryList;
    }

    private ArrayList<SoilSeriesSummaryReport> getSoilSeriesSummaryList() {
        return new ArrayList(summaryList.values());
    }

    public ArrayList<SoilTypeSummaryReport> getSoilTypeSummaryList() {
        ArrayList<SoilTypeSummaryReport> ret = new ArrayList();
        for (SoilSeriesSummaryReport report : getSoilSeriesSummaryList()) {
            ret.addAll(report.getSoilTypeSummaryReportList());
        }
        return ret;
    }

//    public ArrayList<SoilTypeSummaryReport> getSoilTypeSummaryList(ArrayList<Soil> soils) {
//        ArrayList<SoilTypeSummaryReport> ret = new ArrayList();
//        for (Soil soil : soils) {
//            ret.add(getSoilTypeSummaryReport(soil));
//        }
//        return ret;
//    }
    public SoilSeriesSummaryReport getSoilSeriesSummaryReport(Soil soil) {
        String soilSeriesKey = soil.getSOILSERIESKEY();
        if (summaryList.containsKey(soilSeriesKey)) {
            SoilSeriesSummaryReport ret = summaryList.get(soilSeriesKey);
            ret.setCurReport(soil);
            return ret;
        } else {
            SoilSeriesSummaryReport report = new SoilSeriesSummaryReport(soil);
            summaryList.put(soilSeriesKey, report);
            return report;
        }
    }
    
    public SoilSeriesSummaryReport getSoilSeriesSummaryReport(SoilTypeSummaryReport report) {
        String soilSeriesKey = report.getSoilSeriesKey();
        if (summaryList.containsKey(soilSeriesKey)) {
            SoilSeriesSummaryReport ret = summaryList.get(soilSeriesKey);
            ret.setCurReport(report);
            return ret;
        } else {
            SoilSeriesSummaryReport ret = new SoilSeriesSummaryReport(report);
            summaryList.put(soilSeriesKey, ret);
            return ret;
        }
    }

    public SoilTypeSummaryReport getSoilTypeSummaryReport(Soil soil) {
        String soilSeriesKey = soil.getSOILSERIESKEY();
        if (summaryList.containsKey(soilSeriesKey)) {
            return summaryList.get(soilSeriesKey).getSoilTypeSummaryReport(soil);
        } else {
            SoilSeriesSummaryReport report = new SoilSeriesSummaryReport(soil);
            summaryList.put(soilSeriesKey, report);
            return report.getSoilTypeSummaryReport(soil);
        }
    }
}
