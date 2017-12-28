package org.afsirs.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import lombok.Data;
import static org.afsirs.module.DateUtil.MDAY;

/**
 * The container class for user input data
 *
 * @author Meng Zhang
 */
@Data
public class UserInput {

    int IR, ISIM, J1REP, JNREP, J1SAVE, JNSAVE, ICODE, IPRT;

    int J1, JN, ICROP, NDAYS, IDCODE = 0;
    double ARZI, ARZN, FIX, FRIR, PIR, IEFF;
    double EXIR, EPS = 0.000001;
    double DRZIRR, DRZTOT;
    double DWT;
    double plantedAcres = 0.0;
    double mapArea = 0.0;

    String coefficentType;
    double[] AKC = new double[12];
    double[] ALDP = new double[12];

    double AKC3, AKC4;
    double DZN, DZX;
    double[] F;
    double[] ALD;
    double HGT;

    int MONTH, IIDAY, IYEAR;
    int MO1, MON, DAY1, DAYN; //For Irrigation Season

    String outFile, summaryFile, summaryFileExcel, calculationExcel;
    String cropName, IRNAME, CLIMFIL;
    Weather weather = new Weather();
    private String SITE, UNIT, OWNER; //Name changed in desktop to Permit ID, Map Name, Output file name

//    private boolean perennial, IVERS, net = false;
    private String IVERS;
    private String cropType;
    private String irrOption = "GROSS";

    public boolean isIVERS() {
        return "true".equals(IVERS);
    }

    public boolean isPerennial() {
        return cropType.equalsIgnoreCase("perennial");
    }

    public boolean isNet() {
        return irrOption.equalsIgnoreCase("NET");
    }

    public LinkedHashMap deviation = new LinkedHashMap();

//    private SoilData soilData;
    private ArrayList<Soil> soils = new ArrayList();
    private String WATERHOLDINGCAPACITY;
    private String soilSource;
    private String CLIMATESTATION;
    private String RAINFALLSTATION;
    private InputStreamReader climIR;
    private InputStreamReader rainfallIR;
    private InputStreamReader climIRDate;
    private InputStreamReader rainfallIRDate;

    public void addDeviation(String varName, String varText) {
        deviation.put(varName, varText);
    }
    
    public UserInput() {
        this(new Date());
    }

    public UserInput(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
        String[] date = sdf.format(d).split(" ");
        IYEAR = Integer.parseInt(date[0]);
        MONTH = Integer.parseInt(date[1]);
        IIDAY = Integer.parseInt(date[2]);
    }
    
    public void setOWNER(String OWNER, String outPath) {
        this.OWNER = OWNER;
        outFile = Paths.get(outPath, OWNER).toFile().getPath(); //siteName +"-"+ unitName;
        summaryFile = outFile + "-Summary.pdf";
        summaryFileExcel = outFile + "-Summary.xlsx";
        calculationExcel = outFile + "-Cal.xlsx";
        outFile += ".txt";
    }

    public void setCodes(int code, int print) {
        ICODE = code;
        IPRT = print;
    }

    public void setCropData(int code, String name) {
        cropName = name;
        ICROP = code;
        System.out.println("CTYPE" + cropName);
    }

    //public void setIrrigationSeason(Date startDate, Date endDate) {
    public void setIrrigationSeason(String sMonth, String sDay, String eMonth, String eDay) {
        setIrrigationSeason(
                Integer.parseInt(sMonth),
                Integer.parseInt(sDay),
                Integer.parseInt(eMonth),
                Integer.parseInt(eDay));
    }

    public void setIrrigationSeason(int sMonth, int sDay, int eMonth, int eDay) {
        //J1, JN calculation and saved in
        //J1Save and JNSave
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
        //String[] date = sdf.format(startDate).split(" ");
        //MO1 = Integer.parseInt(date[1]);
        //DAY1 = Integer.parseInt(date[2]);
        MO1 = sMonth;
        DAY1 = sDay;
        MON = eMonth;
        DAYN = eDay;

        J1 = DAY1;
        for (int i = 0; i < MO1 - 1; i++) {
            J1 += MDAY[i];
        }
        JN = DAYN;
        for (int i = 0; i < MON - 1; i++) {
            JN += MDAY[i];
        }

        NDAYS = JN - J1 + 1;

        if (NDAYS < 0) {
            NDAYS += 365;
        }

        //Save growing data for next simulation
        J1SAVE = J1;
        JNSAVE = JN;

        if (JN < J1) {
            J1 = 1;
            JN = NDAYS;
        }
    }

    public void setIDCODE(String code, String value) {
        if (value == null || value.isEmpty()) {
            value = "0";
        }
        setIDCODE(
                Integer.parseInt(code),
                Double.parseDouble(value));
    }

    public void setIDCODE(int code, double value) {
        if (code == 1) {
            FIX = value;
        } else if (code == 2) {
            PIR = value;
        }

        IDCODE = code;
    }

    public void setIrrigationSystem(String ir, String arzi, String exir, String eff, String name) {
        setIrrigationSystem(
                Integer.parseInt(ir),
                Double.parseDouble(arzi),
                Double.parseDouble(exir),
                Double.parseDouble(eff),
                name);
    }

    public void setIrrigationSystem(int ir, double arzi, double exir, double eff, String name) {
        IR = ir;
        ARZI = arzi;
        EXIR = exir;
        IEFF = eff;
        IRNAME = name;
    }

    public int getIrrigationSystem() {
        return IR;
    }

    public String getCLIMATELOC() {
        return weather.getETLoc();
    }

    public double[][] getETP() {
        return weather.getETP();
    }

    public String getRAINFALLLOC() {
        return weather.getRainLoc();
    }

    public double[][] getRAIN() {
        return weather.getRAIN();
    }

    public int getNYR() {
        return weather.getNYR();
    }

    public int getStartYear() {
        return weather.getStartYear();
    }

    public int getEndYear() {
        return weather.getEndYear();
    }

    public int[] getJDAY() {
        return weather.getJDAY();
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        this.RAINFALLSTATION = weather.getRainLoc();
        this.CLIMATESTATION = weather.getETLoc();
        updateDate(weather);
    }

    private void updateDate(Weather weather) {
        int NYR = weather.getNYR();
        int[] JDAY = weather.getJDAY();
        double[][] ETP = weather.getETP();
        double[][] RAIN = weather.getRAIN();
        for (int k = 0; k < 365; k++) {
            JDAY[k] = k + 1;
        }

        if (J1SAVE >= JNSAVE) {
            NYR = NYR - 1; // Reduced no of year
            for (int iy = 0; iy < NYR; iy++) {
                int j = -1;
                int iy1 = iy + 1;
                for (int jd = J1SAVE - 1; jd < 365; jd++) {
                    j = j + 1;
                    JDAY[j] = jd + 1;
                    ETP[iy][j] = ETP[iy][jd];
                    RAIN[iy][j] = RAIN[iy][jd];
                }
                for (int jd = 0; jd < JNSAVE; jd++) {
                    j = j + 1;
                    JDAY[j] = jd + 1;

                    ETP[iy][j] = ETP[iy1][jd];
                    RAIN[iy][j] = RAIN[iy1][jd];
                }
            }
            weather.setNYR(NYR);
            weather.setJDAY(JDAY);
        }
    }
    
    public void setWATERHOLDINGCAPACITY(String WHC) {
        for (Soil soil : soils) {
            soil.setWHC(WHC);
        }
        this.WATERHOLDINGCAPACITY = WHC;
    }
    
    public void setSoils(ArrayList<Soil> soils, String WHC) {
        this.soils = soils;
        setWATERHOLDINGCAPACITY(WATERHOLDINGCAPACITY);
    }

    //read the file for climate data and set it to a array
    public void setClimateFile(InputStreamReader ir) {
        try {
            BufferedReader br = new BufferedReader(ir);
            String line = br.readLine();
            weather.setETLoc(line.split(" ")[0]);
            double[][] ETP = weather.getETP();
            final int startYear = weather.getStartYear();
            final int endYear = weather.getEndYear();
            int NYR = weather.getNYR();
            int[] JDAY = weather.getJDAY();
            line = br.readLine();
            int i = 0;
            while (line.charAt(i) == ' ') {
                i++;
            }
            line = line.substring(i);
            i = 0;
            while (line.charAt(i) != ' ') {
                i++;
            }
            String year = line.substring(0, i).trim();
            int totalYears = Integer.parseInt(year);
            int cyear = 0;
            for (int j = 0; j < totalYears; j++) {
                int iYear = Integer.parseInt(br.readLine());
                int k = 0;
                int l = 0;

                while (l < 365) {

                    String[] parts = br.readLine().split(" ");

                    for (String x : parts) {

                        if (x.length() > 0 && iYear >= startYear && iYear <= endYear) {
                            ETP[cyear][k] = Double.parseDouble(x);
                            k++;
                        }
                        if (x.length() > 0) {
                            l++;
                        }
                    }
                }
                if (iYear >= startYear && iYear <= endYear) {
                    cyear++;
                }
            }
            br.readLine();

            for (int k = 0; k < 365; k++) {
                JDAY[k] = k + 1;
            }

            if (J1SAVE >= JNSAVE) {
                NYR = NYR - 1; // Reduced no of year
                for (int iy = 0; iy < NYR; iy++) {
                    int j = -1;
                    int iy1 = iy + 1;
                    for (int jd = J1SAVE - 1; jd < 365; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;
                        ETP[iy][j] = ETP[iy][jd];

                    }
                    for (int jd = 0; jd < JNSAVE; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;

                        ETP[iy][j] = ETP[iy1][jd];

                    }
                }
            }
//            for(int k=0;k<ETP.length;k++){
//                System.out.println("Year "+k);
//                for(int j=0;j<ETP[0].length;j++){
//                    System.out.println("ETP " +j+":"+ETP[k][j]);
//                }
//            }

            weather.setNYR(NYR);
            weather.setJDAY(JDAY);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
    }

    //read the file for climate data and set it to a array
    //Hiranava 25 Aug 2016 Separating Rainfall data to another method.
    public void setRainfallFile(InputStreamReader ir) {
        try {
            BufferedReader br = new BufferedReader(ir);
            String line = br.readLine();
            weather.setRainLoc(line.split(" ")[0]);
            double[][] RAIN = weather.getRAIN();
            final int startYear = weather.getStartYear();
            final int endYear = weather.getEndYear();
            int NYR = weather.getNYR();
            int[] JDAY = weather.getJDAY();
            line = br.readLine();
            int i = 0;
            int cyear = 0;
            while (line.charAt(i) == ' ') {
                i++;
            }
            line = line.substring(i);
            i = 0;
            while (line.charAt(i) != ' ') {
                i++;
            }
            String year = line.substring(0, i).trim();
            int totalYears = Integer.parseInt(year);

            for (int j = 0; j < totalYears; j++) {
                String line1 = br.readLine();
                int iYear = Integer.parseInt(line1);

                int k = 0, l = 0;
                while (l < 365) {
                    String curLine = br.readLine();
                    String[] parts = curLine.split(" ");

                    for (String x : parts) {

                        if (x.length() > 0 && iYear >= startYear && iYear <= endYear) {
                            RAIN[cyear][k] = Double.parseDouble(x);
                            k++;
                        }
                        if (x.length() > 0) {
                            l++;
                        }
                    }

                }
                if (iYear >= startYear && iYear <= endYear) {
                    cyear++;
                }
            }

            for (int k = 0; k < 365; k++) {
                JDAY[k] = k + 1;
            }

            if (J1SAVE >= JNSAVE) {
                NYR = NYR - 1; // Reduced no of year
                for (int iy = 0; iy < NYR; iy++) {
                    int j = -1;
                    int iy1 = iy + 1;
                    for (int jd = J1SAVE - 1; jd < 365; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;
                        RAIN[iy][j] = RAIN[iy][jd];
                    }
                    for (int jd = 0; jd < JNSAVE; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;
                        RAIN[iy][j] = RAIN[iy1][jd];
                    }
                }
            }

            weather.setNYR(NYR);
            weather.setJDAY(JDAY);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
    }

    public void setDefaultSoil(Soil soil) {
//        SSERIESNAME = soil.getSERIESNAME();
//        SOILSMAPUNITCODE = soil.getSOILSERIESKEY();
//        
//        SNAME = soil.getSNAME();
//        SOILCOMPCODE = soil.getCOMPKEY();
//        
//        TXT = soil.getTXT();
//        DU = soil.getDU();
//        WCL = soil.getWCL();
//        WC = soil.getWC();
//        WCU = soil.getWCU();
//        NL = soil.getNL();
    }

    //04 Sep 2016: Hiranava Das: new function to set start and end year
    public void setStartEndYear(InputStreamReader irET, InputStreamReader irRF) throws IOException {
        BufferedReader brRF;
        BufferedReader brET;
        int startYear = weather.getStartYear();
        int endYear = weather.getEndYear();
        int NYR = weather.getNYR();
        try {
            //getting start year and end year from ET data

            brET = new BufferedReader(irET);

            String lineET = brET.readLine();
            //brET.mark(10000);
            weather.setETLoc(lineET.split(" ")[0]);
            lineET = brET.readLine();
            int i = 0;
            int year = 0;
            while (lineET.charAt(i) == ' ') {
                i++;
            }
            lineET = lineET.substring(i);
            i = 0;
            while (lineET.charAt(i) != ' ') {
                i++;
            }
            int newYear = Integer.parseInt(lineET.substring(0, i).trim());
            for (int j = 0; j < newYear; j++) {
                lineET = brET.readLine();
                year = Integer.parseInt(lineET);
                if (j == 0) {
                    startYear = year;
                }
                int k = 0;
                while (k < 365) {
                    String[] parts = brET.readLine().split(" ");
                    for (String x : parts) {
                        if (x.length() > 0) {
                            k++;
                        }
                    }
                }

            }
            endYear = year;
            //getting start year and end year from Rainfall data
            brRF = new BufferedReader(irRF);
            String lineRF = brRF.readLine();
            weather.setRainLoc(lineRF.split(" ")[0]);
            lineRF = brRF.readLine();
            i = 0;
            year = 0;
            while (lineRF.charAt(i) == ' ') {
                i++;
            }
            lineRF = lineRF.substring(i);
            i = 0;
            while (lineRF.charAt(i) != ' ') {
                i++;
            }
            newYear = Integer.parseInt(lineRF.substring(0, i).trim());

            for (int j = 0; j < newYear; j++) {
                lineRF = brRF.readLine();
                year = Integer.parseInt(lineRF);

                if (j == 0 && startYear < year) {
                    startYear = year;
                }
                int k = 0;
                while (k < 365) {
                    String[] parts = brRF.readLine().split(" ");
                    for (String x : parts) {
                        if (x.length() > 0) {
                            k++;
                        }
                    }
                }

            }
            if (endYear > year) {
                endYear = year;
            }

            NYR = endYear - startYear + 1;

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        } finally {
            weather.setStartYear(startYear);
            weather.setEndYear(endYear);
            weather.setNYR(NYR);
        }

    }

    public void setAKC34(double akc3, double akc4) {
        AKC3 = akc3;
        AKC4 = akc4;
    }

    // Not used
    public double getAKC34(int index) {
        if (index == 3) {
            return AKC3;
        } else {
            return AKC4;
        }
    }

    public void setDCOEFPerennial(double drzirr, double drztot, double[] akc, double[] aldp) {
        DRZIRR = drzirr;
        DRZTOT = drztot;
        AKC = akc;
        ALDP = aldp;
    }

    public void setDCOEFAnnual(double dzn, double dzx, double[] f, double[] ald) {
        DZN = dzn;
        DZX = dzx;
        F = f;
        ALD = ald;
    }

//    public void setSoils(ArrayList soils, ) {
//        
//    }
    public void setSoilData(SoilData soilData) {
        //this.soil = soilData.getSoils().get(0);
//        this.soilData = soilData;
        this.soils = soilData.getSoils();

        //SNAME = soil.getName();
        //TXT = soil.getTXT();
        //DU = soil.getDU();
//        double WCLn[] = {.9,.9,.9,.9,.9,.9};
//        WCL = WCLn;
        //WCL = soil.getWCL();
        //WCU = soil.getWCU();
//        double WCUn [] = {.9,.9,.9,.9,.9,.9};
//        WCU = WCUn;
        //WC = soil.getWC();
        //NL = soil.getNL();
        /*SNAME = firstSoil.getName();
         TXT = firstSoil.getTXT();
         DU = firstSoil.getDU();
         WCL = firstSoil.getWCL();
         WCU = firstSoil.getWCU();
         WC = firstSoil.getWC();
         NL = firstSoil.getNL();*/
    }
}
