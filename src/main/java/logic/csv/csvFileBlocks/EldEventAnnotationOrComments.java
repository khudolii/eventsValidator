package logic.csv.csvFileBlocks;

import logic.ErrorsLog;
import logic.csv.CsvValidator;
import logic.entities.Event;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EldEventAnnotationOrComments extends EventCSV {

    @Override
    public String toString() {
        return "EldEventAnnotationOrComments{" +
                "eldUserName='" + eldUserName + '\'' +
                ", commentTextOrAnnotation='" + commentTextOrAnnotation + '\'' +
                ", driverLocationDescription='" + driverLocationDescription + '\'' +
                ", eventSequence='" + eventSequence + '\'' +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                "}\n";
    }

    @Override
    public void compareEventsFromCsvAndDb(List<Event> eventsFromDb) {
        Optional<Event> foundEvent = findEventFromCsvInDb(eventsFromDb);
        try {
            log.info("*** CHECK: Compare event form CSV and DB: ELD Sequence = " + foundEvent.get().getEldSequence());
            checkStringValue(CsvValidator.currentDriver.getLoginName(), getEldUserName(), "getEldUserName", foundEvent.get().getEldSequence());
            checkStringValue(foundEvent.get().getComment().replaceAll("[^ a-zA-Z0-9]","").replaceAll("\\s",""), getCommentTextOrAnnotation(), "getCommentTextOrAnnotation", foundEvent.get().getEldSequence());
            checkStringValue(buildEventTimestampByMilis(foundEvent.get().getEventTimestamp().getTime()) , csvTimeFormatToTimestamp(getEventDate(), getEventTime()), "getEventTimeStamp",foundEvent.get().getEldSequence());
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
        private EldEventAnnotationOrComments newEldEventAnnotationOrComments;

        public Builder() {
            newEldEventAnnotationOrComments = new EldEventAnnotationOrComments();
        }

        public Builder setEventSequence(String eventSequence) {
            newEldEventAnnotationOrComments.eventSequence = eventSequence;
            return this;
        }

        public Builder setCommentTextOrAnnotation(String commentTextOrAnnotation) {
            newEldEventAnnotationOrComments.commentTextOrAnnotation = commentTextOrAnnotation;
            return this;
        }

        public Builder setDriverLocationDescription(String driverLocationDescription) {
            newEldEventAnnotationOrComments.driverLocationDescription = driverLocationDescription;
            return this;
        }

        public Builder setEldUserName(String eldUserName) {
            newEldEventAnnotationOrComments.eldUserName = eldUserName;
            return this;
        }

        public Builder setEventTime(String eventTime) {
            newEldEventAnnotationOrComments.eventTime = eventTime;
            return this;
        }

        public Builder setEventDate(String eventDate) {
            newEldEventAnnotationOrComments.eventDate = eventDate;
            return this;
        }

        public EldEventAnnotationOrComments build() {
            newEldEventAnnotationOrComments.setEventTimeStamp();
            return newEldEventAnnotationOrComments;
        }
    }
}
