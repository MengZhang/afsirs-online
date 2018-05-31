package org.afsirs.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.afsirs.module.util.JSONArray;
import org.afsirs.module.util.JSONObject;

/**
 *
 * @author rohit
 * @author Meng Zhang
 */
public abstract class SummaryReport {

//    private int curMonth;           // Unused for final output files
    private double totalTwoinTen;
    private double totalOneinTen;
    private double totalAvgIrr;
    private ArrayList<Double> totalRainFall;
    private ArrayList<Double> totalEvaporationPotential;

    private ArrayList<Double> peakMonthlyEvaporationPotential;  // Unused for final output files

    private ArrayList<Double> totalEvaporationCrop;
    private ArrayList<Double> peakMonthlyEvaporationCrop;       // Unused for final output files

    private ArrayList<Double> totalIrrigationRequired;          // Unused for final output files

    private ArrayList<Double> averageIrrigationRequired;
    private ArrayList<Double> twoin10IrrigationRequired;
    private ArrayList<Double> onein10IrrigationRequired;

    private ArrayList<Double> weightedAverageIrrRequired;
    private ArrayList<Double> weighted2In10IrrRequired;
    private ArrayList<Double> weighted1In10IrrRequired;

    @Getter @Setter private String soilName;
    @Getter @Setter private String soilKey;
    @Getter @Setter private double soilArea;
    @Getter @Setter private String soilSymbolNum;

    private SummaryReport() {
//        curMonth = 1;

        this.totalRainFall = new ArrayList<>();

        this.totalEvaporationPotential = new ArrayList<>();
        this.peakMonthlyEvaporationPotential = new ArrayList<>();

        this.totalEvaporationCrop = new ArrayList<>();
        this.peakMonthlyEvaporationCrop = new ArrayList<>();

        this.totalIrrigationRequired = new ArrayList<>();
        this.averageIrrigationRequired = new ArrayList<>();
        this.twoin10IrrigationRequired = new ArrayList<>();
        this.onein10IrrigationRequired = new ArrayList<>();

        this.weightedAverageIrrRequired = new ArrayList<>();
        this.weighted2In10IrrRequired = new ArrayList<>();
        this.weighted1In10IrrRequired = new ArrayList<>();

        for (int i = 0; i < 12; i++) {

            this.totalRainFall.add(0.0);

            this.totalEvaporationPotential.add(0.0);
            this.peakMonthlyEvaporationPotential.add(0.0);

            this.totalEvaporationCrop.add(0.0);
            this.peakMonthlyEvaporationCrop.add(0.0);

            this.totalIrrigationRequired.add(0.0);
            this.averageIrrigationRequired.add(0.0);
            this.twoin10IrrigationRequired.add(0.0);
            this.onein10IrrigationRequired.add(0.0);

            this.weightedAverageIrrRequired.add(0.0);
            this.weighted2In10IrrRequired.add(0.0);
            this.weighted1In10IrrRequired.add(0.0);
        }

    }

    public SummaryReport(String soilKey, String soilName, String soilSymbolNum, double soilArea) {
        this(soilKey, soilName, soilSymbolNum);
        this.soilArea = soilArea;
    }

    public SummaryReport(String soilKey, String soilName, String soilSymbolNum) {
        this();
        this.soilKey = soilKey;
        this.soilName = soilName;
        this.soilSymbolNum = soilSymbolNum;
    }

    public String getSoilAreaStr() {
        String ret = String.format("%6.3f", soilArea);
        if (ret.equals("0.000")) {
            return "< 0.001";
        } else {
            return ret;
        }
    }

//    public void reset() {
//        for (int i = 0; i < 12; i++) {
//
//            this.totalRainFall.add(0.0);
//            this.totalEvaporationPotential.add(0.0);
//            this.peakMonthlyEvaporationPotential.add(0.0);
//
//            this.totalEvaporationCrop.add(0.0);
//            this.peakMonthlyEvaporationCrop.add(0.0);
//
//            this.totalIrrigationRequired.add(0.0);
//            this.averageIrrigationRequired.add(0.0);
//            this.twoin10IrrigationRequired.add(0.0);
//            this.onein10IrrigationRequired.add(0.0);
//
//            this.weightedAverageIrrRequired.add(0.0);
//            this.weighted2In10IrrRequired.add(0.0);
//            this.weighted1In10IrrRequired.add(0.0);
//        }
//    }

//    public int getCurMonth() {
//        return curMonth;
//    }
//
//    public void setCurMonth(int curMonth) {
//        this.curMonth = curMonth;
//    }

    public Double getTotalRainFallByMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return totalRainFall.get(month - 1);
    }

    public void setTotalRainFall(int month, double rainFall) throws IllegalArgumentException {
        if (month > 12) {
            month = month - 12;
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.totalRainFall.set(month - 1, rainFall);
    }

    public Double getTotalEvaporationByMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.totalEvaporationPotential.get(month - 1);
    }

    public void setTotalEvaporation(int month, double evaporation) throws IllegalArgumentException {
        if (month > 12) {
            month = month - 12;
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.totalEvaporationPotential.set(month - 1, evaporation);
    }

    public Double getPeakEvaporationByMonth(int month) {
        return this.peakMonthlyEvaporationPotential.get(month - 1);
    }

    public void setPeakMonthlyEvaporation(int month, double evaporation) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.peakMonthlyEvaporationPotential.set(month - 1, Math.max(this.peakMonthlyEvaporationPotential.get(month - 1), evaporation));
    }

    public Double getEvaporationCropByMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.totalEvaporationCrop.get(month - 1);
    }

    public void setEvaporationCrop(int month, double evaporation) throws IllegalArgumentException {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.totalEvaporationCrop.set(month - 1, evaporation);
    }

    public Double getPeakEvaporationCropByMonth(int month) {
        return this.peakMonthlyEvaporationCrop.get(month - 1);
    }

    public void setPeakMonthlyEvaporationCrop(int month, double evaporation) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.peakMonthlyEvaporationCrop.set(month - 1, Math.max(this.peakMonthlyEvaporationCrop.get(month - 1), evaporation));
    }

    public Double getTotalIrrigationRequiredByMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.totalIrrigationRequired.get(month - 1);
    }

    public void addTotalIrrigationRequiredByMonth(int month, double irrigationRequired) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.totalIrrigationRequired.set(month - 1, this.totalIrrigationRequired.get(month - 1) + irrigationRequired);
    }

    public ArrayList<Double> getAverageIrrigationRequired() {
        return this.averageIrrigationRequired;
    }

    public Double getAverageIrrigationRequired(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.averageIrrigationRequired.get(month - 1);
    }

    public void setAverageIrrigationRequired(int month, double irrigationRequired) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.averageIrrigationRequired.set(month - 1, irrigationRequired);
    }

    public ArrayList<Double> getTwoin10IrrigationRequired() {
        return this.twoin10IrrigationRequired;
    }

    public Double getTwoin10IrrigationRequired(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.twoin10IrrigationRequired.get(month - 1);
    }

    public void setTwoin10IrrigationRequired(int month, double irrigationRequired) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.twoin10IrrigationRequired.set(month - 1, irrigationRequired);
    }

    public ArrayList<Double> getOnein10IrrigationRequired() {
        return this.onein10IrrigationRequired;
    }

    public Double getOnein10IrrigationRequired(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.onein10IrrigationRequired.get(month - 1);
    }

    public void setOnein10IrrigationRequired(int month, double irrigationRequired) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.onein10IrrigationRequired.set(month - 1, irrigationRequired);
    }

    public void setWeightedAvgIrrRequired(int month, double val) {
        this.weightedAverageIrrRequired.set(month - 1, weightedAverageIrrRequired.get(month - 1) + val);
    }

    public void setWeighted2In10IrrRequired(int month, double val) {
        this.weighted2In10IrrRequired.set(month - 1, weighted2In10IrrRequired.get(month - 1) + val);
    }

    public void setWeighted1In10IrrRequired(int month, double val) {
        this.weighted1In10IrrRequired.set(month - 1, weighted1In10IrrRequired.get(month - 1) + val);
    }

    public ArrayList<Double> getWeightedAvgIrrRequired() {
        return this.weightedAverageIrrRequired;
    }

    public ArrayList<Double> getWeighted2In10IrrRequired() {
        return this.weighted2In10IrrRequired;
    }

    public ArrayList<Double> getWeighted1In10IrrRequired() {
        return this.weighted1In10IrrRequired;
    }

    public double getWeightedAvgIrrRequired(int month) {
        return this.weightedAverageIrrRequired.get(month - 1);
    }

    public double getWeighted2In10IrrRequired(int month) {
        return this.weighted2In10IrrRequired.get(month - 1);
    }

    public double getWeighted1In10IrrRequired(int month) {
        return this.weighted1In10IrrRequired.get(month - 1);
    }

    /**
     * @return the totalTwoinTen
     */
    public double getTotalTwoinTen() {
        return totalTwoinTen;
    }

    /**
     * @param totalTwoinTen the totalTwoinTen to set
     */
    public void setTotalTwoinTen(double totalTwoinTen) {
        this.totalTwoinTen = totalTwoinTen;
    }

    /**
     * @return the totalOneinTen
     */
    public double getTotalOneinTen() {
        return totalOneinTen;
    }

    /**
     * @param totalOneinTen the totalOneinTen to set
     */
    public void setTotalOneinTen(double totalOneinTen) {
        this.totalOneinTen = totalOneinTen;
    }

    /**
     * @return the totalAvgIrr
     */
    public double getTotalAvgIrr() {
        return totalAvgIrr;
    }

    /**
     * @param totalAvgIrr the totalAvgIrr to set
     */
    public void setTotalAvgIrr(double totalAvgIrr) {
        this.totalAvgIrr = totalAvgIrr;
    }

    public void readJsonData(JSONObject data) {
        setTotalTwoinTen(data.getAsDouble("total_2in10"));
        setTotalOneinTen(data.getAsDouble("total_1in10"));
        setTotalAvgIrr(data.getAsDouble("total_avg_irr"));
    
        try {
            readJsonData(data.getArrAsDouble("total_rain_fall"),
                    this.getClass().getMethod("setTotalRainFall", int.class, double.class));
            readJsonData(data.getArrAsDouble("total_evaporation_potential"),
                    this.getClass().getMethod("setTotalEvaporation", int.class, double.class));
            readJsonData(data.getArrAsDouble("peak_monthly_evaporation_potential"),
                    this.getClass().getMethod("setPeakMonthlyEvaporation", int.class, double.class));
            readJsonData(data.getArrAsDouble("total_evaporation_crop"),
                    this.getClass().getMethod("setEvaporationCrop", int.class, double.class));
            readJsonData(data.getArrAsDouble("peak_monthly_evaporation_crop"),
                    this.getClass().getMethod("setPeakMonthlyEvaporationCrop", int.class, double.class));

            readJsonData(data.getArrAsDouble("total_irrigation_required"),
                    this.getClass().getMethod("addTotalIrrigationRequiredByMonth", int.class, double.class));

            readJsonData(data.getArrAsDouble("average_irrigation_required"),
                    this.getClass().getMethod("setAverageIrrigationRequired", int.class, double.class));
            readJsonData(data.getArrAsDouble("2in10_irrigation_required"),
                    this.getClass().getMethod("setTwoin10IrrigationRequired", int.class, double.class));
            readJsonData(data.getArrAsDouble("1in10_irrigation_required"),
                    this.getClass().getMethod("setOnein10IrrigationRequired", int.class, double.class));

            readJsonData(data.getArrAsDouble("weighted_average_irr_required"),
                    this.getClass().getMethod("setWeightedAvgIrrRequired", int.class, double.class));
            readJsonData(data.getArrAsDouble("weighted_2in10_irr_required"),
                    this.getClass().getMethod("setWeighted2In10IrrRequired", int.class, double.class));
            readJsonData(data.getArrAsDouble("weighted_1in10_irr_required"),
                    this.getClass().getMethod("setWeighted1In10IrrRequired", int.class, double.class));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.err);
        }
        
    }
    
    private void readJsonData(ArrayList<Double> arrFrom, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < arrFrom.size(); i++) {
            method.invoke(this, i + 1, arrFrom.get(i));
        }
    }
    
    public JSONObject toJsonData() {
        JSONObject data = new JSONObject();
        data.put("musym", soilSymbolNum);
        data.put("cokey", soilKey);
        data.put("soilName", soilName);
        data.put("compArea", soilArea);
        
        data.put("total_2in10", totalTwoinTen);
        data.put("total_1in10", totalOneinTen);
        data.put("total_avg_irr", totalAvgIrr);
        
        data.put("total_rain_fall", new JSONArray().putAll(totalRainFall));
        data.put("total_evaporation_potential", new JSONArray().putAll(totalEvaporationPotential));
        data.put("peak_monthly_evaporation_potential", new JSONArray().putAll(peakMonthlyEvaporationPotential));
        data.put("total_evaporation_crop", new JSONArray().putAll(totalEvaporationCrop));
        data.put("peak_monthly_evaporation_crop", new JSONArray().putAll(peakMonthlyEvaporationCrop));

        data.put("total_irrigation_required", new JSONArray().putAll(totalIrrigationRequired));

        data.put("average_irrigation_required", new JSONArray().putAll(averageIrrigationRequired));
        data.put("2in10_irrigation_required", new JSONArray().putAll(twoin10IrrigationRequired));
        data.put("1in10_irrigation_required", new JSONArray().putAll(onein10IrrigationRequired));

        data.put("weighted_average_irr_required", new JSONArray().putAll(weightedAverageIrrRequired));
        data.put("weighted_2in10_irr_required", new JSONArray().putAll(weighted2In10IrrRequired));
        data.put("weighted_1in10_irr_required", new JSONArray().putAll(weighted1In10IrrRequired));
        
        return data;
    }
}
