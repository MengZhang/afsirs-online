package org.afsirs.module;

import java.util.Arrays;
import lombok.Data;

/**
 * The container for DECOEF method result
 *
 * @author Meng Zhang
 */
@Data
public final class DeCoefResult {
    
    private double[] DRZ = new double[365]; // Meng: Not related with any user input yet
    private double[] DRZI = new double[365];
    private double[] AWD = new double[365]; // Meng: Not related with any user input yet
    private int[] NF = new int[4]; // Meng: Not related with any user input yet
    private double[] RKC = new double[365]; // Meng: Not related with any user input yet
    
//    public DeCoefResult () {}
//    
//    public DeCoefResult(DeCoefResult copy) {
//        this();
//        reset(copy);
//    }
//    
//    public void reset(DeCoefResult copy) {
//        this.DRZ = Arrays.copyOf(copy.getDRZ(), DRZ.length);
//        this.DRZI = Arrays.copyOf(copy.getDRZI(), DRZI.length);
//        this.AWD = Arrays.copyOf(copy.getAWD(), AWD.length);
//        this.NF = Arrays.copyOf(copy.getNF(), NF.length);
//        this.RKC = Arrays.copyOf(copy.getRKC(), RKC.length);
//    }
}
