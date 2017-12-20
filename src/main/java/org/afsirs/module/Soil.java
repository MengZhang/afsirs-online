package org.afsirs.module;

import lombok.Data;

/**
 *
 * @author rohit
 * @author Meng Zhang
 */
@Data
public class Soil {
        private int ID;
        private String SNAME;
        private String SOILSERIESKEY;

        private String COMPKEY;
        private String SERIESNAME;
        private String[] TXT = new String[3];
        private int NL;
        private double[] WC;
        private double[] WCL;
        private double[] WCU;
        private double[] DU;
        private double soilTypeArea;
        private double totalAvgIrrReq;

        public Soil(int id, String soilCompName, String soilSeriesKey, String compKey, String seriesName, int nl){
            
            // SOil Series Name and the Soil Map Unit Code
            SERIESNAME = seriesName;
            SOILSERIESKEY = soilSeriesKey;

            // Soil Name and the Soil Code
            SNAME = soilCompName;
            COMPKEY = compKey;

            
            ID = id;
            NL = nl;
        }

        public void setValues(double[] wc, double[] wcl, double[] wcu, double[] du, String[] txt){
            WC = wc;
            WCL = wcl;
            WCU = wcu;
            DU = du;
            TXT = txt;
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
