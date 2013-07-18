package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class SubAlbumRequest extends BaseUrlRequest {
    
    private long user_id;
    private long album_id;
    
    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    @Override
    public String getMethod() {
        return "sub_album";
    }
}
