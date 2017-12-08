/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afsirs.module;

/**
 *
 * @author rohit
 */
public class Soil {
        int ID;
        String SNAME;
        String SOILSERIESKEY;


        String COMPKEY;
        String SERIESNAME;
        String[] TXT = new String[3];
        int NL;
        double[] WC;
        double[] WCL;
        double[] WCU;
        double[] DU;
        double soilTypeArea;
        double totalAvgIrrReq;

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

        public String getName(){
            return SNAME;
        }

        public String[] getTXT(){
            return TXT;
        }
        public double[] getWC(){
            return WC;
        }

        public double[] getWCL(){
            return WCL;
        }

        public double[] getWCU(){
            return WCU;
        }

        public double[] getDU(){
            return DU;
        }

        public int getNL(){
            return NL;
        }
        
        public String getSOILSERIESKEY() {
            return SOILSERIESKEY;
        }

        public String getCOMPKEY() {
            return COMPKEY;
        }

        public void setSoilTypeArea (double soilTypeArea) {
            this.soilTypeArea = soilTypeArea;
        }

        public double getSoilTypeArea () {
            return this.soilTypeArea;
        }

        public String getSNAME() {
            return SNAME;
        }

        public String getSERIESNAME() {
            return SERIESNAME;
        }
        public void setTotalAvgIrrReq(double totalAvgIrrReq){
            this.totalAvgIrrReq = totalAvgIrrReq;
        }
        public double getTotalAvgIrrReq(){
            return this.totalAvgIrrReq;
        }
}
