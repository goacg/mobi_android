package mobi.hubtech.goacg.request;

public class BaseResponse implements IResponse {

    public static final int ERROR_CODE_SUCCESS = 0;
    public static final int FLAG_CACHEED = 0x00000001;

    private int flag;
    
    private int error_code;
    private String msg;
    
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public void addFlag(int flag) {
    	this.flag |= flag;
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
	@Override
	public int getErrorCode() {
		return error_code;
	}
}
