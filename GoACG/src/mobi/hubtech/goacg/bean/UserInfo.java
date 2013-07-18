package mobi.hubtech.goacg.bean;

public class UserInfo {
    
    private long UserID = -1;
    private String UserMAC;
    
    public long getUserID() {
        return UserID;
    }
    public void setUserID(long userID) {
        UserID = userID;
    }
    public String getUserMAC() {
        return UserMAC;
    }
    public void setUserMAC(String userMAC) {
        UserMAC = userMAC;
    }
}
