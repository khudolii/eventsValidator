package logic.dao;

import logic.ValidatorAttributes;
import logic.entities.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private final static String GET_DRIVER_EVENTS_BY_ID = "SELECT * FROM eld.eld_event WHERE eld_sequence is not null and event_timestamp between ";
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

    public static List<Event> getEvents() throws SQLException {
        List<Event> eventsList = new ArrayList<>();
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            ResultSet resultSet = st.executeQuery(GET_DRIVER_EVENTS_BY_ID + "'" + ValidatorAttributes.getDateFrom()
                    + "' and '" + ValidatorAttributes.getDateTo() + " 23:59:59' and  driver_id_1=" + ValidatorAttributes.getDriverId() + " order by event_timestamp");
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
                        .build();
                eventsList.add(event);
            }
        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
        } finally {
            if (st != null) st.close();
        }
        return eventsList;
    }

    public static List<Event> getUnidentifiedEventsByTruckId(long truckId) {
        List<Event> eventsList = new ArrayList<>();
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            System.out.println("select * from eld.eld_event where truck_id = \" + truckId  +\n" +
                    "                    \" and driver_id_1 is null and event_timestamp between '\" + ValidatorAttributes.getDateFrom()\n" +
                    "                    + \"' and '\" + ValidatorAttributes.getDateTo() + \" 23:59:59' order by event_timestamp\"");
            ResultSet resultSet = st.executeQuery("select * from eld.eld_event where truck_id = " + truckId  +
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
                        .build();
                eventsList.add(event);
            }
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
}
