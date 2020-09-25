package logic;

import logic.dao.EventDAO;
import logic.entities.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/QaTool")
public class QaToolServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println(req.getParameter("eventType"));
        System.out.println(req.getParameter("driverId"));
        System.out.println(req.getParameter("truckId"));
        System.out.println(req.getParameter("orgId"));
        System.out.println(req.getParameter("date"));
        System.out.println(req.getParameter("time"));
        if(!req.getParameter("eventType").equals("BDX")){
        int eventType = 0;
        int eventCode = 0;
        int ro=0;
        switch (req.getParameter("eventType")){
            case "On Duty": {
                eventType=1;
                eventCode=4;
                ro=2;
            }break;
            case "Off Duty": {
                eventType=1;
                eventCode=1;
                ro=2;
            }break;
            case "Sleep": {
                eventType=1;
                eventCode=2;
                ro=2;
            }break;
            case "Driving": {
                eventType=1;
                eventCode=3;
                ro=1;
            }break;
            case "Intermediate": {
                eventType=2;
                eventCode=1;
                ro=1;
            }break;
            case "Logout": {
                eventType=5;
                eventCode=2;
                ro=1;
            }break;
            case "PowerUp": {
                eventType=6;
                eventCode=1;
                ro=1;
            }break;
            case "PowerDown": {
                eventType=6;
                eventCode=3;
                ro=1;
            }break;
            case "Login": {
                eventType=5;
                eventCode=1;
                ro=1;
            }break;
        }

        Event event = new Event
                .Builder()
                .setDriverId1(Long.parseLong(req.getParameter("driverId")))
                .setTruckId(Long.parseLong(req.getParameter("truckId")))
                .setOrgId(Long.parseLong(req.getParameter("orgId")))
                .setEventTimestamp(req.getParameter("date") + " " + req.getParameter("time")+":00")
                .setRecordOrigin(ro)
                .setEventType(eventType)
                .setEventCode(eventCode)
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
