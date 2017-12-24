package org.afsirs.module;

import lombok.Data;

/**
 *
 * @author Rohit Kumar Malik
 * @author Meng Zhang
 */
@Data
public class Irrigation {
    private int code;
    private double eff; //Efficiency
    private double area; //Fraction of soil surface irrigated
    private double ex; //Fraction of ET extracted from the irrigated zone when water is avaialable in the nonirrigated zone
    private double dwt; //Water Table Depth
    private String sys; //System type
}
