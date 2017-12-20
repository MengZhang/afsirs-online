package org.afsirs.module;

import lombok.Data;

/**
 *
 * @author Meng Zhang
 */
@Data
public class PDAT {

    private String soilName = "";
    private Double[] PDATM = new Double[12];
    private Double[] PDATBW = new Double[12];
    private Double[] PDATW = new Double[12];

    //Double[] PDATBW = new Double[26];
    //Double[] PDATW = new Double[52];
}
