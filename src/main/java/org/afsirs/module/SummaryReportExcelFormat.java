package org.afsirs.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rohit
 * @author Meng Zhang
 * 
 * 
 * Note: this is necessaary to close the file.
 * 
 */
public class SummaryReportExcelFormat {

    private static int rowNum = 1;
    private static int colNum = 1;
    
    private String fileName;
    private FileOutputStream out;
    private Map < Integer, Object[] > summaryInfo;
    private XSSFRow row;
    private XSSFSheet spreadsheet;
    private XSSFWorkbook workbook;
    
    
    public SummaryReportExcelFormat(String fName) {
        
        String [] fNameString = fName.split("/");
        this.fileName = fNameString[fNameString.length-1];
        if (fName == null) {
            //throw new Exception ("File Name cant be null");
        }
        rowNum = 1;
        colNum = 1;
        //Create blank workbook
        workbook = new XSSFWorkbook();
        //Create a blank sheet
        spreadsheet = workbook.createSheet(fileName);
        spreadsheet.addMergedRegion(new CellRangeAddress (rowNum,rowNum,1,14));
        //This data needs to be written (Object[])
        summaryInfo =  new TreeMap < Integer, Object[] >();
        row = spreadsheet.createRow(rowNum);
        try {
            //Write the workbook in file system
            out = new FileOutputStream(new File(fName));
        } catch (FileNotFoundException e) {
            
            
        }
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        SummaryReportExcelFormat.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        SummaryReportExcelFormat.colNum = colNum;
    }
    
    
    
    public void insertEmptyLine (int rowCount) {
        rowNum+=rowCount;
        colNum = 1;
        row = spreadsheet.createRow(rowNum);
    }
    
    public void insertTableRow () {
        rowNum++;
    }
    
    public void insertTableData () {
        rowNum++;
    }
    
    private XSSFCellStyle getCellStyle (int type) {
        XSSFFont font = workbook.createFont();
        XSSFCellStyle style = null;

        switch (type) {
            case 1: 
                font.setFontHeightInPoints((short) 15);
                font.setFontName("IMPACT");
                font.setItalic(true);
                font.setColor(HSSFColor.BLUE.index);
                style = workbook.createCellStyle();
                style.setWrapText(true);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFont(font);
                break;
                
            case 2: 
                font.setFontHeightInPoints((short) 15);
                font.setFontName("IMPACT");
                font.setItalic(true);
                font.setColor(HSSFColor.BLUE.index);
                style = workbook.createCellStyle();
                style.setWrapText(true);
                style.setShrinkToFit(true);
                style.setFont(font);
                break;
                
            case 3: 
                font.setFontHeightInPoints((short) 15);
                font.setFontName("IMPACT");
                font.setItalic(true);
                font.setColor(HSSFColor.BLUE.index);
                style = workbook.createCellStyle();
                style.setWrapText(true);
                style.setShrinkToFit(true);
                style.setFont(font);
                break;

            case 4: 
                font.setFontHeightInPoints((short) 15);
                font.setFontName("IMPACT");
                font.setItalic(true);
                font.setColor(HSSFColor.BLUE.index);
                style = workbook.createCellStyle();
                style.setWrapText(true);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFont(font);
                break;
                
            case 5: 
                font.setFontHeightInPoints((short) 15);
                font.setFontName("IMPACT");
                font.setItalic(true);
                font.setColor(HSSFColor.BLUE.index);
                style = workbook.createCellStyle();
                style.setWrapText(true);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFont(font);
                break;                
                

        }
        
        
        return style;
    }
    
    public void mergeCells () {
        spreadsheet.addMergedRegion(new CellRangeAddress (rowNum,rowNum,colNum,14));
        spreadsheet.autoSizeColumn(colNum, true);
    }
    
    public void insertDataWithStyle (String s, int styleType, boolean incrementRow, boolean incrementCol) {
        
        Cell cell = row.createCell(colNum);
        cell.setCellValue(s);
       
        if (styleType>0) {
            XSSFCellStyle style = getCellStyle(styleType);
            cell.setCellStyle(style);
            
        }
        
        if (incrementRow) {
            rowNum++;
            row = spreadsheet.createRow(rowNum);
        }
        
        if (incrementCol) {
            colNum++;
        }
    }
    
    public void insertDataWithStyle (Double d, int styleType, boolean incrementRow, boolean incrementCol) {
        
        Cell cell = row.createCell(colNum);
        /*BigDecimal bd = null;
        try {
            bd = new BigDecimal(d);
            bd = bd.round(new MathContext(3));
            double rounded = bd.doubleValue();
            cell.setCellValue(rounded);
        } catch (Exception e) {
            System.out.println("Some values were null");
        }*/
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.CEILING);
        cell.setCellValue(Double.parseDouble(df.format(d)));

        if (styleType>0) {
            XSSFCellStyle style = getCellStyle(styleType);
            cell.setCellStyle(style);
            
        }
        
        if (incrementRow) {
            rowNum++;
            row = spreadsheet.createRow(rowNum);
        }
        
        if (incrementCol) {
            colNum++;
        }
    }
    
    
    public void insertData (String [] s) {
        
        Object [] objArr = new Object [s.length];
        for (int i = 0; i < s.length; i++) {
            objArr[i] = (Object)s[i];
        }
        summaryInfo.put(rowNum++, objArr);
        //rowNum++;
    }
    
    public void setFootNoteExcelFile (String s) {
        
        insertEmptyLine(1);
        insertDataWithStyle("Note ", 0, false, true);
        mergeCells();
        insertDataWithStyle(s, 0, false, true);        
        
        
        
    }
    
    
    public void closeFileHandler () throws Exception {
//        Set < Integer > keyid = summaryInfo.keySet();
//        int rowid = 0;
//        for (Integer key : keyid)
//        {
//            row = spreadsheet.createRow(rowid++);
//            Object [] objectArr = summaryInfo.get(key);
//            int cellid = 2;
//            for (Object obj : objectArr)
//            {
//               Cell cell = row.createCell(cellid++);
//               cell.setCellValue((String)obj);
//               cell.setCellStyle(getCellStyle(1));
//            }
//        }
        workbook.write(out);
        out.close();
    }
}
