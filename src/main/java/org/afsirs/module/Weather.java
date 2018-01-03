package org.afsirs.module;

import java.util.Arrays;
import lombok.Data;
import org.apache.log4j.Logger;

/**
 *
 * @author Meng Zhang
 */
@Data
public class Weather {
    
    private int NYR, startYear, endYear;
    private int[] JDAY = new int[365];
    
    private String ETLoc;
    private double[][] ETP = new double[64][365];
    
    private String RainLoc;
    private double[][] RAIN = new double[64][365];
    
    static Logger logger = Logger.getLogger(Weather.class.getName());
    public Weather(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.NYR = endYear - startYear + 1;
    }
    
    public void setETP(double[][] data, int startYear, int endYear) {
        int nyr = endYear - startYear + 1;
        if (startYear > this.startYear) {
            logger.warn("Start year is greater than available climate data! Use available data instead!");
            this.startYear = startYear;
        }
        for (int y = this.startYear - startYear, i = 0; y <= this.endYear - startYear && y < nyr && i < ETP.length; y++, i++) {
            ETP[i] = Arrays.copyOf(data[y], data[y].length);
        }
    }
    
    public void setRAIN(double[][] data, int startYear, int endYear) {
        int nyr = endYear - startYear + 1;
        if (startYear > this.startYear) {
            logger.warn("Start year is greater than available rain data! Use available data instead!");
            this.startYear = startYear;
        }
        for (int y = this.startYear - startYear, i = 0; y <= this.endYear - startYear && y < nyr && i < RAIN.length; y++, i++) {
            RAIN[i] = Arrays.copyOf(data[y], data[y].length);
        }
    }
}
