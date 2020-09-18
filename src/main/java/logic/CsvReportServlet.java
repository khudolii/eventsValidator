package logic;

import logic.csv.CsvAnalyzer;
import logic.csv.CsvReader;
import logic.dao.EventDAO;
import logic.entities.Event;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@MultipartConfig
@WebServlet("/getCsvReport")
public class CsvReportServlet extends HttpServlet {
    private static ByteArrayOutputStream byteArrayOutputStream;

    private String readReqPart(HttpServletRequest req, String partName) throws IOException, ServletException {
        return new BufferedReader(new InputStreamReader(req.getPart(partName)
                .getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println(new BufferedReader(new InputStreamReader(req.getPart("data").getInputStream())).lines().collect(Collectors.joining("\n")));
        System.out.println(readReqPart(req,"driverId"));
        System.out.println(readReqPart(req,"dateTo"));
        System.out.println(readReqPart(req,"dateFrom"));

       /* InputStreamReader reader = new InputStreamReader(req.getInputStream());
        CsvAnalyzer csvAnalyzer = new CsvAnalyzer(reader);
        ErrorsLog.createReportFile(AnalyzeAttributes.getDriverId(), csvAnalyzer.getEvents().size());
        try {
            csvAnalyzer.toAnalyzeCsvFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Analyzer(csvAnalyzer.getEvents()).toAnalyzeEvent();
        ErrorsLog.writeResultsToFile();
        byteArrayOutputStream = ErrorsLog.getBout();*/
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Disposition", "attachment;" +
                " filename=\"Report_Driver_" + AnalyzeAttributes.getDriverId() + "_" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + ".pdf\"");
        resp.setHeader("Expires", "0");
        resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        resp.setHeader("Pragma", "public");
        resp.setContentType("application/pdf");
        ServletOutputStream outputStream = resp.getOutputStream();
        resp.setContentLength(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(outputStream);
        outputStream.flush();
        byteArrayOutputStream.flush();
    }
}
