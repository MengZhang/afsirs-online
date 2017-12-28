package org.afsirs.web.view;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.afsirs.module.AFSIRSModule;
import org.afsirs.module.SimResult;
import org.afsirs.module.SoilSpecificPeriodData;
import org.afsirs.module.SummaryReport;
import org.afsirs.module.UserInput;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.ViewUtil.setCommonParam;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

/**
 *
 * @author Meng Zhang
 */
public class SimulationViewUtil {

    protected static void setSimulationCommonParam(Request request, Map<String, Object> attributes) {
        // Set common attribute in this function and always call this one in the particular view function
//        if (!attributes.containsKey("name") && attributes.containsKey("project_name")) {
//            attributes.put("name", attributes.get("project_name"));
//        }
    }

    public static String getAfsirsPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS));
    }

    public static String getAfsirsResultPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
        File out = Path.Folder.getUserWaterUsePermitOutputDir(ViewUtil.getUserID(request));
        UserInput input = (UserInput) attributes.get("afsirs_input");
        SimResult simRet = AFSIRSModule.run(input, out);

        LinkedHashMap<String, double[]> irrReqData = new LinkedHashMap();
        LinkedHashMap<String, double[]> twoIn10Data = new LinkedHashMap();
        LinkedHashMap<String, double[]> oneIn10Data = new LinkedHashMap();
        double[] irrReqWgtAvgData = new double[12];
        double[] twoIn10WgtAvgData = new double[12];
        double[] oneIn10WgtAvgData = new double[12];
        double irrReqAvg = 0.0;
        double twoIn10Avg = 0.0;
        double oneIn10Avg = 0.0;

        LinkedHashMap<String, ArrayList<Double>> wgtAvgDataArr = new LinkedHashMap();
        double twoIn10AvgTot = 0.0;
        double oneIn10AvgTot = 0.0;
        String oneIn10Key = "Weighted Avg 1-in-10";
        String twoIn10Key = "Weighted Avg 2-in-10";
        wgtAvgDataArr.put(oneIn10Key, new ArrayList());
        wgtAvgDataArr.put(twoIn10Key, new ArrayList());
//        double areaSum = simRet.getTotalArea();

        ArrayList<SoilSpecificPeriodData> PDATA = AFSIRSModule.getGraphData(simRet, 0);
        for (int j = 0; j < simRet.getSummaryList().size(); j++) {
            String soilName = simRet.getSummaryList().get(j).soilName;
            irrReqData.put(soilName, new double[12]);
            twoIn10Data.put(soilName, new double[12]);
            oneIn10Data.put(soilName, new double[12]);
        }

        for (int i = 0; i < PDATA.get(0).getSoilDataPoints().length; i++) {

            for (int j = 0; j < simRet.getSummaryList().size(); j++) {
                String soilName = simRet.getSummaryList().get(j).soilName;
                SummaryReport report = simRet.getSummaryList().get(j);

                irrReqAvg += report.getWeightedAvgIrrRequired(i + 1);
                twoIn10Avg += report.getWeighted2In10IrrRequired((i + 1));
                oneIn10Avg += report.getWeighted1In10IrrRequired((i + 1));
                irrReqData.get(soilName)[i] = (report.getAverageIrrigationRequired(i + 1));
                twoIn10Data.get(soilName)[i] = (report.getTwoin10IrrigationRequired(i + 1));
                oneIn10Data.get(soilName)[i] = (report.getOnein10IrrigationRequired(i + 1));
                twoIn10AvgTot += report.getWeighted2In10IrrRequired((i + 1));
                oneIn10AvgTot += report.getWeighted2In10IrrRequired((i + 1));
            }

            irrReqWgtAvgData[i] = irrReqAvg;
            twoIn10WgtAvgData[i] = twoIn10Avg;
            oneIn10WgtAvgData[i] = oneIn10Avg;
            wgtAvgDataArr.get(twoIn10Key).add(twoIn10AvgTot);
            wgtAvgDataArr.get(oneIn10Key).add(oneIn10AvgTot);
//            wgtAvgDataArr.get(twoIn10Key).add(twoIn10AvgTot/areaSum);
//            wgtAvgDataArr.get(oneIn10Key).add(oneIn10AvgTot/areaSum);
            irrReqAvg = 0.0;
            twoIn10Avg = 0.0;
            oneIn10Avg = 0.0;
            twoIn10AvgTot = 0.0;
            oneIn10AvgTot = 0.0;
        }

        irrReqData.put("Weighted Avg.", irrReqWgtAvgData);
        twoIn10Data.put("Weighted Avg.", twoIn10WgtAvgData);
        oneIn10Data.put("Weighted Avg.", oneIn10WgtAvgData);
        attributes.put("irrReqData", irrReqData);
        attributes.put("twoIn10Data", twoIn10Data);
        attributes.put("oneIn10Data", oneIn10Data);
        attributes.put("wgtAvgData", wgtAvgDataArr);

//        for (int i = 0; i < PDATA.get(0).getSoilDataPoints().length; i++) {
//            for (int j = 0; j < simRet.getSummaryList().size(); j++) {
//                SummaryReport report = simRet.getSummaryList().get(j);
//            }
//            twoIn10AvgTot /= areaSum;
//            oneIn10AvgTot /= areaSum;
//            twoIn10AvgTot = 0;
//            oneIn10AvgTot = 0;
//        }
        LinkedHashMap<String, double[]> climateData = new LinkedHashMap();
        climateData.put("Mean RainFall", new double[12]);
        climateData.put("Mean ET (Crop)", new double[12]);
        for (SummaryReport summaryReport : simRet.getSummaryList()) {
            for (int i = 0; i < 12; i++) {
                double val = summaryReport.getTotalRainFallByMonth(i + 1);
                climateData.get("Mean RainFall")[i] = val;
            }
        }

        for (SummaryReport summaryReport : simRet.getSummaryList()) {
            for (int i = 0; i < 12; i++) {
                double val = summaryReport.getEvaporationCropByMonth(i + 1);
                climateData.get("Mean ET (Crop)")[i] = val;
            }
        }
        attributes.put("climateData", climateData);

        attributes.put("permit_id", request.queryParams("permit_id"));
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS_RESULT));
    }
}
