package logic;

public class ValidatorAttributes {
    public static String dateTo;
    public static String dateFrom;
    public static String driverId;

    public static String getDateTo() {
        return dateTo;
    }

    public static void setDateTo(String dateTo) {
        ValidatorAttributes.dateTo = dateTo;
    }

    public static String getDateFrom() {
        return dateFrom;
    }

    public static void setDateFrom(String dateFrom) {
        ValidatorAttributes.dateFrom = dateFrom;
    }

    public static String getDriverId() {
        return driverId;
    }

    public static void setDriverId(String driverId) {
        ValidatorAttributes.driverId = driverId;
    }
}
