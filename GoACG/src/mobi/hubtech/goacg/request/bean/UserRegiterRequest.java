package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class UserRegiterRequest extends BaseUrlRequest {
    
    private String user_push;
    
    public String getUser_push() {
        return user_push;
    }

    public void setUser_push(String user_push) {
        this.user_push = user_push;
    }

    @Override
    public String getMethod() {
        return "user_regiter";
    }
}
