package logic.entities;

import java.util.Calendar;
import java.util.TimeZone;

public class Driver {
    private long driverId;
    private String firstName;
    private String lastName;
    private String loginName;
    private String licenseIssuingState;
    private String licenseNumber;
    private String organizationName;
    private String usdotNumber;
    private String multiDayBasisUsed;
    private String homeTerminalTimezone;
    private String homeTerminalTimezoneOffset;

    public Driver(long driverId, String firstName, String lastName, String loginName, String licenseIssuingState,
                  String licenseNumber, String organizationName, String usdotNumber, String multiDayBasisUsed, String homeTerminalTimezone) {
        this.driverId = driverId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.licenseIssuingState = licenseIssuingState;
        this.licenseNumber = licenseNumber;
        this.organizationName = organizationName;
        this.usdotNumber = usdotNumber;
        this.multiDayBasisUsed = multiDayBasisUsed;
        this.homeTerminalTimezone = homeTerminalTimezone;
        TimeZone offtime_zone = TimeZone.getTimeZone(homeTerminalTimezone);
        this.homeTerminalTimezoneOffset ="0" +  Math.abs(offtime_zone.getOffset(Calendar.ZONE_OFFSET)/3600000);
    }

    public String getMultiDayBasisUsed() {
        return multiDayBasisUsed;
    }

    public String getHomeTerminalTimezoneOffset() {
        return homeTerminalTimezoneOffset;
    }

    public String getHomeTerminalTimezone() {
        return homeTerminalTimezone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getDriverId() {
        return driverId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLicenseIssuingState() {
        return licenseIssuingState;
    }

    public void setLicenseIssuingState(String licenseIssuingState) {
        this.licenseIssuingState = licenseIssuingState;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUsdotNumber() {
        return usdotNumber;
    }

    public void setUsdotNumber(String usdotNumber) {
        this.usdotNumber = usdotNumber;
    }
}
