package org.afsirs.module;

import lombok.Data;

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
}
