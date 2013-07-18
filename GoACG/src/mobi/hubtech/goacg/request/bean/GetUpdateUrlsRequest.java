package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class GetUpdateUrlsRequest extends BaseUrlRequest {
    
    private String user_id;
    
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String getMethod() {
        return "get_update_urls";
    }
}
