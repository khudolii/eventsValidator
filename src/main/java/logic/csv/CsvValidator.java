package logic.csv;

import logic.ValidatorAttributes;
import logic.ErrorsLog;
import logic.csv.csvFileBlocks.CmvList;
import logic.csv.csvFileBlocks.EldFileHeaderSegment;
import logic.csv.csvFileBlocks.UserList;
import logic.dao.DriverDAO;
import logic.dao.EventDAO;
import logic.entities.Driver;
import logic.entities.Event;
import logic.entities.Truck;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CsvValidator {

    public static Driver currentDriver;
    private List<Event> events;
    private List<Driver> userList;
    private Set<Truck> trucks;
    private CsvReader csvReader;

    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Driver> getCoDriversList() {
        return userList;
    }

    public Set<Truck> getTrucks() {
        return trucks;
    }

    public CsvReader getCsvReader() {
        return csvReader;
    }

    public CsvValidator(InputStreamReader reader) {
        csvReader = new CsvReader(reader);
        try {
            currentDriver = DriverDAO.getDriver(ValidatorAttributes.getDriverId());
            this.events = EventDAO.getEvents(Long.parseLong(ValidatorAttributes.getDriverId()),ValidatorAttributes.getDateFrom(),ValidatorAttributes.getDateTo());
            trucks = findTrucks(events);
            userList = findCoDrivers(trucks);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void checkAllRequireHeaders(List<String[]> csvFileRowsList) {
        ErrorsLog.createNewSubAnchor("Required headers:");
        int headerIndex = 0;
        String currentHeader;
        for (String[] row : csvFileRowsList) {
            if (row.length == 1 && headerIndex < HeadersCsvReport.getHeadersList().size()) {
                currentHeader = Arrays.toString(row).replaceAll("\\[", "").replaceAll("]", "");
                String resultMessage = "Header: Actual = " + currentHeader + ", Expected = "
                        + HeadersCsvReport.getHeadersList().get(headerIndex);
                ErrorsLog.writeCsvTestResultToReport(resultMessage, currentHeader.equals(HeadersCsvReport.getHeadersList().get(headerIndex)));
                headerIndex++;
            }
        }
    }

    private void checkEldFileHeaderSegmentAttribute(EldFileHeaderSegment eldFileHeaderSegment) {
        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_FILE_HEADER_SEGMENT);
        ErrorsLog.writeCsvTestResultToReport("Driver First Name: Actual = " + eldFileHeaderSegment.getDriverFirstName()
                + ", Expected = " + currentDriver.getFirstName(), currentDriver.getFirstName().equals(eldFileHeaderSegment.getDriverFirstName()));
        ErrorsLog.writeCsvTestResultToReport("Driver Last Name: Actual = " + eldFileHeaderSegment.getDriverLastName()
                + ", Expected = " + currentDriver.getLastName(), currentDriver.getLastName().equals(eldFileHeaderSegment.getDriverLastName()));
        ErrorsLog.writeCsvTestResultToReport("ELD username: Actual = " + eldFileHeaderSegment.getDriverEldUserName()
                + ", Expected = " + currentDriver.getLoginName(), currentDriver.getLoginName().equals(eldFileHeaderSegment.getDriverEldUserName()));
        ErrorsLog.writeCsvTestResultToReport("License Number: Actual = " + eldFileHeaderSegment.getDriverLicenseNumber()
                + ", Expected = " + currentDriver.getLicenseNumber(), currentDriver.getLicenseNumber().equals(eldFileHeaderSegment.getDriverLicenseNumber()));
        ErrorsLog.writeCsvTestResultToReport("License Issuing State: Actual = " + eldFileHeaderSegment.getDriverLicenseIssuingState()
                + ", Expected = " + currentDriver.getLicenseIssuingState(), currentDriver.getLicenseIssuingState().equals(eldFileHeaderSegment.getDriverLicenseIssuingState()));
        ErrorsLog.writeCsvTestResultToReport("Carrier Name: Actual = " + eldFileHeaderSegment.getCarrierName()
                + ", Expected = " + currentDriver.getOrganizationName(), currentDriver.getOrganizationName().equals(eldFileHeaderSegment.getCarrierName()));
        ErrorsLog.writeCsvTestResultToReport("Carrier's USDOT Number: Actual = " + eldFileHeaderSegment.getCarriersUSDOTNumber()
                + ", Expected = " + currentDriver.getUsdotNumber(), currentDriver.getUsdotNumber().equals(eldFileHeaderSegment.getCarriersUSDOTNumber()));
        ErrorsLog.writeCsvTestResultToReport("ELD Registration ID: Actual = " + eldFileHeaderSegment.getEldRegistrationId()
                + ", Expected = OLN4", eldFileHeaderSegment.getEldRegistrationId().equals("OLN4"));
        ErrorsLog.writeCsvTestResultToReport("ELD Identifier: Actual = " + eldFileHeaderSegment.getEldIdentifier()
                + ", Expected = TE0101", eldFileHeaderSegment.getEldIdentifier().equals("TE0101"));
        ErrorsLog.writeCsvTestResultToReport("Multiday-basis Used: Actual = " + csvReader.getEldFileHeaderSegment().getMultiDayBasisUsed()
                + ", Expected = " + currentDriver.getMultiDayBasisUsed(), csvReader.getEldFileHeaderSegment().getMultiDayBasisUsed().equals(currentDriver.getMultiDayBasisUsed()));
        ErrorsLog.writeCsvTestResultToReport("24-Hour Period Starting Time: Actual = " + csvReader.getEldFileHeaderSegment().getHour24PeriodStartingTime() +
                ", Cannot be empty!", csvReader.getEldFileHeaderSegment().getHour24PeriodStartingTime() != null && !csvReader.getEldFileHeaderSegment().getHour24PeriodStartingTime().equals(""));
        ErrorsLog.writeCsvTestResultToReport("Time Zone Offset From UTC: Actual = " + csvReader.getEldFileHeaderSegment().getTimeZoneOffsetFromUtc() +
                ", Expected = " + currentDriver.getHomeTerminalTimezoneOffset(), csvReader.getEldFileHeaderSegment().getTimeZoneOffsetFromUtc().equals(currentDriver.getHomeTerminalTimezoneOffset()));
        ErrorsLog.writeCsvTestResultToReport("ELD Authentication: Actual = " + csvReader.getEldFileHeaderSegment().getEldAuthenticated() +
                ", Cannot be empty!", csvReader.getEldFileHeaderSegment().getEldAuthenticated() != null && !csvReader.getEldFileHeaderSegment().getEldAuthenticated().equals(""));

        ErrorsLog.writeCsvTestResultToReport("Size of User List: Actual = " + csvReader.getUserList().size() + ", Expected = " + userList.size(),
                csvReader.getUserList().size() == userList.size());
        ErrorsLog.writeCsvTestResultToReport("Size of CMV List: Actual = " + csvReader.getCmvList().size() + ", Expected = " + trucks.size(),
                csvReader.getCmvList().size() == trucks.size());

        /* Find the latest co-driver by comparing the User List data and the data in the Co-Driver field. */
        if (userList.size() > 1) {
            Driver coDriver = userList
                    .stream()
                    .findFirst()
                    .filter(x -> x.getFirstName().equals(eldFileHeaderSegment.getCoDriverFirstName())
                            && x.getLastName().equals(eldFileHeaderSegment.getCoDriverLastName())
                            && x.getLoginName().equals(eldFileHeaderSegment.getCoDriverEldUserName())
                            && x.getDriverId() != currentDriver.getDriverId())
                    .orElse(null);
            if (coDriver != null)
                ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                        + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Expected: " + coDriver.getLastName() + ", " + coDriver.getFirstName()
                        + ", " + coDriver.getLoginName(), true);
            else
                ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                        + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Not found this co driver in DB.", false);
        } else {
            ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                            + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Expected = , , ,"
                    , eldFileHeaderSegment.getCoDriverLastName().equals("")
                            && eldFileHeaderSegment.getCoDriverFirstName().equals("")
                            && eldFileHeaderSegment.getCoDriverEldUserName().equals(""));
        }

        /*Find the last truck by comparing the CMV List data and the CMV data.*/
        Truck currentTruck = trucks
                .stream()
                .findFirst()
                .filter(x -> x.getTruckNumber().equals(eldFileHeaderSegment.getCmvPowerUnitNumber())
                        && x.getVinNumber().equals(eldFileHeaderSegment.getCmvVinNumber()))
                .orElse(null);
        if (currentTruck != null)
            ErrorsLog.writeCsvTestResultToReport("Current Truck: Actual = " + eldFileHeaderSegment.getCmvPowerUnitNumber() + ", "
                    + eldFileHeaderSegment.getCmvVinNumber() + ", Expected = " + currentTruck.getTruckNumber() + ", " + currentTruck.getVinNumber(), true);
        else
            ErrorsLog.writeCsvTestResultToReport("Current Truck: Actual = " + eldFileHeaderSegment.getCmvPowerUnitNumber() + ", "
                    + eldFileHeaderSegment.getCmvVinNumber() + ", Expected = Not found this truck in driver events from DB", false);

        //Event lastDutyEvent = events.stream().filter(event -> event.getEventType() == 1).findFirst().orElse(null);
        Event lastDutyEvent = null; //TODO СДЕЛАТЬ ПОИСК ПОСЛЕДНЕГО ИВЕНТА
        if (lastDutyEvent != null) {
            ErrorsLog.writeCsvTestResultToReport("Trailer Number(s): Actual = " + eldFileHeaderSegment.getTrailerNumber()
                    + ", Expected = " + lastDutyEvent.getTrailerNumber(), eldFileHeaderSegment
                    .checkShippingTrailerNumbersValue(lastDutyEvent.getTrailerNumber(), eldFileHeaderSegment.getTrailerNumber(),
                            "Trailer Number(s)", lastDutyEvent.getEldSequence()));
            ErrorsLog.writeCsvTestResultToReport("Shipping Document Number: Actual = " + eldFileHeaderSegment.getShippingDocumentNumber()
                    + ", Expected = " + lastDutyEvent.getShippingNumber(), eldFileHeaderSegment
                    .checkShippingTrailerNumbersValue(lastDutyEvent.getShippingNumber(), eldFileHeaderSegment.getShippingDocumentNumber(),
                            "Shipping Document Number", lastDutyEvent.getEldSequence()));
            ErrorsLog.writeCsvTestResultToReport("Current Laitude: Actual = " + eldFileHeaderSegment.getCurrentLatitude()
                    + ", Expected = " + lastDutyEvent.getLatitude(), eldFileHeaderSegment
                    .checkCoordinatesValue(lastDutyEvent.getLatitude(), eldFileHeaderSegment.getCurrentLatitude(),
                            "Current Latitude", lastDutyEvent.getEldSequence()));
            ErrorsLog.writeCsvTestResultToReport("Current Longitude: Actual = " + eldFileHeaderSegment.getCurrentLongitude()
                    + ", Expected = " + lastDutyEvent.getLongitude(), eldFileHeaderSegment
                    .checkCoordinatesValue(lastDutyEvent.getLongitude(), eldFileHeaderSegment.getCurrentLongitude(),
                            "Current Longitude", lastDutyEvent.getEldSequence()));
            ErrorsLog.writeCsvTestResultToReport("Current TVM: Actual = " + eldFileHeaderSegment.getTotalVehicleMiles()
                    + ", Expected = " + lastDutyEvent.getTotalVehicleMiles(), eldFileHeaderSegment
                    .checkDoubleValue(lastDutyEvent.getTotalVehicleMiles(), eldFileHeaderSegment.getTotalVehicleMiles(),
                            "Current TVM", lastDutyEvent.getEldSequence()));
            ErrorsLog.writeCsvTestResultToReport("Current TEH: Actual = " + eldFileHeaderSegment.getTotalEngineHours()
                    + ", Expected = " + lastDutyEvent.getTotalEngineHours(), eldFileHeaderSegment
                    .checkDoubleValue(lastDutyEvent.getTotalEngineHours(), eldFileHeaderSegment.getTotalEngineHours(),
                            "Current TEH", lastDutyEvent.getEldSequence()));
            EldFileHeaderSegment.errorLogs.clear();
        }
    }

    private List<Driver> findCoDrivers(Set<Truck> trucks) {
        List<Driver> coDrivers = new ArrayList<>();
        trucks
                .stream()
                .map(truck -> DriverDAO.getDriversByTruck(truck.getTruckId(), ValidatorAttributes.getDateFrom(), ValidatorAttributes.getDateTo()))
                .forEach(coDrivers::addAll);
        return coDrivers;
    }

    private Set<Truck> findTrucks(List<Event> events) {
        return events
                .stream()
                .filter(event -> event.getTruckId() > 0)
                .map(event -> new Truck(event.getTruckId(), event.getTruckNumber(), event.getTruckVin()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private void checkCmvList(CmvList cmv) {
        Truck truck = trucks.stream()
                .findFirst()
                .filter(x -> x.getTruckNumber().equals(cmv.getCmvPowerUnitNumber()) && x.getVinNumber().equals(cmv.getCmvVinNumber())).orElse(null);
        if (truck != null)
            ErrorsLog.writeCsvTestResultToReport("CMV: Actual = " + cmv.getCmvPowerUnitNumber() + ", " + cmv.getCmvVinNumber() + ", Expected = " +
                    truck.getTruckNumber() + ", " + truck.getVinNumber(), true);
        else
            ErrorsLog.writeCsvTestResultToReport("CMV: Actual = " + cmv.getCmvPowerUnitNumber() + ", " + cmv.getCmvVinNumber() + ", " +
                    "Truck from events not found! ", false);
    }

    private void checkUserList(UserList user) {
        Driver driver = userList.stream()
                .findFirst()
                .filter(x -> x.getLastName().equals(user.getUserLastName()) && x.getFirstName().equals(user.getUserFirstName())).orElse(null);
        if (driver != null)
            ErrorsLog.writeCsvTestResultToReport("User: Actual = " + user.getUserLastName() + ", " + user.getUserFirstName() + ", Expected = " +
                    driver.getLastName() + ", " + driver.getFirstName(), true);
        else
            ErrorsLog.writeCsvTestResultToReport("User: Actual = " + user.getUserLastName() + ", " + user.getUserFirstName() + ", " +
                    "User from events not found! ", false);
    }

    private ArrayList<Integer> getELDSeqEventsFromCSV() {
        ArrayList<Integer> eldSeqOfEventsFromDb = new ArrayList<>();
        csvReader.getEldEventsList()
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getEventSequence(), 16))
                .boxed()
                .forEach(eldSeqOfEventsFromDb::add);
        csvReader.getEldLoginAndLogoutEvents()
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getEventSequence(), 16))
                .boxed()
                .forEach(eldSeqOfEventsFromDb::add);
        csvReader.getCmvEnginePowerUpAndShutDownEvents()
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getEventSequence(), 16))
                .boxed()
                .forEach(eldSeqOfEventsFromDb::add);
        csvReader.getMalfunctionsAndDataDiagnosticEvents()
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getEventSequence(), 16))
                .boxed()
                .forEach(eldSeqOfEventsFromDb::add);
        csvReader.getDriversCertificationActions()
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getEventSequence(), 16))
                .boxed()
                .forEach(eldSeqOfEventsFromDb::add);
        return eldSeqOfEventsFromDb;
    }

    private void checkTheUniquenessELDSequence(ArrayList<Integer> eldSequenceList) {
        ErrorsLog.createNewSubAnchor("Checking the uniqueness of the ELD Sequence:");
        Set<Integer> repeatSeq = eldSequenceList.stream().filter(i -> Collections.frequency(eldSequenceList, i) > 1).collect(Collectors.toSet());
        if (repeatSeq.size() > 0) {
            ErrorsLog.writeCsvTestResultToReport("Found " + repeatSeq.size() + " repetitions of ELD Sequence", false);
            ArrayList<String> repetitionEvents = new ArrayList<>();
            repeatSeq
                    .forEach(seq -> events
                            .stream()
                            .filter(e -> Integer.parseInt(e.getEldSequence())  == seq)
                            .map(event -> "ELD Sequence = " + event.getEldSequence() +
                                    ", Event = " + event.getEventName() + ", Time = " + event.getEventTimestamp()
                                    + " RS = " + event.getRecordStatus() + ", RO = " + event.getRecordOrigin())
                            .forEach(repetitionEvents::add));
            ErrorsLog.writeNumberedErrorsList(repetitionEvents);
        } else
            ErrorsLog.writeCsvTestResultToReport("Repetitions of ELD Sequence not found", true);
    }

    public void toAnalyzeCsvFile() throws Exception {
        ErrorsLog.createNewAnchor("CSV Structure");
        checkAllRequireHeaders(csvReader.getCsvFileRowsList());
        csvReader.parseEldEvents();
        if (csvReader.getEldFileHeaderSegment() != null && currentDriver != null)
            checkEldFileHeaderSegmentAttribute(csvReader.getEldFileHeaderSegment());

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.USER_LIST);
        csvReader.getUserList().forEach(this::checkUserList);

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.CMV_LIST);
        csvReader.getCmvList().forEach(this::checkCmvList);

        checkTheUniquenessELDSequence(getELDSeqEventsFromCSV());

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_EVENT_LIST);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getEldEventsList().size() + " events.", true);
        csvReader.getEldEventsList().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_EVENT_ANNOTATIONS_OR_COMMENTS);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getEldEventAnnotationOrComments().size() + " events.", true);
        csvReader.getEldEventAnnotationOrComments().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.CMV_ENGINE_POWER_UP_AND_SHUT_DOWN_ACTIVITY);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getCmvEnginePowerUpAndShutDownEvents().size() + " events.", true);
        csvReader.getCmvEnginePowerUpAndShutDownEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.DRIVERS_CERTIFICATION_ACTIONS);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getDriversCertificationActions().size() + " events.", true);
        csvReader.getDriversCertificationActions().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_LOGIN_LOGOUT_REPORT);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getEldLoginAndLogoutEvents().size() + " events.", true);
        csvReader.getEldLoginAndLogoutEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.MALFUNCTIONS_AND_DATA_DIAGNOSTIC_EVENTS);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getMalfunctionsAndDataDiagnosticEvents().size() + " events.", true);
        csvReader.getMalfunctionsAndDataDiagnosticEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        /*Search for Unidentified events by truck numbers and compare them with the Unidentified field in the CSV report.*/
        ErrorsLog.createNewSubAnchor(HeadersCsvReport.UNIDENTIFIED_DRIVER_PROFILE_RECORDS);
        ErrorsLog.writeCsvTestResultToReport("Checked " + csvReader.getUnidentifiedEvents().size() + " events.", true);
        List<Event> unidentifiedEvents = new ArrayList<>();
        trucks
                .stream()
                .map(truck -> EventDAO.getUnidentifiedEventsByTruckId(truck.getTruckId()))
                .forEach(unidentifiedEvents::addAll);
        if (unidentifiedEvents.size() != 0)
            csvReader.getUnidentifiedEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(unidentifiedEvents));
        else if (csvReader.getUnidentifiedEvents().size() != 0)
            ErrorsLog.writeCsvTestResultToReport("List size of UE from DB = " + unidentifiedEvents.size() + ", but list size from CSV = "
                    + csvReader.getUnidentifiedEvents().size(), false);

        /*Search for events that are in the database, but not in the CSV report.*/
        int numOfEventsFromCsv = csvReader.getEldEventsList().size() + csvReader.getCmvEnginePowerUpAndShutDownEvents().size() + csvReader.getMalfunctionsAndDataDiagnosticEvents().size()
                + csvReader.getEldLoginAndLogoutEvents().size() + csvReader.getDriversCertificationActions().size();
        ErrorsLog.createNewSubAnchor("Events from DB that were not included in the report:");
        ErrorsLog.writeCsvTestResultToReport("Num of events from DB = " + events.size() + ", num of events from CSV = "
                + numOfEventsFromCsv, true);
        getELDSeqEventsFromCSV().forEach(eldSeq -> events.removeIf(event -> eldSeq == Integer.parseInt(event.getEldSequence())));
        ErrorsLog.writeNumberedErrorsList(new ArrayList<>(events.stream()
                .map(event -> "ELD Sequence = " + event.getEldSequence() + ", Event = " + event.getEventName() + ", Time = " + event.getEventTimestamp()
                        + " RS = " + event.getRecordStatus() + ", RO = " + event.getRecordOrigin())
                .collect(Collectors.toList())));
    }
}
