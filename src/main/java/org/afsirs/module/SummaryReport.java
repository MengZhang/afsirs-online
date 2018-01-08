package org.afsirs.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author rohit
 * @author Meng Zhang
 */
public abstract class SummaryReport {

    private int curMonth;
    private double totalTwoinTen;
    private double totalOneinTen;
    private double totalAvgIrr;
    private ArrayList<Double> totalRainFall;
    private ArrayList<Double> totalEvaporationPotential;

    private ArrayList<Double> peakMonthlyEvaporationPotential;

    private ArrayList<Double> totalEvaporationCrop;
    private ArrayList<Double> peakMonthlyEvaporationCrop;

    private ArrayList<Double> totalIrrigationRequired;

    private ArrayList<Double> averageIrrigationRequired;
    private ArrayList<Double> twoin10IrrigationRequired;
    private ArrayList<Double> onein10IrrigationRequired;

    private ArrayList<Double> weightedAverageIrrRequired;
    private ArrayList<Double> weighted2In10IrrRequired;
    private ArrayList<Double> weighted1In10IrrRequired;

    @Getter @Setter private String soilName;
    @Getter @Setter private String soilKey;
    @Getter @Setter private double soilArea;

    private SummaryReport() {
        curMonth = 1;

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
    
    public SummaryReport(String soilKey, String soilName, double soilArea) {
        this();
        this.soilKey = soilKey;
        this.soilName = soilName;
        this.soilArea = soilArea;
    }
    
    public SummaryReport(String soilKey, String soilName) {
        this();
        this.soilKey = soilKey;
        this.soilName = soilName;
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

    public int getCurMonth() {
        return curMonth;
    }

    public void setCurMonth(int curMonth) {
        this.curMonth = curMonth;
    }

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

    public Double getTwoin10IrrigationRequired(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.twoin10IrrigationRequired.get(month - 1);
    }

    public void setTwoin10IrrigationRequired(int month, Double irrigationRequired) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        this.twoin10IrrigationRequired.set(month - 1, irrigationRequired);
    }

    public Double getOnein10IrrigationRequired(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month " + month + " is not in the valid range. It should be 1-12.");
        }
        return this.onein10IrrigationRequired.get(month - 1);
    }

    public void setOnein10IrrigationRequired(int month, Double irrigationRequired) {

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

}
