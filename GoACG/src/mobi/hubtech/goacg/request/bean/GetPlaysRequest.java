package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.request.BaseUrlRequest;

public class GetPlaysRequest extends BaseUrlRequest {
    
    private long album_id;
    private int vol;
    private int n;
    
    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    public String getMethod() {
        return "get_plays";
    }
}
