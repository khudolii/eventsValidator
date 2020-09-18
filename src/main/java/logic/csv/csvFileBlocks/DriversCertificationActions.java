package logic.csv.csvFileBlocks;

import logic.ErrorsLog;
import logic.entities.Event;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class DriversCertificationActions  extends EventCSV {

    @Override
    public String toString() {
        return "DriversCertificationActions{" +
                "dateOfTheCertifiedRecord='" + dateOfTheCertifiedRecord + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", eventSequence='" + eventSequence + '\'' +
                ", eventCode=" + eventCode +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                "}\n";
    }

    @Override
    public void compareEventsFromCsvAndDb(List<Event> eventsFromDb) {
        Optional<Event> foundEvent = findEventFromCsvInDb(eventsFromDb);
        try{
            log.info("*** CHECK: Compare event form CSV and DB: ELD Sequence = " + foundEvent.get().getEldSequence());
            checkIntValue(foundEvent.get().getEventCode(), getEventCode(), "getEventCode",foundEvent.get().getEldSequence());
            checkStringValue(buildEventTimestampByMilis(foundEvent.get().getEventTimestamp().getTime()) , csvTimeFormatToTimestamp(getEventDate(), getEventTime()), "getEventTimeStamp",foundEvent.get().getEldSequence());
        } catch (NoSuchElementException e){
            log.error("* * * * No Such Event!");
            errorLogs.add("ELD Sequence= " + eventSequence + "-> No Such Event!");
        }
        finally {
            if(errorLogs.size()>0){
                ErrorsLog.createNewSubAnchor("ELD Sequence = " + eventSequence);
                errorLogs.forEach(message -> ErrorsLog.writeCsvTestResultToReport(message,false));
                errorLogs.clear();
            }
        }
    }

    public static class Builder {
        private DriversCertificationActions newDriversCertificationActions;
        public Builder(){
            newDriversCertificationActions = new DriversCertificationActions();
        }
        public Builder setEventSequence(String eventSequence){
            newDriversCertificationActions.eventSequence =eventSequence;
            return this;
        }
        public Builder setEventTime(String eventTime){
            newDriversCertificationActions.eventTime=eventTime;
            return this;
        }
        public Builder setEventDate(String eventDate){
            newDriversCertificationActions.eventDate=eventDate;
            return this;
        }
        public Builder setEventCode(int eventCode){
            newDriversCertificationActions.eventCode=eventCode;
            return this;
        }
        public Builder setDateOfTheCertifiedRecord(String dateOfTheCertifiedRecord){
            newDriversCertificationActions.dateOfTheCertifiedRecord=dateOfTheCertifiedRecord;
            return this;
        }
        public Builder setOrderNumber(String orderNumber){
            newDriversCertificationActions.orderNumber=orderNumber;
            return this;
        }
        public DriversCertificationActions build(){
            return newDriversCertificationActions;
        }
    }
}