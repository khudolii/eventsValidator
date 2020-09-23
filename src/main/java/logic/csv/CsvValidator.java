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

    private Driver currentDriver;
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
            this.events = EventDAO.getEvents();
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
                            + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Expected ', , ,'"
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

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_EVENT_LIST);
        csvReader.getEldEventsList().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_EVENT_ANNOTATIONS_OR_COMMENTS);
        csvReader.getEldEventAnnotationOrComments().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.CMV_ENGINE_POWER_UP_AND_SHUT_DOWN_ACTIVITY);
        csvReader.getCmvEnginePowerUpAndShutDownEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.DRIVERS_CERTIFICATION_ACTIONS);
        csvReader.getDriversCertificationActions().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.ELD_LOGIN_LOGOUT_REPORT);
        csvReader.getEldLoginAndLogoutEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.MALFUNCTIONS_AND_DATA_DIAGNOSTIC_EVENTS);
        csvReader.getMalfunctionsAndDataDiagnosticEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

        ErrorsLog.createNewSubAnchor(HeadersCsvReport.UNIDENTIFIED_DRIVER_PROFILE_RECORDS);
        csvReader.getUnidentifiedEvents().forEach(eldEvents -> eldEvents.compareEventsFromCsvAndDb(events));

    }

    public static void main(String[] args) throws Exception {
        ValidatorAttributes.setDateTo("2020-08-07");
        ValidatorAttributes.setDateFrom("2020-08-01");
        ErrorsLog.createReportFile("34131", 40);
        // CsvAnalyzer csvAnalyzer = new CsvAnalyzer();
        //csvAnalyzer.toAnalyzeCsvFile();
        ErrorsLog.writeResultsToFile();
    }
}
