package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class GetAlbumRequest extends BaseUrlRequest {
    
    private long album_id;
    private long update_time;
    
    public long getAlbum_id() {
        return album_id;
    }
    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }
    public long getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    
    @Override
    public String getMethod() {
        return "get_album";
    }
}
