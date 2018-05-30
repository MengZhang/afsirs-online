package org.afsirs.web.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import org.afsirs.module.AFSIRSOutput;
import org.afsirs.module.SimResult;
import org.afsirs.module.SoilSeriesSummaryReport;
import org.afsirs.web.dao.WaterUsePermitDAO;
import org.afsirs.web.dao.bean.WaterUsePermit;
import static org.afsirs.web.dao.bean.WaterUsePermit.setDeviation;
import org.afsirs.web.util.Path;
import static org.afsirs.web.view.ViewUtil.setCommonParam;
import org.eclipse.jetty.io.EofException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
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
    
    public static String getAfsirsResultLoadingPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
        attributes.put("user_id", ViewUtil.getUserID(request));
        attributes.put("permit_id", request.queryParams("permit_id"));
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS_RESULT_ASYN));
    }

    public static String getAfsirsResultPage(Request request, Map<String, Object> attributes) {
        setCommonParam(request, attributes);
        setSimulationCommonParam(request, attributes);
//        UserInput input = (UserInput) attributes.get("afsirs_input");
//        SimResult simRetOrg = AFSIRSModule.run(input);
//        String json = simRetOrg.toJson();

        String userId = ViewUtil.getUserID(request);
        String permitId = request.queryParams("permit_id");
        File jsonFile = Path.Folder.getUserWaterUsePermitOutputJsonFile(ViewUtil.getUserID(request), permitId);
        
        WaterUsePermit permit = WaterUsePermitDAO.find(permitId, userId);
        if (permit == null) {
            return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.NOT_FOUND)); // TODO
        }
        SimResult simRet = SimResult.fromJson(jsonFile);
        AFSIRSOutput.run(simRet, setDeviation(permit.toAFSIRSInputData(userId), permit));

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

        ArrayList<SoilSeriesSummaryReport> summaryList = simRet.getSummaryList();
        for (SoilSeriesSummaryReport report : summaryList) {
            String soilName = report.getSoilName();
            irrReqData.put(soilName, new double[12]);
            twoIn10Data.put(soilName, new double[12]);
            oneIn10Data.put(soilName, new double[12]);
        }

        for (int i = 0; i < simRet.getTotalMonth(); i++) {

            for (SoilSeriesSummaryReport report : summaryList) {
                String soilName = report.getSoilName();

                irrReqAvg += report.getWeightedAvgIrrRequired(i + 1);
                twoIn10Avg += report.getWeighted2In10IrrRequired((i + 1));
                oneIn10Avg += report.getWeighted1In10IrrRequired((i + 1));
                irrReqData.get(soilName)[i] = (report.getAverageIrrigationRequired(i + 1));
                twoIn10Data.get(soilName)[i] = (report.getTwoin10IrrigationRequired(i + 1));
                oneIn10Data.get(soilName)[i] = (report.getOnein10IrrigationRequired(i + 1));
                twoIn10AvgTot += report.getWeighted2In10IrrRequired((i + 1));
                oneIn10AvgTot += report.getWeighted1In10IrrRequired((i + 1));
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
        for (SoilSeriesSummaryReport summaryReport : summaryList) {
            for (int i = 0; i < 12; i++) {
                double val = summaryReport.getTotalRainFallByMonth(i + 1);
                climateData.get("Mean RainFall")[i] = val;
            }
        }

        for (SoilSeriesSummaryReport summaryReport : summaryList) {
            for (int i = 0; i < 12; i++) {
                double val = summaryReport.getEvaporationCropByMonth(i + 1);
                climateData.get("Mean ET (Crop)")[i] = val;
            }
        }
        attributes.put("climateData", climateData);

        attributes.put("permit_id", permitId);
        attributes.put("user_id", userId);
        return new FreeMarkerEngine().render(new ModelAndView(attributes, Path.Template.Simulation.AFSIRS_RESULT));
    }

    public static Object getAfsirsDownloadResponse(Response response, File downloandFile) {
        response.status(200);
        if (downloandFile.getName().endsWith(".pdf")) {
            response.type("application/pdf");
            response.header("Content-Disposition", "filename=" + downloandFile.getName());
        } else if (downloandFile.getName().endsWith(".xlsx")) {
            response.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.header("Content-Disposition", "attachment;filename=" + downloandFile.getName());
        } else {
            response.type("text/plain");
            response.header("Content-Disposition", "filename=" + downloandFile.getName());
        }

        try {
            FileInputStream in = new FileInputStream(downloandFile);
            ServletOutputStream out = response.raw().getOutputStream();

            byte[] outputByte = new byte[4096];
            //copy binary contect to output stream
            int bytesRead;
            while ((bytesRead = in.read(outputByte, 0, 4096)) != -1) {
                out.write(outputByte, 0, bytesRead);
            }
            out.flush();
            return out;
        } catch (EofException ex) {
            return "";
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            response.status(404);
            return "FILE BROKEN";
        }
    }
}
