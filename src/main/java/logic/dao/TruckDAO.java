package logic.dao;

import logic.entities.Driver;
import logic.entities.Truck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TruckDAO {

    private static final String GET_TRUCK_BY_ID = "select truck_id, truck_number, vin from public.truck where truck_id=";

    public static Truck getTruck(String truckId) throws SQLException {
        Truck truck = null;
        Statement st = null;
        try {
            st = DBConnection.getConnection().createStatement();
            ResultSet resultSet = st.executeQuery(GET_TRUCK_BY_ID + truckId);
            while (resultSet.next()) {
                truck = new Truck(
                        resultSet.getLong("truck_id"),
                        resultSet.getString("truck_number"),
                        resultSet.getString("vin")
                );
            }
        } catch (SQLException e) {
            System.out.println(("Select is not successful " + e));
        } finally {
            if (st != null) st.close();
        }
        return truck;
    }
}
