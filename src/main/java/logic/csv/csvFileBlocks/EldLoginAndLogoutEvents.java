package logic.csv.csvFileBlocks;

import logic.ErrorsLog;
import logic.csv.CsvValidator;
import logic.entities.Event;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EldLoginAndLogoutEvents extends EventCSV {
    @Override
    public void compareEventsFromCsvAndDb(List<Event> eventsFromDb) {
        Optional<Event> foundEvent = findEventFromCsvInDb(eventsFromDb);
        setEventTimeStamp();

        try {
            log.info("*** CHECK: Compare event form CSV and DB: ELD Sequence = " + foundEvent.get().getEldSequence());
            checkStringValue(CsvValidator.currentDriver.getLoginName(),getEldUserName(), "getEldUserName",foundEvent.get().getEldSequence());
            checkIntValue(foundEvent.get().getEventCode(), getEventCode(), "getEventCode", foundEvent.get().getEldSequence());
            //checkStringValue(buildEventTimestampByMilis(foundEvent.get().getEventTimestamp().getTime()), csvTimeFormatToTimestamp(getEventDate(), getEventTime()), "getEventTimeStamp", foundEvent.get().getEldSequence());
            checkEventTimestamp(setTimeZoneByDriverHomeTerminalTimeZone(foundEvent.get().getEventTimestamp()).getTime(), getEventTimeStamp().getTime(), foundEvent.get().getEldSequence());
            checkDoubleValue(foundEvent.get().getTotalVehicleMiles(), getTotalVehicleMiles(), "getTotalVehicleMiles", foundEvent.get().getEldSequence());
            checkDoubleValue(foundEvent.get().getTotalEngineHours(), getTotalEngineHours(), "getTotalEngineHours", foundEvent.get().getEldSequence());
        } catch (NoSuchElementException e) {
            log.error("* * * * No Such Event!");
            errorLogs.add("ELD Sequence= " + Integer.parseInt(eventSequence, 16) + "-> No Such Event!");
        } finally {
            if (errorLogs.size() > 0) {
                ErrorsLog.createNewSubAnchor("ELD Sequence = " + eventSequence);
                errorLogs.forEach(message -> ErrorsLog.writeCsvTestResultToReport(message, false));
                errorLogs.clear();
            }
        }
    }

    @Override
    public String toString() {
        return "EldLoginAndLogoutEvents{" +
                "eldUserName='" + eldUserName + '\'' +
                ", eventSequence='" + eventSequence + '\'' +
                ", eventCode=" + eventCode +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                ", totalVehicleMiles='" + totalVehicleMiles + '\'' +
                ", totalEngineHours='" + totalEngineHours + '\'' +
                "}\n";
    }

    public static class Builder {
        private EldLoginAndLogoutEvents newEldLoginAndLogoutEvents;

        public Builder() {
            newEldLoginAndLogoutEvents = new EldLoginAndLogoutEvents();
        }

        public Builder setEventSequence(String eventSequence) {
            newEldLoginAndLogoutEvents.eventSequence = eventSequence;
            return this;
        }

        public Builder setEventCode(int eventCode) {
            newEldLoginAndLogoutEvents.eventCode = eventCode;
            return this;
        }

        public Builder setEventTime(String eventTime) {
            newEldLoginAndLogoutEvents.eventTime = eventTime;
            return this;
        }

        public Builder setEventDate(String eventDate) {
            newEldLoginAndLogoutEvents.eventDate = eventDate;
            return this;
        }

        public Builder setEldUserName(String eldUserName) {
            newEldLoginAndLogoutEvents.eldUserName = eldUserName;
            return this;
        }

        public Builder setTotalVehicleMiles(double totalVehicleMiles) {
            newEldLoginAndLogoutEvents.totalVehicleMiles = totalVehicleMiles;
            return this;
        }

        public Builder setTotalEngineHours(double totalEngineHours) {
            newEldLoginAndLogoutEvents.totalEngineHours = totalEngineHours;
            return this;
        }

        public EldLoginAndLogoutEvents build() {
            return newEldLoginAndLogoutEvents;
        }
    }
}
