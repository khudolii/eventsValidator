package logic.csv.csvFileBlocks;

public class UserList {
    private int assignedUserOrderNumber;
    private String userEldAccountType;
    private String userLastName;
    private String userFirstName;

    public int getAssignedUserOrderNumber() {
        return assignedUserOrderNumber;
    }

    public void setAssignedUserOrderNumber(int assignedUserOrderNumber) {
        this.assignedUserOrderNumber = assignedUserOrderNumber;
    }

    public String getUserEldAccountType() {
        return userEldAccountType;
    }

    public void setUserEldAccountType(String userEldAccountType) {
        this.userEldAccountType = userEldAccountType;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public static class Builder {
        private UserList userList;

        public Builder() {
            this.userList = new UserList();
        }

        public Builder setAssignedUserNumber(int assignedUserOrderNumber) {
            userList.assignedUserOrderNumber = assignedUserOrderNumber;
            return this;
        }

        public Builder setUserEldAccountType(String  userEldAccountType) {
            userList.userEldAccountType = userEldAccountType;
            return this;
        }

        public Builder setUserLastName(String userLastName) {
            userList.userLastName = userLastName;
            return this;
        }

        public Builder setUserFirstName(String userFirstName) {
            userList.userFirstName = userFirstName;
            return this;
        }

        public UserList build() {
            return userList;
        }
    }
}
