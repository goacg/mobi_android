package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseResponse;

public class UserRegiterResponse extends BaseResponse {

    public static final int ERROR_CODE_PARAM = -1;
    public static final int ERROR_CODE_ALREADY_REGISTER = -2;
    
    private int uid;

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
}
