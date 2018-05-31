package org.afsirs.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import static org.afsirs.module.DateUtil.MDAY;
import static org.afsirs.module.DateUtil.NKC;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DEFINITIONS....
 * 
 * IR = IRRIGATION SYSTEM IDENTIFICATION CODE
 * IRCRFL = CROWN FLOOD IRRIGATION SYSTEM CODE
 * IRNSCY = CONTAINER NURSERY IRRIGATION SYSTEM CODE
 * IRRICE = RICE FLOOD IRRIGATION SYSTEM CODE
 * IRSEEP = SEEPAGE (SUBIRRIGATION) IRRIGATION SYSTEM CODE
 * ISIM = COUNTER FOR MULTIPLE SIMULATIONS
 * JDAY = CALENDAR DAY OF CROP IRRIGATION SEASON
 * J1SAVE = CALENDAR DAY OF FIRST DAY OF IRRIGATION SEASON FOR
 * PREVIOUS SIMULATION
 * JNSAVE = CALENDAR DAY OF LAST DAY OF IRRIGATION SEASON FOR
 * PREVIOUS SIMULATION
 * SITE = DESCRIPTION OF LOCATION OF THE PRODUCTION SYSTEM SIMULATED
 * 
 * @author rkmalik
 * @author Meng Zhang
 */
public class AFSIRSModule {

    static Logger LOG = LoggerFactory.getLogger(AFSIRSModule.class);

    // Initialization of constants
    public static final int IRSEEP = 6;
    public static final int IRCRFL = 7;
    public static final int IRNSCY = 4;
    public static final int IRRICE = 8;
    public static final int ICIT = 3;
    public static final int INSCY = 7;
    public static final int IRICE = 31;
    public static final int ICGP = 5; //Generic Crop
    public static final int ICGA = 17;
    public static final int IRNSYC = 4;

    public static final boolean defaultMode = true;

    //Irrigation parameters
    double[] EFF = new double[10];
    
    private static final String EOL = Messages.EOL;

    //BAL.for
    private static void calculateBalance(SoilSeriesSummaryReport report, UserInput input, SWResult swRet, BufferedWriter bwOutputFile) throws IOException {

        //Initialize parameters
        final int NYR = input.getNYR();
        final int IR = input.getIR();
        final int J1 = input.getJ1();
        final int JN = input.getJN();
        final double EPS = input.getEPS();
        final double ARZI = input.getARZI();
        final int NDAYS = input.getNDAYS();
        final int IDCODE = input.getIDCODE();
        final int ICODE = input.getICODE();
        final double FRIR = input.getFRIR();
        final double FIX = input.getFIX();
        final double IEFF = input.getIEFF();
        final int MO1 = input.getMO1();
        final int MON = input.getMON();
        int[] JDAY = input.getJDAY();

        double[][] RAIN = swRet.getRAIN();
        double[][] ETP = swRet.getETP();
        final double EXIR = swRet.getEXIR();
        double[] DRZI = swRet.getDRZI();
        double[] DRZ = swRet.getDRZ();
        double[] RKC = swRet.getRKC();
        int[] NF = swRet.getNF();

        final double SWCN1 = swRet.getSWCN1();
        final double SWCI1 = swRet.getSWCI1();
        double[] SWCIX = swRet.getSWCIX();
        double[] SWCNX = swRet.getSWCNX();
        double[] SWIRR = swRet.getSWIRR();
        double[] SWMAX = swRet.getSWMAX();
        double[][] IRR = swRet.getIRR();
//        double[] SDR = swRet.getSDR();
//        double[] SET = swRet.getSET();
//        double[] SETP = swRet.getSETP();
//        double[] SRAIN = swRet.getSRAIN();

        double SWCISV = 0.0, SWCNSV = 0.0, WCISAVE, WCNSAVE;
        int NYX = NYR;
        double REWAT = 1.0;
        if (input.getJ1SAVE() > input.getJNSAVE()) {
            NYX += 1;
        }
        double EXNR = 1.0 - EXIR;

        double[] KC = new double[365];

        //Iterate years
        for (int iy = 0; iy < NYR; iy++) {  //TODO : check if should use NYX instead of NYR
            //Zero parameter for current year
            double[] ET = new double[365];
            double[] SWCI = new double[365];
            double[] SWCN = new double[365];
            double[] ER = new double[365];
            double[] NIR = new double[365];
            double[] DR = new double[365];

            //For rice flood irrigation systems
            if (IR != IRRICE) {
                for (int j = J1 - 1; j < JN; j++) {
                    int j1 = j - 1;
                    REWAT = REWAT + 1.0;
                    double NRR;
                    double NR;
                    //Calculate number of days required for redistribution to field capacity
                    if (IR != IRSEEP && IR != IRCRFL) {
                        NRR = RAIN[iy][j] + 1;
                        NR = 2;
                        if (DRZI[j] < 12) {
                            NR = 1;
                        }
                        if (DRZI[j] > 24) {
                            NR = 3;
                        }
                        if (NRR > NR) {
                            NR = NRR;
                        }
                        if (NR > 5) {
                            NR = 5;
                        }
                    } else {
                        NR = 2;
                        if (RAIN[iy][j] > 0.50) {
                            NR = 2.0 * RAIN[iy][j] + 1;
                        }
                        if (NR > 7) {
                            NR = 7;
                        }
                    }

                    //Calculate crop ET from ETP and crop coefficient
                    KC[j] = RKC[j];
                    double RKCS = 0.00;
                    /*
                     Do not increase crop coefficient for rain if RKC = 0.0 (No crop being grown)
                     Modifies KC based on days since last rain or irrigation during growth stages 1 and 2
                     */
                    if (!input.isPerennialCrop() && KC[j] >= EPS && j < NF[1]) {
                        RKCS = 1.0 / Math.sqrt(REWAT);
                    }
                    if (KC[j] >= EPS) {
                        //Adjust KC for low versus high ETP days
                        KC[j] = KC[j] - 0.03 * (ETP[iy][j] - 0.20);
                        if (RKCS > KC[j]) {
                            KC[j] = RKCS;
                        }
                        //Do not reduce crop coefficient for rain if KC is already > 1.0
                        if (KC[j] <= 1.0) {
                            double RKCR = 1.00;
                            //Calculate ET at potential rate for the day that Rain occurs.
                            if (ETP[iy][j] > EPS) {
                                RKCR = RAIN[iy][j] / ETP[iy][j];
                            }
                            if (RKCR > 1.00) {
                                RKCR = 1.00;
                            }
                            if (RKCR > KC[j]) {
                                KC[j] = RKCR;
                            }
                        }
                        if (KC[j] > 1.25) {
                            KC[j] = 1.25;
                        }
                    }
                    ET[j] = KC[j] * ETP[iy][j];

                    //Update Soil water content in crop root zone for expanding root zone, rain and ET
                    if (j != J1 - 1 || (iy != 1 && NDAYS > 365)) {
                        double SWCEPS = SWCIX[j1] + EPS;
                        if (SWCIX[j] > SWCEPS) {
                            double DRZEPS = DRZ[j] - EPS;
                            if (DRZI[j] >= DRZEPS) {
                                SWCI[j] = SWCI[j1] + SWCIX[j] - SWCIX[j1];
                                SWCN[j] = SWCN[j1] + SWCNX[j] - SWCNX[j1];
                            } else {
                                if (SWCNX[j1] >= EPS) {
                                    double ADX = SWCN[j1] / SWCNX[j1] * (SWCIX[j] - SWCIX[j1]);
                                    SWCI[j] = SWCI[j1] + ADX;
                                    SWCN[j] = SWCN[j1] + SWMAX[j] - SWMAX[j1] - ADX;
                                }
                            }
                        } else {
                            SWCI[j] = SWCI[j1];
                            SWCN[j] = SWCN[j1];
                        }
                        WCISAVE = SWCI[j];
                        WCNSAVE = SWCN[j];
                        SWCI[j] = SWCI[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCN[j] + (1.0 - ARZI) * RAIN[iy][j];
                        if (SWCI[j] - SWCIX[j] >= EPS || SWCN[j] - SWCNX[j] >= EPS) {
                            SWCI[j] = SWCI[j] - EXIR * ET[j];
                            SWCN[j] = SWCN[j] - EXNR * ET[j];
                        } else if (SWCN[j] < EPS) {
                            SWCI[j] = SWCI[j] - ET[j];
                        } else {
                            double XX = 0.5 * SWCNX[j] + EPS;
                            double ETNMAX = EXNR * ET[j];
                            if (SWCN[j] > XX && XX > ETNMAX) {
                                SWCI[j] = SWCI[j] - EXIR * ET[j];
                                SWCN[j] = SWCN[j] - EXNR * ET[j];
                            } else {
                                double XET = SWCN[j] / XX;
                                if (XET > 0.5) {
                                    XET = 0.5;
                                }
                                double ETN = ETNMAX * XET;
                                SWCI[j] = SWCI[j] - (ET[j] - ETN);
                                SWCN[j] = SWCN[j] - ETN;
                                if (SWCN[j] <= 0.0) {
                                    SWCI[j] = SWCI[j] + SWCN[j];
                                    SWCN[j] = 0.0;
                                }
                            }
                        }
                    } else if (iy == 0 || NDAYS < 365) {
                        SWCI[j] = SWCI1 - EXIR * ET[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCN1 - EXNR * ET[j] + (1.0 - ARZI) * RAIN[iy][j];
                    } else if (NDAYS == 365) {
                        SWCI[j] = SWCISV - EXIR * ET[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCNSV - EXNR * ET[j] + (1.0 - ARZI) * RAIN[iy][j];
                        if (SWCN[j] <= 0.0) {
                            SWCI[j] = SWCI[j] + SWCN[j];
                            SWCN[j] = 0.0;
                        }
                    }

                    // Check for drainage from root zone
                    if (RAIN[iy][j] >= EPS && (SWCI[j] >= SWCIX[j] || SWCN[j] >= SWCNX[j]) && (SWCI[j] >= SWCIX[j] || SWCN[j] >= EPS)) {
                        double PERCI;
                        double RINCI = 0.0;
                        double PERCN;
                        double RINCN = 0.0;

                        //Calculate percolate
                        for (int JJ = 0; JJ < (int) NR; JJ++) {
                            int JDINC = JJ + j + 1;
                            if (JDINC >= JN) {
                                break;
                            }
                            RINCI = RINCI + RKC[JDINC] * EXIR * ETP[iy][JDINC];
                            RINCN = RINCN + RKC[JDINC] * EXNR * ETP[iy][JDINC];
                        }

                        //Calculate drainage from irrigated zone
                        PERCI = SWCI[j] - SWCIX[j];
                        if (PERCI < RINCI) {
                            RINCI = PERCI;
                        }
                        double DRI = PERCI - RINCI;
                        SWCI[j] = SWCIX[j] + RINCI;
                        if (SWCNX[j] > EPS) {
                            //Calculate drainage from non-irrigated zone
                            if (DRZI[j] < (DRZ[j] - EPS)) {
                                //When DRZ > DRZI
                                double SWCX = SWCN[j] + DRI;
                                PERCN = SWCN[j] - SWCNX[j] + DRI;
                                if (PERCN < RINCN) {
                                    RINCN = PERCN;
                                }
                                double SWCZ = SWCNX[j] + RINCN;
                                SWCN[j] = Math.min(SWCX, SWCZ);
                                DR[j] = SWCX - SWCN[j];
                            } else {
                                //When DRZ = DRZI
                                PERCN = SWCN[j] - SWCNX[j];
                                if (PERCN < RINCN) {
                                    RINCN = PERCN;
                                }
                                double DRN = PERCN - RINCN;
                                SWCN[j] = SWCNX[j] + RINCN;
                                DR[j] = DRI + DRN;
                            }
                        } else {
                            DR[j] = DRI;
                        }
                        //Calculate effective rainfall
                        ER[j] = RAIN[iy][j] - DR[j];
                        if (ER[j] < 0.0) {
                            //Correct for redistribution increment
                            double EXINC = -ER[j];
                            SWCI[j] = SWCI[j] + EXIR * EXINC;
                            SWCN[j] = SWCN[j] + EXNR * EXINC;
                            ER[j] = 0.0;
                        }
                    } else {
                        //Calculate effective rainfall when drainage = 0.0
                        DR[j] = 0.0;
                        ER[j] = RAIN[iy][j];
                    }

                    //Check for need to irrigate
                    if (SWCI[j] > SWIRR[j]) {
                        NIR[j] = 0.0;
                    } else if (IR == IRSEEP) {
                        //For seepage irrigaton systems
                        NIR[j] = 0.9 * SWCIX[j] - SWCI[j];
                        if (NIR[j] < 0.0) {
                            NIR[j] = 0.0;
                        }
                    } else if (IR == IRCRFL) {
                        //For crown flood irrigation systems
                        //Assume 3 days required to irrigate, and
                        // 3 days for drainage after irrigation
                        double ETINC = 0.0;
                        for (int jj = 0; j < 5; j++) {
                            int JDP = j + jj;
                            if (JDP > JN) {
                                break;
                            }
                            ETINC = ETINC + ETP[iy][JDP];
                        }
                        NIR[j] = SWCIX[j] - SWCI[j] + ETINC;
                    } else {
                        //Irrigate to restore firstSoil water content to field capacity
                        NIR[j] = SWCIX[j] - SWCI[j];
                        //Irrigate to only a fraction of field capacity
                        if (IDCODE == 2) {
                            NIR[j] = FRIR * SWCIX[j] - SWCI[j];
                        } //Irrigate a fixed irrigation amount
                        else if (IDCODE == 1) {
                            NIR[j] = FIX;
                        }
                    }

                    //Update Soil Water content for irrigation
                    SWCI[j] = SWCI[j] + NIR[j];
                    if (IR == IRNSYC) {
                        //For container nursery sprinkler irrigation system
                        SWCN[j] = SWCN[j] + (1.0 - ARZI) / ARZI * NIR[j];
                        if (SWCN[j] > SWCNX[j]) {
                            SWCN[j] = SWCNX[j];
                        }
                    }
                    //Calculate gross irrigation requirements
                    IRR[iy][j] = NIR[j] / IEFF;
                    if (RAIN[iy][j] > 0.1 || IRR[iy][j] > EPS) {
                        REWAT = 1.0;
                    }
                }

                //Save last day's firstSoil water contents to begin first day ofnext year for perennial crops
                SWCISV = SWCI[JN - 1];
                SWCNSV = SWCN[JN - 1];
            } else {
                //For rice flood irrigation
                for (int j = J1 - 1; j < JN; j++) {
                    int j1 = j - 1;
                    ET[j] = RKC[j] * ETP[iy][j];
                    if (j == J1 - 1) {
                        SWCI[j] = SWCI1;
                    } else if (j > J1 - 1) {
                        SWCI[j] = SWCI[j1];
                    }
                    SWCI[j] = SWCI[j] + RAIN[iy][j] - ET[j];
                    double XX = SWCI[j];
                    DR[j] = 0.0;
                    if (SWCI[j] > SWMAX[j]) {
                        SWCI[j] = SWMAX[j];
                        DR[j] = XX - SWMAX[j];
                    }
                    ER[j] = RAIN[iy][j] - DR[j];
                    NIR[j] = 0.0;
                    if (SWCI[j] < SWIRR[j]) {
                        NIR[j] = SWCIX[j] - SWCI[j];
                    }
                    SWCI[j] = SWCI[j] + NIR[j];
                    IRR[iy][j] = NIR[j] / IEFF;
                }
            }

            //Sum parameters for each day of the irrigation season
            for (int j = J1 - 1; j < JN; j++) {
                swRet.getSDR()[j] += DR[j];
                swRet.getSET()[j] += ET[j];
                //System.out.println("ETP "+iy+":"+j+":"+ETP[iy][j]+":"+RAIN[iy][j]);
                swRet.getSETP()[j] += ETP[iy][j];
                swRet.getSRAIN()[j] += RAIN[iy][j];
            }
            if (ICODE == 2) {
                bwOutputFile.append(EOL + "C   RAIN = TOTAL RAINFALL (INCHES)");
                bwOutputFile.append(EOL + "C   ET = EVAPOTRANSPIRATION (INCHES)");

                bwOutputFile.append(EOL + "       OUTPUT DAILY COMPONENTS OF SOIL WATER BUDGET FOR DEBUGGING" + EOL + EOL);
                bwOutputFile.append(String.format("                            YEAR =  %2d" + EOL, iy + 1));
                bwOutputFile.append(" CDY JDY  SWCN SWCI  ETP   KC  ET  RAIN  ER   DR     SWMX  SWIX SWIR  NIR  IRR" + EOL);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, 2007);

                for (int jd = J1 - 1; jd < JN; jd++) {
                    String str = String.format(" %3d %4d %6.3f %5.3f %4.3f %5.3f %4.3f %5.3f %5.3f %5.3f %6.3f %5.3f %5.3f %5.3f %5.3f" + EOL,
                            jd + 1, JDAY[jd], SWCN[jd], SWCI[jd], ETP[iy][jd], KC[jd], ET[jd], RAIN[iy][jd], ER[jd], DR[jd], SWMAX[jd], SWCIX[jd], SWIRR[jd], NIR[jd], IRR[iy][jd]);
                    bwOutputFile.append(str);

                    cal.set(Calendar.DAY_OF_YEAR, jd + 1);
                    int monthOfDay = cal.get(Calendar.MONTH);
                    monthOfDay++;

                    if (monthOfDay >= MO1 && monthOfDay <= MON) {
                        report.setPeakMonthlyEvaporationCrop(monthOfDay, ET[jd]);
                    }

                }
            }

        }

    }

    /*
     This subroutine calculates the LEAST SQUARE CURVE fit for a
     set of N linearly distributed data points to fit the equation
     Y = SLOPE * X + INTCPT
     */
    private static LSQResult LSQ(double[] X, double[] Y, int N) {
        //Calculate mean XBAR and YBAR
        double SUMX = 0.0;
        double SUMY = 0.0;
        for (int i = 0; i < N; i++) {
            SUMX += X[i];
            SUMY += Y[i];
        }
        double XBAR = SUMX / N;
        double YBAR = SUMY / N;

        //Calculate sums of deviations squared and cross products
        double SX2 = 0.0;
        double SY2 = 0.0;
        double SXY = 0.0;

        for (int i = 0; i < N; i++) {
            SX2 += Math.pow(X[i] - XBAR, 2);
            SY2 += Math.pow(Y[i] - YBAR, 2);
            SXY += (X[i] - XBAR) * (Y[i] - YBAR);
        }

        //Calculate intercept and slope
        double slope = SXY / SX2;
        double intercept = YBAR - slope * XBAR;

        //Calculate sample correlation coefficient, R
        //and Coefficient of determination, RSQ
        double R = SXY / Math.sqrt(SX2 * SY2);
        double RSQ = Math.pow(R, 2);

        return new LSQResult(slope, intercept, RSQ, R);
    }

    /*
     This subroutine calculates the 50%, 80%, 90% and 95%
     probability values of IRR based on an extreme value
     TYPE I rpobability distribution
     */
    private static PROBResult PROBX(double[] AI, int NYR, double[] PROB, double XMEAN, double EPS) {
        double[] W = new double[64];
        double[] ALIRR = new double[64];

        PROBResult result = new PROBResult();

        EPS = 0.000001;
        int RNYR = NYR;

        //Return -1 if database is not adequate to calculate indicated extreme value
        result.setAll(-1.0, -1.0);

        //Return 0 for all values if mean is 0.0
        if (XMEAN <= 0.0001) {
            result.setAll(0.0, 1.0);
            return result;
        }

        //Eliminate 0 datae values from analysis
        int IPOS = 0;
        for (int i = 0; i < NYR; i++) {
            if (AI[i] < EPS) {
                break;
            }
            IPOS = i;
        }
        //Calculate fraction of zero values
        result.X00 = (double) (NYR - IPOS - 1) / RNYR;
        double XPOS = 1.0 - result.X00;

        //Calculating pllotting positions for PROBs and IRRs
        result.RSQ = -9.99;
        if (IPOS < 2) {
            result.setAll(-99.0, -99.0);
            return result;
        }

        for (int i = 0; i <= IPOS; i++) {
            W[i] = Math.log10(-Math.log10(PROB[i]));
            ALIRR[i] = Math.log10(AI[i]);
        }

        //Call subroutine LSQ to FIT Straight line to transformed data by the method
        //of Least Squares
        LSQResult l = LSQ(W, ALIRR, IPOS + 1);
        result.RSQ = l.RSQ;
        if (l.RSQ < 0.500) {
            result.setAll(-99.0, result.RSQ);
            return result;
        }
        if ((IPOS + 1) >= NYR) {
            // Calculate extreme values of IRR from regression equation when no values are 0.0
            result.X50 = Math.pow(10.0, (-0.52139 * l.slope + l.intercept));
            result.X80 = Math.pow(10.0, (-0.15554 * l.slope + l.intercept));
            result.X90 = Math.pow(10.0, l.intercept);
            result.X95 = Math.pow(10.0, (0.11429 * l.slope + l.intercept));
            return result;
        }

        //Calculate extreme values of IRR when some values = 0.0
        double C50 = (XPOS - 0.5) / XPOS;
        if (C50 <= EPS) {
            result.X50 = 0.0;
        } else {
            C50 = Math.log10(-Math.log10(1.0 - C50));
            result.X50 = Math.pow(10.0, (C50 * l.slope + l.intercept));
        }

        double C80 = (XPOS - 0.2) / XPOS;
        if (C80 <= EPS) {
            result.X80 = 0.0;
        } else {
            C80 = Math.log10(-Math.log10(1.0 - C80));
            result.X80 = Math.pow(10.0, (C80 * l.slope + l.intercept));
        }

        double C90 = (XPOS - 0.1) / XPOS;
        if (C90 <= EPS) {
            result.X90 = 0.0;
        } else {
            C90 = Math.log10(-Math.log10(1.0 - C90));
            result.X90 = Math.pow(10.0, (C90 * l.slope + l.intercept));
        }

        double C95 = (XPOS - 0.05) / XPOS;
        if (C95 <= EPS) {
            result.X95 = 0.0;
        } else {
            C95 = Math.log10(-Math.log10(1.0 - C95));
            result.X95 = Math.pow(10.0, (C95 * l.slope + l.intercept));
        }

        return result;
    }

    private static SWResult SW(UserInput input, Soil soil, BufferedWriter bwOutputFile) throws IOException {
        
        SWResult ret = new SWResult(input, DECOEF(input));
        double[] SWIRR = ret.getSWIRR();
        double[] SWMAX = ret.getSWMAX();
        double[] SWCIX = ret.getSWCIX();
        double[] SWCNX = ret.getSWCNX();
        double[] DRZI = ret.getDRZI();
        double[] DRZ = ret.getDRZ();
        double[] RKC = ret.getRKC();
        double[] AWD = ret.getAWD();
        int[] NF = ret.getNF();

        final double DWT = input.getDWT();
        final int J1 = input.getJ1();
        final int JN = input.getJN();
        final int IR = input.getIR();
        final double ARZI = input.getARZI();
        final int ICROP = input.getICROP();
        final double EPS = input.getEPS();
        final double AKC3 = input.getAKC3();
        int[] JDAY = input.getJDAY();

//        String SSERIESNAME = soil.getSERIESNAME();
//        String SOILSMAPUNITCODE = soil.getSOILSERIESKEY();
//        String SNAME = soil.getName();
//        String SOILCOMPCODE = soil.getCOMPKEY();
//        String[] TXT = soil.getTXT();
        double[] DU = soil.getDU();
        double[] WCL = soil.getWCL();
        double[] WCU = soil.getWCU();
        double[] WC = soil.getWC();
        int NL = soil.getNL();

        double SWX = 0.0;
        double SWN;

        double[] DL = new double[NL];

        int IW0 = 0;
        int IW1 = 0;
        int IW2 = 0;
        int IW3 = 0;

        DL[0] = 0.0;
        for (int k = 1;
                k < NL;
                k++) {
            DL[k] = DU[k - 1];
        }

        //Calculate maximum firstSoil water-holding capacities
        //For perennial crops
        if (input.isPerennialCrop()) {
            //Check for high water table limiting irrigated root zone

            if (DRZI[J1 - 1] >= DWT) {
                DRZI[J1 - 1] = DWT;
            }
            int IS = 0;
            boolean flag = false;
            for (int I = 0; I < NL; I++) {
                IS = I;
                if (DRZI[J1 - 1] < DU[I]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                for (int JD = J1 - 1; JD < JN; JD++) {
                    DRZI[JD] = DU[NL - 1];
                }
            }

            //Calculate available firstSoil water in irrigated root zone
            int IS1 = IS - 1;
            double SWI = 0.0;
            if (IS != 0) {
                for (int I = 0; I <= IS1; I++) {
                    SWI += (DU[I] - DL[I]) * WC[I];
                }
            }
            SWI += (DRZI[J1 - 1] - DL[IS]) * WC[IS];
            // For Total Root Zone
            // Check for high water table limiting root eexpansion
            if (DRZ[J1 - 1] >= DWT) {
                DRZ[J1 - 1] = DWT;
                if (DWT < 2.0 * DRZI[J1 - 1]) {
                    DRZ[J1 - 1] = 2.0 * DRZI[J1 - 1];
                }
            }
            flag = false;
            for (int I = 0; I < NL; I++) {
                IS = I;
                if (DRZ[J1 - 1] < DU[I]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                DRZ[J1 - 1] = DU[NL - 1];
                if (DRZ[J1 - 1] < 2.0 * DRZI[J1 - 1]) {
                    DRZ[J1 - 1] = 2.0 * DRZI[J1 - 1];
                }
                for (int JD = J1 - 1; JD < JN; JD++) {
                    DRZ[JD] = DRZ[J1 - 1];
                }
            }

            IS1 = IS - 1;
            double WCX;
            if (IS != 0) {
                for (int I = 0; I <= IS1; I++) {
                    WCX = WC[I];
                    if (IR == IRCRFL) {
                        WCX = 0.4;
                    }
                    SWX += (DU[I] - DL[I]) * WCX;
                }
            }
            WCX = WC[IS];
            if (IR == IRCRFL) {
                WCX = 0.4;
            }
            SWX += (DRZ[J1 - 1] - DL[IS]) * WCX;
            if (DRZ[J1 - 1] > DWT && WCX < 0.4) {
                SWX += (DRZ[J1 - 1] - DWT) * (0.4 - WCX);
            }

            //Correct for irrigation of only a fraction of the root zone
            SWI = ARZI * SWI;
            SWN = SWX - SWI;

            //Correct for nonirrigated zone in container nurseries
            if (ICROP == INSCY) {
                SWN = 0.20;
            }
            //Correct for bedded surface in crown flood citrus irrigation
            if (IR == IRCRFL) {
                SWI *= 0.667;
            }
            SWX = SWI + SWN;
            if (SWN < EPS) {
                ret.setEXIR(1.00);
            }

            //Assign daily firstSoil water capacity data
            for (int JD = J1 - 1; JD < JN; JD++) {
                SWMAX[JD] = SWX;
                SWCIX[JD] = SWI;
                SWCNX[JD] = SWN;

            }

        } else {
            //For annual crops

            if (IR == IRRICE) {
                double DRINC = -1.0;
                while (DRINC < 0.00 || DRINC > 3.00) {
                    String str = "Enter Rice Flood Stroage Depth(Inches) : ";
                    if (DRINC != -1.0) {
                        str = "Enter Rice Flood Stroage Depth(Inches) [Value between 0.0 and 3.0]: ";
                    }
                    String result = JOptionPane.showInputDialog(null, str);
                    try {
                        DRINC = Double.parseDouble(result);
                    } catch (Exception e) {
                        DRINC = -2.0;
                    }
                }

                for (int JD = J1 - 1; JD < JN; JD++) {
                    SWCIX[JD] = 3.0;
                    SWMAX[JD] = SWCIX[JD] + DRINC;
                    SWCNX[JD] = 0.00;
                }
            } else {
                for (int JD = J1 - 1; JD < JN; JD++) {
                    if (DRZI[JD] >= DWT) {
                        DRZI[JD] = DWT;
                    }
                }

                //Check for firstSoil depth limiting irrigated root zone
                int IS = 0;

                for (int JD = J1 - 1; JD < JN; JD++) {
                    boolean flag = false;
                    for (int I = 0; I < NL; I++) {
                        IS = I;
                        if (DRZI[JD] < DU[I]) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        DRZI[JD] = DU[NL - 1];
                    }

                    //Calculate irrigated zone available firstSoil water for each day from root zone expansion
                    double SWI = 0.0;
                    if (IS != 0) {
                        int IS1 = IS - 1;
                        for (int I = 0; I <= IS1; I++) {
                            SWI += (DU[I] - DL[I]) * WC[I];
                        }
                    }
                    SWI += (DRZI[JD] - DL[IS]) * WC[IS];

                    //Correct for  irrigation of only a fraction of the root zone
                    SWCIX[JD] = ARZI * SWI;
                }

                //Correct crop water use coefficients for growth stages 1 and 2
                //Based on Soil properties and stage 1 crop growth
                //For Growth Stage 1
                double AKKC = 0.8 * Math.sqrt(WC[0] + 0.05);
                int J5 = J1 + 4;
                for (int JD = J1 - 1; JD < NF[0]; JD++) {
                    RKC[JD] = AKKC;
                    if (JD >= J5) {
                        RKC[JD] = AKKC + 0.12 * (JD - J5 + 1) / (NF[0] - J5);
                    }
                }

                //For Growth Stage 2
                int NFP = NF[0];
                AKKC = AKKC + 0.12;
                for (int JD = NFP; JD < NF[1]; JD++) {
                    int RJD = JD;
                    RKC[JD] = AKKC + (RJD - NF[0] + 1) / (double) (NF[1] - NF[0]) * (AKC3 - AKKC);
                    if (RKC[JD] > AKC3) {
                        RKC[JD] = AKC3;
                    }
                }

                //For total root zone
                //Check for high water table limiting total root zone
                for (int JD = J1 - 1; JD < JN; JD++) {
                    if (DRZ[JD] >= DWT) {
                        DRZ[JD] = DWT;
                        if (DRZ[JD] < 2.0 * DRZI[JD]) {
                            DRZ[JD] = 2.0 * DRZI[JD];
                        }
                    }
                }

                for (int JD = J1 - 1; JD < JN; JD++) {
                    //Check for firstSoil depth limiting total root zone
                    IS = 0;
                    boolean flag = false;
                    for (int I = 0; I < NL; I++) {
                        IS = I;
                        if (DRZ[JD] < DU[I]) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        DRZ[JD] = DU[NL - 1];
                        if (DRZ[JD] < 2.0 * DRZI[JD]) {
                            DRZ[JD] = 2.0 * DRZI[JD];
                        }
                    }
                    SWMAX[JD] = 0.0;
                    if (IS != 0) {
                        for (int I = 0; I < IS; I++) {
                            SWMAX[JD] += (DU[I] - DL[I]) * WC[I];
                        }
                    }
                    SWMAX[JD] += (DRZ[JD] - DL[IS]) * WC[IS];
                    if (DRZ[JD] > DWT && WC[IS] < 0.4) {
                        SWMAX[JD] += (DRZ[JD] - DWT) * (0.4 * WC[IS]);
                    }
                    SWCNX[JD] = SWMAX[JD] - SWCIX[JD];
                }
            }
        }

        //Calculate starting firstSoil water content as 0.9*Field Capacity
        ret.setSWCI1(0.9 * SWCIX[J1 - 1]);
        ret.setSWCN1(0.9 * SWCNX[J1 - 1]);

        if (ret.getSWCN1() < EPS) {
            ret.setEXIR(1.00);
        }

        //Calculate firstSoil water content at which irrigation will be scheduled
        for (int JD = J1 - 1;
                JD < JN;
                JD++) {
            SWIRR[JD] = (1.0 - AWD[JD]) * SWCIX[JD];
        }

        //Print all soil data
        String txt = "";
//        double[] WCL = soil.getWCL();
//        double[] WCU = soil.getWCU();
        for (String t : soil.getTXT()) {
            if (t != null) {
                txt += t + " ";
            }
        }
        bwOutputFile.append(EOL + EOL + "     SOIL :  SERIES = " + soil.getSERIESNAME() + "         TEXTURE = " + txt + "           AREA(Fraction ) = " + soil.getSoilTypeArea() + EOL);
        bwOutputFile.append("             TYPE = " + soil.getSNAME() + EOL + EOL);
        bwOutputFile.append(EOL + "               SOIL LAYER DEPTHS (INCHES) AND WATER CONTENTS" + EOL);
        String str = "";
        str += "                   lDepth(I)        WCON(Min)    WCON(Max)\r\n";
        for (int i = 0; i < soil.getNL(); i++) {
            str += String.format("                %2d%8.3f%18.3f%12.3f" + EOL, i + 1, DU[i], Math.round(WCL[i] * 100.0) / 100.0, Math.round(WCU[i] * 100.0) / 100.0);
        }
        bwOutputFile.append(str);

        bwOutputFile.append(EOL + "                DEPTH TO WATER TABLE ENTERED =  " + DWT / 12.0 + " FEET");
        bwOutputFile.append(EOL + "                WATER HOLDING CAPACITY = " + input.getWATERHOLDINGCAPACITY() + EOL + EOL);
        if (input.getICODE() >= 1) {
            bwOutputFile.append(EOL + "     OUTPUT PARAMETERS - ROOT DEPTHS, KCs, AND SOIL WATER CONTENTS" + EOL);
            bwOutputFile.append("       CDAY JDAY   DRZ    DRZI   RKC   SWMAX  SWCIX  SWCNX  SWIRR" + EOL + "       ");
            for (int i = J1 - 1; i < JN; i++) {
                String row = String.format("%4d %4d %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f", (i + 1), JDAY[i], DRZ[i], DRZI[i], RKC[i], SWMAX[i], SWCIX[i], SWCNX[i], SWIRR[i]);
                bwOutputFile.append(row + EOL + "       ");
            }
        }

//        ret.setSWCIX(SWCIX);
//        ret.setSWCNX(SWCNX);
//        ret.setSWIRR(SWIRR);
//        ret.setSWMAX(SWMAX);
//        ret.setAWD(AWD);
//        ret.setDRZ(DRZ);
//        ret.setDRZI(DRZI);
//        ret.setNF(NF);
//        ret.setRKC(RKC);
        return ret;
    }

    protected static DeCoefResult DECOEF(UserInput input) {
        DeCoefResult ret = new DeCoefResult();
        double[] DRZI = ret.getDRZI();
        double[] DRZ = ret.getDRZ();
        double[] AWD = ret.getAWD();
        double[] RKC = ret.getRKC();

        final int J1 = input.getJ1();
        final int JN = input.getJN();

        if (input.isPerennialCrop()) {
            //Calculate daily root depths and allowable firstSoil water depletions
            for (int i = 0; i < 365; i++) {
                DRZI[i] = input.getDRZIRR();
                DRZ[i] = input.getDRZTOT();
            }

            //Calculate daily root depths & allowable firstSoil water depletions
            int jd = -1;
            for (int imo = 0; imo < 12; imo++) {
                for (int j = 0; j < MDAY[imo]; j++) {
                    jd++;
                    AWD[jd] = input.getALDP()[imo];
                }
            }

            //Calculate daily crop water use coefficients
            //For first half of January
            double rj = -1;
            double rjdif = 14;
            double[] AKC = input.getAKC();
            double rkdif = 0.5 * (AKC[0] - AKC[11]);
            double rk2 = 0.5 * (AKC[0] + AKC[11]);
            for (jd = 0; jd < 14; jd++) {
                rj++;
                RKC[jd] = rk2 + rj / rjdif * rkdif;
            }

            //January 15 through December 15
            for (int jmo = 0; jmo < 11; jmo++) {
                int jmo1 = jmo + 1;
                int nkm1 = NKC[jmo1] - 1;
                rjdif = NKC[jmo1] - NKC[jmo];
                rkdif = AKC[jmo1] - AKC[jmo];
                rj = -1;
                for (jd = NKC[jmo] - 1; jd < nkm1; jd++) {
                    rj++;
                    RKC[jd] = AKC[jmo] + rj / rjdif * rkdif;
                }
            }

            //Decemeber 16 throgh december 31
            rj = -1;
            rjdif = 16;
            rkdif = 0.5 * (AKC[0] - AKC[11]);
            for (jd = 348; jd < 365; jd++) {
                rj++;
                RKC[jd] = AKC[11] + rj / rjdif * rkdif;
            }

            double[] AWTMP = new double[365];
            double[] RKTMP = new double[365];
            int JNSAVE = input.getJNSAVE();
            int J1SAVE = input.getJ1SAVE();
            if (J1SAVE >= JNSAVE) {
                //Rearrange data for crop year that extends beyond 1 calendar year
                int j = -1;
                for (jd = JNSAVE - 1; jd < 365; jd++) {
                    j++;
                    AWTMP[j] = AWD[jd];
                    RKTMP[j] = RKC[jd];
                }
                for (jd = 0; jd < JNSAVE; jd++) {
                    j++;
                    AWTMP[j] = AWD[jd];
                    RKTMP[j] = RKC[jd];
                }
                for (jd = J1 - 1; jd < JN; jd++) {
                    AWD[jd] = AWTMP[jd];
                    RKC[jd] = RKTMP[jd];
                }

            }
        } else {
            //Calculate number of days in each growth stage
            int[] NF = ret.getNF();
            double ff = input.getEPS();
            double[] F = input.getF();
            for (int i = 0; i < 4; i++) {
                ff = ff + F[i];
                NF[i] = (int) (J1 + ff * (JN - J1));
            }

            //Calculate daily root zone depths and depths irrigated
            double AKKC = 0.4;
            int IR = input.getIR();
            if (IR == IRSEEP) {
                AKKC = 1.0;
            }
            if (IR == IRRICE) {
                AKKC = 1.0;
            }

            double DZN = input.getDZN();
            double DZX = input.getDZX();
            double AKC3 = input.getAKC3();
            double AKC4 = input.getAKC4();
            for (int jd = J1 - 1; jd < JN; jd++) {
                int rjd = jd + 1;
                DRZI[jd] = DZN + (rjd - NF[0]) * (DZX - DZN) / (double) (NF[1] - NF[0]);
                if (jd < NF[0]) {
                    DRZI[jd] = DZN;
                }
                if (jd >= NF[1]) {
                    DRZI[jd] = DZX;
                }
                if (IR == IRRICE) {
                    DRZ[jd] = DRZI[jd];
                } else {
                    DRZ[jd] = 2.0 * DRZI[jd];
                }

                //Calculate daily crop coefficients
                RKC[jd] = AKKC;
                if (jd >= NF[0]) {
                    RKC[jd] = AKKC + (rjd - NF[0]) / (double) (NF[1] - NF[0]) * (AKC3 - AKKC);
                }
                if (jd >= NF[1]) {
                    RKC[jd] = AKC3;
                }
                if (jd >= NF[2]) {
                    RKC[jd] = AKC3 - (rjd - NF[2]) / (double) (JN - NF[2]) * (AKC3 - AKC4);
                }

                //Assign daily values of allowable water depletion
                double[] ALD = input.getALD();
                AWD[jd] = ALD[1];
                if (jd >= NF[3]) {
                    AWD[jd] = ALD[4];
                } else if (jd >= NF[2]) {
                    AWD[jd] = ALD[3];
                } else if (jd >= NF[1]) {
                    AWD[jd] = ALD[2];
                }

            }

//            ret.setNF(NF);
        }

//        ret.setDRZI(DRZI);
        ret.setDRZ(DRZ);
//        ret.setAWD(AWD);
//        ret.setRKC(RKC);
        return ret;
    }

    /*
     This subroutine orders the irrigation requirements data base from
     largest to smallest and it calculates the plotting positions for
     the IRR data
     */
    private static STATResult STATX(double[] X, int N) {

        STATResult result = new STATResult(N);

        int NP = N + 1;
        int NM = N - 1;
        double DATP = N + 1.0;

        //Order data from largest to smallest
        for (int i = 0; i < NM; i++) {
            int ip = i + 1;
            for (int j = ip; j < N; j++) {
                if (X[j] >= X[i]) {
                    double temp = X[i];
                    X[i] = X[j];
                    X[j] = temp;
                }
            }
            result.PROB[i] = (double) (i + 1) / DATP;
        }
        result.PROB[N - 1] = (double) N / DATP;

        //Calculate Mean
        double XSUM = 0.0;
        for (int i = 0; i < N; i++) {
            XSUM += X[i];
        }
        result.XMEAN = XSUM / N;

        //Compute variance, standard deviation, and XCV
        double VSUM = 0.0;
        for (int i = 0; i < N; i++) {
            VSUM += Math.pow(X[i] - result.XMEAN, 2);
        }
        result.XVAR = VSUM / (N - 1);
        result.XSDEV = 0.0;
        if (result.XVAR > 0.0) {
            result.XSDEV = Math.sqrt(result.XVAR);
        }
        result.XCV = 0.0;
        if (result.XMEAN > 0.0001) {
            result.XCV = result.XSDEV / result.XMEAN;
        }

        //Define Maximum and MMinimum values
        result.XMAX = X[0];
        result.XMIN = X[N - 1];

        //Determine the median
        int NT = N / 2;
        int NNT = NT * 2;
        result.XMED = X[NT];
        if (NNT == N) {
            result.XMED = 0.5 * (X[NT - 1] + X[NT]);
        }

        return result;
    }

    private static void SUMX(SoilSeriesSummaryReport report, UserInput input, SWResult swRet, Soil soil, SimResult simRet, BufferedWriter bwOutputFile) throws IOException {

        final int NYR = input.getNYR();
        final int JN = input.getJN();
        final int J1 = input.getJ1();
        final int JNSAVE = input.getJNSAVE();
        final int J1SAVE = input.getJ1SAVE();
        final int ICODE = input.getICODE();
        final boolean isNet = input.isNetCalc();
        final double IEFF = input.getIEFF();
        final double EPS = input.getEPS();
        final int MO1 = input.getMO1();
        final int MON = input.getMON();

        double[][] ETP = swRet.getETP();
        double[][] RAIN = swRet.getRAIN();
        double[][] IRR = swRet.getIRR();
        double[] SDR = swRet.getSDR();
        double[] SET = swRet.getSET();
        double[] SETP = swRet.getSETP();
        double[] SRAIN = swRet.getSRAIN();

        double[][] RAIN_S = swRet.getRAIN_S();
        double[][] ETP_S = swRet.getETP_S();
        double[][] IRR_S = swRet.getIRR_S();
//        double[] PDATBW = swRet.getPDATBW();
//        double[] PDATM = swRet.getPDATM();
//        double[] PDATW = swRet.getPDATW();

        double[][] AETP = new double[64][52];
        double[][] ARAIN = new double[64][52];
        double[][] AIRR = new double[64][52];
        double[] TET = new double[52];
        double[] TETP = new double[52];
        double[] TDR = new double[52];
        double[] TRAIN = new double[52];
        double[] AI = new double[NYR];
        double[] PROB = new double[NYR];

        int IS = 0;
        for (int IY = 0; IY < NYR; IY++) {
            AETP[IY][0] = 0.0;
            ARAIN[IY][0] = 0.0;
            AIRR[IY][0] = 0.0;
            for (int JD = J1 - 1; JD < JN; JD++) {
                AETP[IY][0] += ETP[IY][JD];
                ARAIN[IY][0] += RAIN[IY][JD];
                AIRR[IY][0] += IRR[IY][JD];
            }
        }

        TET[0] = 0.0;
        TETP[0] = 0.0;
        TDR[0] = 0.0;
        TRAIN[0] = 0.0;
        for (int JD = J1 - 1; JD < JN; JD++) {
            TET[0] += SET[JD];
            TETP[0] += SETP[JD];
            TDR[0] += SDR[JD];
            TRAIN[0] += SRAIN[JD];
        }
        TET[0] /= NYR;
        TETP[0] /= NYR;
        TDR[0] /= NYR;
        TRAIN[0] /= NYR;

        if (isNet) {
            bwOutputFile.append(EOL + "          SEASONAL OR ANNUAL NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
        } else {
            bwOutputFile.append(EOL + "          SEASONAL OR ANNUAL GROSS IRRIGATION REQUIREMENT (INCHES)" + EOL);
        }
        bwOutputFile.append("          --------------------------------------------------------" + EOL);

        //writeOutput(EOL + EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON" + EOL);
        if (ICODE >= 1) {
            bwOutputFile.append("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
            bwOutputFile.append("                  YEAR    ETP(INCHES)    RAIN(INCHES)  IRR.RQD.(INCHES)" + EOL);
            for (int i = 0; i < NYR; i++) {
                String row = String.format("                 %4d%-16.3f%8.3f%8.3f" + EOL, i + 1, AETP[i][0], ARAIN[i][0], AIRR[i][0]);
                bwOutputFile.append(row);
                AI[i] = AIRR[i][0];

            }
        } else {
            for (int i = 0; i < NYR; i++) {
                AI[i] = AIRR[i][0];
            }
        }
        //For yearly data in output.txt for each soil
        bwOutputFile.append(EOL + "   MEAN  MED.  CV  XMAX  XMIN ZERO  RSQ  50%  80%  90% 95% RAIN  ETP  ET   DR" + EOL);

        //displayArray(AI);
        STATResult statResult = STATX(AI, NYR);
        //displayArray(AI);
        PROBResult probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN, EPS);
        //displayArray(AI);
        double yearlyTwoinTen;
        double yearlyOneinTen;
        double XIRR = TET[0] / IEFF;
        probResult.X50 = Math.min(probResult.X50, XIRR);
        probResult.X80 = Math.min(probResult.X80, XIRR);
        probResult.X90 = Math.min(probResult.X90, XIRR);
        probResult.X95 = Math.min(probResult.X95, XIRR);
        yearlyTwoinTen = probResult.X80;
        yearlyOneinTen = probResult.X90;
        String str = String.format(" %6.3f %6.3f %5.3f %6.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f",
                statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[0], TETP[0], TET[0], TDR[0]);

        bwOutputFile.append(str + EOL);

        if (J1SAVE > JNSAVE) {
            for (int i = 0; i < NYR; i++) {
                int NY = NYR - i - 1;
                int NY1 = NY + 1;
                int JJ = 365 - J1SAVE;

                for (int JD = 0; JD < JNSAVE; JD++) {
                    int J = JJ + JD;
                    ETP_S[NY1][JD] = ETP[NY][J];
                    RAIN_S[NY1][JD] = RAIN[NY][J];
                    IRR_S[NY1][JD] = IRR[NY][J];
                }

                int J = 0;
                for (JJ = J1SAVE - 1; JJ < 365; JJ++) {
                    J++;
                    ETP_S[NY][365 - J] = ETP[NY][365 - JJ];
                    RAIN_S[NY][365 - J] = RAIN[NY][365 - JJ];
                    IRR_S[NY][365 - J] = IRR[NY][365 - JJ];
                }
            }

            for (int JD = 0; JD < JNSAVE; JD++) {
                ETP_S[0][JD] = ETP[NYR + 1][JD];
                RAIN_S[0][JD] = RAIN[NYR + 1][JD];
                IRR_S[0][JD] = IRR[NYR + 1][JD];
            }

            if (J1SAVE - JNSAVE >= 2) {
                for (int IY = 0; IY < NYR; IY++) {
                    for (int JD = JNSAVE; JD < J1SAVE; JD++) {
                        ETP_S[IY][JD] = 0.0;
                        RAIN_S[IY][JD] = 0.0;
                        IRR_S[IY][JD] = 0.0;
                    }
                }
            }
        } else {
            ETP_S = ETP;
            RAIN_S = RAIN;
            IRR_S = IRR;
        }
        if (isNet) {
            bwOutputFile.append(EOL + "                MONTHLY NET IRRIGATION REQUIREMENTS (INCHES)" + EOL);
        } else {
            bwOutputFile.append(EOL + "                MONTHLY GROSS IRRIGATION REQUIREMENTS (INCHES)" + EOL);
        }

        bwOutputFile.append("                -------------------------------------------" + EOL);

        PDAT pdat = new PDAT();

        int NX = -1;
        for (int imo = 0; imo < 12; imo++) {
            int NN = NX + 1;
            NX = NN + MDAY[imo] - 1;
            for (int iy = 0; iy < NYR; iy++) {
                AETP[iy][imo] = 0.0;
                ARAIN[iy][imo] = 0.0;
                AIRR[iy][imo] = 0.0;
                for (int JD = NN; JD <= NX; JD++) {
                    AETP[iy][imo] += ETP_S[iy][JD];
                    ARAIN[iy][imo] += RAIN_S[iy][JD];
                    AIRR[iy][imo] += IRR_S[iy][JD];
                }
            }
            TET[imo] = 0.0;
            TETP[imo] = 0.0;
            TDR[imo] = 0.0;
            TRAIN[imo] = 0.0;

            for (int JD = NN; JD <= NX; JD++) {
                TET[imo] += SET[JD];
                TETP[imo] += SETP[JD];
                TDR[imo] += SDR[JD];
                TRAIN[imo] += SRAIN[JD];
            }
            TET[imo] /= NYR;
            TETP[imo] /= NYR;
            TDR[imo] /= NYR;
            TRAIN[imo] /= NYR;

            double AISUM = 0.0;
            for (int iy = 0; iy < NYR; iy++) {
                AISUM += AIRR[iy][imo];
                AI[iy] = AIRR[iy][imo];
            }
            if (ICODE >= 1 || (ICODE < 1 && imo == 0)) {
                bwOutputFile.append(EOL + " MO  MEAN  MED.    CV  XMAX  XMIN  ZERO   RSQ   50%   80%   90%   95%  RAIN   ETP    ET    DR" + EOL);
            }

            //System.out.println ("---------------Month : " + (imo+1)+"------------------");
            statResult = STATX(AI, NYR);
            probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN, EPS);
            XIRR = TET[imo] / IEFF;
            probResult.X50 = Math.min(probResult.X50, XIRR);
            probResult.X80 = Math.min(probResult.X80, XIRR);
            probResult.X90 = Math.min(probResult.X90, XIRR);
            probResult.X95 = Math.min(probResult.X95, XIRR);

            str = String.format(" %2d %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f %5.3f", (imo + 1),
                    statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                    probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[imo], TETP[imo], TET[imo], TDR[imo]);

            bwOutputFile.append(str + EOL);

            /* summaryReport.reset ();
             summaryReport.setTotalRainFall(imo+1, TRAIN[imo]);
             summaryReport.setTotalEvaporation(imo+1, TETP[imo]);
             summaryReport.setEvaporationCrop(imo+1, TET[imo]);
             summaryReport.addTotalIrrigationRequiredByMonth(imo+1, 0.0);
             summaryReport.setAverageIrrigationRequired(imo+1, probResult.X50);
             summaryReport.setTwoin10IrrigationRequired(imo+1, probResult.X80);
             summaryReport.setOnein10IrrigationRequired(imo+1, probResult.X90);
             summaryList.set(reportCounter,summaryReport);*/
            //System.out.println("imo::TRAIN::statResult.XMEAN"+(imo+1)+":"+TRAIN[imo]+":"+statResult.XMEAN);
//            int startMonth = 11;
//            report.reset();
            report.setTotalRainFall(imo + 1, TRAIN[imo]);
            report.setTotalEvaporation(imo + 1, TETP[imo]);
            report.addTotalIrrigationRequiredByMonth(imo + 1, 0.0);
            report.setAverageIrrigationRequired(imo + 1, statResult.XMEAN);
            report.setTwoin10IrrigationRequired(imo + 1, probResult.X80);
            report.setOnein10IrrigationRequired(imo + 1, probResult.X90);
            report.setEvaporationCrop(imo + 1, TET[imo]);
            report.setTotalTwoinTen(yearlyTwoinTen);
            report.setTotalOneinTen(yearlyOneinTen);
            //pdat.PDATM[imo] = statResult.XMEAN;

            if (probResult.X50 >= 0) {
                pdat.getPDATM()[imo] = probResult.X50;
            } else {
                pdat.getPDATM()[imo] = 0.0;
            }

            if (probResult.X80 >= 0) {
                pdat.getPDATBW()[imo] = probResult.X80;
            } else {
                pdat.getPDATBW()[imo] = 0.0;
            }

            if (probResult.X90 >= 0) {
                pdat.getPDATW()[imo] = probResult.X90;
            } else {
                pdat.getPDATW()[imo] = 0.0;
            }

            swRet.getPDATM()[imo] = statResult.XMEAN;
            if (ICODE >= 1) {
                bwOutputFile.append(EOL + "                            MONTH = " + (imo + 1) + EOL);
                bwOutputFile.append("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                bwOutputFile.append("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                for (int i = 0; i < NYR; i++) {
                    String row = String.format("                 %4d %8.3f %8.3f %8.3f" + EOL, i + 1, AETP[i][imo], ARAIN[i][imo], AIRR[i][imo]);
                    bwOutputFile.append(row);
                    if (imo + 1 >= MO1 && imo + 1 <= MON) {
                        //summaryReport.setPeakMonthlyEvaporation(imo+1, AETP[i][imo]);
                        report.setPeakMonthlyEvaporation(imo + 1, AETP[i][imo]);
                    }
                    //summaryReport.addTotalIrrigationRequiredByMonth(imo+1, AIRR[i][imo]);
                    report.addTotalIrrigationRequiredByMonth(imo + 1, AIRR[i][imo]);

                }
            }

        }
        if (ICODE >= 0) {
            bwOutputFile.append(EOL + EOL + "                2-WEEK NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
            bwOutputFile.append("                ------------------------------------------");
            NX = -1;
            for (int i2w = 0; i2w < 26; i2w++) {
                int NN = NX + 1;
                NX = NN + 13;
                for (int iy = 0; iy < NYR; iy++) {
                    AETP[iy][i2w] = 0.0;
                    ARAIN[iy][i2w] = 0.0;
                    AIRR[iy][i2w] = 0.0;
                    for (int JD = NN; JD <= NX; JD++) {
                        AETP[iy][i2w] += ETP_S[iy][JD];
                        ARAIN[iy][i2w] += RAIN_S[iy][JD];
                        AIRR[iy][i2w] += IRR_S[iy][JD];
                    }
                }
                TET[i2w] = 0.0;
                TETP[i2w] = 0.0;
                TDR[i2w] = 0.0;
                TRAIN[i2w] = 0.0;

                for (int JD = NN; JD <= NX; JD++) {
                    TET[i2w] += SET[JD];
                    TETP[i2w] += SETP[JD];
                    TDR[i2w] += SDR[JD];
                    TRAIN[i2w] += SRAIN[JD];
                }
                TET[i2w] /= NYR;
                TETP[i2w] /= NYR;
                TDR[i2w] /= NYR;
                TRAIN[i2w] /= NYR;

                double AISUM = 0.0;
                for (int iy = 0; iy < NYR; iy++) {
                    AISUM += AIRR[iy][i2w];
                    AI[iy] = AIRR[iy][i2w];
                }
                if (ICODE >= 1 || (ICODE < 1 && i2w == 0)) {
                    bwOutputFile.append(EOL + " I2W  MEAN  MED.  CV XMAX XMIN ZERO  RSQ  50%  80%  90%  95% RAIN  ETP   ET   DR" + EOL);
                }
                statResult = STATX(AI, NYR);
                probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN, EPS);
                XIRR = TET[i2w] / IEFF;
                probResult.X50 = Math.min(probResult.X50, XIRR);
                probResult.X80 = Math.min(probResult.X80, XIRR);
                probResult.X90 = Math.min(probResult.X90, XIRR);
                probResult.X95 = Math.min(probResult.X95, XIRR);
                str = String.format(" %2d%5.3f%5.3f%5.3f%6.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f", (i2w + 1),
                        statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                        probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[i2w], TETP[i2w], TET[i2w], TDR[i2w]);
                bwOutputFile.append(str + EOL);
                swRet.getPDATBW()[i2w] = statResult.XMEAN;
                //pdat.PDATBW[i2w] = statResult.XMEAN;
                if (ICODE >= 1) {
                    bwOutputFile.append(EOL + "                       2-WEEK PERIOD = " + (i2w + 1) + EOL);
                    bwOutputFile.append("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                    bwOutputFile.append("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                    for (int i = 0; i < NYR; i++) {
                        String row = String.format("                 %4d%8.3f%8.3f%8.3f" + EOL, i + 1, AETP[i][i2w], ARAIN[i][i2w], AIRR[i][i2w]);
                        bwOutputFile.append(row);
                    }
                }
            }
            if (isNet) {
                bwOutputFile.append(EOL + EOL + "                WEEKLY NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
            } else {
                bwOutputFile.append(EOL + EOL + "                WEEKLY GROSS IRRIGATION REQUIREMENT (INCHES)" + EOL);
            }
            bwOutputFile.append("                ------------------------------------------");
            NX = -1;
            for (int i7 = 0; i7 < 52; i7++) {
                int NN = NX + 1;
                NX = NN + 6;
                for (int iy = 0; iy < NYR; iy++) {
                    AETP[iy][i7] = 0.0;
                    ARAIN[iy][i7] = 0.0;
                    AIRR[iy][i7] = 0.0;
                    for (int JD = NN; JD <= NX; JD++) {
                        AETP[iy][i7] += ETP_S[iy][JD];
                        ARAIN[iy][i7] += RAIN_S[iy][JD];
                        AIRR[iy][i7] += IRR_S[iy][JD];
                    }
                }
                TET[i7] = 0.0;
                TETP[i7] = 0.0;
                TDR[i7] = 0.0;
                TRAIN[i7] = 0.0;

                for (int JD = NN; JD <= NX; JD++) {
                    TET[i7] += SET[JD];
                    TETP[i7] += SETP[JD];
                    TDR[i7] += SDR[JD];
                    TRAIN[i7] += SRAIN[JD];
                }
                TET[i7] /= NYR;
                TETP[i7] /= NYR;
                TDR[i7] /= NYR;
                TRAIN[i7] /= NYR;

                double AISUM = 0.0;
                for (int iy = 0; iy < NYR; iy++) {
                    AISUM += AIRR[iy][i7];
                    AI[iy] = AIRR[iy][i7];
                }
                if (ICODE >= 1 || (ICODE < 1 && i7 == 0)) {
                    bwOutputFile.append(EOL + " IWK  MEAN  MED.  CV XMAX XMIN ZERO  RSQ  50%  80%  90%  95% RAIN  ETP   ET   DR" + EOL);
                }
                statResult = STATX(AI, NYR);
                probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN, EPS);
                XIRR = TET[i7] / IEFF;
                probResult.X50 = Math.min(probResult.X50, XIRR);
                probResult.X80 = Math.min(probResult.X80, XIRR);
                probResult.X90 = Math.min(probResult.X90, XIRR);
                probResult.X95 = Math.min(probResult.X95, XIRR);
                str = String.format(" %2d%5.3f%5.3f%5.3f%6.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f%5.3f", (i7 + 1),
                        statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                        probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[i7], TETP[i7], TET[i7], TDR[i7]);
                bwOutputFile.append(str + EOL);
                swRet.getPDATW()[i7] = statResult.XMEAN;
                //pdat.PDATW[i7] = statResult.XMEAN;
                if (ICODE >= 1) {
                    bwOutputFile.append(EOL + "                       WEEKLY PERIOD = " + (i7 + 1) + EOL);
                    bwOutputFile.append("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                    bwOutputFile.append("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                    for (int i = 0; i < NYR; i++) {
                        String row = String.format("                 %4d%8.3f%8.3f%8.3f" + EOL, i + 1, AETP[i][i7], ARAIN[i][i7], AIRR[i][i7]);
                        bwOutputFile.append(row);
                    }
                }
            }
        }
        pdat.setSoilName(soil.getSNAME());
        simRet.getAllSoilInfo().add(pdat);
        simRet.setRAIN(RAIN);
    }

    protected static void initOutputFile(BufferedWriter bw, UserInput input) {
        try {
            String str1 = EOL;
            for (int i = 0; i < 70; i++) {
                str1 += '*';
            }
            String newStr = "                          AGRICULTURAL" + EOL
                    + "                          FIELD" + EOL
                    + "                          SCALE" + EOL
                    + "                          IRRIGATION" + EOL
                    + "                          REQUIREMENTS" + EOL
                    + "                          SIMULATION" + EOL
                    + EOL
                    + "                             MODEL" + EOL
                    + EOL
                    + EOL
                    + "              AFSIRS MODEL: INTERACTIVE VERSION " + Messages.getVersion() + EOL
                    + EOL
                    + EOL
                    + "           THIS MODEL SIMULATES IRRIGATION REQUIREMENTS" + EOL
                    + "         FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS." + EOL
                    + EOL
                    + "      PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS" + EOL
                    + "        ARE CALCULATED USING HISTORICAL WEATHER DATA BASES" + EOL
                    + "                   FOR NINE FLORIDA LOCATIONS." + EOL;

            bw.append(str1 + EOL + EOL);
            bw.append(newStr);
            bw.append(str1 + EOL + EOL);
            newStr = "          INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN" + EOL
                    + "                 IN THE AFSIRS MODEL USER'S GUIDE." + EOL
                    + "" + EOL
                    + "       DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS" + EOL
                    + "    AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL." + EOL;

            bw.append(newStr);
            bw.append(str1 + EOL + EOL);

            newStr = "              AFSIRS MODEL: INTERACTIVE VERSION " + Messages.getVersion() + EOL
                    + EOL
                    + EOL
                    + "           THIS MODEL SIMULATES IRRIGATION REQUIREMENTS" + EOL
                    + "         FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS." + EOL;
            bw.append(newStr);
            bw.append(str1 + EOL + EOL);
//            bw.close();

            DeCoefResult deCoefRet = DECOEF(input);
            int MONTH = input.getMONTH();
            int IIDAY = input.getIIDAY();
            int IYEAR = input.getIYEAR();
            int startYear = input.getStartYear();
            int endYear = input.getEndYear();
            String CTYPE = input.getCropName();
            int MO1 = input.getMO1();
            int MON = input.getMON();
            int DAY1 = input.getDAY1();
            int DAYN = input.getDAYN();
            String CLIMATELOC = input.getCLIMATELOC();
            String RAINFALLLOC = input.getRAINFALLLOC();
            int NYR = input.getNYR();
            int NDAYS = input.getNDAYS();

            bw.append("PERMIT ID = " + input.getSITE() + "     MAP NAME = " + input.getUNIT() + "     OWNER NAME = " + input.getOWNER() + "     DATE = " + MONTH + "-" + IIDAY + "-" + IYEAR + "" + EOL);
            bw.append(EOL + "                      CROP TYPE = " + CTYPE + EOL);
            bw.append(EOL + " IRRIGATION SEASON = " + MO1 + "-" + DAY1 + " TO " + MON + "-" + DAYN + "                LENGTH = " + NDAYS + " DAYS" + EOL);
            bw.append(EOL + " ET DATA BASE: LOCATION = " + CLIMATELOC + " LENGTH = " + NYR + " YEARS" + "(" + startYear + "-" + endYear + ")" + EOL);
            bw.append(EOL + " RAINFALL DATA BASE: LOCATION = " + RAINFALLLOC + " LENGTH = " + NYR + " YEARS" + "(" + startYear + "-" + endYear + ")" + EOL);

            double FIX = input.getFIX();
            double PIR = input.getPIR();
            switch (input.getIDCODE()) {
                case 0:
                    bw.append("         NORMAL IRRIGATION: SOIL WILL BE IRRIGATED TO FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                    break;
                case 1:
                    bw.append("         FIXED DEPTH IRRIGATION: A FIXED (CONSTANT) DEPTH OF WATER WILL BE APPLIED AT EACH IRRIGATION" + EOL);
                    bw.append("         DEPTH OF WATER TO APPLY PER IRRIGATION = " + FIX + EOL);
                    break;
                case 2:
                    bw.append("         DEFICIT IRRIGATION: THE SOIL WILL BE IRRIGATED TO A FRACTION OF FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                    bw.append("         PERCENT OF FIELD CAPACITY ENTERED FOR DEFICIT IRRIGATION" + PIR + EOL);
                    break;
                default:
                    bw.append(EOL);
            }

            String IRNAME = input.getIRNAME();
            double IEFF = input.getIEFF();
            double ARZI = input.getARZI();
            double EXIR = input.getEXIR();
            double HGT = input.getHGT();
            bw.append(EOL + " IRRIGATION SYSTEM : TYPE = " + IRNAME + EOL);
            bw.append("                     DESIGN APPLICATION EFFICIENCY = " + (int) (100 * IEFF) + " %" + EOL);
            bw.append("                     FRACTION OF SOIL SURFACE IRRIGATED = " + (int) (100 * ARZI) + " %" + EOL);
            bw.append("                     FRACTION EXTRACTED FROM IRRIGATED ZONE = " + EXIR + EOL);
            if (input.getIR() == IRCRFL) {
                bw.append("           HEIGHT OF CROWN FLOOD IRRIGATION SYSTEM BEDS = " + HGT + " FT" + EOL);
            }

            int J1 = input.getJ1();
            int JN = input.getJN();
            int[] JDAY = input.getJDAY();
            int[] NF = deCoefRet.getNF();
            double[] DRZI = deCoefRet.getDRZI();
            double[] DRZ = deCoefRet.getDRZ();
            double[] RKC = deCoefRet.getRKC();
            double[] AWD = deCoefRet.getAWD();
            if (input.isPerennialCrop()) {
                double DRZIRR = input.getDRZIRR();
                double DRZTOT = input.getDRZTOT();
                double[] AKC = input.getAKC();
                double[] ALDP = input.getALDP();
                bw.append(EOL + "                      CROP TYPE = " + CTYPE + "(PERENNIAL)" + EOL);
                bw.append(EOL + "               ROOT ZONE DEPTH IRRIGATED (INCHES) = " + DRZIRR);
                bw.append(EOL + "               TOTAL CROP ROOT ZONE DEPTH (INCHES)= " + DRZTOT + EOL);
                bw.append(EOL + "                                MONTH" + EOL + "         ");

                for (int i = 0; i < 12; i++) {
                    bw.append(String.format("%5d", (i + 1)));
                }
                bw.append(EOL + "     KC= ");
                for (int i = 0; i < 12; i++) {
                    bw.append(String.format(" %.3f", AKC[i]));
                }
                bw.append(EOL + "   ALDP= ");
                for (int i = 0; i < 12; i++) {
                    bw.append(String.format(" %.3f", ALDP[i]));
                }

            } else {
                double DZN = input.getDZN();
                double DZX = input.getDZX();
                double AKC3 = input.getAKC3();
                double AKC4 = input.getAKC4();
                double[] F = input.getF();
                double[] ALD = input.getALD();
                bw.append(EOL + "                      CROP TYPE = " + CTYPE + "(ANNUAL)");
                bw.append(EOL + "    DZN   DZX  AKC3  AKC4    F1    F2    F3    F4  ALD1  ALD2  ALD3  ALD4" + EOL);
                bw.append(String.format("    %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f\r\n", DZN, DZX,
                        AKC3, AKC4, F[0], F[1], F[2], F[3], ALD[0], ALD[1], ALD[2], ALD[3]));
                if (input.getICODE() == 2) {
                    double ff = input.getEPS();
                    for (int i = 0; i < 4; i++) {
                        String row = EOL + "          I,FF,NF(I),J1,JN = ";
                        ff += F[i];
                        bw.append(String.format("%s %5d%7.3f%5d%5d%5d", row, (i + 1), ff, NF[i], J1, JN));
                    }
                    bw.append(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
                    bw.append(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
                    bw.append(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");

                    bw.append(EOL + EOL + "   CDAY JDAY NF1  NF2  NF3  DRZI DRZ  RKC ALD1 ALD2 ALD3 ALD4   AWD" + EOL);
                    for (int i = J1 - 1; i < JN; i++) {
                        bw.append(String.format(" %5d%5d%5d%5d%5d %3.3f %3.3f %.3f %.3f %.3f %.3f %.3f  %.2f\r\n", i + 1, JDAY[i], NF[0], NF[1], NF[2], DRZI[i], DRZ[i], RKC[i], ALD[0], ALD[1], ALD[2], ALD[3], AWD[i]));
                    }
                }
            }

            if (input.getICODE() >= 1) {
                bw.append(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
                bw.append(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
                bw.append(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");
                bw.append(EOL + " DAILY ROOT ZONE DEPTHS, CROP COEFFICIENTS, & ALLOWABLE WATER DEPLETIONS" + EOL);
                bw.append("               CDAY JDAY  DRZI(JD) DRZ(JD) RKC(JD) AWD(JD)" + EOL);
                for (int i = J1 - 1; i < JN; i++) {
                    String row = String.format("               %3d %4d %9.3f %7.3f %7.3f %7.3f", (i + 1), JDAY[i], DRZI[i], DRZ[i], RKC[i], AWD[i]);
                    bw.append(row + EOL);
                }
            }

        //Don'tIn Delete the below commented code. This is very important for reference
        /*SW();
             String txt = "";
             for (String tIn : TXT) {
             if (tIn != null) {
             txt += tIn + " ";
             }
             }
             writeOutput(EOL + EOL + "     SOIL :  SERIES = " + SNAME + "         TEXTURE = " + txt + EOL + EOL);
             writeOutput(EOL + "               SOIL LAYER DEPTHS (INCHES) AND WATER CONTENTS" + EOL);
             String str = "";
             str += "                   Depth(I)        WCON(Min)    WCON(Max)\r\n";
             for (int i = 0; i < NL; i++) {
             str += String.format("                %2d%8.1f%18.2f%12.2f" + EOL, i + 1, DU[i], Math.round(WCL[i] * 100.0) / 100.0, Math.round(WCU[i] * 100.0) / 100.0);
             }
             writeOutput(str);
        
             writeOutput(EOL + "                DEPTH TO WATER TABLE ENTERED =  " + DWT / 12.0 + " FEET" + EOL + EOL);
        
             if (ICODE >= 1) {
             writeOutput(EOL + "     OUTPUT PARAMETERS - ROOT DEPTHS, KCs, AND SOIL WATER CONTENTS" + EOL);
             writeOutput("       CDAY JDAY   DRZ    DRZI   RKC   SWMAX  SWCIX  SWCNX  SWIRR" + EOL + "       ");
             for (int i = J1 - 1; i < JN; i++) {
             String row = String.format("%4d %4d %6.1f %6.1f %6.3f %6.2f %6.2f %6.2f %6.2f", (i + 1), JDAY[i], DRZ[i], DRZI[i], RKC[i], SWMAX[i], SWCIX[i], SWCNX[i], SWIRR[i]);
             writeOutput(row + EOL + "       ");
             }
             }
             calculateBalance();
             SUMX();*/
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }
    
    public static SimResult run(UserInput input) {

//        saveInputData(outputDir, input);
        SimResult ret = new SimResult();
        ret.setOutFile(new File(input.getOutFile()));
        ret.setTotalArea(input.getMapArea());

        //to replace total area by planted area -- Hiranava Das
        if (ret.getTotalArea() == 0.0) {
            ret.setTotalArea(input.getPlantedAcres());
        }
//        try {
        try (BufferedWriter bwOutputFile = new BufferedWriter(new FileWriter(ret.getOutFile(), false))) {
            

            initOutputFile(bwOutputFile, input);

            ArrayList<Soil> soils = input.getSoils();
            double totalArea = 0.0;

            for (Soil s : soils) {
                totalArea += s.getSoilTypeArea();
            }
            if (totalArea == 0) {
                totalArea = input.getPlantedAcres();
            }
            ret.setTotalArea(totalArea);
            ret.setPlantedArea(input.getPlantedAcres());
            //05 Sep 2016: H Das: Need to sort here
            //Hiranava Das:16 Sep 2015:New function call to sort the output before printing
//            boolean isSorted = false;
            for (Soil soil : soils) {
                if (soil.getNL() > 0) {
                    ret.addSoilTypeSummaryReport(new SoilTypeSummaryReport(soil));
                }
            }
            
            for (Soil soil : soils) {
                
                if (soil.getNL() <= 0) {
                    continue;
                }
                SWResult swRet = SW(input, soil, bwOutputFile);
                SoilSeriesSummaryReport report = ret.getSoilSeriesSummaryReport(soil);
                calculateBalance(report, input, swRet, bwOutputFile);
                SUMX(report, input, swRet, soil, ret, bwOutputFile);
                storeTotalIrr(report, soil, ret);
            }
            
            ArrayList<SoilSeriesSummaryReport> reports = ret.getSummaryList();
            for (SoilSeriesSummaryReport report : reports) {
                infoInInches(report, ret);
            }
            
            bwOutputFile.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace(System.err);
        } finally {
            ret.setTotalMonth(ret.getAllSoilInfo().get(0).getPDATM().length);
            return ret;
        }
    }
    
        public static SimResult run(UserInput input, int soilIdx) {

//        saveInputData(outputDir, input);
        SimResult ret = new SimResult();
        ret.setOutFile(new File(input.getOutFile()));
        ret.setTotalArea(input.getMapArea());

        //to replace total area by planted area -- Hiranava Das
        if (ret.getTotalArea() == 0.0) {
            ret.setTotalArea(input.getPlantedAcres());
        }
//        try {
        try (BufferedWriter bwOutputFile = new BufferedWriter(new FileWriter(ret.getOutFile(), false))) {
            

            initOutputFile(bwOutputFile, input);

            ArrayList<Soil> soils = input.getSoils();
            double totalArea = 0.0;

            for (Soil s : soils) {
                totalArea += s.getSoilTypeArea();
            }
            if (totalArea == 0) {
                totalArea = input.getPlantedAcres();
            }
            ret.setTotalArea(totalArea);
            ret.setPlantedArea(input.getPlantedAcres());
            //05 Sep 2016: H Das: Need to sort here
            //Hiranava Das:16 Sep 2015:New function call to sort the output before printing
//            boolean isSorted = false;
//            for (Soil soil : soils) {
//                ret.addSoilTypeSummaryReport(new SoilTypeSummaryReport(soil));
//            }
            
            Soil soil = soils.get(soilIdx);
            SWResult swRet = SW(input, soil, bwOutputFile);
            SoilSeriesSummaryReport report = ret.getSoilSeriesSummaryReport(soil);
            calculateBalance(report, input, swRet, bwOutputFile);
            SUMX(report, input, swRet, soil, ret, bwOutputFile);
            storeTotalIrr(report, soil, ret);
            
            infoInInches(report, ret);
            
            bwOutputFile.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace(System.err);
        } finally {
            ret.setTotalMonth(ret.getAllSoilInfo().get(0).getPDATM().length);
            return ret;
        }
    }

    //Hiranava Das: 16 Sep 2016: New function call to sort the output
    private static void storeTotalIrr(SoilSeriesSummaryReport report, Soil soil, SimResult ret) throws DocumentException {

        double areaSum = ret.getTotalArea();

        double totalVal = 0.0;
        //System.out.print(SNAME);
        for (int i = 1; i <= 12; i++) {

            double val = report.getAverageIrrigationRequired(i);
            if (val >= 0) {
                double wIrr = (val * soil.getSoilTypeArea()) / areaSum;
//                report.setWeightedAvgIrrRequired(i, wIrr); // TODO check if we need to recover this
                totalVal += val;

            }
        }
//        SNAME = soil.getName();
//        soil.setTotalAvgIrrReq(totalVal);
        ret.getSoilSeriesSummaryReport(soil).setTotalAvgIrr(totalVal);

    }

    private static void infoInInches(SoilSeriesSummaryReport summaryReport, SimResult ret) throws DocumentException {
        for (SoilTypeSummaryReport report : summaryReport.getSoilTypeSummaryReportList()) {
            infoInInches(report, ret);
        }
    }
    
    private static void infoInInches(SoilTypeSummaryReport summaryReport, SimResult ret) throws DocumentException {
        double totalVal;
        SoilSeriesSummaryReport soilSeriesReport = ret.getSoilSeriesSummaryReport(summaryReport);

//        double [] soilArea = getSoilArea();
//        double areaSum=0.0;
//        
//        for (double a: soilArea){
//            areaSum+=a;
//        }
        /**
         * *********Peak Evaporation Details************
         */
        LOG.debug("Soil Name :" + summaryReport.getSoilName());
        LOG.debug("Soil Area :" + summaryReport.getSoilAreaStr());
        LOG.debug("Total Area :" + ret.getTotalArea());
        LOG.debug("****Mean Irr Required ******");

        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getAverageIrrigationRequired(i);
            LOG.debug("Average Irrigation Required :" + val + " for month no " + i);
            double wIrr = (val * summaryReport.getSoilArea()) / ret.getTotalArea();
            if (val >= 0) {
                LOG.debug("Weighted Irrigation :" + wIrr + " for month no " + i);
                soilSeriesReport.setWeightedAvgIrrRequired(i, wIrr);
                totalVal += val;
            }

        }
        soilSeriesReport.setTotalAvgIrr(totalVal);
        double totalAvgIrrVal = totalVal;
        LOG.debug("Total Avg  Irrigation value " + totalAvgIrrVal);
        /**
         * *********2-in-10 Irrigation Required************
         */
        LOG.debug("**** 2-in-10 Irr Req ******");
        totalVal = summaryReport.getTotalTwoinTen();
        LOG.debug("Total 2-in-10 irrigation :" + totalVal);
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getAverageIrrigationRequired(i);
            LOG.debug("Average Irrigation Required :" + val + " for month no " + i);
            val = (val / totalAvgIrrVal) * totalVal;
            if (val > 0) {
                LOG.debug("2 - 10 irrigation required :" + val + " for month no " + i);
                LOG.debug("Total Avg  Irrigation value " + totalAvgIrrVal);
                soilSeriesReport.setTwoin10IrrigationRequired(i, val);
            }
            val = summaryReport.getTwoin10IrrigationRequired(i);
            if (val > 0) {
                LOG.debug("2 - 10 irrigation required :" + val + " for month no " + i);
                double wIrr = (val * summaryReport.getSoilArea()) / ret.getTotalArea();
                LOG.debug("Weighted 2In10 Irr Required for this soil" + String.valueOf(wIrr));
                soilSeriesReport.setWeighted2In10IrrRequired(i, wIrr);
            } else if (val == 0.00) {
                LOG.debug("1 - 10 Irrigation Required :" + val + "for month no " + i);
                soilSeriesReport.setOnein10IrrigationRequired(i, val);
            }
        }
        /**
         * *********1-in-10 Irrigation Required************
         */
        LOG.debug("****1-in-10 Irr Req ******");
        totalVal = summaryReport.getTotalOneinTen();
        if (totalVal == -99.0) { // TODO what's purpose for this process?? why only for 1-in-10??
            ret.getSoilNames().add(summaryReport.getSoilName());
        }

        LOG.debug("Total 1-in-10 irrigation :" + totalVal);
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getAverageIrrigationRequired(i);
            LOG.debug("Average Irrigation Required :" + val + "for month no " + i);
            val = (val / totalAvgIrrVal) * totalVal;
            if (val > 0.00) {
                LOG.debug("1 - 10 Irrigation Required :" + val + "for month no " + i);
                soilSeriesReport.setOnein10IrrigationRequired(i, val);
            } else if (val == 0.00) {
                LOG.debug("1 - 10 Irrigation Required :" + val + "for month no " + i);
                soilSeriesReport.setOnein10IrrigationRequired(i, val);
            }
            val = summaryReport.getOnein10IrrigationRequired(i);
            if (val >= 0) {
                double wIrr = (val * summaryReport.getSoilArea()) / ret.getTotalArea();
                soilSeriesReport.setWeighted1In10IrrRequired(i, wIrr);

            }
        }
    }
    
    //save permit files in JSON format
    public static File savePermitFile(File outputDir, UserInput input) {
        File ret = Paths.get(outputDir.getPath(), input.getSITE() + ".json").toFile();
        try (FileWriter file = new FileWriter(ret)) {

            file.write(savePermitJson(input));
            file.flush();

            return ret;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static String savePermitJson(UserInput input) {
        JSONObject obj = new JSONObject();
        obj.put("afsirs_version", Messages.getVersion());
        //page 1
        obj.put("permit_id", input.getSITE());
        obj.put("owner_name", input.getOWNER());
        obj.put("et_loc", input.getCLIMATELOC());
        obj.put("et_nearest_flg", input.getCLIM_FLG());
        obj.put("rain_loc", input.getRAINFALLLOC());
        obj.put("rain_nearest_flg", input.getRAIN_FLG());
        obj.put("planted_area", input.getPlantedAcres() + "");
        obj.put("total_area", input.getMapArea() + "");
        obj.put("crop_type", input.getCropType());
        obj.put("crop_name", input.getCropName());
        obj.put("beg_date_month", input.getMO1() + "");
        obj.put("beg_date_day", input.getDAY1() + "");
        obj.put("end_date_month", input.getMON() + "");
        obj.put("end_date_day", input.getDAYN() + "");
        obj.put("irr_type", input.getIR() + "");
        obj.put("irr_option", input.getIrrOption());
        obj.put("irr_depth_type", input.getIDCODE() + "");
        if (input.getIDCODE() == 1) {
            obj.put("irr_depth", input.getFIX() + "");
        } else if (input.getIDCODE() == 2) {
            obj.put("irr_depth", input.getPIR() + "");
        }
        obj.put("irr_efficiency", input.getIEFF() + "");
        obj.put("soil_surface_irr", input.getARZI() + "");
        obj.put("et_extracted", input.getEXIR() + "");
        obj.put("ir_dat", input.getIVERS() + "");
        obj.put("water_table_depth", input.getDWT() + "");
        obj.put("water_hold_capacity", input.getWATERHOLDINGCAPACITY());
        obj.put("soil_source", input.getSoilSource());
        obj.put("soil_unit_name", input.getUNIT());
        ArrayList soils = new ArrayList();
        for (Soil soil : input.getSoils()) {
            HashMap soilData = new HashMap();
            soilData.put("mukey", soil.getSOILSERIESKEY());
            soilData.put("musym", soil.getSoilSymbolNum());
            soilData.put("mukeyName", soil.getSERIESNAME());
            soilData.put("cokey", soil.getCOMPKEY());
            soilData.put("soilName", soil.getSNAME());
            soilData.put("compArea", soil.getSoilTypeArea() + "");
            soilData.put("comppct_r", soil.getSoilTypePct() + "");
            ArrayList soilLayers = new ArrayList();
            for (int i = 0; i < soil.getNL(); i++) {
                HashMap soilLayer = new HashMap();
                soilLayer.put("sllb", soil.getDU()[i] + "");
                soilLayer.put("slll", soil.getWCL()[i] + "");
                soilLayer.put("sldul", soil.getWCU()[i] + "");
                soilLayers.add(soilLayer);
            }
            soilData.put("soilLayer", soilLayers);
            soils.add(soilData);
            obj.put("soil_version", soil.getVersion());
        }
        obj.put("soils", soils);
        JSONObject polygonInfo = input.getPolygonInfoJSONObject();
        if (polygonInfo != null && !polygonInfo.isEmpty()) {
            obj.put("polygon", polygonInfo.get("polygon"));
        }
        JSONObject polygonLocInfo = input.getPolygonLocInfoJSONObject();
        if (polygonLocInfo != null && !polygonLocInfo.isEmpty()) {
            obj.put("afsirs", polygonLocInfo.get("afsirs"));
        }
        obj.put("coefficent_type", input.getCoefficentType());
        if ("annual".equalsIgnoreCase(input.getCropType())) {
            obj.put("dzn", input.getDZN() + "");
            obj.put("dzx", input.getDZX() + "");
            obj.put("akc3", input.getAKC3() + "");
            obj.put("akc4", input.getAKC4() + "");
            obj.put("f1", input.getF()[0] + "");
            obj.put("f2", input.getF()[1] + "");
            obj.put("f3", input.getF()[2] + "");
            obj.put("f4", input.getF()[3] + "");
            obj.put("ald1", input.getALD()[0] + "");
            obj.put("ald2", input.getALD()[1] + "");
            obj.put("ald3", input.getALD()[2] + "");
            obj.put("ald4", input.getALD()[3] + "");
        } else {
            obj.put("drzirr", input.getDRZIRR() + "");
            obj.put("drztot", input.getDRZTOT() + "");

            obj.put("akc", Arrays.stream(input.getAKC()).mapToObj(String::valueOf).collect(Collectors.toList()));
            obj.put("aldp", Arrays.stream(input.getALDP()).mapToObj(String::valueOf).collect(Collectors.toList()));
            if (input.getIR() == IRCRFL) {
                obj.put("hgt", input.getHGT() + "");
            }
        }

        return obj.toJSONString();
    }

    public static boolean saveInputData(File outputDir, UserInput input) {
        ObjectMapper mapperObj = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = mapperObj.writeValueAsString(input);
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(AFSIRSModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (FileWriter file = new FileWriter(Paths.get(outputDir.getPath(), input.getSITE() + "_input.json").toFile())) {

            file.write(jsonStr);
            file.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return false;
        }

    }

}

class LSQResult {

    double slope;
    double intercept;
    double RSQ;
    double R;

    public LSQResult(double slope, double intercept, double RSQ, double R) {
        this.slope = slope;
        this.intercept = intercept;
        this.RSQ = RSQ;
        this.R = R;
    }
}

class PROBResult {

    double X00, X50, X80, X90, X95, RSQ;

    public PROBResult() {

    }

    public void setAll(double val, double rsq) {
        X00 = X50 = X80 = X90 = X95 = val;
        RSQ = rsq;
    }
}

class STATResult {

    double[] PROB = new double[64];
    double XMEAN, XVAR, XSDEV, XMAX, XMIN, XMED, XCV;

    public STATResult(int N) {
        for (int i = 0; i < N; i++) {
            PROB[i] = 1.0;
        }
    }

}
