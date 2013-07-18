package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.bean.Album;
import mobi.hubtech.goacg.request.BaseResponse;

public class GetAlbumsResponse extends BaseResponse {

    public static final int ERROR_CODE_TIME_ERROR = -2;
    
    private Album[] albums;
    
    public Album[] getAlbums() {
        return albums;
    }
    public void setAlbums(Album[] albums) {
        this.albums = albums;
    }
}
