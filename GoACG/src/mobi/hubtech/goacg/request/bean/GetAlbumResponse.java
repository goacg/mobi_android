package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.request.BaseResponse;

public class GetAlbumResponse extends BaseResponse {

    public static final int ERROR_CODE_NO_UPDATE = -2;
    
    private Album album;
    
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
}
