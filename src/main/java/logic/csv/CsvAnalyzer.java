package logic.csv;

import logic.AnalyzeAttributes;
import logic.ErrorsLog;
import logic.csv.csvFileBlocks.CmvList;
import logic.csv.csvFileBlocks.EldEvents;
import logic.csv.csvFileBlocks.EldFileHeaderSegment;
import logic.csv.csvFileBlocks.UserList;
import logic.dao.DriverDAO;
import logic.dao.EventDAO;
import logic.entities.Driver;
import logic.entities.Event;
import logic.entities.Truck;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CsvAnalyzer {

    private Driver currentDriver;
    private List<Event> events;
    private List<Driver> coDriversList;
    private Set<Truck> trucks;
    private CsvReader csvReader;

    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Driver> getCoDriversList() {
        return coDriversList;
    }

    public Set<Truck> getTrucks() {
        return trucks;
    }

    public CsvReader getCsvReader() {
        return csvReader;
    }

    public CsvAnalyzer(InputStreamReader reader) {
        csvReader = new CsvReader(reader);
        try {
            System.out.println("22222222");
            currentDriver = DriverDAO.getDriver(AnalyzeAttributes.getDriverId());
            //csvReader.readCsvFile("/home/evgeniy/Desktop/Tagie.txt");
            this.events = EventDAO.getEvents();
            trucks = findTrucks(events);
            coDriversList = findCoDrivers(trucks);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void checkAllRequireHeaders(List<String[]> csvFileRowsList) {
        ErrorsLog.createNewSubAnchor("Required headers:");
        int headerIndex = 0;
        String currentHeader;
        for (int i = 16; i < csvFileRowsList.size(); i++) {
            String[] row = csvFileRowsList.get(i);
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
        if (coDriversList.size() > 0) {
            //TODO FIX CURRENT DRIVER AS CO DRIVER
            boolean flag = false;
            for (Driver coDriver : coDriversList) {
                if (coDriver.getFirstName().equals(eldFileHeaderSegment.getCoDriverFirstName())
                        && coDriver.getLastName().equals(eldFileHeaderSegment.getCoDriverLastName())
                        && coDriver.getLoginName().equals(eldFileHeaderSegment.getCoDriverEldUserName())) {
                    flag = true;
                    ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                            + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Expected: " + coDriver.getLastName() + ", " + coDriver.getFirstName()
                            + ", " + coDriver.getLoginName(), flag);
                    break;
                }
            }
            if (!flag && !eldFileHeaderSegment.getCoDriverEldUserName().equals(currentDriver.getLoginName()))
                ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                        + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Not found this co driver in DB.", flag);
        } else {
            ErrorsLog.writeCsvTestResultToReport("Co Driver Info: Actual = " + eldFileHeaderSegment.getCoDriverLastName() + ", " + eldFileHeaderSegment.getCoDriverFirstName()
                            + ", " + eldFileHeaderSegment.getCoDriverEldUserName() + ", Expected ', , ,'"
                    , eldFileHeaderSegment.getCoDriverLastName().equals("")
                            && eldFileHeaderSegment.getCoDriverFirstName().equals("")
                            && eldFileHeaderSegment.getCoDriverEldUserName().equals(""));
        }

        boolean flag = false;
        for (Truck truck : trucks) {
            if (truck.getTruckNumber().equals(eldFileHeaderSegment.getCmvPowerUnitNumber())
                    && truck.getVinNumber().equals(eldFileHeaderSegment.getCmvVinNumber())) {
                flag = true;
                ErrorsLog.writeCsvTestResultToReport("Current Truck: Actual = " + eldFileHeaderSegment.getCmvPowerUnitNumber() + ", "
                        + eldFileHeaderSegment.getCmvVinNumber() + ", Expected = " + truck.getTruckNumber() + ", " + truck.getVinNumber(), flag);
                break;
            }
        }
        if (!flag) {
            ErrorsLog.writeCsvTestResultToReport("Current Truck: Actual = " + eldFileHeaderSegment.getCmvPowerUnitNumber() + ", "
                    + eldFileHeaderSegment.getCmvVinNumber() + ", Expected = Not found this truck in driver events from DB", flag);
        }
    }

    private List<Driver> findCoDrivers(Set<Truck> trucks) throws SQLException {
        List<Driver> coDrivers = new ArrayList<>();
        for (Truck truck : trucks)
            coDrivers.addAll(DriverDAO.getDriversByTruck(truck.getTruckId(), AnalyzeAttributes.getDateFrom(), AnalyzeAttributes.getDateTo()));
        //coDrivers.removeIf(driver -> driver.getLoginName().equals(currentDriver.getLoginName()));
        return coDrivers;
    }

    private Set<Truck> findTrucks(List<Event> events) {
        TreeSet<Truck> trucks = new TreeSet<>();
        for (Event event : events) {
            if (event.getTruckId() > 0) {
                Truck truck = new Truck(event.getTruckId(), event.getTruckNumber(), event.getTruckVin());
                trucks.add(truck);
            }
        }
        return trucks;
    }

    private void checkCmvList(CmvList cmv) {
        boolean flag = false;
        for (Truck truck : trucks) {
            if (cmv.getCmvPowerUnitNumber().equals(truck.getTruckNumber()) && cmv.getCmvVinNumber().equals(truck.getVinNumber())) {
                flag = true;
                ErrorsLog.writeCsvTestResultToReport("CMV: Actual = " + cmv.getCmvPowerUnitNumber() + ", " + cmv.getCmvVinNumber() + ", Expected = " +
                        truck.getTruckNumber() + ", " + truck.getVinNumber(), flag);
                break;
            }
        }
        if (!flag) {
            ErrorsLog.writeCsvTestResultToReport("CMV: Actual = " + cmv.getCmvPowerUnitNumber() + ", " + cmv.getCmvVinNumber() + ", Truck from events not found! ", flag);
        }
    }

    private void checkUserList(UserList user) {
        boolean flag = false;
        for (Driver driver : coDriversList) {
            if (driver.getLastName().equals(user.getUserLastName()) && driver.getFirstName().equals(user.getUserFirstName())) {
                flag = true;
                ErrorsLog.writeCsvTestResultToReport("User: Actual = " + user.getUserLastName() + ", " + user.getUserFirstName() + ", Expected = " +
                        driver.getLastName() + ", " + driver.getFirstName(), flag);
                break;
            }
        }
        if (!flag) {
            ErrorsLog.writeCsvTestResultToReport("User: Actual = " + user.getUserLastName() + ", " + user.getUserFirstName() + ", User from events not found! ", flag);
        }
    }

    public void toAnalyzeCsvFile() throws Exception {
        ErrorsLog.createNewAnchor("CSV Structure");
        checkAllRequireHeaders(csvReader.getCsvFileRowsList());
        csvReader.parseEldEvents();
        EldFileHeaderSegment eldFileHeaderSegment = csvReader.parseEldHeader();
        if (eldFileHeaderSegment != null && currentDriver != null) {
            checkEldFileHeaderSegmentAttribute(eldFileHeaderSegment);
        }
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
        AnalyzeAttributes.setDateTo("2020-08-07");
        AnalyzeAttributes.setDateFrom("2020-08-01");
        ErrorsLog.createReportFile("34131", 40);
       // CsvAnalyzer csvAnalyzer = new CsvAnalyzer();
        //csvAnalyzer.toAnalyzeCsvFile();
        ErrorsLog.writeResultsToFile();
    }
}
