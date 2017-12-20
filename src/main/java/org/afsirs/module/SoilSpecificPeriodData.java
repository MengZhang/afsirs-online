/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afsirs.module;

/**
 *
 * @author rohit
 */
public class SoilSpecificPeriodData {
    
    private Double [] soilDataPoints;
    private String soilName;
    
    public Double [] getSoilDataPoints () {
        return soilDataPoints;
    }
    
    public String getSoilName () {
        return soilName;
    }
    
    public void setSoilDataPoints (Double [] soilDataPoints) {
        this.soilDataPoints = soilDataPoints;        
    }
    
    public void setSoilName (String soilName) {
        this.soilName = soilName;
    }
    
}
