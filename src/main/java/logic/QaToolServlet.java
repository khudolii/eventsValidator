package logic;

import logic.dao.DriverDAO;
import logic.dao.EventDAO;
import logic.entities.Driver;
import logic.entities.Event;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/QaTool")
public class QaToolServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(QaToolServlet.class);

    private static int certNum = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String actionName = req.getParameter("actionName");
        log.info("ACTION NAME = " + actionName);
        switch (actionName) {
            case "createEvent":
                createEvent(req, resp);
                break;
            case "addGroupOfEvent": {
                if (EventDAO.addBlockOfEventsToDriver(Long.parseLong(req.getParameter("driverId")),
                        Long.parseLong(req.getParameter("truckId")),
                        Integer.parseInt(req.getParameter("orgId")),
                        Integer.parseInt(req.getParameter("days"))))
                    log.info("Successful created!");
                else
                    log.error("Find error! Not Created");
                log.info("Got parameters: Driver ID = " + Long.parseLong(req.getParameter("driverId")) + ", Truck ID = " +
                        Long.parseLong(req.getParameter("truckId")) + ", Org ID =  " +
                        Integer.parseInt(req.getParameter("orgId")) + ", Days =  " +
                        Integer.parseInt(req.getParameter("days")));

            }
            break;
        }
    }

    private void createEvent(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Got parameters: eventType = " + req.getParameter("eventType") + ", driverId = " + req.getParameter("driverId") + ", truckId = " + req.getParameter("truckId")
                + ", orgId = " + req.getParameter("orgId") + ", date = " + req.getParameter("date") + ", time = " + req.getParameter("time"));

        if (!req.getParameter("eventType").equals("BDX")) {
            int eventType = 0;
            int eventCode = 0;
            switch (req.getParameter("eventType")) {
                case "On Duty": {
                    eventType = 1;
                    eventCode = 4;
                }
                break;
                case "Off Duty": {
                    eventType = 1;
                    eventCode = 1;
                }
                break;
                case "Sleep": {
                    eventType = 1;
                    eventCode = 2;
                }
                break;
                case "Driving": {
                    eventType = 1;
                    eventCode = 3;
                }
                break;
                case "Intermediate": {
                    eventType = 2;
                    eventCode = 1;
                }
                break;
                case "Logout": {
                    eventType = 5;
                    eventCode = 2;
                }
                break;
                case "PowerUp": {
                    eventType = 6;
                    eventCode = 1;
                }
                break;
                case "PowerDown": {
                    eventType = 6;
                    eventCode = 3;
                }
                break;
                case "Login": {
                    eventType = 5;
                    eventCode = 1;
                }
                break;
                case "Cert": {
                    eventType = 4;
                    certNum++;
                    eventCode = certNum;
                }
                break;
            }
            //log.info("Set Event Type = " + eventType + ", Event Code = " + eventCode + ", RO = " + ro);
            String timeStampWithTimeZone;
            try {
                Driver driver = DriverDAO.getDriver(req.getParameter("driverId"));
                timeStampWithTimeZone = Timestamp
                        .getStringDateWithCustomTimeZone(req.getParameter("date") + " " + req.getParameter("time") + ":00", driver.getHomeTerminalTimezone());
            } catch (SQLException throwables) {
                timeStampWithTimeZone = req.getParameter("date") + " " + req.getParameter("time") + ":00";
                throwables.printStackTrace();
            }
            Event event = new Event
                    .Builder()
                    .setDriverId1(Long.parseLong(req.getParameter("driverId")))
                    .setTruckId(Long.parseLong(req.getParameter("truckId")))
                    .setOrgId(Long.parseLong(req.getParameter("orgId")))
                    .setEventTimestamp(timeStampWithTimeZone)
                    .setRecordOrigin(Integer.parseInt(req.getParameter("ro")))
                    .setEventType(eventType)
                    .setEventCode(eventCode)
                    .setTotalVehicleMiles(Double.parseDouble(req.getParameter("tvm")))
                    .setTotalEngineHours(Double.parseDouble(req.getParameter("teh")))
                    .setLocationDescription(req.getParameter("location"))
                    .setRecordStatus(1)
                    .setLatitude("M")
                    .setLongitude("M")
                    .build();
            try {
                EventDAO.createEvents(event);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
