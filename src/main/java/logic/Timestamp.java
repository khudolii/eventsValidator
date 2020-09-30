package logic;

import logic.csv.CsvValidator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Timestamp {
    public static String getStringDateWithCustomTimeZone(String timeStamp, String timeZone){
        DateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Date oldTimestamp = null;
        String newTimestamp = null;
        try {
            oldTimestamp = oldFormat.parse(timeStamp);
            oldFormat.setTimeZone(tz);
            newTimestamp = oldFormat.format(oldTimestamp);
        } catch (ParseException e) {
            System.out.println(e);
        }
        return newTimestamp;
    }


}
