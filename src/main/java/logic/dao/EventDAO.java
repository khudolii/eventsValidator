package logic.dao;

import logic.EventsValidator;
import logic.ValidatorAttributes;
import logic.entities.Event;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EventDAO {
    private static final Logger log = Logger.getLogger(EventDAO.class);

    private final static String GET_DRIVER_EVENTS_BY_ID = "SELECT * FROM eld.eld_event WHERE record_status=1 and eld_sequence is not null and event_timestamp between ";
    static private final String INSERT_EVENTS = "INSERT INTO eld.eld_event (driver_id_1, truck_id,org_id, record_status, record_origin, " +
            "event_type, event_code, latitude, longitude, location_description, device_uid, event_timestamp, total_vehicle_miles, total_engine_hours) VALUES (";
    public static String driverId;
    public static String dateFrom;
    public static String dateTo;

    public static String getDriverId() {
        return driverId;
    }

    public static void setDriverId(String driverId) {
        EventDAO.driverId = driverId;
    }

    public static String getDateFrom() {
        return dateFrom;
    }

    public static void setDateFrom(String dateFrom) {
        EventDAO.dateFrom = dateFrom;
    }

    public static String getDateTo() {
        return dateTo;
    }

    public static void setDateTo(String dateTo) {
        EventDAO.dateTo = dateTo;
    }

    public static List<Event> getEvents(long driverId, String dateTo, String dateFrom) {
        List<Event> eventsList = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery(GET_DRIVER_EVENTS_BY_ID + "'" + dateFrom
                    + " 00:00:00' and '" + dateTo + " 23:59:59' and  driver_id_1=" + driverId);
            while (resultSet.next()) {
                Event event = new Event
                        .Builder()
                        .setEventSequence(resultSet.getLong("event_sequence"))
                        .setEldSequence(resultSet.getString("eld_sequence"))
                        .setDriverId1(resultSet.getLong("driver_id_1"))
                        .setDriverId2(resultSet.getLong("driver_id_2"))
                        .setTruckId(resultSet.getLong("truck_id"))
                        .setTruckVin(resultSet.getString("truck_vin"))
                        .setTruckNumber(resultSet.getString("truck_number"))
                        .setEventTimestamp(resultSet.getString("event_timestamp"))
                        .setTimeZoneOffset(resultSet.getString("time_zone_offset"))
                        .setTrailerNumber(resultSet.getString("trailer_number"))
                        .setShippingNumber(resultSet.getString("shipping_number"))
                        .setOrgId(resultSet.getLong("org_id"))
                        .seteLogAppMode(resultSet.getString("elog_app_mode"))
                        .setRecordStatus(resultSet.getInt("record_status"))
                        .setRecordOrigin(resultSet.getInt("record_origin"))
                        .setEventType(resultSet.getInt("event_type"))
                        .setEventCode(resultSet.getInt("event_code"))
                        .setTotalVehicleMiles(resultSet.getDouble("total_vehicle_miles"))
                        .setAccumulatedVehicleMiles(resultSet.getDouble("accumulated_vehicle_miles"))
                        .setTotalEngineHours(resultSet.getDouble("total_engine_hours"))
                        .setElapsedEngineHours(resultSet.getDouble("elapsed_engine_hours"))
                        .setLatitude(resultSet.getString("latitude"))
                        .setLongitude(resultSet.getString("longitude"))
                        .setComment(resultSet.getString("comment"))
                        .setLocationDescription(resultSet.getString("location_description"))
                        .setDistanceSinceLastValidCoords(resultSet.getDouble("distance_since_last_valid_coords"))
                        .setMalfunctionIndicatorStatus(resultSet.getInt("malfunction_indicator_status"))
                        .setDataDiagnosticEventIndicatorStatus(resultSet.getInt("data_diagnostic_event_idicator_status"))
                        .setMalfunctionDiagnosticCode(resultSet.getString("malfunction_diagnostic_code"))
                        .setDataCheckValue(resultSet.getString("data_check_value"))
                        .setDriverCertified(resultSet.getString("driver_certified"))
                        .setCountry(resultSet.getString("country"))
                        .setProvState(resultSet.getString("prov_state"))
                        .setCity(resultSet.getString("city"))
                        .build();
                eventsList.add(event);
            }
            log.info("Selected " + eventsList.size() + " events");

        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
        }
        return eventsList;
    }

    public static boolean createEvents(Event event) throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement()) {
            st.executeUpdate(INSERT_EVENTS + event.getDriverId1() + " ," + event.getTruckId() + " ," + event.getOrgId() + " ," + event.getRecordStatus()
                    + " ," + event.getRecordOrigin() + " ," + event.getEventType() + " ," + event.getEventCode() + " ,'" + event.getLatitude() + "' ,'" + event.getLongitude()
                    + "' , '" + event.getLocationDescription() + "' , 'server' ,'" + event.getEventTimestamp() + "', " + event.getTotalVehicleMiles() + ", " + event.getTotalEngineHours() + ")");
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static List<Event> getUnidentifiedEventsByTruckId(long truckId) {
        List<Event> eventsList = new ArrayList<>();
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            System.out.println("select * from eld.eld_event where truck_id = \" + truckId  +\n" +
                    "                    \" and driver_id_1 is null and event_timestamp between '\" + ValidatorAttributes.getDateFrom()\n" +
                    "                    + \"' and '\" + ValidatorAttributes.getDateTo() + \" 23:59:59' order by event_timestamp\"");
            ResultSet resultSet = st.executeQuery("select * from eld.eld_event where truck_id = " + truckId +
                    " and driver_id_1 is null and event_timestamp between '" + ValidatorAttributes.getDateFrom()
                    + "' and '" + ValidatorAttributes.getDateTo() + " 23:59:59' order by event_timestamp");
            while (resultSet.next()) {
                Event event = new Event
                        .Builder()
                        .setEventSequence(resultSet.getLong("event_sequence"))
                        .setEldSequence(resultSet.getString("eld_sequence"))
                        .setDriverId1(resultSet.getLong("driver_id_1"))
                        .setDriverId2(resultSet.getLong("driver_id_2"))
                        .setTruckId(resultSet.getLong("truck_id"))
                        .setTruckVin(resultSet.getString("truck_vin"))
                        .setTruckNumber(resultSet.getString("truck_number"))
                        .setEventTimestamp(resultSet.getString("event_timestamp"))
                        .setTimeZoneOffset(resultSet.getString("time_zone_offset"))
                        .setTrailerNumber(resultSet.getString("trailer_number"))
                        .setShippingNumber(resultSet.getString("shipping_number"))
                        .setOrgId(resultSet.getLong("org_id"))
                        .seteLogAppMode(resultSet.getString("elog_app_mode"))
                        .setRecordStatus(resultSet.getInt("record_status"))
                        .setRecordOrigin(resultSet.getInt("record_origin"))
                        .setEventType(resultSet.getInt("event_type"))
                        .setEventCode(resultSet.getInt("event_code"))
                        .setTotalVehicleMiles(resultSet.getDouble("total_vehicle_miles"))
                        .setAccumulatedVehicleMiles(resultSet.getDouble("accumulated_vehicle_miles"))
                        .setTotalEngineHours(resultSet.getDouble("total_engine_hours"))
                        .setElapsedEngineHours(resultSet.getDouble("elapsed_engine_hours"))
                        .setLatitude(resultSet.getString("latitude"))
                        .setLongitude(resultSet.getString("longitude"))
                        .setComment(resultSet.getString("comment"))
                        .setDistanceSinceLastValidCoords(resultSet.getDouble("distance_since_last_valid_coords"))
                        .setMalfunctionIndicatorStatus(resultSet.getInt("malfunction_indicator_status"))
                        .setDataDiagnosticEventIndicatorStatus(resultSet.getInt("data_diagnostic_event_idicator_status"))
                        .setMalfunctionDiagnosticCode(resultSet.getString("malfunction_diagnostic_code"))
                        .setDataCheckValue(resultSet.getString("data_check_value"))
                        .setDriverCertified(resultSet.getString("driver_certified"))
                        .seteLogAppMode(resultSet.getString("elog_app_mode"))
                        .setCountry(resultSet.getString("country"))
                        .setProvState(resultSet.getString("prov_state"))
                        .setCity(resultSet.getString("city"))
                        .build();
                eventsList.add(event);
            }
            log.info("Selected " + eventsList.size() + " unidentified events");

        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return eventsList;
    }

    public static boolean createFullEventToCustomTable(Event event, long driverId, long truckId, long orgId) {
        try (Statement st = DBConnection.getConnection().createStatement()) {
            st.executeUpdate("INSERT INTO eld.eld_event (driver_id_1, truck_id, device_uid, " +
                    "record_status, record_origin, event_type, event_code, " +
                    "event_timestamp, accumulated_vehicle_miles, total_vehicle_miles, elapsed_engine_hours," +
                    " total_engine_hours, latitude, longitude, location_description, distance_since_last_valid_coords, " +
                    "malfunction_indicator_status, data_diagnostic_event_idicator_status, malfunction_diagnostic_code, " +
                    "comment, data_check_value, driver_certified, shipping_number, " +
                    "org_id, trailer_number,truck_number, " +
                    "country, prov_state, city) VALUES (" + driverId + " ," + truckId + " , 'server', " + event.getRecordStatus() + " ," +
                    event.getRecordOrigin() + " ," + event.getEventType() + " ," + event.getEventCode() + " ,'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getEventTimestamp()) + "' ," +
                    event.getAccumulatedVehicleMiles() + " ," + event.getTotalVehicleMiles() + " ," + event.getElapsedEngineHours() + " ," +
                    event.getTotalEngineHours() + " ,'" + event.getLatitude() + "' ,'" + event.getLongitude() + "' ,'" + event.getLocationDescription() + "' ,'" +
                    event.getDistanceSinceLastValidCoords() + "' ,'" + event.getMalfunctionIndicatorStatus() + "' ,'" + event.getDataDiagnosticEventIndicatorStatus()
                    + "' ,'" + event.getMalfunctionDiagnosticCode() + "' ,'" + event.getComment() + "' ,'" + event.getDataCheckValue()
                    + "' ,'" + event.getDriverCertified() + "' ,'" + event.getShippingNumber() + "' ," + orgId + " ,'" +
                    event.getTrailerNumber() + "', (SELECT truck_number from public.truck where truck_id=" + truckId + ")  ,'" + event.getCountry() + "' ,'" + event.getProvState() + "' ,'" + event.getCity() + "')");
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static boolean addBlockOfEventsToDriver(long driverIdTo, long truckId, long orgId, int countOfDays) {
        if (driverIdTo <= 0 || truckId <= 0 || orgId <= 0)
            return false;
        List<Event> eventsList;
        Date dateTo = null;
        Date dateFrom = null;
        Date randomDateTo = null;
        Date randomDateFrom = null;
        int dayDiffBetweenDates = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateTo = format.parse("2020-01-01");
            dateFrom = format.parse("2020-07-30");
            randomDateFrom = new Date(ThreadLocalRandom.current().nextLong(dateTo.getTime(), dateFrom.getTime()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(randomDateFrom);
            calendar.add(Calendar.HOUR, countOfDays * 24);
            randomDateTo = calendar.getTime();
            log.info("Get events between random date: " + format.format(randomDateFrom) + " - " + format.format(randomDateTo));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long driverFromId = 0;
        long eventCount = 0;
        try (Statement st = DBConnection.getConnection().createStatement()) {
            try (ResultSet resultSet = st.executeQuery("select driver_id_1, count(event_id) from eld.eld_event where " +
                    "  event_timestamp between '" + format.format(randomDateFrom) + " 00:00:00' and '" + format.format(randomDateTo) + " 23:59:00' " +
                    "and org_id=1629 group by driver_id_1 order by count(event_id) desc limit 10;")) {
                while (resultSet.next()) {
                    long count = resultSet.getLong("count");
                    long id = resultSet.getLong("driver_id_1");
                    log.info("Found Driver = " + id + ", with events num = " + count);
                    if (eventCount < count && id > 0) {
                        eventCount = count;
                        driverFromId = id;
                    }
                }
            }
            log.info("Driver with max events num = " + eventCount + ", Driver ID = " + driverFromId);

            eventsList = EventDAO.getEvents(driverFromId, format.format(randomDateTo), format.format(randomDateFrom));
            Calendar calendarDel = Calendar.getInstance();
            calendarDel.setTime(new Date());
            calendarDel.add(Calendar.HOUR, -countOfDays * 24);
            System.out.println("DELETE from eld.eld_event WHERE driver_id_1=" + driverIdTo + " and org_id=" + orgId +
                    " and event_timestamp>'" + format.format(calendarDel.getTime()) + "'");
            st.executeUpdate("DELETE FROM eld.eld_event WHERE driver_id_1 = " + driverIdTo + " and org_id = " + orgId +
                    " and event_timestamp > '" + format.format(calendarDel.getTime()) + "'");
            log.info("Deleted events for Driver ID = " + driverIdTo + ", from " + format.format(calendarDel.getTime()));

            dayDiffBetweenDates = (int) ((new Date().getTime() - randomDateTo.getTime()) / (24 * 60 * 60 * 1000));
            for (Event event : eventsList) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getEventTimestamp());
                calendar.add(Calendar.HOUR, dayDiffBetweenDates * 24);
                event.setEventTimestamp(calendar.getTime());
                EventDAO.createFullEventToCustomTable(event, driverIdTo, truckId, orgId);
            }
            log.info("Created " + eventsList.size() + " events for Driver ID = " + driverIdTo);
        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
            return false;
        }
        return true;
    }


    /*public static boolean createBDX(List<BdxEntity> events) throws SQLException {
        if (events!=null){
            Statement st = null;
            try {
                st = DBConnection.getConnection().createStatement();
                for (BdxEntity event:
                        events) {
                    st.executeUpdate(INSERT_BDX + event.getOrgId() + " ," + event.getCrossingDate() + " ," + event.getDriverId() + " ," + event.getTruckId()
                            + " ," + event.getRecordOrigin() + " ," + event.getFromCountryCode() + " ," + event.getToCountryCode() + ")");
                    log.info("Insert BDX event to DB: " + event.toString());
                }
            } catch (SQLException e) {
                log.error("Insert BDX event is not successful: " + e);
                return false;
            }
            finally {
                if(st != null) st.close();
            } return true;
        }  else return false;
    }*/
}
