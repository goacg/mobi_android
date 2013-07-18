package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class GetAlbumsRequest extends BaseUrlRequest {
    
    private long user_id;
    private long begin;
    private long end;
    
    public long getUser_id() {
        return user_id;
    }
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
    public long getBegin() {
        return begin;
    }
    public void setBegin(long begin) {
        this.begin = begin;
    }
    public long getEnd() {
        return end;
    }
    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String getMethod() {
        return "get_albums";
    }
    
    @Override
    public boolean isNeedCache() {
        return true;
    }
}
