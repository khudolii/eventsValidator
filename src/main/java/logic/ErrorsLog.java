package logic;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import logic.entities.Event;

public class ErrorsLog {
    private static Font anchorFont = new Font(Font.FontFamily.TIMES_ROMAN, 20,
            Font.BOLD);
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL, BaseColor.RED);
    private static Font greyFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.NORMAL, BaseColor.DARK_GRAY);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);
    public static ByteArrayOutputStream bout;

    public static ByteArrayOutputStream getBout() {
        return bout;
    }

    public static Document document = null;
    private static Anchor anchor;
    private static Chapter catPart;


    public static void createReportFile(String driverId, int numOfEvents) {
        try {
            document = new Document();
            //PdfWriter.getInstance(document, new FileOutputStream("/home/evgeniy/IdeaProjects/te.data.analyzer/Reports/Analyze_driver_" + driverId + ".pdf"));
            bout = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, bout);
            Paragraph article = new Paragraph();
            document.open();
            article.add(new Paragraph("DATA CHECK REPORT", anchorFont));
            article.add(new Paragraph("Driver ID: " + driverId, smallBold));
            article.add(new Paragraph("Date From: " + ValidatorAttributes.getDateFrom(), smallBold));
            article.add(new Paragraph("Date To: " + ValidatorAttributes.getDateTo(), smallBold));
            article.add(new Paragraph("Current date: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), smallBold));
            article.add(new Paragraph("Checked events: " + numOfEvents, smallBold));
            document.add(article);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void writeCsvTestResultToReport(String message, boolean result) {
        try {
            if (result)
                document.add(new Paragraph("+ " + message, greyFont));
            else
                document.add(new Paragraph("- " + message, redFont));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void writeNumberedErrorsList(ArrayList <String> errors){
        List numberedL = new List();
        numberedL.setIndentationLeft(30f);
        numberedL.setNumbered(true);
        errors.forEach(numberedL::add);
        try {
            document.add(numberedL);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void createNewAnchor(String name) {
        anchor = new Anchor(name+"\n", anchorFont);
        try {
            document.add(anchor);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    public static void createNewSubAnchor(String name) {
        anchor = new Anchor(name+"\n", smallBold);
        try {
            document.add(anchor);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void createNewSubAnchorNotBold(String name) {
        anchor = new Anchor(name+"\n", bold);
        try {
            document.add(anchor);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void writeErrorsFromEvent(ArrayList<String> errorsLog, Event event, Event previousEvent) {
        Paragraph subPara = new Paragraph("Event Sequence = " + event.getEventSequence(), subFont);
        List errorsList = new List(List.ORDERED);
        errorsLog.stream().map(error -> new ListItem(error, redFont)).forEach(errorsList::add);
        List eventsSubList = new List(true, false, 30);
        eventsSubList.setListSymbol(new Chunk("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
        eventsSubList.add("Current event: " + event.toString());
        eventsSubList.add("Previous event: " + previousEvent.toString());
        errorsList.add(eventsSubList);
        try {
            document.add(subPara);
            document.add(errorsList);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void writeResultsToFile() {
        document.close();
    }

}
