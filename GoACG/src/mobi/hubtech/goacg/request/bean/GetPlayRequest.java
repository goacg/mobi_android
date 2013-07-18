package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class GetPlayRequest extends BaseUrlRequest {
    
    private long play_id;
    private long update_time;
    
    public long getPlay_id() {
        return play_id;
    }
    public void setPlay_id(long play_id) {
        this.play_id = play_id;
    }
    public long getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    
    @Override
    public String getMethod() {
        return "get_play";
    }
}
