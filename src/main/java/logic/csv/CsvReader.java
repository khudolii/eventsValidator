package logic.csv;

import au.com.bytecode.opencsv.CSVReader;
import logic.ErrorsLog;
import logic.csv.csvFileBlocks.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static logic.csv.HeadersCsvReport.*;

public class CsvReader {
    private EldFileHeaderSegment eldFileHeaderSegment = null;
    private List<UserList> userList = new ArrayList<>();
    private List<CmvList> cmvList = new ArrayList<>();
    private List<EldEvents> eldEventsList = new ArrayList<>();
    private List<DriversCertificationActions> driversCertificationActions = new ArrayList<>();
    private List<CmvEnginePowerUpAndShutDownEvents> cmvEnginePowerUpAndShutDownEvents = new ArrayList<>();
    private List<EldEventAnnotationOrComments> eldEventAnnotationOrComments = new ArrayList<>();
    private List<EldLoginAndLogoutEvents> eldLoginAndLogoutEvents = new ArrayList<>();
    private List<MalfunctionsAndDataDiagnosticEvents> malfunctionsAndDataDiagnosticEvents = new ArrayList<>();
    private List<UnidentifiedEvents> unidentifiedEvents = new ArrayList<>();
    private static ArrayList<String> errorsLog = new ArrayList<>();

    public CsvReader(InputStreamReader reader) {
        CSVReader csvReader = new CSVReader(reader, ',', '"', 0);
        try {
            csvFileRowsList = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CsvReader() {
    }

    public EldFileHeaderSegment getEldFileHeaderSegment() {
        return eldFileHeaderSegment;
    }

    public List<String[]> getCsvFileRowsList() {
        return csvFileRowsList;
    }

    public List<UserList> getUserList() {
        return userList;
    }

    public List<CmvList> getCmvList() {
        return cmvList;
    }

    public List<EldEvents> getEldEventsList() {
        return eldEventsList;
    }

    public List<DriversCertificationActions> getDriversCertificationActions() {
        return driversCertificationActions;
    }

    public List<CmvEnginePowerUpAndShutDownEvents> getCmvEnginePowerUpAndShutDownEvents() {
        return cmvEnginePowerUpAndShutDownEvents;
    }

    public List<EldEventAnnotationOrComments> getEldEventAnnotationOrComments() {
        return eldEventAnnotationOrComments;
    }

    public List<EldLoginAndLogoutEvents> getEldLoginAndLogoutEvents() {
        return eldLoginAndLogoutEvents;
    }

    public List<MalfunctionsAndDataDiagnosticEvents> getMalfunctionsAndDataDiagnosticEvents() {
        return malfunctionsAndDataDiagnosticEvents;
    }

    public List<UnidentifiedEvents> getUnidentifiedEvents() {
        return unidentifiedEvents;
    }

    public List<String[]> csvFileRowsList;

    public void readCsvFile(String pathToCsvFile) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(pathToCsvFile), ',', '"', 0);
        this.csvFileRowsList = reader.readAll();
    }

    public void parseEldEvents() throws Exception {
        String header = "";
        int expectedElementsInRow = 0;
        for (int i = 0; i < csvFileRowsList.size(); i++) {
            String[] row = csvFileRowsList.get(i);
            System.out.println(Arrays.toString(row));
            if (row.length == 1) {
                header = Arrays.toString(row).replaceAll("\\[", "").replaceAll("\\]", "");
                continue;
            }
            switch (header) {
                case ELD_FILE_HEADER_SEGMENT: {
                    eldFileHeaderSegment = new EldFileHeaderSegment
                            .Builder()
                            .setDriverLastName(csvFileRowsList.get(i)[0])
                            .setDriverFirstName(csvFileRowsList.get(i)[1])
                            .setDriverEldUserName(csvFileRowsList.get(i)[2])
                            .setDriverLicenseIssuingState(csvFileRowsList.get(i)[3])
                            .setDriverLicenseNumber(csvFileRowsList.get(i)[4])
                            .setCoDriverLastName(csvFileRowsList.get(i + 1)[0])
                            .setCoDriverFirstName(csvFileRowsList.get(i + 1)[1])
                            .setCoDriverEldUserName(csvFileRowsList.get(i + 1)[2])
                            .setCmvPowerUnitNumber(csvFileRowsList.get(i + 2)[0])
                            .setCmvVinNumber(csvFileRowsList.get(i + 2)[1])
                            .setTrailerNumber(csvFileRowsList.get(i + 2)[2])
                            .setCarriersUSDOTNumber(csvFileRowsList.get(i + 3)[0])
                            .setCarrierName(csvFileRowsList.get(i + 3)[1])
                            .setMultiDayBasisUsed(csvFileRowsList.get(i + 3)[2])
                            .setTimeZoneOffsetFromUtc(csvFileRowsList.get(i + 3)[4])
                            .setShippingDocumentNumber(csvFileRowsList.get(i + 4)[0])
                            .setExemptDriverConfiguration(csvFileRowsList.get(i + 4)[1])
                            .setCurrentDate(csvFileRowsList.get(i + 5)[0])
                            .setCurrentTime(csvFileRowsList.get(i + 5)[1])
                            .setCurrentLatitude(csvFileRowsList.get(i + 5)[2])
                            .setCurrentLongitude(csvFileRowsList.get(i + 5)[3])
                            .setCurrentTotalVehicleMiles(Double.parseDouble(csvFileRowsList.get(i + 5)[4]))
                            .setCurrentTotalEngineHours(Double.parseDouble(csvFileRowsList.get(i + 5)[5]))
                            .setEldRegistrationId(csvFileRowsList.get(i + 6)[0])
                            .setEldIdentifier(csvFileRowsList.get(i + 6)[1])
                            .setEldAuthenticated(csvFileRowsList.get(i + 6)[2])
                            .setOutputFileComment(csvFileRowsList.get(i + 6)[3])
                            .build();
                    header="";
                }
                break;
                case USER_LIST: {
                    expectedElementsInRow = 5;
                    userList.add(new UserList
                            .Builder()
                            .setUserEldAccountType(row[1])
                            .setUserLastName(row[2])
                            .setUserFirstName(row[3])
                            .build());
                }
                break;
                case CMV_LIST: {
                    expectedElementsInRow = 4;
                    cmvList.add(new CmvList
                            .Builder()
                            .setCmvPowerUnitNumber(row[1])
                            .setCmvVinNumber(row[2])
                            .build());
                }
                break;
                case ELD_EVENT_LIST: {
                    expectedElementsInRow = 18;
                    eldEventsList.add(new EldEvents
                            .Builder()
                            .setEventSequence(row[0])
                            .setRecordStatus(parseInt(row[1]))
                            .setRecordOrigin(parseInt(row[2]))
                            .setEventType(parseInt(row[3]))
                            .setEventCode(parseInt(row[4]))
                            .setEventDate(row[5])
                            .setEventTime(row[6])
                            .setAccumulatedVehicleMiles(Double.parseDouble(row[7]))
                            .setElapsedEngineHours(Double.parseDouble(row[8]))
                            .setLatitude(row[9])
                            .setLongitude(row[10])
                            .setDistanceLastValidCoordinates(Double.parseDouble(row[11]))
                            .setOrderNumber(row[12])
                            .setOrderNumberForRecordOriginator(row[13])
                            .setMalfunctionIndicatorStatus(parseInt(row[14]))
                            .setDataDiagnosticEventIndicatorStatus(parseInt(row[15]))
                            .setEventDataCheckValue(row[16])
                            .build());
                }
                break;
                case ELD_EVENT_ANNOTATIONS_OR_COMMENTS: {
                    expectedElementsInRow = 7;
                    eldEventAnnotationOrComments.add(new EldEventAnnotationOrComments
                            .Builder()
                            .setEventSequence(row[0])
                            .setEldUserName(row[1])
                            .setCommentTextOrAnnotation(row[2])
                            .setEventDate(row[3])
                            .setEventTime(row[4])
                            .setDriverLocationDescription(row[5])
                            .build());
                }
                break;
                case DRIVERS_CERTIFICATION_ACTIONS: {
                    expectedElementsInRow = 7;
                    driversCertificationActions.add(new DriversCertificationActions
                            .Builder()
                            .setEventSequence(row[0])
                            .setEventCode(parseInt(row[1]))
                            .setEventDate(row[2])
                            .setEventTime(row[3])
                            .setDateOfTheCertifiedRecord(row[4])
                            .setOrderNumber(row[5])
                            .build());
                }
                break;
                case MALFUNCTIONS_AND_DATA_DIAGNOSTIC_EVENTS: {
                    expectedElementsInRow = 9;
                    malfunctionsAndDataDiagnosticEvents.add(new MalfunctionsAndDataDiagnosticEvents
                            .Builder()
                            .setEventSequence(row[0])
                            .setEventCode(parseInt(row[1]))
                            .setMalfunctionOrDiagnosticCode(row[2])
                            .setEventDate(row[3])
                            .setEventTime(row[4])
                            .setTotalVehicleMiles(Double.parseDouble(row[5]))
                            .setTotalEngineHours(Double.parseDouble(row[6]))
                            .setOrderNumber(row[7])
                            .build());
                }
                break;
                case ELD_LOGIN_LOGOUT_REPORT: {
                    expectedElementsInRow = 8;
                    eldLoginAndLogoutEvents.add(new EldLoginAndLogoutEvents
                            .Builder()
                            .setEventSequence(row[0])
                            .setEventCode(parseInt(row[1]))
                            .setEldUserName(row[2])
                            .setEventDate(row[3])
                            .setEventTime(row[4])
                            .setTotalVehicleMiles(Double.parseDouble(row[5]))
                            .setTotalEngineHours(Double.parseDouble(row[6]))
                            .build());
                }
                break;
                case CMV_ENGINE_POWER_UP_AND_SHUT_DOWN_ACTIVITY: {
                    expectedElementsInRow = 13;
                    cmvEnginePowerUpAndShutDownEvents.add(new CmvEnginePowerUpAndShutDownEvents
                            .Builder()
                            .setEventSequence(row[0])
                            .setEventCode(parseInt(row[1]))
                            .setEventDate(row[2])
                            .setEventTime(row[3])
                            .setTotalVehicleMiles(Double.parseDouble(row[4]))
                            .setTotalEngineHours(Double.parseDouble(row[5]))
                            .setLatitude(row[6])
                            .setLongitude(row[7])
                            .setPowerUnitNumber(row[8])
                            .setVinNumber(row[9])
                            .setTrailerNumber(row[10])
                            .setShippingNumber(row[11])
                            .build());
                }
                break;
                case UNIDENTIFIED_DRIVER_PROFILE_RECORDS: {
                    expectedElementsInRow = 16;
                    unidentifiedEvents.add(new UnidentifiedEvents
                            .Builder()
                            .setEventSequence(row[0])
                            .setRecordStatus(parseInt(row[1]))
                            .setRecordOrigin(parseInt(row[2]))
                            .setEventType(parseInt(row[3]))
                            .setEventCode(parseInt(row[4]))
                            .setEventDate(row[5])
                            .setEventTime(row[6])
                            .setAccumulatedVehicleMiles(Double.parseDouble(row[7]))
                            .setElapsedEngineHours(Double.parseDouble(row[8]))
                            .setLatitude(row[9])
                            .setLongitude(row[10])
                            .setDistanceLastValidCoordinates(Double.parseDouble(row[11]))
                            .setOrderNumber(row[12])
                            .setMalfunctionIndicatorStatus(parseInt(row[13]))
                            .setEventDataCheckValue(row[14])
                            .build());
                }
            }
            if (row.length != expectedElementsInRow && !header.equals(ELD_FILE_HEADER_SEGMENT) && !header.equals(END_OF_FILE) && !header.equals("")) {
                ErrorsLog.writeCsvTestResultToReport("Block: " + header + ". Invalid number of elements per line, actual= " + row.length + " ," +
                        " but expected = " + expectedElementsInRow + ".\n Line: " + Arrays.toString(row),false);
            }
        }
    }
}
