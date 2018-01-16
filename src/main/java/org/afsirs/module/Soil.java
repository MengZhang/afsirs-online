package org.afsirs.module;

import java.util.Arrays;
import lombok.Data;
import static org.afsirs.module.util.Util.round;

/**
 *
 * @author rohit
 * @author Meng Zhang
 */
@Data
public class Soil {
//        private int ID;
        private String SNAME;
        private String SOILSERIESKEY;
        private String soilSymbolNum;

        private String COMPKEY;
        private String SERIESNAME;
        private String[] TXT = new String[3];
        private int NL;
        private double[] WC;
        private double[] WCL;
        private double[] WCU;
        private double[] DU;
        private double soilTypeArea;
//        private double totalAvgIrrReq;
        
        public Soil cloneData() {
            Soil ret = new Soil(SNAME, SOILSERIESKEY, COMPKEY, SERIESNAME, soilSymbolNum, NL);
            ret.setValues(
                    Arrays.copyOf(WC, WC.length),
                    Arrays.copyOf(WCL, WCL.length),
                    Arrays.copyOf(WCU, WCU.length),
                    Arrays.copyOf(DU, DU.length),
                    Arrays.copyOf(TXT, TXT.length));
            ret.setSoilTypeArea(soilTypeArea);
            return ret;
        }

        public Soil(int id, String soilCompName, String soilSeriesKey, String compKey, String seriesName, int nl){
            
            this(id, soilCompName, soilSeriesKey, compKey, seriesName, "", nl);
        }

        public Soil(int id, String soilCompName, String soilSeriesKey, String compKey, String seriesName,String soilSymbolNum, int nl){
            
            // SOil Series Name and the Soil Map Unit Code
            SERIESNAME = seriesName;
            SOILSERIESKEY = soilSeriesKey;

            // Soil Name and the Soil Code
            SNAME = soilCompName;
            COMPKEY = compKey;
            this.soilSymbolNum = soilSymbolNum;
            
//            ID = id;
            NL = nl;
        }
        
//        public Soil(String soilCompName, String soilSeriesKey, String compKey, String seriesName, int nl){
//            this(soilCompName, soilSeriesKey, compKey, seriesName, "", nl);
//        }

        public Soil(String soilCompName, String soilSeriesKey, String compKey, String seriesName, String soilSymbolNum, int nl){
            
            // SOil Series Name and the Soil Map Unit Code
            SERIESNAME = seriesName;
            SOILSERIESKEY = soilSeriesKey;

            // Soil Name and the Soil Code
            SNAME = soilCompName;
            COMPKEY = compKey;
            this.soilSymbolNum = soilSymbolNum;
            NL = nl;
        }

        public void setValues(double[] wc, double[] wcl, double[] wcu, double[] du, String[] txt){
            WC = wc;
            WCL = wcl;
            WCU = wcu;
            DU = du;
            TXT = txt;
        }
        
        public void setValues(String whc, double[] wcl, double[] wcu, double[] du, String[] txt){
            WCL = wcl;
            WCU = wcu;
            DU = du;
            TXT = txt;
            setWHC(whc);
        }
        
        public void setWHC(String whc) {
            if (whc.equalsIgnoreCase("Average")) {
                WC = average(WCL, WCU);
            } else if (whc.equals("Maximum")) {
                WC = roundArr(WCU, 3);
            } else if (whc.equals("Minimum")) {
                WC = roundArr(WCL, 3);
            }
        }
        
        private double[] average(double[] arr1, double[] arr2) {
            double[] ret = new double[arr1.length];
            for (int i = 0; i < arr1.length; i++) {
                ret[i] = round((arr1[i] + arr2[i]) * 0.5, 3);
            }
            return ret;
        }
        
        private double[] roundArr(double[] input, int digit) {
            double[] ret = new double[input.length];
            for (int i = 0; i < input.length; i++) {
                ret[i] = round(input[i], digit);
            }
            return ret;
        }

//        public String getName(){
//            return SNAME;
//        }
//
//        public String[] getTXT(){
//            return TXT;
//        }
//        public double[] getWC(){
//            return WC;
//        }
//
//        public double[] getWCL(){
//            return WCL;
//        }
//
//        public double[] getWCU(){
//            return WCU;
//        }
//
//        public double[] getDU(){
//            return DU;
//        }
//
//        public int getNL(){
//            return NL;
//        }
//        
//        public String getSOILSERIESKEY() {
//            return SOILSERIESKEY;
//        }
//
//        public String getCOMPKEY() {
//            return COMPKEY;
//        }
//
//        public void setSoilTypeArea (double soilTypeArea) {
//            this.soilTypeArea = soilTypeArea;
//        }
//
//        public double getSoilTypeArea () {
//            return this.soilTypeArea;
//        }
//
//        public String getSNAME() {
//            return SNAME;
//        }
//
//        public String getSERIESNAME() {
//            return SERIESNAME;
//        }
//        public void setTotalAvgIrrReq(double totalAvgIrrReq){
//            this.totalAvgIrrReq = totalAvgIrrReq;
//        }
//        public double getTotalAvgIrrReq(){
//            return this.totalAvgIrrReq;
//        }
}
