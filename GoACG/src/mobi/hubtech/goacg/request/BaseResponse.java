package mobi.hubtech.goacg.request;

public class BaseResponse {

    public static final int ERROR_CODE_SUCCESS = 0;
    public static final int TAG_CACHEED = 65535;

    private int mark;
    
    private int error_code;
    private String msg;
    
    public int getTag() {
        return mark;
    }
    public void setMark(int mark) {
        this.mark = mark;
    }
    public int getError_code() {
        return error_code;
    }
    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
