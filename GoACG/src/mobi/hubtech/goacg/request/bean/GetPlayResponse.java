package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.request.BaseResponse;

public class GetPlayResponse extends BaseResponse {

    public static final int ERROR_CODE_NO_UPDATE = -2;
    
    private Play play;

    public Play getPlay() {
        return play;
    }

    public void setPlays(Play play) {
        this.play = play;
    }
}
