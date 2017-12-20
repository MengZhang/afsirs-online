/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afsirs.module;

/**
 *
 * @author Rohit Kumar Malik
 */
public class Irrigation {
    private int code;
    private double eff; //Efficiency
    private double area; //Fraction of soil surface irrigated
    private double ex; //Fraction of ET extracted from the irrigated zone when water is avaialable in the nonirrigated zone
    private String sys; //System type
    
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getEff() {
        return eff;
    }

    public void setEff(double eff) {
        this.eff = eff;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getEx() {
        return ex;
    }

    public void setEx(double ex) {
        this.ex = ex;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }
    
    
}
