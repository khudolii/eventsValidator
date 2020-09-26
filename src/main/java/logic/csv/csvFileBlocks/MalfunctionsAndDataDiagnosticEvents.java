package logic.csv.csvFileBlocks;

import logic.ErrorsLog;
import logic.entities.Event;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MalfunctionsAndDataDiagnosticEvents extends EventCSV {
    @Override
    public String toString() {
        return "MalfunctionsAndDataDiagnosticEvents{" +
                "eventSequence='" + eventSequence + '\'' +
                ", eventCode=" + eventCode +
                ", malfunctionOrDiagnosticCode='" + malfunctionOrDiagnosticCode + '\'' +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                ", totalVehicleMiles='" + totalVehicleMiles + '\'' +
                ", totalEngineHours='" + totalEngineHours + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                '}';
    }

    @Override
    public void compareEventsFromCsvAndDb(List<Event> eventsFromDb) {
        Optional<Event> foundEvent = findEventFromCsvInDb(eventsFromDb);
        try {
            log.info("*** CHECK: Compare event form CSV and DB: ELD Sequence = " + foundEvent.get().getEldSequence());
            if (foundEvent.get().getRecordStatus()==2)
                errorLogs.add("ELD Sequence= " + Integer.parseInt(eventSequence, 16) + "-> This event cannot have RS = 2!");
            checkIntValue(foundEvent.get().getEventCode(), getEventCode(), "getEventCode", foundEvent.get().getEldSequence());
            checkStringValue(foundEvent.get().getMalfunctionDiagnosticCode(),getMalfunctionOrDiagnosticCode(), "getMalfunctionOrDiagnosticCodeventCode",foundEvent.get().getEldSequence());
            checkStringValue(buildEventTimestampByMilis(foundEvent.get().getEventTimestamp().getTime()), csvTimeFormatToTimestamp(getEventDate(), getEventTime()), "getEventTimeStamp", foundEvent.get().getEldSequence());
            checkDoubleValue(foundEvent.get().getTotalVehicleMiles(), getTotalVehicleMiles(), "getTotalVehicleMiles", foundEvent.get().getEldSequence());
            checkDoubleValue(foundEvent.get().getTotalEngineHours(), getTotalEngineHours(), "getTotalEngineHours", foundEvent.get().getEldSequence());
        } catch (NoSuchElementException e) {
            log.error("* * * * No Such Event!");
            errorLogs.add("ELD Sequence= " + Integer.parseInt(eventSequence, 16) + "-> No Such Event!");
        } finally {
            if (errorLogs.size() > 0) {
                ErrorsLog.createNewSubAnchorNotBold("ELD Sequence = " + eventSequence);
                errorLogs.forEach(message -> ErrorsLog.writeCsvTestResultToReport(message, false));
                errorLogs.clear();
            }
        }
    }

    public static class Builder {
        private MalfunctionsAndDataDiagnosticEvents newMalfunctionsAndDataDiagnosticEvents;

        public Builder() {
            newMalfunctionsAndDataDiagnosticEvents = new MalfunctionsAndDataDiagnosticEvents();
        }

        public Builder setEventSequence(String eventSequence) {
            newMalfunctionsAndDataDiagnosticEvents.eventSequence = eventSequence;
            return this;
        }

        public Builder setEventTime(String eventTime) {
            newMalfunctionsAndDataDiagnosticEvents.eventTime = eventTime;
            return this;
        }

        public Builder setEventDate(String eventDate) {
            newMalfunctionsAndDataDiagnosticEvents.eventDate = eventDate;
            return this;
        }

        public Builder setEventCode(int eventCode) {
            newMalfunctionsAndDataDiagnosticEvents.eventCode = eventCode;
            return this;
        }

        public Builder setOrderNumber(String orderNumber) {
            newMalfunctionsAndDataDiagnosticEvents.orderNumber = orderNumber;
            return this;
        }

        public Builder setMalfunctionOrDiagnosticCode(String malfunctionOrDiagnosticCode) {
            newMalfunctionsAndDataDiagnosticEvents.malfunctionOrDiagnosticCode = malfunctionOrDiagnosticCode;
            return this;
        }

        public Builder setTotalVehicleMiles(double totalVehicleMiles) {
            newMalfunctionsAndDataDiagnosticEvents.totalVehicleMiles = totalVehicleMiles;
            return this;
        }

        public Builder setTotalEngineHours(double totalEngineHours) {
            newMalfunctionsAndDataDiagnosticEvents.totalEngineHours = totalEngineHours;
            return this;
        }

        public MalfunctionsAndDataDiagnosticEvents build() {
            newMalfunctionsAndDataDiagnosticEvents.setEventTimeStamp();
            return newMalfunctionsAndDataDiagnosticEvents;
        }
    }
}