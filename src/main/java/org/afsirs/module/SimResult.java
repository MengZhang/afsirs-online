package org.afsirs.module;

import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.util.ArrayList;
import lombok.Data;

/**
 * The container class for simulation result
 *
 * @author Meng Zhang
 */
@Data
public class SimResult {
    
    double totalArea = 0.0;
//    double[] soilArea;
    double[][] RAIN;
    
    // ArrayList for Soilnames with negative value error
    ArrayList<String> soilNames = new ArrayList<>();
    
    // ArrayList to Hold all the data
    ArrayList<PDAT> allSoilInfo = new ArrayList<>();
    
    ArrayList<SummaryReport> summaryList = new ArrayList<>();
    ArrayList<PdfPTable> summaryTables = new ArrayList<>();
    File outFile, summaryFile, summaryFileExcel, calculationExcel;
}
