package org.afsirs.module;

import java.util.Arrays;
import lombok.Data;

/**
 * The container for SW calculation result
 *
 * @author Meng Zhang
 */
@Data
public class SWResult {
    private double[] SWIRR = new double[365];
    private double[] SWMAX = new double[365];
    private double[] SWCIX = new double[365];
    private double[] SWCNX = new double[365];
    private double SWCI1, SWCN1;
    
    private double[] DRZ = new double[365]; // Meng: Not related with any user input yet
    private double[] DRZI = new double[365];
    private double[] AWD = new double[365]; // Meng: Not related with any user input yet
    private int[] NF = new int[4]; // Meng: Not related with any user input yet
    private double[] RKC = new double[365]; // Meng: Not related with any user input yet
    
    private double EXIR;
    private double[][] RAIN = new double[64][365];
    private double[][] ETP = new double[64][365];
    private double[][] IRR = new double[64][365];
    private double[][] RAIN_S = new double[64][365];
    private double[][] ETP_S = new double[64][365];
    private double[][] IRR_S = new double[64][365];
    
    private double[] SDR = new double[365];
    private double[] SET = new double[365];
    private double[] SETP = new double[365];
    private double[] SRAIN = new double[365];
    
    private double[] PDATM = new double[12];
    private double[] PDATBW = new double[26];
    private double[] PDATW = new double[52];
    
    public SWResult(UserInput input, DeCoefResult copy) {
        this.DRZ = Arrays.copyOf(copy.getDRZ(), DRZ.length);
        this.DRZI = Arrays.copyOf(copy.getDRZI(), DRZI.length);
        this.AWD = Arrays.copyOf(copy.getAWD(), AWD.length);
        this.NF = Arrays.copyOf(copy.getNF(), NF.length);
        this.RKC = Arrays.copyOf(copy.getRKC(), RKC.length);
    
        this.EXIR = input.getEXIR();
        this.RAIN = deepCopy(input.getRAIN());
        this.ETP = deepCopy(input.getETP());
    }
    
    private double[][] deepCopy(double[][] original) {
        double[][] ret = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            ret[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return ret;
    }
}
