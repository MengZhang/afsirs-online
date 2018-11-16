package org.afsirs.module;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static org.afsirs.module.Messages.DOC_HEADER;
import static org.afsirs.module.Messages.USER_DETAILS;
import static org.afsirs.module.Messages.USER_DETAILS_EXCEL;
import org.afsirs.module.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class AFSIRSOutput {

    static Logger LOG = LoggerFactory.getLogger(AFSIRSModule.class);

    private static final Font BLACK_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    private static final Font BLACK_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    //private final Font BLACK_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.RED);
    private static final Font BLUE_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLUE);
    private static final Font BLUE_LINK = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLUE);
    private static final Font BLUE_HEADER = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);
    private static final Font GREEN_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GREEN);

    public static SimOutput run(UserInput input) {
        return run(AFSIRSModule.run(input), input);
    }
    
    public static SimOutput run(SimResult simRet, UserInput input) {

        SimOutput output = new SimOutput();

//        output.setOutFile(new File(input.getOutFile()));
        output.setSummaryFile(new File(input.getSummaryFile()));
        output.setSummaryFileExcel(new File(input.getSummaryFileExcel()));
        output.setCalculationExcel(new File(input.getCalculationExcel()));

        SummaryReportExcelFormat excelSummary, excelCal;
        Document bwOutputSummaryFile = new Document();
        ArrayList<Soil> soils = input.getSoils();
        double totalArea = 0.0;

        for (Soil s : soils) {
            totalArea += s.getSoilTypeArea();
        }
        if (totalArea == 0) {
            totalArea = input.getPlantedAcres();
        }
        simRet.setTotalArea(totalArea);
        simRet.setPlantedArea(input.getPlantedAcres());
        
        try {
//        try (BufferedWriter bwOutputFile = new BufferedWriter(new FileWriter(output.getOutFile(), false))) {
            PdfWriter.getInstance(bwOutputSummaryFile, new FileOutputStream(output.getSummaryFile()));
            excelSummary = new SummaryReportExcelFormat(output.getSummaryFileExcel());

//            initOutputFile(bwOutputFile, input);
            bwOutputSummaryFile.open();
            excelSummary.insertEmptyLine(12);

            formatSummaryOutputFile(input, simRet, excelSummary, bwOutputSummaryFile);

            ArrayList<SoilSeriesSummaryReport> reports = simRet.getSummaryList();
//            for (SoilSeriesSummaryReport report : reports) {
//                infoInInches(report, ret);
//            }

            //Write the permit file.
//            savePermitFile(new File("Permit/"), input);
            excelCal = buildCalculationExcel(input, simRet, output);
            // Set the foot note here in the excel file and the pdf
            excelSummary.setFootNoteExcelFile(Messages.FOOTNOTE[0]);
            setIrrigationWeightedAverageExcel(simRet, excelSummary, simRet.getSoilTypeSummaryList());
            PdfPTable tableIn = prepareWeightedAverageInchesTable();
            PdfPTable tableGal = prepareWeightedAverageGallonTable();
            setIrrigationWeightedAverage(tableIn, tableGal, simRet.getSoilTypeSummaryList(), input);

            bwOutputSummaryFile.add(new Paragraph("\r\n"));
            if (!reports.isEmpty()) {
                PdfPTable excel = generalInformation(reports.get(0), excelSummary);
                bwOutputSummaryFile.add(createSectionTitle("Weather data during the season: "));
                bwOutputSummaryFile.add(excel);
                bwOutputSummaryFile.add(new Paragraph("\r\n"));

                bwOutputSummaryFile.add(createSectionTitle("Total Irrigation requirements (all soil series): "));
                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(tableIn);
                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(tableGal);
            }
            addDeviations(input, bwOutputSummaryFile);

            bwOutputSummaryFile.newPage();
            bwOutputSummaryFile.add(createSectionTitle("Total Irrigaiton Requirements by soil series: "));
            for (SoilSeriesSummaryReport report : reports) {
                ArrayList<PdfPTable> summaryTables = finalSummaryOutput(report, excelSummary, simRet);
                for (PdfPTable t : summaryTables) {
                    bwOutputSummaryFile.add(t);
                }
            }

            bwOutputSummaryFile.newPage();
            addSoilMap(input, bwOutputSummaryFile, simRet);

//            bwOutputFile.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace(System.err);
            return output;
        }

        bwOutputSummaryFile.close();
        try {
            excelSummary.closeFileHandler();
            excelCal.closeFileHandler();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return output;
    }
    
    /**
     * Reorganize data for graph output
     *
     * @param simRet
     * @param type 0, Monthly; 1, Bi-Weekly; 2, Weekly
     * @return Time-series output value by soil in a list
     */
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
        addParagraphToTableSoilName(t, key, value, false, Element.ALIGN_LEFT);
    }

    private static void addParagraphToTableSoilName(PdfPTable t, String key, String value, boolean noWrap, int align) {
        PdfPCell c;
        Paragraph p = new Paragraph();

        Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
        Chunk valChunk = new Chunk(value, BLACK_BOLD);

        p.add(keyChunk);
        p.add(valChunk);

        c = new PdfPCell(p);
        c.setNoWrap(noWrap);
        c.setHorizontalAlignment(align);
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
            e.printStackTrace(System.err);
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
            addUserDetails(t, USER_DETAILS[10], input.getCLIMATELOC());
            addUserDetails(t, USER_DETAILS[11], input.getRAINFALLLOC());
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
            if (input.isPerennialCrop()) {
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
            excelSummary.insertDataWithStyle(input.getCLIMATELOC(), 0, false, true);

            excelSummary.insertEmptyLine(1);

        } catch (DocumentException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static PdfPTable prepareWeightedAverageInchesTable() throws DocumentException {
        PdfPTable tableWeightedInches = new PdfPTable(14);

        tableWeightedInches.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedInches, "Irrigation Weighted Average (Inches)");
        //bwOutputSummaryFileExcel.insertDataWithStyle("Irrigation Weighted Average (Inches)", 0, true, true);
        createTableHeader(tableWeightedInches);

        return tableWeightedInches;
    }

    private static PdfPTable prepareWeightedAverageGallonTable() throws DocumentException {
        PdfPTable tableWeightedGallon = new PdfPTable(14);
        tableWeightedGallon.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedGallon, "Irrigation Weighted Average (Million Gallons)");
        //bwOutputSummaryFileExcel.insertDataWithStyle("Irrigation Weighted Average (Gallons)", 0, true, true);
        createTableHeader(tableWeightedGallon);

        return tableWeightedGallon;
    }

    private static void setIrrigationWeightedAverageExcel(SimResult simRet, SummaryReportExcelFormat excelSummary, ArrayList<SoilTypeSummaryReport> summaryList) throws DocumentException {
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
            double irrGa = irr * simRet.getPlantedArea() * 27154;
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
            double irrGa = irr * simRet.getPlantedArea() * 27154;
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
            double irrGa = irr * simRet.getPlantedArea() * 27154;
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

    private static void setIrrigationWeightedAverage(PdfPTable tIn, PdfPTable tGa, ArrayList<SoilTypeSummaryReport> summaryList, UserInput input) throws DocumentException {

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

    private static ArrayList<PdfPTable> finalSummaryOutput(SoilSeriesSummaryReport summaryReport, SummaryReportExcelFormat excelSummary, SimResult ret) {
        ArrayList<PdfPTable> summaryTables = new ArrayList();
        try {

            double area = ret.getPlantedArea();

            PdfPTable t = new PdfPTable(3);
            for (int i = 0; i < 6; i++) {
                addParagraphToTableSoilName(t, " ", " ");
                addParagraphToTableSoilName(t, " ", " ");
            }

            String soilPercentStr = "< 0.01";
            if (ret.getTotalArea() != 0) {
                double soilPercent = ((summaryReport.getSoilArea() * 100) / ret.getTotalArea());
                String tmp = String.format("%6.2f", soilPercent);
                if (!tmp.equals("0.00")) {
                    soilPercentStr = tmp;
                }
            }

            addParagraphToTableSoilName(t, "Soil Series Name : ", summaryReport.getSoilName(), true, Element.ALIGN_LEFT);
            addParagraphToTableSoilName(t, " ", " ");
            addParagraphToTableSoilName(t, " ", " ");
            addParagraphToTableSoilName(t, "Soil Map Unit Symbol# : ", summaryReport.getSoilSymbolNum());
            addParagraphToTableSoilName(t, "Soil Map Unit Code : ", summaryReport.getSoilKey());
            addParagraphToTableSoilName(t, "Soil Percentage : ", soilPercentStr);
            addParagraphToTableSoilName(t, "Soil Area(ACRES) : ", summaryReport.getSoilAreaStr());
            addParagraphToTableSoilName(t, " ", " ");

            excelSummary.insertEmptyLine(2);
            excelSummary.insertDataWithStyle("Soil Series Name", 5, false, true);
            excelSummary.insertDataWithStyle(summaryReport.getSoilName(), 5, false, true);
            excelSummary.insertEmptyLine(1);
            excelSummary.insertDataWithStyle("Soil Map Unit Symbol#", 5, false, true);
            excelSummary.insertDataWithStyle(summaryReport.getSoilSymbolNum(), 5, false, true);
            excelSummary.insertDataWithStyle("Soil Map Unit Code", 5, false, true);
            excelSummary.insertDataWithStyle(summaryReport.getSoilKey(), 5, false, true);
            excelSummary.insertDataWithStyle("Soil Percentage", 5, false, true);
            excelSummary.insertDataWithStyle(soilPercentStr, 5, false, true);
            excelSummary.insertDataWithStyle("Soil Area(ACRES)", 5, false, true);
            excelSummary.insertDataWithStyle(summaryReport.getSoilAreaStr(), 5, false, true);

            summaryTables.add(t);
            if (summaryReport.getTotalOneinTen() == -99.0 || summaryReport.getTotalTwoinTen() == -99.0) {
                PdfPTable error = new PdfPTable(1);
                addParagraphToTable(error, Messages.AFSIRS_ERROR);
                summaryTables.add(error);

            }
            summaryTables.add(infoInInchesSummary(summaryReport, excelSummary, ret));
            summaryTables.add(probablityInfoInGallons(summaryReport, excelSummary, area));
            summaryTables.add(infoInInchesWeightedSummary(summaryReport, excelSummary, ret));
            summaryTables.add(probablityInfoWeightedInGallons(summaryReport, excelSummary, area));

        } catch (DocumentException e) {
            e.printStackTrace(System.err);
        }
        return summaryTables;
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
        designTableHeaderRowCell(table, str, 1, 1);
    }

    private static void designTableHeaderRowCell(PdfPTable table, String str, int colSpan, int rowSpan) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        table.addCell(cell);
    }

    private static void designDataCell(PdfPTable table, String str) {
        designDataCell(table, str, 1, 1);
    }

    private static void designDataCell(PdfPTable table, String str, int colSpan, int rowSpan) {
        designDataCell(table, str, colSpan, rowSpan, Element.ALIGN_CENTER);
    }

    private static void designDataCell(PdfPTable table, String str, int colSpan, int rowSpan, int align) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLACK_NORMAL));
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(BaseColor.CYAN);
        cell.setColspan(colSpan);
        cell.setRowspan(rowSpan);
        table.addCell(cell);
    }

    private static Paragraph createSectionTitle(String str) {
        Paragraph p = new Paragraph(str + "\r\n\r\n");
        p.setAlignment(Paragraph.ALIGN_CENTER);
        return p;
    }

    private static PdfPTable infoInInchesSummary(SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, SimResult ret) throws DocumentException {
        double totalVal;
        String str;
        PdfPTable table = new PdfPTable(14);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});

        designTableTitleCell(table, "Details in Inches");
        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Details in Inches", 4, false, true);
        excelSummary.insertEmptyLine(1);

        createTableHeader(table);
        createExcelTableHeader(excelSummary);

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Mean Irr Req");
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);

        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getAverageIrrigationRequired(i);
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
        excelSummary.insertDataWithStyle(str, 0, false, true);
        excelSummary.insertEmptyLine(1);
        /**
         * *********2-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "2-in-10 Irr Req");
        excelSummary.insertDataWithStyle("2-in-10 Irr Req", 0, false, true);
        totalVal = summaryReport.getTotalTwoinTen();
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getTwoin10IrrigationRequired(i);
            if (val > 0) {
                str = String.format("%6.2f", val);
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
        designRowTitleCell(table, "1-in-10 Irr Req");
        excelSummary.insertDataWithStyle("1-in-10 Irr Req", 0, false, true);
        totalVal = summaryReport.getTotalOneinTen();
        if (totalVal == -99.0) {
            ret.getSoilNames().add(summaryReport.getSoilName());
        }

        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getOnein10IrrigationRequired(i);
            if (val > 0.00) {
                str = String.format("%6.2f", val);
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
        return table;
    }

    private static PdfPTable infoInInchesWeightedSummary(SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, SimResult ret) throws DocumentException {
        double totalVal;
        String str;
        PdfPTable table = new PdfPTable(14);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});

        designTableTitleCell(table, "Details in Inches(Weighted)");
        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Details in Inches(Weighted)", 4, false, true);
        excelSummary.insertEmptyLine(1);

        createTableHeader(table);
        createExcelTableHeader(excelSummary);

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Mean Irr Req");
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);

        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getWeightedAvgIrrRequired(i);
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
        excelSummary.insertDataWithStyle(str, 0, false, true);
        excelSummary.insertEmptyLine(1);
        /**
         * *********2-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "2-in-10 Irr Req");
        excelSummary.insertDataWithStyle("2-in-10 Irr Req", 0, false, true);
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getWeighted2In10IrrRequired(i);
            if (val > 0) {
                str = String.format("%6.2f", val);
                totalVal += val;
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
        designRowTitleCell(table, "1-in-10 Irr Req");
        excelSummary.insertDataWithStyle("1-in-10 Irr Req", 0, false, true);
        totalVal = 0.0;
        if (totalVal == -99.0) {
            ret.getSoilNames().add(summaryReport.getSoilName());
        }

        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getWeighted1In10IrrRequired(i);
            if (val > 0.00) {
                str = String.format("%6.2f", val);
                totalVal += val;
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

    private static PdfPTable probablityInfoInGallons(SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, double area) throws DocumentException {
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
            double val = summaryReport.getAverageIrrigationRequired(i);
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
            double val = summaryReport.getTwoin10IrrigationRequired(i);
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
            double val = summaryReport.getOnein10IrrigationRequired(i);
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
        excelSummary.insertDataWithStyle(totalVal, 0, false, true);
        excelSummary.insertEmptyLine(1);
        //bwOutputSummaryFile1.add(table);
        return table;
    }

    private static PdfPTable probablityInfoWeightedInGallons(SummaryReport summaryReport, SummaryReportExcelFormat excelSummary, double area) throws DocumentException {
        PdfPTable table;
        PdfPCell cell;
        double totalVal;
        String str;
        //bwOutputSummaryFile1.add(new Paragraph("\r\n"));
        table = new PdfPTable(14);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(table, "Details in Million Gallons(Weighted)");
        createTableHeader(table);

        excelSummary.insertEmptyLine(1);
        excelSummary.mergeCells();
        excelSummary.insertDataWithStyle("Details in Million Gallons(Weighted)", 4, false, true);
        excelSummary.insertEmptyLine(1);
        createExcelTableHeader(excelSummary);

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Mean Irr Req");
        excelSummary.insertDataWithStyle("Mean Irr Req", 0, false, true);
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport.getWeightedAvgIrrRequired(i);
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
            double val = summaryReport.getWeighted2In10IrrRequired(i);
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
            double val = summaryReport.getWeighted1In10IrrRequired(i);
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

    private static void addSoilMap(UserInput input, Document bwOutputSummaryFile, SimResult ret) throws DocumentException {
        bwOutputSummaryFile.add(new Paragraph("\r\n"));
        bwOutputSummaryFile.add(createSectionTitle("Soil Data: "));

        // Soil Data Table
        PdfPTable table = new PdfPTable(8);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.setTotalWidth(new float[]{80, 240, 80, 120, 60, 80, 80, 80});

        designTableHeaderRowCell(table, "Symbol", 1, 2);
        designTableHeaderRowCell(table, "Soil Name", 1, 2);
        designTableHeaderRowCell(table, "Area", 1, 2);
        designTableHeaderRowCell(table, "Soil Type", 1, 2);
        designTableHeaderRowCell(table, "Pct.", 1, 2);
        designTableHeaderRowCell(table, "Soil layer info", 3, 1);
        designTableHeaderRowCell(table, "SLLB", 1, 1);
        designTableHeaderRowCell(table, "SLLL", 1, 1);
        designTableHeaderRowCell(table, "SLDUL", 1, 1);

        LinkedHashMap<String, ArrayList<Soil>> soilSeriesMap = new LinkedHashMap();
        LinkedHashMap<String, Integer> soilSeriesSpan = new LinkedHashMap();
        for (Soil soil : input.getSoils()) {
            String key = soil.getSOILSERIESKEY();
            if (!soilSeriesMap.containsKey(key)) {
                soilSeriesMap.put(key, new ArrayList());
                soilSeriesSpan.put(key, 0);
            }
            soilSeriesMap.get(key).add(soil);
            soilSeriesSpan.put(key, soilSeriesSpan.get(key) + soil.getDU().length);
        }

        for (String key : soilSeriesMap.keySet()) {
            ArrayList<Soil> soils = soilSeriesMap.get(key);
            if (!soils.isEmpty()) {
                SoilSeriesSummaryReport report = ret.getSoilSeriesSummaryReport(soils.get(0));
                int rowSpan = soilSeriesSpan.get(key);
                designDataCell(table, report.getSoilSymbolNum(), 1, rowSpan);
                designDataCell(table, report.getSoilName(), 1, rowSpan, Element.ALIGN_LEFT);
                designDataCell(table, report.getSoilAreaStr(), 1, rowSpan);
                for (Soil soil : soils) {
                    rowSpan = soil.getDU().length;
                    designDataCell(table, soil.getSNAME(), 1, rowSpan, Element.ALIGN_LEFT);
                    designDataCell(table, soil.getSoilTypePct() + "%", 1, rowSpan);
                    for (int i = 0; i < rowSpan; i++) {
                        designDataCell(table, String.format("%5.1f", soil.getDU()[i]));
                        designDataCell(table, String.format("%6.3f", soil.getWCL()[i]));
                        designDataCell(table, String.format("%6.3f", soil.getWCU()[i]));
                    }
                }
            }
        }
        bwOutputSummaryFile.add(table);

        // Meta Table
        PdfPTable metaTable = new PdfPTable(1);
        Chunk headerChunk = new Chunk("\r\nMeta Info:", BLACK_BOLD);
        Paragraph metaPara = new Paragraph();
        PdfPCell metaTitleCell = new PdfPCell(metaPara);
        metaPara.add(headerChunk);
        metaTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        metaTitleCell.setBorder(0);
        metaTable.addCell(metaTitleCell);
        addUserDetails(metaTable, "Symbol : ", "Soil Unit Symbol #");
        addUserDetails(metaTable, "Soil Name : ", "Soil Series Name");
        addUserDetails(metaTable, "Area : ", "Map Unit Area (acres)");
        addUserDetails(metaTable, "Soil Type : ", "Soil Type Name");
        addUserDetails(metaTable, "Pct. : ", "Soil Type Percentage");
        addUserDetails(metaTable, "Soil layer info : ", "Soil Layer info for a soil type");
        addUserDetails(metaTable, "    SLLB : ", "Soil layer base depth (in)");
        addUserDetails(metaTable, "    SLLL : ", "Soil water, drained lower limit (in3/in3)");
        addUserDetails(metaTable, "    SLDUL : ", "Soil water, drained upper limit (in3/in3)");
        bwOutputSummaryFile.add(metaTable);

        // Soil Map link
        Paragraph p;
        Chunk imdb = new Chunk("here", BLUE_LINK);
        Chunk imdb2 = new Chunk("Soil Map", BLUE_LINK);
        try {
            imdb.setAnchor(Util.getSoilMapUrl(input));
            imdb2.setAnchor(Util.SOIL_MAP_BASE_URL);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(AFSIRSModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        p = new Paragraph("Click ");
        p.setAlignment(Paragraph.ALIGN_CENTER);
        p.add(imdb);
        p.add(" to view the field polygon on ");
        p.add(imdb2);
        p.add(".");
        bwOutputSummaryFile.add(p);

    }

    private static void addDeviations(UserInput input, Document bwOutputSummaryFile) throws DocumentException {
        bwOutputSummaryFile.add(new Paragraph("\r\n"));
        bwOutputSummaryFile.add(createSectionTitle("Deviations List: "));
        Paragraph p;
        PdfPCell c;
        p = new Paragraph();
        PdfPTable t = new PdfPTable(1);
        Chunk headerChunk = new Chunk("\r\nDeviations", BLACK_BOLD);
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

    private static SummaryReportExcelFormat buildCalculationExcel(UserInput input, SimResult ret, SimOutput output) {
        SummaryReportExcelFormat excelCal = new SummaryReportExcelFormat(output.getCalculationExcel());
        ArrayList<SoilSeriesSummaryReport> summaryReports = ret.getSummaryList();
//        ArrayList<Soil> soils = input.getSoils();
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
                    writeCalExcelInches(excelCal, ret, summaryReports, "getAverageIrrigationRequired");
                    break;
                case 1:
                    writeCalExcelInches(excelCal, ret, summaryReports, "getWeightedAvgIrrRequired");
                    break;
                case 2:
                    writeCalExcelInches(excelCal, ret, summaryReports, "getTwoin10IrrigationRequired");
                    break;
                case 3:
                    writeCalExcelInches(excelCal, ret, summaryReports, "getWeighted2In10IrrRequired");
                    break;
                case 4:
                    writeCalExcelInches(excelCal, ret, summaryReports, "getOnein10IrrigationRequired");
                    break;
                case 5:
                    writeCalExcelInches(excelCal, ret, summaryReports, "getWeighted1In10IrrRequired");
                    break;
                case 6:
                    writeCalExcelGallonsNet(excelCal, input, summaryReports, factorNet, "getAverageIrrigationRequired");
                    break;
                case 7:
                    writeCalExcelGallonsGross(excelCal, summaryReports, factorGross, "getAverageIrrigationRequired");
                    break;
                case 8:
                    writeCalExcelGallonsNet(excelCal, input, summaryReports, factorNet, "getTwoin10IrrigationRequired");
                    break;
                case 9:
                    writeCalExcelGallonsGross(excelCal, summaryReports, factorGross, "getTwoin10IrrigationRequired");
                    break;
                case 10:
                    writeCalExcelGallonsNet(excelCal, input, summaryReports, factorNet, "getOnein10IrrigationRequired");
                    break;
                case 11:
                    writeCalExcelGallonsGross(excelCal, summaryReports, factorGross, "getOnein10IrrigationRequired");
                    break;
                case 12:
                    excelCal.insertDataWithStyle("Mean Irr Req", 0, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    excelCal.insertDataWithStyle("", 3, false, true);
                    for (int j = 0; j < 12; j++) {
                        double meanIrrSum = 0;
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                meanIrrSum = meanIrrSum + summaryReport.getAverageIrrigationRequired(j + 1) * factorNet;
                            }
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
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                twoInTenIrrSum = twoInTenIrrSum + summaryReport.getWeighted2In10IrrRequired(j + 1) * factorNet;
                            }
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
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                oneInTenIrrSum = oneInTenIrrSum + summaryReport.getWeighted1In10IrrRequired(j + 1) * factorNet;
                            }
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
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                meanIrrSum = meanIrrSum + summaryReport.getAverageIrrigationRequired(j + 1) * factorGross;
                            }
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
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                twoInTenIrrSum = twoInTenIrrSum + summaryReport.getWeighted2In10IrrRequired(j + 1) * factorGross;
                            }
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
                        for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                            for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                                oneInTenIrrSum = oneInTenIrrSum + summaryReport.getWeighted1In10IrrRequired(j + 1) * factorGross;
                            }
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

    private static void writeCalExcelInches(SummaryReportExcelFormat excelCal, SimResult ret, ArrayList<SoilSeriesSummaryReport> summaryReports, String methodName) {
        excelCal.insertDataWithStyle("", 3, false, true);
        excelCal.insertDataWithStyle("", 3, false, true);
        excelCal.insertDataWithStyle("Area Fraction", 0, false, true);
        excelCal.insertDataWithStyle("Area Percentage", 0, true, false);
        excelCal.insertEmptyLine(1);
        try {
            Method seriesMethod = SoilSeriesSummaryReport.class.getMethod(methodName);
            Method typeMethod = SoilTypeSummaryReport.class.getMethod(methodName);
            for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                writeCalExcelRecord(seriesReport,
                        seriesReport.getSoilArea() + "",
                        seriesReport.getSoilArea() / ret.getTotalArea() * 100 + "",
                        (ArrayList<Double>) seriesMethod.invoke(seriesReport),
                        excelCal, 5, 1);
                for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                    writeCalExcelRecord(summaryReport,
                            summaryReport.getSoilArea() + "",
                            summaryReport.getSoilArea() / seriesReport.getSoilArea() * 100 + "",
                            (ArrayList<Double>) typeMethod.invoke(summaryReport),
                            excelCal, 0, 1);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void writeCalExcelGallonsNet(SummaryReportExcelFormat excelCal, UserInput input, ArrayList<SoilSeriesSummaryReport> summaryReports, double factor, String methodName) {
        excelCal.insertDataWithStyle("", 3, false, true);
        excelCal.insertDataWithStyle("", 3, false, true);
        excelCal.insertDataWithStyle("Planted Area", 0, false, true);
        excelCal.insertDataWithStyle("Efficiency", 0, true, false);
        excelCal.insertEmptyLine(1);
        try {
            Method seriesMethod = SoilSeriesSummaryReport.class.getMethod(methodName);
            Method typeMethod = SoilTypeSummaryReport.class.getMethod(methodName);
            for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                writeCalExcelRecord(seriesReport,
                        input.getPlantedAcres() + "",
                        input.getIEFF() + "",
                        (ArrayList<Double>) seriesMethod.invoke(seriesReport),
                        excelCal, 5, factor);
                for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                    writeCalExcelRecord(summaryReport,
                            summaryReport.getSoilArea() + "",
                            summaryReport.getSoilArea() / seriesReport.getSoilArea() * 100 + "",
                            (ArrayList<Double>) typeMethod.invoke(summaryReport),
                            excelCal, 0, factor);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void writeCalExcelGallonsGross(SummaryReportExcelFormat excelCal, ArrayList<SoilSeriesSummaryReport> summaryReports, double factor, String methodName) {
        try {
            Method seriesMethod = SoilSeriesSummaryReport.class.getMethod(methodName);
            Method typeMethod = SoilTypeSummaryReport.class.getMethod(methodName);
            for (SoilSeriesSummaryReport seriesReport : summaryReports) {
                writeCalExcelRecord(seriesReport,
                        "",
                        "",
                        (ArrayList<Double>) seriesMethod.invoke(seriesReport),
                        excelCal, 5, factor);
                for (SoilTypeSummaryReport summaryReport : seriesReport.getSoilTypeSummaryReportList()) {
                    writeCalExcelRecord(summaryReport,
                            summaryReport.getSoilArea() + "",
                            summaryReport.getSoilArea() / seriesReport.getSoilArea() * 100 + "",
                            (ArrayList<Double>) typeMethod.invoke(summaryReport),
                            excelCal, 0, factor);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void writeCalExcelRecord(SummaryReport summaryReport, String val1, String val2, ArrayList<Double> monthlyVals, SummaryReportExcelFormat excelCal, int style, double factor) {
        excelCal.insertDataWithStyle(summaryReport.getSoilName(), style, false, true);
        excelCal.insertDataWithStyle("", style, false, true);
        excelCal.insertDataWithStyle(val1, style, false, true);
        excelCal.insertDataWithStyle(val2, style, false, true);
        double sum = 0;
        double peak = 0;
        for (Double val : monthlyVals) {
            val = val * factor;
            sum = sum + val;
            if (val > peak) {
                peak = val;
            }
            excelCal.insertDataWithStyle(val, style, false, true);
        }
        excelCal.insertDataWithStyle(sum, style, false, true);
        excelCal.insertDataWithStyle(peak, style, false, true);
        excelCal.insertDataWithStyle(sum / 365, style, false, true);
        excelCal.insertEmptyLine(1);
    }

}
