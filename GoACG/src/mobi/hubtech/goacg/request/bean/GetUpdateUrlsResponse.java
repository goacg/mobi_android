package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.request.BaseResponse;

public class GetUpdateUrlsResponse extends BaseResponse {
    
    private Play[] update_urls;
    
    public Play[] getUpdate_urls() {
        return update_urls;
    }

    public void setUpdate_urls(Play[] update_urls) {
        this.update_urls = update_urls;
    }
}
