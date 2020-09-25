package logic.dao;

import logic.entities.Driver;
import logic.entities.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DriverDAO {
    private static final String GET_DRIVER_BY_ID = "select DP.driver_id, DP.first_name, DP.last_name, DP.login_name, DP.license_number, DP.license_state, O.organization_name, O.usdot_number" +
            ", DP.eld_multiday_basis_used, DP.home_terminal_timezone from fleet.driver_profile DP" +
            " join public.organization O on DP.org_id=O.organization_id where driver_id=";
    private static final String GET_DRIVERS_BY_TRUCK_ID = "select DP.driver_id,DP.first_name, DP.last_name, DP.login_name, DP.license_number, DP.license_state, O.organization_name, O.usdot_number" +
            ", DP.eld_multiday_basis_used, DP.home_terminal_timezone from fleet.driver_profile DP" +
            " join public.organization O on DP.org_id=O.organization_id join eld.eld_event E on DP.driver_id=E.driver_id_1 where DP.truck_id=";

    public static Driver getDriver(String driverId) throws SQLException {
        Driver driver = null;
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            ResultSet resultSet = st.executeQuery(GET_DRIVER_BY_ID + driverId);
            while (resultSet.next()) {
                driver = new Driver(
                        resultSet.getLong("driver_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("login_name"),
                        resultSet.getString("license_state"),
                        resultSet.getString("license_number"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("usdot_number"),
                        resultSet.getString("eld_multiday_basis_used"),
                        resultSet.getString("home_terminal_timezone")
                );
            }
        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
        } finally {
            if (st != null) st.close();
        }
        return driver;
    }

    public static List<Driver> getDriversByTruck(long truckId, String dateFrom, String dateTo) {
        List<Driver> drivers = new ArrayList<>();
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            ResultSet resultSet = st.executeQuery(GET_DRIVERS_BY_TRUCK_ID
                    + truckId + " and event_timestamp between '" + dateFrom + " 00:00:00' and '" + dateTo + " 23:59:59' group by O.organization_id, DP.driver_id");
            while (resultSet.next()) {
                drivers.add(new Driver(
                        resultSet.getLong("driver_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("login_name"),
                        resultSet.getString("license_state"),
                        resultSet.getString("license_number"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("usdot_number"),
                        resultSet.getString("eld_multiday_basis_used"),
                        resultSet.getString("home_terminal_timezone")
                ));
            }
        } catch (SQLException e) {
            System.out.println(("Select is not successful2 " + e));
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return drivers;
    }
}
