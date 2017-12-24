/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.afsirs.module;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author rohit
 * Singleton class holding all the information about all the files.
 */
public class SoilData {

    // This map was not giving the proper details of the soils when there were multiple soils with same name. 
    public HashMap<String, ArrayList<Soil>> completeSoilData;

    private static String currentKey = "";
    public static SoilData soilData;
    private static String WHC = "Minimum";
 
    public SoilData () {
        completeSoilData = new HashMap<String, ArrayList<Soil>>();

    }
    
    /*public static SoilData getSoilDataInstance () {
        if (soilData == null) {
            soilData = new SoilData ();
        }
        return soilData;
    }*/
    
    public void setKey (String key) {
        currentKey = key;
    }
    
    public void setWHC(String whc) {
        WHC = whc;
        updateCurrentSoilWC();
    }
    
    private void updateCurrentSoilWC() {
        for (Soil soil : getSoils()) {
            soil.setWHC(WHC);
        }
    }
   
    public void addSoil (String key, Soil soil) {
        ArrayList<Soil> list = null;
        currentKey = key;
        if (completeSoilData.containsKey(key)) {
            list = completeSoilData.get(key);
            list.add(soil);
        } else {
            list = new ArrayList<Soil> ();
            list.add(soil);
            completeSoilData.put (key, list);
        }
    }
    
    public void addSoilList (String key, ArrayList<Soil> soilList) {
            currentKey = key;
            completeSoilData.put(key, soilList);
    }
    
    public boolean containSoilList(String key) {
        return completeSoilData.containsKey(key);
    }
    
    public ArrayList<Soil> getSoilsFromFile (String fileName) {
        currentKey = fileName;
        ArrayList<Soil> list = null;
        if (completeSoilData.containsKey(fileName)) {
            list = completeSoilData.get(fileName);
            updateCurrentSoilWC();
        }
        return list;
    }
    
    public ArrayList<Soil> getSoils () {
        ArrayList<Soil> list = null;
        if (completeSoilData.containsKey(currentKey)) {
            list = completeSoilData.get(currentKey);
        }
        return list;
    }
}
