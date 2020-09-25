package logic;

import logic.csv.CsvValidator;

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
import java.text.SimpleDateFormat;
import java.util.Date;
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
        ValidatorAttributes.setDriverId(readReqPart(req, "driverId"));
        ValidatorAttributes.setDateFrom(readReqPart(req, "dateFrom"));
        ValidatorAttributes.setDateTo(readReqPart(req, "dateTo"));
        CsvValidator csvValidator = new CsvValidator(new InputStreamReader(req.getPart("data")
                .getInputStream()));
        ErrorsLog.createReportFile(ValidatorAttributes.getDriverId(), csvValidator.getEvents().size());
        try {
            csvValidator.toAnalyzeCsvFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new EventsValidator(csvValidator.getEvents()).toAnalyzeEvent();
        ErrorsLog.writeResultsToFile();
        byteArrayOutputStream = ErrorsLog.getBout();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Disposition", "attachment;" +
                " filename=\"Report_Driver_" + ValidatorAttributes.getDriverId() + "_" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + ".pdf\"");
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
