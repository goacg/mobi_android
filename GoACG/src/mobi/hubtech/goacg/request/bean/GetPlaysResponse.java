package mobi.hubtech.goacg.request.bean;

import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.request.BaseResponse;

public class GetPlaysResponse extends BaseResponse {
    
    private Play[] plays;

    public Play[] getPlays() {
        return plays;
    }

    public void setPlays(Play[] plays) {
        this.plays = plays;
    }
}
