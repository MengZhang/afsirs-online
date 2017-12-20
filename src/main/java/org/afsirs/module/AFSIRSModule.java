package org.afsirs.module;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import static org.afsirs.module.DateUtil.MDAY;
import static org.afsirs.module.DateUtil.NKC;
import static org.afsirs.module.Messages.DOC_HEADER;
import static org.afsirs.module.Messages.USER_DETAILS;
import static org.afsirs.module.Messages.USER_DETAILS_EXCEL;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.json.simple.JSONObject;

/**
 *
 * @author rkmalik
 * @author Meng Zhang
 */

/*
 DEFINITIONS....

 IR = IRRIGATION SYSTEM IDENTIFICATION CODE
 IRCRFL = CROWN FLOOD IRRIGATION SYSTEM CODE
 IRNSCY = CONTAINER NURSERY IRRIGATION SYSTEM CODE
 IRRICE = RICE FLOOD IRRIGATION SYSTEM CODE
 IRSEEP = SEEPAGE (SUBIRRIGATION) IRRIGATION SYSTEM CODE
 ISIM = COUNTER FOR MULTIPLE SIMULATIONS
 JDAY = CALENDAR DAY OF CROP IRRIGATION SEASON
 J1SAVE = CALENDAR DAY OF FIRST DAY OF IRRIGATION SEASON FOR
 PREVIOUS SIMULATION
 JNSAVE = CALENDAR DAY OF LAST DAY OF IRRIGATION SEASON FOR
 PREVIOUS SIMULATION
 SITE = DESCRIPTION OF LOCATION OF THE PRODUCTION SYSTEM SIMULATED

 */
public class AFSIRSModule {

//    /**
//     * @return the climIR
//     */
//    public InputStreamReader getClimIR() {
//        return climIR;
//    }
//    
//    /**
//     * @param climIR the climIR to set
//     */
//    public void setClimIR(InputStreamReader climIR) {
//        this.climIR = climIR;
//    }
//    
//    /**
//     * @return the rainfallIR
//     */
//    public InputStreamReader getRainfallIR() {
//        return rainfallIR;
//    }
//    
//    /**
//     * @param rainfallIR the rainfallIR to set
//     */
//    public void setRainfallIR(InputStreamReader rainfallIR) {
//        this.rainfallIR = rainfallIR;
//    }
//    
//    /**
//     * @return the climIRDate
//     */
//    public InputStreamReader getClimIRDate() {
//        return climIRDate;
//    }
//    
//    /**
//     * @param climIRDate the climIRDate to set
//     */
//    public void setClimIRDate(InputStreamReader climIRDate) {
//        this.climIRDate = climIRDate;
//    }
//    
//    /**
//     * @return the rainfallIRDate
//     */
//    public InputStreamReader getRainfallIRDate() {
//        return rainfallIRDate;
//    }
//    
//    /**
//     * @param rainfallIRDate the rainfallIRDate to set
//     */
//    public void setRainfallIRDate(InputStreamReader rainfallIRDate) {
//        this.rainfallIRDate = rainfallIRDate;
//    }
//    
//    public String getWATERHOLDINGCAPACITY() {
//        return WATERHOLDINGCAPACITY;
//    }
//
//    public void setWATERHOLDINGCAPACITY(String WATERHOLDINGCAPACITY) {
//        this.WATERHOLDINGCAPACITY = WATERHOLDINGCAPACITY;
//    }
    static Logger logger = Logger.getLogger(AFSIRSModule.class.getName());

    private static final Font BLACK_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    private static final Font BLACK_BOLD = new Font(FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
    //private final Font BLACK_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.RED);
    private static final Font BLUE_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLUE);
    private static final Font BLUE_HEADER = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);
    private static final Font GREEN_ITALIC = new Font(FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GREEN);

//    int IR, ISIM, J1REP, JNREP, J1SAVE, JNSAVE, ICODE, IPRT;
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

//    int NYR, J1, JN, ICROP, NDAYS, IDCODE = 0;
//    double ARZI, ARZN, FIX, FRIR, PIR, IEFF;
//    double EXIR, EPS = 0.000001, SWCI1, SWCN1;
//    double DRZIRR, DRZTOT;
//    double DWT;
//    double PLANTEDACRES = 0.0;
//    double MAPAREA = 0.0;
//    double[][] RAIN = new double[64][365];
//    double[][] ETP = new double[64][365];
//    double[][] IRR = new double[64][365];
//    double[][] RAIN_S = new double[64][365];
//    double[][] ETP_S = new double[64][365];
//    double[][] IRR_S = new double[64][365];
//
//    double[][] RAIN_1 = new double[64][365]; // Go to local finishInput
//    double[][] ETP_1 = new double[64][365]; // Go to local finishInput
//    double[] DRZ_1 = new double[365]; // Go to local finishInput
//    double[] DRZI_1 = new double[365]; // Go to local finishInput
//    double[] AWD_1 = new double[365]; // Go to local finishInput
//    double[] RKC_1 = new double[365]; // Go to local finishInput
//    int[] NF_1 = new int[4]; // Go to local finishInput
//
//    double[] SDR = new double[365];
//    double[] SET = new double[365];
//    double[] SETP = new double[365];
//    double[] SRAIN = new double[365];
//    double[] DRZ = new double[365]; // Meng: Not related with any user input yet
//    double[] DRZI = new double[365];
//    double[] RKC = new double[365]; // Meng: Not related with any user input yet
//    double[] NIR = new double[365];
//    double[] SWIRR = new double[365];
//    double[] SWMAX = new double[365];
//    double[] SWCIX = new double[365];
//    double[] SWCNX = new double[365];

//    double[] AWD = new double[365]; // Meng: Not related with any user input yet
//    double[] AKC = new double[12];
//    double[] ALDP = new double[12];
//    int[] NF = new int[4]; // Meng: Not related with any user input yet
//    double AKC3, AKC4;
//    double DZN, DZX;
//    double[] F;
//    double[] ALD;
//    double HGT;
//    double[] PDATM = new double[12];
//    double[] PDATBW = new double[26];
//    double[] PDATW = new double[52];
//
//    // ArrayList to Hold all the data
//    ArrayList<PDAT> allSoilInfo = new ArrayList<>();
//
//    // ArrayList for Soilnames with negative value error
//    ArrayList<String> soilNames = new ArrayList<>();
//
//    int[] JDAY = new int[365];
//    int MONTH, IIDAY, IYEAR, startYear, endYear;
//    int MO1, MON, DAY1, DAYN; //For Irrigation Season
    //Irrigation parameters
    double[] EFF = new double[10];

//    //Soil Data
//    String SSERIESNAME;
//    String SOILSMAPUNITCODE;
//    String WATERHOLDINGCAPACITY;
//
//   
//    String SNAME;
//    String SOILCOMPCODE;
//    
//    String[] TXT;
//    double[] DU, WCL, WCU, WC;
//    int NL;
//    
//    String OUTFIL, SUMMARYFILE, SUMMARYFILEEXCEL, CTYPE, CLIMFIL, CLIMATELOC, RAINFALLLOC, IRNAME;
//    private String CALCULATIONEXCEL;
//    String SITE, UNIT, OWNER; //Name changed in desktop to Permit ID, Map Name, Output file name
//    
    //public SummaryReport summaryReport = new SummaryReport();
//    public ArrayList<SummaryReport> summaryList = new ArrayList<>(); // Go to local finishInput

//    boolean isPerennial, IVERS, isNet = false;
//    int[] NKC = {15, 45, 74, 105, 135, 166, 196, 227, 258, 288, 319, 349};
//    final static int[] MDAY = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final String EOL = Messages.EOL;

//    private static AFSIRSUtils instance;
//    BufferedWriter bwOutputFile; // Go to local finishInput
    //BufferedWriter bwOutputSummaryFile;
//    Document bwOutputSummaryFile; // Go to local finishInput
//    SummaryReportExcelFormat excelSummary, excelCal; // Go to local finishInput
//    ArrayList<PdfPTable> summaryTables; // Go to local finishInput
//    private double[] soilFractions; // Go to local finishInput
//    static private double[] soilArea;
//    private double totalArea; // Go to SimResult
//    private Soil soil; // Go to local finishInput
//    private SoilData soilData;
//    private String CLIMATESTATION;
//    private String RAINFALLSTATION;
//    private String irrOption;
    //moving the variables from SiteInfoFrame
//    private InputStreamReader climIR;
//    private InputStreamReader rainfallIR;
//    private InputStreamReader climIRDate;
//    private InputStreamReader rainfallIRDate;

    private AFSIRSModule() {
        Appender fh;
        try {
            SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
            fh = new FileAppender(new SimpleLayout(), "AFSIRS_" + format.format(Calendar.getInstance().getTime()) + ".log");
            logger.addAppender(fh);
            fh.setLayout(new SimpleLayout());
            logger.info("AFSIRS log file");
        } catch (SecurityException | IOException e) {
            e.printStackTrace(System.err);
        }
    }

//    private static void getDataCopy() {
//
//        RAIN_1 = Arrays.copyOf(RAIN, RAIN.length);
//        ETP_1 = Arrays.copyOf(ETP, ETP.length);
//
//        DRZ_1 = Arrays.copyOf(DRZ, DRZ.length);
//        DRZI_1 = Arrays.copyOf(DRZI, DRZI.length);
//        AWD_1 = Arrays.copyOf(AWD, AWD.length);
//        RKC_1 = Arrays.copyOf(RKC, RKC.length);
//        NF_1 = Arrays.copyOf(NF, NF.length);
//    }
//
//    private static void resetAllocate (UserInput input, double EXIR, double[][] RAIN, double[][] ETP, DeCoefResult deCoefRet, DeCoefResult deCoefbackup) {
////        SDR = new double[365];
////        SET = new double[365];
////        SETP = new double[365];
////        SRAIN = new double[365];
////        IRR = new double[64][365];
//
//        input.setEXIR(EXIR);
//        input.setRAIN(RAIN);
//        input.setETP(RAIN);
////        RAIN = Arrays.copyOf(RAIN_1, RAIN_1.length);
////        ETP = Arrays.copyOf(ETP_1, ETP_1.length);
//        deCoefRet.reset(deCoefbackup);
////        DRZ = Arrays.copyOf(DRZ_1, DRZ_1.length);
////        DRZI = Arrays.copyOf(DRZI_1, DRZI_1.length);
////        AWD = Arrays.copyOf(AWD_1, AWD_1.length);
////        RKC = Arrays.copyOf(RKC_1, RKC_1.length);
////        NF = Arrays.copyOf(NF_1, NF_1.length);
//    }

//    public static AFSIRSUtils getInstance() {
//        if (instance == null) {
//            instance = new AFSIRSUtils();
//        }
//        return instance;
//    }
//    
//    public static void resetData(){
//        instance = new AFSIRSUtils();
//    }
//    
//    public void INIT() {
//        IR = 0;
//        ISIM = 0;
//        J1SAVE = 0;
//        JNSAVE = 0;
//        EPS = 0.000001;
//    }
//    
//    public void setOutFile(String name) {
//        OUTFIL = name;
//    }
//    
//    public String getOutFile() {
//        return OUTFIL;
//    }
//    
//    public void setSummaryFile(String fName) {
//        SUMMARYFILE = fName;
//    }
//    
//    public String getSummaryFile () {
//        return SUMMARYFILE;
//    }
//    
//    public void setSummaryFileExcel(String fName) {
//        SUMMARYFILEEXCEL = fName;
//    }
//    
//    public String getSummaryFileExcel () {
//        return SUMMARYFILEEXCEL;
//    }
//    
//    
//    public void setTodayDate(Date d) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
//        String[] date = sdf.format(d).split(" ");
//        IYEAR = Integer.parseInt(date[0]);
//        MONTH = Integer.parseInt(date[1]);
//        IIDAY = Integer.parseInt(date[2]);
//    }
//    
//    public void setSiteName(String site) {
//        SITE = site;
//    }
//    
//    public String getSITE() {
//        return SITE;
//    }
//    
//    public double getPLANTEDACRES() {
//        return PLANTEDACRES;
//    }
//    
//    public void setPLANTEDACRES(double PLANTEDACRES) {
//        this.PLANTEDACRES = PLANTEDACRES;
//    }
//    
//    public String getUNIT() {
//        return UNIT;
//    }
//    
//    public void setMapName(String UNIT) {
//        this.UNIT = UNIT;
//    }
//    public void setMapArea(double MAPAREA){
//        
//        this.MAPAREA = MAPAREA;
//    }
//    public double getMapArea(){
//        
//        return MAPAREA;
//    }
//    
//    public String getOWNER() {
//        return OWNER;
//    }
//    
//    public void setOWNER(String OWNER) {
//        this.OWNER = OWNER;
//    }
//    public void setCodes(int code, int print) {
//        ICODE = code;
//        IPRT = print;
//    }
//    
//    public int getICODE() {
//        return ICODE;
//    }
//    
//    public int getICODE() {
//        return ICODE;
//    }
//    
//    public String getCLIMATESTATION() {
//        return CLIMATESTATION;
//    }
//    
//    //Hiranava 25 Aug 2016 Added rainfall staion case
//    public String getRAINFALLSTATION() {
//        return RAINFALLSTATION;
//    }
//    
//    public void setCLIMATESTATION(String CLIMATESTATION) {
//        this.CLIMATESTATION = CLIMATESTATION;
//    }
//    
//    //Hiranava 25 Aug 2016 Added rainfall staion case
//    public void setRAINFALLSTATION(String RAINFALLSTATION) {
//        this.RAINFALLSTATION = RAINFALLSTATION;
//    }
//    
//    public void setCropData(int code, String name) {
//        CTYPE = name;
//        ICROP = code;
//        System.out.println("CTYPE" +CTYPE);
//    }
//    
//    public String getCropName() {
//        return CTYPE;
//    }
//    
//    public int getICROP() {
//        return ICROP;
//    }
//    
//    public void setIVERS(boolean ivers) {
//        IVERS = ivers;
//    }
//    
//    public boolean getIVERS() {
//        return IVERS;
//    }
//    
//    public void setIrrigationSystem(int ir, double arzi, double exir, double eff, String name) {
//        IR = ir;
//        ARZI = arzi;
//        EXIR = exir;
//        IEFF = eff;
//        IRNAME = name;
//    }
//    
//    public void setEXIR(double exir) {
//        EXIR = exir;
//    }
//    
//    public double getARZI() {
//        return ARZI;
//    }
//    
//    public int getIrrigationSystem() {
//        return IR;
//    }
//    
//    public String getIrrigationSystemName() {
//        return IRNAME;
//    }
//    
//    public int getJ1SAVE() {
//        return J1SAVE;
//    }
//    
//    public int getJNSAVE() {
//        return JNSAVE;
//    }
//    
//    public int getJ1() {
//        return J1;
//    }
//    
//    public int getJN() {
//        return JN;
//    }
//    
//    public double getEPS() {
//        return EPS;
//    }
//    
//    public void setIDCODE(int code, double value) {
//        if (code == 1) {
//            FIX = value;
//        } else if (code == 2) {
//            PIR = value;
//        }
//        
//        IDCODE = code;
//    }
//    
//    public void setDCOEFPerennial(double drzirr, double drztot, double[] akc, double[] aldp) {
//        DRZIRR = drzirr;
//        DRZTOT = drztot;
//        AKC = akc;
//        ALDP = aldp;
//    }
//    
//    public void setDRZ(double[] drz) {
//        DRZ = drz;
//    }
//    
//    public double[] getDRZ() {
//        return DRZ;
//    }
//    
//    public void setAWD(double[] awd) {
//        AWD = awd;
//    }
//    
//    public double[] getAWD() {
//        return AWD;
//    }
//    
//    public void setRKC(double[] rkc) {
//        RKC = rkc;
//    }
//    
//    public double[] getRKC() {
//        return RKC;
//    }
//    
//    public void setDWT(double val) {
//        DWT = val;
//    }
//    
//    public double getDWT() {
//        return DWT;
//    }
//    
//    public void setPerennial(boolean perennial) {
//        isPerennial = perennial;
//    }
//    
//    public boolean getPerennial() {
//        return isPerennial;
//    }
//    
//    public void setNF(int[] NF) {
//        this.NF = NF;
//    }
//    
//    public int[] getNF() {
//        return NF;
//    }
//
//    public void setDCOEFAnnual(double dzn, double dzx, double[] f, double[] ald) {
//        DZN = dzn;
//        DZX = dzx;
//        F = f;
//        ALD = ald;
//    }
//    
//    public void setHGT(double HGT) {
//        this.HGT = HGT;
//    }
//    
//    public void setAKC(double akc3, double akc4) {
//        AKC3 = akc3;
//        AKC4 = akc4;
//    }
//    
//    public double getAKC(int index) {
//        if (index == 3) {
//            return AKC3;
//        } else {
//            return AKC4;
//        }
//    }
//    
//    public void setHGT(double HGT) {
//        this.HGT = HGT;
//    }
//    
//    public void setSoilData(SoilData soilData) {
//        //this.soil = soilData.getSoils().get(0);
//        this.soilData = soilData;
//        
//        
//        //SNAME = soil.getName();
//        //TXT = soil.getTXT();
//        //DU = soil.getDU();
//        
//        double WCLn[] = {.9,.9,.9,.9,.9,.9};
//        WCL = WCLn;
//        //WCL = soil.getWCL();
//        //WCU = soil.getWCU();
//        double WCUn [] = {.9,.9,.9,.9,.9,.9};
//        WCU = WCUn;
//        //WC = soil.getWC();
//        //NL = soil.getNL();
//        
//        
//        /*SNAME = firstSoil.getName();
//        TXT = firstSoil.getTXT();
//        DU = firstSoil.getDU();
//        WCL = firstSoil.getWCL();
//        WCU = firstSoil.getWCU();
//        WC = firstSoil.getWC();
//        NL = firstSoil.getNL();*/
//    }
//
//    public void setDefaultSoil (Soil soil) {
//        SSERIESNAME = soil.getSERIESNAME();
//        SOILSMAPUNITCODE = soil.getSOILSERIESKEY();
//        
//        SNAME = soil.getName();
//        SOILCOMPCODE = soil.getCOMPKEY();
//        
//        TXT = soil.getTXT();
//        DU = soil.getDU();
//        WCL = soil.getWCL();
//        WC = soil.getWC();
//        WCU = soil.getWCU();
//        NL = soil.getNL();
//    }
//    
//    public SoilData getSoilData() {
//        return soilData;
//    }
//    public void setSWIRR(double[] swirr) {
//        SWIRR = swirr;
//    }
//
//    public void setSWCIX(double[] swcix) {
//        SWCIX = swcix;
//    }
//
//    public void setSWCNX(double[] swcnx) {
//        SWCNX = swcnx;
//    }
//
//    public void setSWMAX(double[] swmax) {
//        SWMAX = swmax;
//    }
//
//    public double[] getSWCIX() {
//        return SWCIX;
//    }
//
//    public double[] getSWCNX() {
//        return SWCNX;
//    }
//
//    public double[] getSWMAX() {
//        return SWMAX;
//    }
//
//    public void setSWCN1(double swcn1) {
//        SWCN1 = swcn1;
//    }
//    
//    public void setSWCI1(double swci1) {
//        SWCI1 = swci1;
//    }
    //type = 0, Monthly : 1, Bi-Weekly : 2, Weekly
    public static ArrayList<SoilSpecificPeriodData> getGraphData(SimResult simRet, int type) {

        ArrayList<SoilSpecificPeriodData> data = new ArrayList<>();
        int index = 1;

        switch (type) {
            case 0:

                for (PDAT i : simRet.getAllSoilInfo()) {
                    SoilSpecificPeriodData d = new SoilSpecificPeriodData();
                    d.setSoilDataPoints(i.getPDATM());
                    d.setSoilName(i.getSoilName() + "-" + (index++));
                    data.add(d);
                }
                return data;
            case 1:
                for (PDAT i : simRet.getAllSoilInfo()) {

                    SoilSpecificPeriodData d = new SoilSpecificPeriodData();

                    d.setSoilDataPoints(i.getPDATBW());
                    d.setSoilName(i.getSoilName() + "-" + (index++));
                    data.add(d);
                }
                return data;
            case 2:
                for (PDAT i : simRet.getAllSoilInfo()) {
                    SoilSpecificPeriodData d = new SoilSpecificPeriodData();
                    d.setSoilDataPoints(i.getPDATW());
                    d.setSoilName(i.getSoilName() + "-" + (index++));
                    data.add(d);
                }
                return data;
        }
        return null;
    }

//    public void setisNet(boolean net) {
//        isNet = net;
//    }
//    
//    //public void setIrrigationSeason(Date startDate, Date endDate) {
//    public void setIrrigationSeason(int sMonth, int sDay, int eMonth, int eDay) {
//        //J1, JN calculation and saved in
//        //J1Save and JNSave
//        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
//        //String[] date = sdf.format(startDate).split(" ");
//        //MO1 = Integer.parseInt(date[1]);
//        //DAY1 = Integer.parseInt(date[2]);
//        MO1 = sMonth;
//        DAY1 = sDay;
//        MON = eMonth;
//        DAYN = eDay;
//        
//        J1 = DAY1;
//        for (int i = 0; i < MO1 - 1; i++) {
//            J1 += MDAY[i];
//        }
//        JN = DAYN;
//        for (int i = 0; i < MON - 1; i++) {
//            JN += MDAY[i];
//        }
//        
//        NDAYS = JN - J1 + 1;
//        
//        if (NDAYS < 0) {
//            NDAYS += 365;
//        }
//        
//        //Save growing data for next simulation
//        J1SAVE = J1;
//        JNSAVE = JN;
//        
//        if (JN < J1) {
//            J1 = 1;
//            JN = NDAYS;
//        }
//    }
//    
//    public int getNYR() {
//        return NYR;
//    }
//    
//    public double[][] getRain() {
//        return RAIN;
//    }
//    
//    public double[][] getET() {
//        return ETP;
//    }
//    //04 Sep 2016: Hiranava Das: new function to set start and end year
//    public void setStartEndYear(InputStreamReader irET, InputStreamReader irRF ) throws IOException{
//        BufferedReader brRF;
//        BufferedReader brET;
//        try {
//            //getting start year and end year from ET data
//            
//            brET = new BufferedReader(irET);
//            
//            String lineET = brET.readLine();
//            //brET.mark(10000);
//            CLIMATELOC = lineET.split(" ")[0];
//            lineET = brET.readLine();
//            int i = 0;
//            int year = 0;
//            while (lineET.charAt(i) == ' ') {
//                i++;
//            }
//            lineET = lineET.substring(i);
//            i = 0;
//            while (lineET.charAt(i) != ' ') {
//                i++;
//            }
//            int newYear = Integer.parseInt(lineET.substring(0, i).trim());
//            for (int j = 0; j < newYear; j++) {
//                lineET = brET.readLine();
//                year = Integer.parseInt(lineET);
//                if (j==0){
//                    startYear = year;
//                }
//                int k = 0;
//                while (k < 365) {
//                    String[] parts = brET.readLine().split(" ");
//                    for (String x : parts) {
//                        if (x.length() > 0) {
//                            k++;
//                        }
//                    }
//                }
//                
//            }
//            endYear = year;
//            //getting start year and end year from Rainfall data
//            brRF = new BufferedReader(irRF);
//            String lineRF = brRF.readLine();
//            RAINFALLLOC = lineRF.split(" ")[0];
//            lineRF = brRF.readLine();
//            i = 0;
//            year = 0;
//            while (lineRF.charAt(i) == ' ') {
//                i++;
//            }
//            lineRF = lineRF.substring(i);
//            i = 0;
//            while (lineRF.charAt(i) != ' ') {
//                i++;
//            }
//            newYear = Integer.parseInt(lineRF.substring(0, i).trim());
//            
//            for (int j = 0; j < newYear; j++) {
//                lineRF = brRF.readLine();
//                year = Integer.parseInt(lineRF);
//                
//                if (j==0 && startYear < year){
//                    startYear = year;
//                }
//                int k = 0;
//                while (k < 365) {
//                    String[] parts = brRF.readLine().split(" ");
//                    for (String x : parts) {
//                        if (x.length() > 0) {
//                            k++;
//                        }
//                    }
//                }
//                
//            }
//            if (endYear > year){
//                endYear = year;
//            }
//            
//            NYR = endYear - startYear + 1;
//            
//        }
//        catch (IOException | NumberFormatException e) {
//            e.printStackTrace(System.err);
//        }
//        
//    }
//    //read the file for climate data and set it to a array
//    public void setClimateFile(InputStreamReader ir) {
//        try {
//            BufferedReader br = new BufferedReader(ir);
//            String line = br.readLine();
//            CLIMATELOC = line.split(" ")[0];
//            line = br.readLine();
//            int i = 0;
//            while (line.charAt(i) == ' ') {
//                i++;
//            }
//            line = line.substring(i);
//            i = 0;
//            while (line.charAt(i) != ' ') {
//                i++;
//            }
//            String year = line.substring(0, i).trim();
//            int totalYears = Integer.parseInt(year);
//            int cyear = 0;
//            for (int j = 0; j < totalYears; j++) {
//                int IYEAR = Integer.parseInt(br.readLine());
//                int k = 0;
//                int l = 0;
//                
//                while (l < 365) {
//                    
//                    String[] parts = br.readLine().split(" ");
//                    
//                    for (String x : parts) {
//                        
//                        if (x.length() > 0 && IYEAR >= startYear && IYEAR <= endYear) {
//                            ETP[cyear][k] = Double.parseDouble(x);
//                            k++;
//                        }
//                        if (x.length() > 0){
//                            l++;
//                        }
//                    }
//                }
//                if(IYEAR >= startYear && IYEAR <= endYear){
//                    cyear++;
//                }
//            }
//            br.readLine();
//            
//            for (int k = 0; k < 365; k++) {
//                JDAY[k] = k + 1;
//            }
//            
//            if (J1SAVE >= JNSAVE) {
//                NYR = NYR - 1; // Reduced no of year
//                for (int iy = 0; iy < NYR; iy++) {
//                    int j = -1;
//                    int iy1 = iy + 1;
//                    for (int jd = J1SAVE - 1; jd < 365; jd++) {
//                        j = j + 1;
//                        JDAY[j] = jd + 1;
//                        ETP[iy][j] = ETP[iy][jd];
//                        
//                    }
//                    for (int jd = 0; jd < JNSAVE; jd++) {
//                        j = j + 1;
//                        JDAY[j] = jd + 1;
//                        
//                        ETP[iy][j] = ETP[iy1][jd];
//                        
//                    }
//                }
//            }
////            for(int k=0;k<ETP.length;k++){
////                System.out.println("Year "+k);
////                for(int j=0;j<ETP[0].length;j++){
////                    System.out.println("ETP " +j+":"+ETP[k][j]);
////                }
////            }
//                
//            
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace(System.err);
//        }
//    }
//    //read the file for climate data and set it to a array
//    //Hiranava 25 Aug 2016 Separating Rainfall data to another method.
//    public void setRainfallFile(InputStreamReader ir) {
//        try {
//            BufferedReader br = new BufferedReader(ir);
//            String line = br.readLine();
//            RAINFALLLOC = line.split(" ")[0];
//            line = br.readLine();
//            int i = 0;
//            int cyear = 0;
//            while (line.charAt(i) == ' ') {
//                i++;
//            }
//            line = line.substring(i);
//            i = 0;
//            while (line.charAt(i) != ' ') {
//                i++;
//            }
//            String year = line.substring(0, i).trim();
//            int totalYears = Integer.parseInt(year);
//            
//            for (int j = 0; j < totalYears; j++) {
//                String line1 = br.readLine();
//                int IYEAR = Integer.parseInt(line1);
//                
//                int k = 0,l=0;
//                while (l < 365) {
//                    String curLine = br.readLine();
//                    String[] parts = curLine.split(" ");
//                    
//                    for (String x : parts) {
//                        
//                        if (x.length() > 0 && IYEAR >= startYear && IYEAR <= endYear) {
//                            RAIN[cyear][k] = Double.parseDouble(x);
//                            k++;
//                        }
//                        if (x.length() > 0){
//                            l++;
//                        }
//                    }
//                    
//                }
//                if(IYEAR >= startYear && IYEAR <= endYear){
//                    cyear++;
//                }
//            }
//            
//            
//            for (int k = 0; k < 365; k++) {
//                JDAY[k] = k + 1;
//            }
//            
//            if (J1SAVE >= JNSAVE) {
//                NYR = NYR - 1; // Reduced no of year
//                for (int iy = 0; iy < NYR; iy++) {
//                    int j = -1;
//                    int iy1 = iy + 1;
//                    for (int jd = J1SAVE - 1; jd < 365; jd++) {
//                        j = j + 1;
//                        JDAY[j] = jd + 1;
//                        RAIN[iy][j] = RAIN[iy][jd];
//                    }
//                    for (int jd = 0; jd < JNSAVE; jd++) {
//                        j = j + 1;
//                        JDAY[j] = jd + 1;
//                        RAIN[iy][j] = RAIN[iy1][jd];
//                    }
//                }
//            }
//            
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace(System.err);
//        }
//    }
//    public static void writeOutput(BufferedWriter bwOutputFile, String str) {
//        try {
//            bwOutputFile.append(str);
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }
//    public void MAIN() {
//        // Iterate Simulations ..
//        FRIR = 1.00;
//        FIX = 0.00;
//        for (int i = 0; i < 365; i++) {
//            JDAY[i] = i;
//        }
//        ISIM = ISIM + 1;
//        J1REP = J1SAVE;
//        JNREP = JNSAVE;
//    }
    //BAL.for
    private static void calculateBalance(SummaryReport report, UserInput input, SWResult swRet, BufferedWriter bwOutputFile) throws IOException {

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
                    if (!input.isPerennial() && KC[j] >= EPS && j < NF[1]) {
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

    private static SWResult SW(UserInput input, DeCoefResult deCoefRet, Soil soil, BufferedWriter bwOutputFile) throws IOException {
        
        SWResult ret = new SWResult(input, deCoefRet);
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

        double[] DL = new double[6];

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
        if (input.isPerennial()) {
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
                        DRZI[JD] = DU[NL];
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
                        DRZ[JD] = DU[NL];
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
        bwOutputFile.append(EOL + EOL + "     SOIL :  SERIES = " + soil.getSNAME() + "         TEXTURE = " + txt + "           AREA(Fraction ) = " + soil.getSoilTypeArea() + EOL + EOL);
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

    private static DeCoefResult DECOEF(UserInput input) {
        DeCoefResult ret = new DeCoefResult();
        double[] DRZI = ret.getDRZI();
        double[] DRZ = ret.getDRZ();
        double[] AWD = ret.getAWD();
        double[] RKC = ret.getRKC();

        final int J1 = input.getJ1();
        final int JN = input.getJN();

        if (input.isPerennial()) {
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
//    private void displayArray (double [] a) {
//
//        for (double v : a) {
//            // System.out.print (v + " ");
//        }
//    }
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

    private static void SUMX(SummaryReport report, UserInput input, SWResult swRet, Soil soil, SimResult simRet, BufferedWriter bwOutputFile) throws IOException {
        
        final int NYR = input.getNYR();
        final int JN = input.getJN();
        final int J1 = input.getJ1();
        final int JNSAVE = input.getJNSAVE();
        final int J1SAVE = input.getJ1SAVE();
        final int ICODE = input.getICODE();
        final boolean isNet = input.isNet();
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
            int startMonth = 11;
            report.reset();
            report.setTotalRainFall(imo + 1 + startMonth, TRAIN[imo]);
            report.setTotalEvaporation(imo + 1 + startMonth, TETP[imo]);
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

    private static void initOutputFile(BufferedWriter bw) {
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

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

    public static SimResult run(UserInput input, File outputDir) {

        SimResult ret = new SimResult();
        ret.setTotalArea(input.getPlantedAcres());
        SummaryReportExcelFormat excelSummary, excelCal;
        Document bwOutputSummaryFile = new Document();
        DeCoefResult deCoefRet;

        //to replace total area by planted area -- Hiranava Das
        if(ret.getTotalArea() == 0.0){
            ret.setTotalArea(input.getPlantedAcres());
        }
        try (BufferedWriter bwOutputFile = new BufferedWriter(new FileWriter(input.getOutFile(), true))) {
            //bwOutputSummaryFile = new BufferedWriter(new FileWriter(SUMMARYFILE, true));

            //PdfWriter.getInstance(bwOutputSummaryFile, new FileOutputStream(SUMMARYFILE+"-Summary.pdf"));
            PdfWriter.getInstance(bwOutputSummaryFile, new FileOutputStream(input.getSummaryFile()));

            excelSummary = new SummaryReportExcelFormat(input.getSummaryFileExcel());
            deCoefRet = DECOEF(input);

            initOutputFile(bwOutputFile);
            bwOutputSummaryFile.open();
            //formatSummaryOutputFile ();
            excelSummary.insertEmptyLine(12);
        ///excelCal.insertEmptyLine(12);

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
            
            bwOutputFile.append("PERMIT ID = " + input.getSITE() + "     MAP NAME = " + input.getUNIT() + "     OUTPUT FILE = " + input.getOWNER() + "     DATE = " + MONTH + "-" + IIDAY + "-" + IYEAR + "" + EOL);
            bwOutputFile.append(EOL + "                      CROP TYPE = " + CTYPE + EOL);
            bwOutputFile.append(EOL + " IRRIGATION SEASON = " + MO1 + "-" + DAY1 + " TO " + MON + "-" + DAYN + "                LENGTH = " + NDAYS + " DAYS" + EOL);
            bwOutputFile.append(EOL + " ET DATA BASE: LOCATION = " + CLIMATELOC + " LENGTH = " + NYR + " YEARS" + "(" + startYear + "-" + endYear + ")" + EOL);
            bwOutputFile.append(EOL + " RAINFALL DATA BASE: LOCATION = " + RAINFALLLOC + " LENGTH = " + NYR + " YEARS" + "(" + startYear + "-" + endYear + ")" + EOL);
            
            double FIX = input.getFIX();
            double PIR = input.getPIR();
            switch (input.getIDCODE()) {
                case 0:
                    bwOutputFile.append("         NORMAL IRRIGATION: SOIL WILL BE IRRIGATED TO FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                    break;
                case 1:
                    bwOutputFile.append("         FIXED DEPTH IRRIGATION: A FIXED (CONSTANT) DEPTH OF WATER WILL BE APPLIED AT EACH IRRIGATION" + EOL);
                    bwOutputFile.append("         DEPTH OF WATER TO APPLY PER IRRIGATION = " + FIX + EOL);
                    break;
                case 2:
                    bwOutputFile.append("         DEFICIT IRRIGATION: THE SOIL WILL BE IRRIGATED TO A FRACTION OF FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                    bwOutputFile.append("         PERCENT OF FIELD CAPACITY ENTERED FOR DEFICIT IRRIGATION" + PIR + EOL);
                    break;
                default:
                    bwOutputFile.append(EOL);
            }
            
            String IRNAME = input.getIRNAME();
            double IEFF = input.getIEFF();
            double ARZI = input.getARZI();
            double EXIR = input.getEXIR();
            double HGT = input.getHGT();
            bwOutputFile.append(EOL + " IRRIGATION SYSTEM : TYPE = " + IRNAME + EOL);
            bwOutputFile.append("                     DESIGN APPLICATION EFFICIENCY = " + (int) (100 * IEFF) + " %" + EOL);
            bwOutputFile.append("                     FRACTION OF SOIL SURFACE IRRIGATED = " + (int) (100 * ARZI) + " %" + EOL);
            bwOutputFile.append("                     FRACTION EXTRACTED FROM IRRIGATED ZONE = " + EXIR + EOL);
            if (input.getIR() == IRCRFL) {
                bwOutputFile.append("           HEIGHT OF CROWN FLOOD IRRIGATION SYSTEM BEDS = " + HGT + " FT" + EOL);
            }

            int J1 = input.getJ1();
            int JN = input.getJN();
            int[] JDAY = input.getJDAY();
            int[] NF = deCoefRet.getNF();
            double[] DRZI = deCoefRet.getDRZI();
            double[] DRZ = deCoefRet.getDRZ();
            double[] RKC = deCoefRet.getRKC();
            double[] AWD = deCoefRet.getAWD();
            if (input.isPerennial()) {
                double DRZIRR = input.getDRZIRR();
                double DRZTOT = input.getDRZTOT();
                double[] AKC = input.getAKC();
                double[] ALDP = input.getALDP();
                bwOutputFile.append(EOL + "                      CROP TYPE = " + CTYPE + "(PERENNIAL)" + EOL);
                bwOutputFile.append(EOL + "               ROOT ZONE DEPTH IRRIGATED (INCHES) = " + DRZIRR);
                bwOutputFile.append(EOL + "               TOTAL CROP ROOT ZONE DEPTH (INCHES)= " + DRZTOT + EOL);
                bwOutputFile.append(EOL + "                                MONTH" + EOL + "         ");

                for (int i = 0; i < 12; i++) {
                    bwOutputFile.append(String.format("%5d", (i + 1)));
                }
                bwOutputFile.append(EOL + "     KC= ");
                for (int i = 0; i < 12; i++) {
                    bwOutputFile.append(String.format(" %.3f", AKC[i]));
                }
                bwOutputFile.append(EOL + "   ALDP= ");
                for (int i = 0; i < 12; i++) {
                    bwOutputFile.append(String.format(" %.3f", ALDP[i]));
                }

            } else {
                double DZN = input.getDZN();
                double DZX = input.getDZX();
                double AKC3 = input.getAKC3();
                double AKC4 = input.getAKC4();
                double[] F = input.getF();
                double[] ALD = input.getALD();
                bwOutputFile.append(EOL + "                      CROP TYPE = " + CTYPE + "(ANNUAL)");
                bwOutputFile.append(EOL + "    DZN   DZX  AKC3  AKC4    F1    F2    F3    F4  ALD1  ALD2  ALD3  ALD4" + EOL);
                bwOutputFile.append(String.format("    %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f  %3.3f\r\n", DZN, DZX,
                        AKC3, AKC4, F[0], F[1], F[2], F[3], ALD[0], ALD[1], ALD[2], ALD[3]));
                if (input.getICODE() == 2) {
                    double ff = input.getEPS();
                    for (int i = 0; i < 4; i++) {
                        String row = EOL + "          I,FF,NF(I),J1,JN = ";
                        ff += F[i];
                        bwOutputFile.append(String.format("%s %5d%7.3f%5d%5d%5d", row, (i + 1), ff, NF[i], J1, JN));
                    }
                    bwOutputFile.append(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
                    bwOutputFile.append(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
                    bwOutputFile.append(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");

                    bwOutputFile.append(EOL + EOL + "   CDAY JDAY NF1  NF2  NF3  DRZI DRZ  RKC ALD1 ALD2 ALD3 ALD4   AWD" + EOL);
                    for (int i = J1 - 1; i < JN; i++) {
                        bwOutputFile.append(String.format(" %5d%5d%5d%5d%5d %3.3f %3.3f %.3f %.3f %.3f %.3f %.3f  %.2f\r\n", i + 1, JDAY[i], NF[0], NF[1], NF[2], DRZI[i], DRZ[i], RKC[i], ALD[0], ALD[1], ALD[2], ALD[3], AWD[i]));
                    }
                }
            }

            if (input.getICODE() >= 1) {
                bwOutputFile.append(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
                bwOutputFile.append(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
                bwOutputFile.append(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");
                bwOutputFile.append(EOL + " DAILY ROOT ZONE DEPTHS, CROP COEFFICIENTS, & ALLOWABLE WATER DEPLETIONS" + EOL);
                bwOutputFile.append("               CDAY JDAY  DRZI(JD) DRZ(JD) RKC(JD) AWD(JD)" + EOL);
                for (int i = J1 - 1; i < JN; i++) {
                    String row = String.format("               %3d %4d %9.3f %7.3f %7.3f %7.3f", (i + 1), JDAY[i], DRZI[i], DRZ[i], RKC[i], AWD[i]);
                    bwOutputFile.append(row + EOL);
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
//            // Get data copied
//            double EXIR_1 = input.getEXIR();
//            double[][] RAIN_1 = Arrays.copyOf(input.getRAIN(), input.getRAIN().length);
//            double[][] ETP_1 = Arrays.copyOf(input.getETP(), input.getETP().length);
//            DeCoefResult deCoefRet_1 = new DeCoefResult(deCoefRet);

            ArrayList<PdfPTable> summaryTables = ret.getSummaryTables();
            ArrayList<SummaryReport> summaryList = ret.getSummaryList();
        

            ArrayList<Soil> soils = input.getSoilData().getSoils();
            ArrayList<Soil> soilsFS = soils;
            double[] soilFractions = new double[soils.size()];
            double[] soilArea = new double[soils.size()];
            double totalArea = 0.0;
            int i = 0;

            for (Soil s : soils) {
                totalArea += s.getSoilTypeArea();
                i++;
            }
            ret.setSoilArea(soilArea);
            if (totalArea == 0) {
                totalArea = input.getPlantedAcres();

            }
            ret.setTotalArea(totalArea);
            formatSummaryOutputFile(input, ret, excelSummary, bwOutputSummaryFile);
            i = 0;
            //05 Sep 2016: H Das: Need to sort here
            //Hiranava Das:16 Sep 2015:New function call to sort the output before printing
//            boolean isSorted = false;
//            ArrayList<SWResult> swRets = new ArrayList();
            for (Soil soil : soils) {
                //setSoilData(soil);
//                this.soil = s;
                if (i > 0) {
                    soilFractions[i] = soilFractions[i - 1];
                }
                soilFractions[i] += soil.getSoilTypeArea();

//                SSERIESNAME = soil.getSERIESNAME();
//                SOILSMAPUNITCODE = soil.getSOILSERIESKEY();
//                String SNAME = soil.getName();
//                SOILCOMPCODE = soil.getCOMPKEY();
//                String[] TXT = soil.getTXT();
//                double[] DU = soil.getDU();
//                double[] WCL = soil.getWCL();
//                double[] WCU = soil.getWCU();
//                double[] WC = soil.getWC();
//                int NL = soil.getNL();
//                resetAllocate(input, EXIR_1, RAIN_1, ETP_1, deCoefRet, deCoefRet_1);
                SWResult swRet = SW(input, deCoefRet, soil, bwOutputFile);
                SummaryReport report = new SummaryReport();
                summaryList.add(report);
                report.soilName = soil.getSNAME();
                calculateBalance(report, input, swRet, bwOutputFile);
                SUMX(report, input, swRet, soil, ret, bwOutputFile);
                storeTotalIrr(report, soil, ret);
//                swRets.add(swRet);
                i++;
            }
            soils = sortOutput(soils, summaryList);
            i = 0;
            for (Soil soil : soils) {
//                this.soil = s;
//
//                SSERIESNAME = soil.getSERIESNAME();
//                SOILSMAPUNITCODE = soil.getSOILSERIESKEY();
//
//                SNAME = soil.getName();
//                SOILCOMPCODE = soil.getCOMPKEY();
//
//                TXT = soil.getTXT();
//                DU = soil.getDU();
//                WCL = soil.getWCL();
//                WCU = soil.getWCU();
//                WC = soil.getWC();
//                NL = soil.getNL();

                finalSummaryOutput(summaryList.get(i), excelSummary, bwOutputSummaryFile, soil, ret, input, summaryTables);
                i++;
            }
            //Write the permit file.
            savePermitFile(new File("Permit/"), input);

//            excelCal = new SummaryReportExcelFormat(input.getCalculationExcel());
            excelCal = buildCalculationExcel(input, ret, summaryList);
            // Set the foot note here in the excel file and the pdf
            excelSummary.setFootNoteExcelFile(Messages.FOOTNOTE[0]);
            setIrrigationWeightedAverageExcel(input, excelSummary, summaryList);
            setIrrigationWeightedAverage(summaryTables, summaryList, input);
            bwOutputSummaryFile.add(new Paragraph("\r\n"));
            if (summaryTables.size() >= 2) {
                bwOutputSummaryFile.add(summaryTables.get(0));
                bwOutputSummaryFile.add(new Paragraph("\r\n"));

                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(summaryTables.get(1));
            //bwOutputSummaryFile.add(new Paragraph("\r\n"));

                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(summaryTables.get(2));
                //bwOutputSummaryFile.add(new Paragraph("\r\n"));
            }
            for (int inx = 3; inx < summaryTables.size(); inx++) {
                PdfPTable t = summaryTables.get(inx);
                bwOutputSummaryFile.add(t);
            }

            bwOutputFile.close();
            addDeviations(input, bwOutputSummaryFile);
        } catch (IOException | DocumentException e) {
            e.printStackTrace(System.err);
            return ret;
        }

        bwOutputSummaryFile.close();
        try {
            excelSummary.closeFileHandler();
            excelCal.closeFileHandler();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
        return ret;
    }

    private static void addParagraphToTable(PdfPTable table, String str) {
        Paragraph p;
        if (str.contains("AFSIRS")) {
            p = new Paragraph(str, BLUE_HEADER);
        } else {
            p = new Paragraph(str, BLUE_NORMAL);
        }

        p.setAlignment(Element.ALIGN_CENTER);

        PdfPCell c = new PdfPCell();
        c.addElement(p);
        c.setBorder(0);
        table.addCell(c);
    }

    private static void addParagraphToTableSoilName(PdfPTable t, String key, String value) {
        PdfPCell c;
        Paragraph p = new Paragraph();

        Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
        Chunk valChunk = new Chunk(value, BLACK_BOLD);

        p.add(keyChunk);
        p.add(valChunk);

        c = new PdfPCell(p);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBorder(0);
        t.addCell(c);
    }

    private static void addUserDetails(PdfPTable t, String key, String value) {
        PdfPCell c;
        Paragraph p = new Paragraph();

        //Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
        try {
            Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
            Chunk valChunk = new Chunk(value, BLACK_BOLD);
            p.add(keyChunk);
            p.add(valChunk);
        } catch (Exception e) {
            System.out.println("Please restart the app");
        }
        //Chunk valChunk = new Chunk(value, BLACK_BOLD);

        c = new PdfPCell(p);
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        c.setBorder(0);
        t.addCell(c);
    }

    //Hiranava Das: 21 Sep 2016: Heading of the PDF Report file

    private static void formatSummaryOutputFile(UserInput input, SimResult ret, SummaryReportExcelFormat excelSummary, Document bwOutputSummaryFile) {
        
            int MO1 = input.getMO1();
            int MON = input.getMON();
            int DAY1 = input.getDAY1();
            int DAYN = input.getDAYN();
        try {
            PdfPTable t = new PdfPTable(1);
            for (String s : DOC_HEADER) {
                addParagraphToTable(t, s);
            }
            //bwOutputSummaryFileExcel.insertData(new String [] {Messages.DOC_HEADER_EXCEL});
            excelSummary.insertDataWithStyle(Messages.DOC_HEADER_EXCEL, 2, true, true);
            excelSummary.setColNum(1);
            bwOutputSummaryFile.add(t);

            t = new PdfPTable(3);
            addUserDetails(t, USER_DETAILS[0], input.getOWNER());
            addUserDetails(t, USER_DETAILS[1], input.getSITE());
            addUserDetails(t, USER_DETAILS[2], input.getUNIT());

            addUserDetails(t, USER_DETAILS[3], input.getCropName());
            addUserDetails(t, USER_DETAILS[4], input.getIRNAME());

            // Default start day is first day of year in case of Perennial
            addUserDetails(t, USER_DETAILS[5], MO1 + "/" + DAY1);

            // Default end date is Last day of the Year in case of Perennial
            addUserDetails(t, USER_DETAILS[6], MON + "/" + DAYN);
            //addUserDetails(t, USER_DETAILS[6], "");
            addUserDetails(t, USER_DETAILS[7], input.getStartYear() + "-" + input.getEndYear());
            //addUserDetails(t, USER_DETAILS[8], String.valueOf(endYear));
            addUserDetails(t, USER_DETAILS[8], String.valueOf(input.getPlantedAcres()));
            addUserDetails(t, USER_DETAILS[9], String.valueOf(input.getMapArea()));
            addUserDetails(t, USER_DETAILS[10], input.getCLIMATESTATION());
            addUserDetails(t, USER_DETAILS[11], input.getRAINFALLSTATION());
            addUserDetails(t, USER_DETAILS[12], input.getIrrOption());
            addUserDetails(t, USER_DETAILS[13], String.valueOf(input.getIEFF()));
            addUserDetails(t, USER_DETAILS[14], String.valueOf(input.getARZI()));
            //addUserDetails(t, USER_DETAILS[15], String.valueOf(DRZIRR));
            addUserDetails(t, USER_DETAILS[15], String.format("%6.2f", ret.getTotalArea()));
            addUserDetails(t, USER_DETAILS[16], String.valueOf(input.getDWT()));
            addUserDetails(t, USER_DETAILS[18], input.getWATERHOLDINGCAPACITY());
            bwOutputSummaryFile.add(t);
            t = new PdfPTable(1);

            String KC = "";
            if (input.isPerennial()) {
                for (int i = 0; i < 12; i++) {

                    KC = KC + " " + input.getAKC()[i] + " ";
                }
            } else {
                KC = String.valueOf(input.getAKC3()) + " " + String.valueOf(input.getAKC4());
            }

            addUserDetails(t, USER_DETAILS[17], KC);

            bwOutputSummaryFile.add(t);
            /**
             * ***********Excel Data************
             */
            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[0], 0, false, true);
            excelSummary.insertDataWithStyle(input.getOWNER(), 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[1], 0, false, true);
            excelSummary.insertDataWithStyle(input.getSITE(), 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[2], 0, false, true);
            excelSummary.insertDataWithStyle(input.getUNIT(), 0, true, true);

            excelSummary.insertEmptyLine(1);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[3], 0, false, true);
            excelSummary.insertDataWithStyle(input.getCropName(), 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[4], 0, false, true);
            excelSummary.insertDataWithStyle(input.getIRNAME(), 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[5], 0, false, true);
            excelSummary.insertDataWithStyle(MO1 + "/" + DAY1, 0, false, true);

            excelSummary.insertEmptyLine(1);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[6], 0, false, true);
            excelSummary.insertDataWithStyle(MON + "/" + DAYN, 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[7], 0, false, true);
            excelSummary.insertDataWithStyle(String.valueOf(input.getPlantedAcres()), 0, false, true);

            excelSummary.insertDataWithStyle(USER_DETAILS_EXCEL[8], 0, false, true);
            excelSummary.insertDataWithStyle(input.getCLIMATESTATION(), 0, false, true);

            excelSummary.insertEmptyLine(1);

        } catch (DocumentException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void prepareWeightedAverageTable(ArrayList<PdfPTable> summaryTables) throws DocumentException {
        PdfPTable tableWeightedInches = new PdfPTable(14);

        tableWeightedInches.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedInches, "Irrigation Weighted Average (Inches)");
        //bwOutputSummaryFileExcel.insertDataWithStyle("Irrigation Weighted Average (Inches)", 0, true, true);
        createTableHeader(tableWeightedInches);
        summaryTables.add(tableWeightedInches);

        PdfPTable tableWeightedGallon = new PdfPTable(14);
        tableWeightedGallon.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedGallon, "Irrigation Weighted Average (Million Gallons)");
        //bwOutputSummaryFileExcel.insertDataWithStyle("Irrigation Weighted Average (Gallons)", 0, true, true);
        createTableHeader(tableWeightedGallon);
        summaryTables.add(tableWeightedGallon);
    }

    private static void setIrrigationWeightedAverageExcel(UserInput input, SummaryReportExcelFormat excelSummary, ArrayList<SummaryReport> summaryList) throws DocumentException {
        excelSummary.setRowNum(7);
        excelSummary.insertEmptyLine(1);

        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Irrigation Weighted Average (Inches)", 4, false, true);
        excelSummary.insertEmptyLine(1);
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);

        double totalVal = 0.0;
        double totalValGa = 0.0;
        String str = "";

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeightedAvgIrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeightedAvgIrrRequired(i);
            }
            //double irrGa = irr * PLANTEDACRES * 27154;
            //irrGa = irrGa / 1000000;

            if (irr >= 0) {
                totalVal += irr;
                str = String.format("%6.2f", irr);

            } else {
                str = "NA";
            }

            excelSummary.insertDataWithStyle(irr, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);

        excelSummary.insertDataWithStyle("2-In-10 Irr Req", 0, false, true);

        totalVal = 0.0;

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeighted2In10IrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted2In10IrrRequired(i);
            }
            if (irr >= 0) {
                totalVal += irr;
                str = String.format("%6.2f", irr);
            } else {
                str = "NA";
            }
            excelSummary.insertDataWithStyle(irr, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);

        excelSummary.insertDataWithStyle("1-In-10 Irr Req", 0, false, true);
        totalVal = 0.0;

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeighted1In10IrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted1In10IrrRequired(i);
            }
            str = String.format("%6.2f", irr);

            excelSummary.insertDataWithStyle(irr, 0, false, true);

            if (irr >= 0) {
                totalVal += irr;
            }
        }

        str = String.format("%6.2f", totalVal);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);

        /**
         * ***********Gallons Information***************
         */
        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Irrigation Weighted Average (Gallons)", 4, false, true);
        excelSummary.insertEmptyLine(1);
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);
        str = "";

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeightedAvgIrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeightedAvgIrrRequired(i);
            }
            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;

            str = String.format("%6.2f", irrGa);
            excelSummary.insertDataWithStyle(irrGa, 0, false, true);

            if (irrGa >= 0) {
                totalValGa += irrGa;
            }
        }
        str = String.format("%6.2f", totalValGa);
        excelSummary.insertDataWithStyle(totalValGa, 0, false, true);
        excelSummary.insertEmptyLine(1);

        excelSummary.insertDataWithStyle("2-In-10 Irr Req", 0, false, true);
        totalValGa = 0.0;

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeighted2In10IrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted2In10IrrRequired(i);
            }
            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;
            str = String.format("%6.2f", irrGa);
            excelSummary.insertDataWithStyle(irrGa, 0, false, true);
            if (irrGa >= 0) {
                totalValGa += irrGa;
            }
        }

        str = String.format("%6.2f", totalValGa);
        excelSummary.insertDataWithStyle(totalValGa, 0, false, true);
        excelSummary.insertEmptyLine(1);

        excelSummary.insertDataWithStyle("1-In-10 Irr Req", 0, false, true);
        totalValGa = 0.0;

        // Find the weighted average of the irrigation
        for (int i = 1; i <= 12; i++) {
            //double irr = summaryReport.getWeighted1In10IrrRequired(i);
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted1In10IrrRequired(i);
            }
            str = String.format("%6.2f", irr);
            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;
            str = String.format("%6.2f", irrGa);
            excelSummary.insertDataWithStyle(irrGa, 0, false, true);
            if (irrGa >= 0) {
                totalValGa += irrGa;
            }
        }

        str = String.format("%6.2f", totalValGa);
        excelSummary.insertDataWithStyle(totalValGa, 0, false, true);
        excelSummary.insertEmptyLine(1);
    }

    private static void setIrrigationWeightedAverage(ArrayList<PdfPTable> summaryTables, ArrayList<SummaryReport> summaryList, UserInput input) throws DocumentException {
        PdfPTable tIn = summaryTables.get(1);
        PdfPTable tGa = summaryTables.get(2);

        designRowTitleCell(tIn, "Mean Irr Req");
        designRowTitleCell(tGa, "Mean Irr Req");

        double totalVal = 0.0;
        double totalValGa = 0.0;
        String str;

        // Find the weighted average of the irrigation
        //for(SummaryReport summaryReport : summaryList){
        for (int i = 1; i <= 12; i++) {
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeightedAvgIrrRequired(i);
            }

            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;

            if (irr >= 0) {
                totalVal += irr;
                str = String.format("%6.2f", irr);
            } else {
                str = "";
            }

            designDataCell(tIn, String.valueOf(str));

            if (irrGa >= 0) {
                totalValGa += irrGa;
                str = String.format("%6.2f", irrGa);
            } else {
                str = "";
            }
            designDataCell(tGa, String.valueOf(str));
        }

        // }
        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);

        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);

        designRowTitleCell(tIn, "2-In-10 Irr Req");
        designRowTitleCell(tGa, "2-In-10 Irr Req");

        totalVal = 0.0;
        totalValGa = 0.0;

        // Find the weighted average of the irrigation
        //for(SummaryReport summaryReport : summaryList){
        for (int i = 1; i <= 12; i++) {
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted2In10IrrRequired(i);

            }

            if (irr >= 0) {
                totalVal += irr;
                str = String.format("%6.2f", irr);
            } else {
                str = "NA";
            }

            designDataCell(tIn, String.valueOf(str));

            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;

            if (irrGa >= 0) {
                totalValGa += irrGa;
                str = String.format("%6.2f", irrGa);
            } else {
                str = "NA";
            }
            designDataCell(tGa, String.valueOf(str));
        }
        // }

        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);

        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);

        designRowTitleCell(tIn, "1-In-10 Irr Req");
        designRowTitleCell(tGa, "1-In-10 Irr Req");

        totalVal = 0.0;
        totalValGa = 0.0;

        // Find the weighted average of the irrigation
        //for(SummaryReport summaryReport : summaryList){
        for (int i = 1; i <= 12; i++) {
            double irr = 0.0;
            for (SummaryReport summaryReport : summaryList) {
                irr = irr + summaryReport.getWeighted1In10IrrRequired(i);
            }

//            str = "";
            if (irr >= 0) {
                totalVal += irr;
                str = String.format("%6.2f", irr);
            } else {
                str = "NA";
            }
            designDataCell(tIn, String.valueOf(str));

            double irrGa = irr * input.getPlantedAcres() * 27154;
            irrGa = irrGa / 1000000;
            if (irrGa >= 0) {
                totalValGa += irrGa;
                str = String.format("%6.2f", irrGa);
            } else {
                str = "NA";
            }
            designDataCell(tGa, String.valueOf(str));

        }

        //}
        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);
        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);
    }

    private static void finalSummaryOutput(SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, Document bwOutputSummaryFile, Soil soil, SimResult ret, UserInput input, ArrayList<PdfPTable> summaryTables) {
        try {
//            String str = "";
//            String str1 = "";

            double area = input.getPlantedAcres();

            if (summaryTables.size() < 2) {
                PdfPTable excel = generalInformation(summaryReport, excelSummary);
                summaryTables.add(excel);
                prepareWeightedAverageTable(summaryTables);
            }

            PdfPTable t = new PdfPTable(3);
            for (int i = 0; i < 6; i++) {
                addParagraphToTableSoilName(t, " ", " ");
                addParagraphToTableSoilName(t, " ", " ");
            }

            double soilPercent = 0.0;

            if (ret.getTotalArea() != 0) {
                soilPercent = ((soil.getSoilTypeArea() * 100) / ret.getTotalArea());
            }

            addParagraphToTableSoilName(t, "Soil : ", soil.getSNAME());
            addParagraphToTableSoilName(t, "Soil Component Code : ", soil.getCOMPKEY());
            addParagraphToTableSoilName(t, "Soil Percentage : ", String.format("%6.2f", soilPercent));
            addParagraphToTableSoilName(t, "Soil Area(ACRES) : ", String.format("%6.2f", soil.getSoilTypeArea()));

            excelSummary.insertDataWithStyle("Soil", 0, false, true);
            excelSummary.insertDataWithStyle(soil.getSNAME(), 0, false, true);
            excelSummary.insertDataWithStyle("Soil Component Code", 0, false, true);
            excelSummary.insertDataWithStyle(soil.getCOMPKEY(), 0, false, true);
            excelSummary.insertDataWithStyle("Soil Percentage", 0, false, true);
            excelSummary.insertDataWithStyle(String.format("%6.2f", soilPercent), 0, false, true);
            excelSummary.insertDataWithStyle("Soil Series Name", 0, false, true);
            excelSummary.insertDataWithStyle(soil.getSERIESNAME(), 0, false, true);
            excelSummary.insertDataWithStyle("Soil Map Unit Code", 0, false, true);
            excelSummary.insertDataWithStyle(soil.getSOILSERIESKEY(), 0, true, true);

            addParagraphToTableSoilName(t, "Soil Series Name : ", soil.getSERIESNAME());
            addParagraphToTableSoilName(t, "Soil Map Unit Code : ", soil.getSOILSERIESKEY());
            addParagraphToTableSoilName(t, " ", " ");

            summaryTables.add(t);
            if (summaryReport.getTotalOneinTen() == -99.0 || summaryReport.getTotalTwoinTen() == -99.0) {
                PdfPTable error = new PdfPTable(1);
                addParagraphToTable(error, Messages.AFSIRS_ERROR);
                summaryTables.add(error);

            }
            //bwOutputSummaryFile.add(tIn);
            //Hiranava Das:16 Sep 2015:New function call to sort the output before printing
            //sortOutput(bwOutputSummaryFile,summaryReport);
            PdfPTable table = infoInInches(bwOutputSummaryFile, summaryReport, summaryReport, excelSummary, soil, ret);
            summaryTables.add(table);
            probablityInfoInGallons(bwOutputSummaryFile, summaryReport, excelSummary, summaryTables, area);

        } catch (DocumentException e) {
            // Logger.getLogger(AFSIRSUtils.class.getName()).log(Level.SEVERE, null, ex);
            e.printStackTrace(System.err);
        }
    }

    private static void designTableTitleCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        cell.setColspan(14);
        table.addCell(cell);
    }

    private static void designRowTitleCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private static void designTableHeaderRowCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private static void designDataCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLACK_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.CYAN);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private static PdfPTable generalInformation(SummaryReport summaryReport1, SummaryReportExcelFormat excelSummary) throws DocumentException {
        // a table with three columns
        PdfPTable table = new PdfPTable(14);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(table, "Inches/Month");
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Inches/Month", 4, true, true);
        excelSummary.insertEmptyLine(2);
        createTableHeader(table);
        createExcelTableHeader(excelSummary);
        /**
         * *************Mean Rainfall Details*****************
         */

        designRowTitleCell(table, "Mean Rainfall");

        excelSummary.insertDataWithStyle("Mean Rainfall", 0, false, true);

        double totalVal = 0.0;
        String str;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTotalRainFallByMonth(i);

            if (val >= 0) {
                totalVal += val;
                str = String.format("%6.2f", val);
            } else {
                str = "NA";
            }

            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);

        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, true, true);
        excelSummary.setColNum(1);
        /**
         * *************Mean Evaporation*****************
         */
        designRowTitleCell(table, "Mean ET");
        excelSummary.insertDataWithStyle("Mean ET", 0, false, true);
        totalVal = 0.0;
//        str = "";
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTotalEvaporationByMonth(i);
            if (val >= 0) {
                totalVal += val;
                str = String.format("%6.2f", val);
            } else {
                str = "NA";
            }

//            str = String.format("%6.2f", val);
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, true, true);
        excelSummary.setColNum(1);
        /**
         * *********Peak Evaporation Details************
         */
        /*designRowTitleCell(table, "Peak ET");
         excelSummary.insertDataWithStyle("Peak ET", 0, false, true);
         totalVal = 0.0;
         str = "";
        
         for (int i = 1; i <= 12; i++) {
         double val = summaryReport1.getPeakEvaporationByMonth(i);
         if (val>=0) {
         totalVal += val;
         str = String.format("%6.2f", val);
         } else {
         str = "NA";
         }
            
         designDataCell(table, str);
         excelSummary.insertDataWithStyle(val, 0, false, true);
         }
         str = String.format("%6.2f", totalVal);
         designDataCell(table, str);
         excelSummary.insertDataWithStyle(totalVal, 0, false, true);
         excelSummary.insertEmptyLine(2);*/

        designRowTitleCell(table, "Mean ET (Crop)");
        excelSummary.insertDataWithStyle("Mean ET (Crop)", 0, false, true);
        totalVal = 0.0;
//        str = "";
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getEvaporationCropByMonth(i);
            if (val >= 0) {
                totalVal += val;
                str = String.format("%6.2f", val);
            } else {
                str = "NA";
            }

//            str = String.format("%6.2f", val); // commented by Meng Zhang
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, true, true);
        excelSummary.setColNum(1);

        /*designRowTitleCell(table, "Peak ET (Inches/Day)");
         excelSummary.insertDataWithStyle("Peak ET (Inches/Day)", 0, false, true);
         totalVal = 0.0;
         str = "";
        
         for (int i = 1; i <= 12; i++) {
         double val = summaryReport1.getPeakEvaporationCropByMonth(i);
         if (val>=0) {
         totalVal += val;
         str = String.format("%6.2f", val);
         } else {
         str = "NA";
         }
            
         designDataCell(table, str);
         excelSummary.insertDataWithStyle(val, 0, false, true);
         }
        
         str = String.format("%6.2f", totalVal);
         designDataCell(table, str);
         excelSummary.insertDataWithStyle(totalVal, 0, false, true);
         excelSummary.insertEmptyLine(2);*/
//        summaryTables.add(table);
        return table;

    }

    //Hiranava Das: 16 Sep 2016: New function call to sort the output

    private static void storeTotalIrr(SummaryReport report, Soil soil, SimResult ret) throws DocumentException {
        // ArrayList<Soil> soils = soilData.getSoils();
        //boolean isSorted = false;
//        double [] soilArea = getSoilArea();
//        double areaSum = 0.0;
//
//        for (double a : soilArea) {
//            areaSum += a;
//        }
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
        soil.setTotalAvgIrrReq(totalVal);

    }

    private static ArrayList<Soil> sortOutput(ArrayList<Soil> soils, ArrayList<SummaryReport> summaryList) throws DocumentException {
        //need to change to merge sort or other efficient sorting methods
        for (int i = 0; i < soils.size() - 1; i++) {
            for (int j = 0; j < soils.size() - 1; j++) {
                if (soils.get(j).getTotalAvgIrrReq() < soils.get(j + 1).getTotalAvgIrrReq()) {
                    Soil temp = soils.get(j);
                    soils.set(j, soils.get(j + 1));
                    soils.set(j + 1, temp);
                    SummaryReport tempReport = summaryList.get(j);
                    summaryList.set(j, summaryList.get(j + 1));
                    summaryList.set(j + 1, tempReport);

                }
            }
        }

        return soils;
    }

    private static PdfPTable infoInInches(Document bwOutputSummaryFile1, SummaryReport summaryReport1, SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, Soil soil, SimResult ret) throws DocumentException {
        PdfPTable table;
        PdfPCell cell;
        double totalVal;
        String str;
        //bwOutputSummaryFile1.add(new Paragraph("\r\n"));
        table = new PdfPTable(14);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});

        designTableTitleCell(table, "Details in Inches");
        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Details in Inches", 4, false, true);
        excelSummary.insertEmptyLine(1);

        createTableHeader(table);
        createExcelTableHeader(excelSummary);

//        double [] soilArea = getSoilArea();
//        double areaSum=0.0;
//        
//        for (double a: soilArea){
//            areaSum+=a;
//        }
        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Mean Irr Req");
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);
        logger.info("Soil Name :" + soil.getSNAME());
        logger.info("Soil Area :" + soil.getSoilTypeArea());
        logger.info("Total Area :" + ret.getTotalArea());
        logger.info("****Mean Irr Required ******");

        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
            logger.info("Average Irrigation Required :" + val + " for month no " + i);
            double wIrr = (val * soil.getSoilTypeArea()) / ret.getTotalArea();
            if (val >= 0) {
                logger.info("Weighted Irrigation :" + wIrr + " for month no " + i);
                summaryReport.setWeightedAvgIrrRequired(i, wIrr);
                totalVal += val;
                str = String.format("%6.2f", val);
            } else {
                str = "NA";
            }

            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        summaryReport.setTotalAvgIrr(totalVal);
        double totalAvgIrrVal = totalVal;
        logger.info("Total Avg  Irrigation value " + totalAvgIrrVal);
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(str, 0, false, true);
        excelSummary.insertEmptyLine(1);
        /**
         * *********2-in-10 Irrigation Required************
         */
        logger.info("**** 2-in-10 Irr Req ******");
        designRowTitleCell(table, "2-in-10 Irr Req");
        excelSummary.insertDataWithStyle("2-in-10 Irr Req", 0, false, true);
        totalVal = summaryReport.getTotalTwoinTen();
        logger.info("Total 2-in-10 irrigation :" + totalVal);
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
            logger.info("Average Irrigation Required :" + val + " for month no " + i);
            val = (val / totalAvgIrrVal) * totalVal;
            if (val > 0) {
                logger.info("2 - 10 irrigation required :" + val + " for month no " + i);
                logger.info("Total Avg  Irrigation value " + totalAvgIrrVal);
                summaryReport1.setTwoin10IrrigationRequired(i, val);
                str = String.format("%6.2f", val);
            } else {
                str = "NA";
            }
            val = summaryReport1.getTwoin10IrrigationRequired(i);
            if (val > 0) {
                logger.info("2 - 10 irrigation required :" + val + " for month no " + i);
                double wIrr = (val * soil.getSoilTypeArea()) / ret.getTotalArea();
                logger.info("Weighted 2In10 Irr Required for this soil" + String.valueOf(wIrr));
                summaryReport.setWeighted2In10IrrRequired(i, wIrr);
            } else if (val == 0.00) {
                logger.info("1 - 10 Irrigation Required :" + val + "for month no " + i);
                summaryReport1.setOnein10IrrigationRequired(i, val);
                str = "0.00";
            } else {
                str = "NA";
            }
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        if (totalVal >= 0) {
            str = String.format("%6.2f", totalVal);
        } else {
            str = "NA";
        }
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);
        /**
         * *********1-in-10 Irrigation Required************
         */
        logger.info("****1-in-10 Irr Req ******");
        designRowTitleCell(table, "1-in-10 Irr Req");
        excelSummary.insertDataWithStyle("1-in-10 Irr Req", 0, false, true);
        totalVal = summaryReport.getTotalOneinTen();
        if (totalVal == -99.0) {
            ret.getSoilNames().add(soil.getSNAME());
        }

        logger.info("Total 1-in-10 irrigation :" + totalVal);
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
            logger.info("Average Irrigation Required :" + val + "for month no " + i);
            val = (val / totalAvgIrrVal) * totalVal;
            if (val > 0.00) {
                logger.info("1 - 10 Irrigation Required :" + val + "for month no " + i);
                summaryReport1.setOnein10IrrigationRequired(i, val);
                str = String.format("%6.2f", val);
            } else if (val == 0.00) {
                logger.info("1 - 10 Irrigation Required :" + val + "for month no " + i);
                summaryReport1.setOnein10IrrigationRequired(i, val);
                str = "0.00";
            } else {
                str = "NA";

            }
            designDataCell(table, str);
            val = summaryReport1.getOnein10IrrigationRequired(i);
            if (val >= 0) {
                double wIrr = (val * soil.getSoilTypeArea()) / ret.getTotalArea();
                summaryReport.setWeighted1In10IrrRequired(i, wIrr);

            }
//            else {
//                str = "NA";
//            }
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        if (totalVal >= 0) {
            str = String.format("%6.2f", totalVal);
        } else {
            str = "NA";
        }

        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);
//        summaryTables.add(table);
        return table;
    }

    private static void createTableHeader(PdfPTable table) {
        for (String str : Messages.TABLE_HEADER) {
            designTableHeaderRowCell(table, str);
        }
    }

    private static void createExcelTableHeader(SummaryReportExcelFormat excelSummary) {
        for (String str : Messages.TABLE_HEADER) {
            excelSummary.insertDataWithStyle(str, 3, false, true);
        }
        excelSummary.insertEmptyLine(1);
    }

    private static PdfPTable probablityInfoInGallons(Document bwOutputSummaryFile1, SummaryReport summaryReport1, SummaryReportExcelFormat excelSummary, ArrayList<PdfPTable> summaryTables, double area) throws DocumentException {
        PdfPTable table;
        PdfPCell cell;
        double totalVal;
        String str;
        //bwOutputSummaryFile1.add(new Paragraph("\r\n"));
        table = new PdfPTable(14);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(table, "Details in Million Gallons");
        createTableHeader(table);

        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Details in Million Gallons", 4, false, true);
        excelSummary.insertEmptyLine(1);
        createExcelTableHeader(excelSummary);

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Mean Irr Req");
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
//            str = "";
            if (val >= 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%6.2f", val);
                totalVal += val;
            } else {
                str = "NA";
            }

            //str = String.format("%6.2f", val);
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);

        }
        str = String.format("%6.2f", totalVal);
        //double totalAvgIrr = totalVal;
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);

        /**
         * *********2-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "2-in-10 Irr Req");
        excelSummary.insertDataWithStyle("2-in-10 Irr Req", 0, false, true);
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTwoin10IrrigationRequired(i);
            //double val = (summaryReport1.getAverageIrrigationRequired(i)/totalAvgIrr) * summaryReport1.getTotalTwoinTen();
//            str = "";
            if (val > 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%6.2f", val);
                totalVal += val;
            } else if (val == 0.00) {
                str = "0.00";
            } else {
                str = "NA";
            }

            //str = String.format("%6.2f", val);
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        //str = "-";
        designDataCell(table, str);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);
        /**
         * *********1-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "1-in-10 Irr Req");
        excelSummary.insertDataWithStyle("1-in-10 Irr Req", 0, false, true);
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getOnein10IrrigationRequired(i);
            //double val = (summaryReport1.getAverageIrrigationRequired(i)/totalAvgIrr) * summaryReport1.getTotalOneinTen();
//            str = "";
            if (val > 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%6.2f", val);
                totalVal += val;
            } else if (val == 0.00) {
                str = "0.00";
            } else {
                str = "NA";
            }

            //str = String.format("%6.2f", val);
            designDataCell(table, str);
            excelSummary.insertDataWithStyle(val, 0, false, true);
        }
        str = String.format("%6.2f", totalVal);
        //str = "-";
        designDataCell(table, str);
        summaryTables.add(table);
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);
        //bwOutputSummaryFile1.add(table);
        return table;
    }

    private static String appendSpace(int n, String str) {
        StringBuilder str1 = new StringBuilder();
        for (int i = 0; i < n; i++) {
            str1.append(' ');
        }

        return str1.append(str).toString();

    }

//    public double[] getFractions() {
//        return soilFractions;
//    }

//    public double [] getSoilArea() {
//        return soilArea;
//    }
    private static void addDeviations(UserInput input, Document bwOutputSummaryFile) throws DocumentException {
        //========================
        Paragraph p;
        Chunk imdb = new Chunk("View Map");
        try {
            imdb.setAnchor(new URL("http://abe.ufl.edu/bmpmodel/arcGIS/Hiranava/index.html?site=test123&unit=test8&json=%7B%22rings%22%3A%5B%5B%5B-9074755.428262126%2C3243513.702381273%5D%2C%5B-9074774.537519196%2C3243499.370438469%5D%2C%5B-9074105.713521684%2C3243556.6982096843%5D%2C%5B-9073580.208952209%2C3243322.609810555%5D%2C%5B-9073699.641808908%2C3242840.101069492%5D%2C%5B-9073976.72603645%2C3242887.8742121714%5D%2C%5B-9074043.6084362%2C3242825.769126688%5D%2C%5B-9074225.146378383%2C3242887.8742121714%5D%2C%5B-9074497.453291656%2C3242744.5547841326%5D%2C%5B-9074870.083804555%2C3242758.886726937%5D%2C%5B-9074870.083804555%2C3243093.298725693%5D%2C%5B-9074760.205576394%2C3243313.055182019%5D%2C%5B-9074755.428262126%2C3243513.702381273%5D%5D%5D%2C%22spatialReference%22%3A%7B%22wkid%22%3A102100%2C%22latestWkid%22%3A3857%7D%7D#"));
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(AFSIRSModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        p = new Paragraph("Click on the link to ");
        p.add(imdb);
        p.add(".");
        bwOutputSummaryFile.add(p);
        //========================
        PdfPCell c;
        p = new Paragraph();
        PdfPTable t = new PdfPTable(1);
        Chunk headerChunk = new Chunk("\r\n\nDeviations", BLACK_BOLD);
        p.add(headerChunk);
        c = new PdfPCell(p);
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        c.setBorder(0);
        t.addCell(c);
        Set set = input.getDeviation().entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            addUserDetails(t, me.getKey() + " has been changed to :", (String) me.getValue());

        }
        //addUserDetails(t, deviations, "");
        bwOutputSummaryFile.add(t);
    }

//    /**
//     * @return the CALCULATIONEXCEL
//     */
//    public String getCalculationExcel() {
//        return CALCULATIONEXCEL;
//    }
//
//    /**
//     * @param CALCULATIONEXCEL the CALCULATIONEXCEL to set
//     */
//    public void setCalculationExcel(String CALCULATIONEXCEL) {
//        this.CALCULATIONEXCEL = CALCULATIONEXCEL;
//    }

    private static SummaryReportExcelFormat buildCalculationExcel(UserInput input, SimResult ret, ArrayList<SummaryReport> summaryList) {
        SummaryReportExcelFormat excelCal = new SummaryReportExcelFormat(input.getCalculationExcel());
        ArrayList<Soil> soils = input.getSoilData().getSoils();
        excelCal.setRowNum(2);
        excelCal.insertDataWithStyle(Messages.DOC_HEADER_EXCEL, 2, true, true);
        excelCal.setColNum(1);
        for (int i = 0; i < 14; i++) {
            excelCal.mergeCells();
            excelCal.insertDataWithStyle(Messages.INFO_TYPES[i], 4, true, false);
            excelCal.insertEmptyLine(1);
            excelCal.insertDataWithStyle("", 3, false, true);
            excelCal.insertDataWithStyle("", 3, false, true);
            excelCal.insertDataWithStyle("", 3, false, true);
            for (String str : Messages.TABLE_HEADER_EXCEL) {
                excelCal.insertDataWithStyle(str, 3, false, true);
            }
            excelCal.insertEmptyLine(1);
            double factorNet = input.getPlantedAcres() * 27154 / 1000000 * input.getIEFF();
            double factorGross = input.getPlantedAcres() * 27154 / 1000000;
            switch (i) {
                case 0:
                    int k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getAverageIrrigationRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getAverageIrrigationRequired(j + 1);
                            if (summaryList.get(k).getAverageIrrigationRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getAverageIrrigationRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getAverageIrrigationRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 1:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Area Fraction", 0, false, true);
                    excelCal.insertDataWithStyle("Area Percentage", 0, true, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(s.getSoilTypeArea(), 0, false, true);
                        excelCal.insertDataWithStyle((s.getSoilTypeArea() / ret.getTotalArea()) * 100, 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getWeightedAvgIrrRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getWeightedAvgIrrRequired(j + 1);
                            if (summaryList.get(k).getWeightedAvgIrrRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getWeightedAvgIrrRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getWeightedAvgIrrRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 2:
                    k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getTwoin10IrrigationRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getTwoin10IrrigationRequired(j + 1);
                            if (summaryList.get(k).getTwoin10IrrigationRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getTwoin10IrrigationRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getTwoin10IrrigationRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 3:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Area Fraction", 0, false, true);
                    excelCal.insertDataWithStyle("Area Percentage", 0, true, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(s.getSoilTypeArea(), 0, false, true);
                        excelCal.insertDataWithStyle((s.getSoilTypeArea() / ret.getTotalArea()) * 100, 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getWeighted2In10IrrRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getWeighted2In10IrrRequired(j + 1);
                            if (summaryList.get(k).getWeighted2In10IrrRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getWeighted2In10IrrRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getWeighted2In10IrrRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 4:
                    k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getOnein10IrrigationRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getOnein10IrrigationRequired(j + 1);
                            if (summaryList.get(k).getOnein10IrrigationRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getOnein10IrrigationRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getOnein10IrrigationRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 5:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Area Fraction", 0, false, true);
                    excelCal.insertDataWithStyle("Area Percentage", 0, true, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(s.getSoilTypeArea(), 0, false, true);
                        excelCal.insertDataWithStyle((s.getSoilTypeArea() / ret.getTotalArea()) * 100, 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getWeighted1In10IrrRequired(1);
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getWeighted1In10IrrRequired(j + 1);
                            if (summaryList.get(k).getWeighted1In10IrrRequired(j + 1) > peak) {
                                peak = summaryList.get(k).getWeighted1In10IrrRequired(j + 1);
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getWeighted1In10IrrRequired(j + 1), 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 6:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Planted Area", 0, false, true);
                    excelCal.insertDataWithStyle("Efficiency", 0, true, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(input.getPlantedAcres(), 0, false, true);
                        excelCal.insertDataWithStyle(input.getIEFF(), 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getAverageIrrigationRequired(1) * factorNet;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorNet;
                            if ((summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorNet) > peak) {
                                peak = summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorNet;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorNet, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 7:
                    k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getAverageIrrigationRequired(1) * factorGross;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorGross;
                            if ((summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorGross) > peak) {
                                peak = summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorGross;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorGross, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 8:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Planted Area", 0, false, true);
                    excelCal.insertDataWithStyle("Efficiency", 0, true, false);
                    excelCal.insertDataWithStyle("", 0, false, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(input.getPlantedAcres(), 0, false, true);
                        excelCal.insertDataWithStyle(input.getIEFF(), 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getTwoin10IrrigationRequired(1) * factorNet;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorNet;
                            if ((summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorNet) > peak) {
                                peak = summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorNet;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorNet, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 9:
                    k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getTwoin10IrrigationRequired(1) * factorGross;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorGross;
                            if ((summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorGross) > peak) {
                                peak = summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorGross;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getTwoin10IrrigationRequired(j + 1) * factorGross, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 10:
                    k = 0;
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("Planted Area", 0, false, true);
                    excelCal.insertDataWithStyle("Efficiency", 0, true, false);
                    excelCal.insertDataWithStyle("", 0, false, false);
                    excelCal.insertEmptyLine(1);
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle(input.getPlantedAcres(), 0, false, true);
                        excelCal.insertDataWithStyle(input.getIEFF(), 0, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getOnein10IrrigationRequired(1) * factorNet;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorNet;
                            if ((summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorNet) > peak) {
                                peak = summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorNet;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorNet, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 11:
                    k = 0;
                    for (Soil s : soils) {
                        excelCal.insertDataWithStyle(s.getSNAME(), 0, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        excelCal.insertDataWithStyle("", 3, false, true);
                        double sum = 0;
                        double peak = summaryList.get(k).getOnein10IrrigationRequired(1) * factorGross;
                        for (int j = 0; j < 12; j++) {
                            sum = sum + summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorGross;
                            if ((summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorGross) > peak) {
                                peak = summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorGross;
                            }
                            excelCal.insertDataWithStyle(summaryList.get(k).getOnein10IrrigationRequired(j + 1) * factorGross, 0, false, true);
                        }
                        excelCal.insertDataWithStyle(sum, 0, false, true);
                        excelCal.insertDataWithStyle(peak, 0, false, true);
                        excelCal.insertDataWithStyle(sum / 365, 0, false, true);
                        excelCal.insertEmptyLine(1);
                        k++;
                    }
                    break;
                case 12:
                    excelCal.insertDataWithStyle("Mean Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double meanIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            meanIrrSum = meanIrrSum + summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorNet;
                        }
                        excelCal.insertDataWithStyle(meanIrrSum * factorNet, 0, false, true);
                    }
                    excelCal.insertEmptyLine(1);
                    excelCal.setColNum(1);
                    excelCal.insertDataWithStyle("2-In-10 Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double twoInTenIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            twoInTenIrrSum = twoInTenIrrSum + summaryList.get(k).getWeighted2In10IrrRequired(j + 1) * factorNet;
                        }
                        excelCal.insertDataWithStyle(twoInTenIrrSum * factorNet, 0, false, true);
                    }
                    excelCal.insertEmptyLine(1);
                    excelCal.setColNum(1);
                    excelCal.insertDataWithStyle("1-In-10 Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double oneInTenIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            oneInTenIrrSum = oneInTenIrrSum + summaryList.get(k).getWeighted1In10IrrRequired(j + 1) * factorNet;
                        }
                        excelCal.insertDataWithStyle(oneInTenIrrSum * factorNet, 0, false, true);
                    }
                    excelCal.insertEmptyLine(1);
                    break;
                case 13:
                    excelCal.insertDataWithStyle("Mean Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double meanIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            meanIrrSum = meanIrrSum + summaryList.get(k).getAverageIrrigationRequired(j + 1) * factorGross;
                        }
                        excelCal.insertDataWithStyle(meanIrrSum * factorGross, 0, false, true);
                    }
                    excelCal.insertEmptyLine(1);
                    excelCal.setColNum(1);
                    excelCal.insertDataWithStyle("2-In-10 Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double twoInTenIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            twoInTenIrrSum = twoInTenIrrSum + summaryList.get(k).getWeighted2In10IrrRequired(j + 1) * factorGross;
                        }
                        excelCal.insertDataWithStyle(twoInTenIrrSum * factorGross, 0, false, true);
                    }
                    excelCal.insertEmptyLine(1);
                    excelCal.setColNum(1);
                    excelCal.insertDataWithStyle("1-In-10 Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double oneInTenIrrSum = 0;
                        for (k = 0; k < soils.size(); k++) {
                            oneInTenIrrSum = oneInTenIrrSum + summaryList.get(k).getWeighted1In10IrrRequired(j + 1) * factorGross;
                        }
                        excelCal.insertDataWithStyle(oneInTenIrrSum * factorGross, 0, false, true);
                    }
                    break;
            }
        }
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Formulas", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Mean_Irr_Req = Mean Irrigation from AFSIRS output ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Weighted_Irr_Req = Mean_Irr_Req  x Soil Area/Total Area ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Proportion for a month= Mean_Irr_req /Total Mean_Irr_req ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Annual 2-in-10= Taken from the AFSIRS output ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("2-in-10 = Proportion x Annual 2-in-10 ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Annual 1-in-10= Taken from the AFSIRS output ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("1-in-10 = Proportion x Annual 1-in-10 ", 4, true, true);
        excelCal.insertEmptyLine(1);
        excelCal.mergeCells();
        excelCal.insertDataWithStyle("Irrigation in  million gallons = Irrigation in inches * PLANTEDACRES * 27154/1000000; ", 4, true, true);
        
        return excelCal;
    }

//    /**
//     * @return the irrOption
//     */
//    public String getIrrOption() {
//        return irrOption;
//    }
//    
//    /**
//     * @param irrOption the irrOption to set
//     */
//    public void setIrrOption(String irrOption) {
//        this.irrOption = irrOption;
//    }
    //save permit files in JSON format
    public static boolean savePermitFile(File outputDir, UserInput input) {
        JSONObject obj = new JSONObject();
        //page 1
        obj.put("permit_id", input.getSITE());
        obj.put("owner_name", input.getOWNER());
        obj.put("et_loc", input.getCLIMATESTATION());
        obj.put("rain_loc", input.getRAINFALLSTATION());
        obj.put("planted_area", input.getPlantedAcres());
        obj.put("crop_type", input.getCropType());
        obj.put("crop_name", input.getCropName());
        obj.put("beg_date_month", input.getMO1());
        obj.put("beg_date_day", input.getDAY1());
        obj.put("end_date_month", input.getMON());
        obj.put("end_date_day", input.getDAYN());
        obj.put("irr_type", input.getIR());
        obj.put("irr_option", input.getIrrOption());
        obj.put("irr_depth_type", input.getIDCODE());
        if (input.getIDCODE() == 1) {
            obj.put("irr_depth", input.getFIX());
        } else if (input.getIDCODE() == 2) {
            obj.put("irr_depth", input.getPIR());
        }
        obj.put("irr_efficiency", input.getIEFF());
        obj.put("soil_surface_irr", input.getARZI());
        obj.put("et_extracted", input.getEXIR());
        obj.put("ir_dat", input.getIVERS());

        try (FileWriter file = new FileWriter(Paths.get(outputDir.getPath(),  input.getSITE() + ".json").toFile())) {

            file.write(obj.toJSONString());
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

//class PDAT {
//
//    String soilName = "";
//    Double[] PDATM = new Double[12];
//    Double[] PDATBW = new Double[12];
//    Double[] PDATW = new Double[12];
//
//    //Double[] PDATBW = new Double[26];
//    //Double[] PDATW = new Double[52];
//}
