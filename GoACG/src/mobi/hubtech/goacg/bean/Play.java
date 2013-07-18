package mobi.hubtech.goacg.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "play")  
public class Play implements Comparable<Play> {
    
    public static final String PLAY_ID = "play_id";
    public static final String VOL = "vol";
    public static final String SHOW_TIME = "show_time";
    public static final String SHOW_URL = "show_url";
    public static final String BIGCOVER = "bigcover";
    public static final String UPDATE_TIME = "update_time";
    
    @DatabaseField(columnName = PLAY_ID, id = true)
    private long play_id;
    
    @DatabaseField(columnName = VOL)
    private int vol;
    
    @DatabaseField(columnName = SHOW_TIME, index = true)
    private long show_time;
    
    @DatabaseField(columnName = SHOW_URL)
    private String show_url;
    
    @DatabaseField(columnName = BIGCOVER)
    private String bigcover;
    
    @DatabaseField(columnName = UPDATE_TIME)
    private long update_time;
    
    @DatabaseField(columnName = Album.ALBUM_ID, foreign = true, foreignAutoRefresh = true)
    private Album album;

    public long getPlay_id() {
        return play_id;
    }
    public void setPlay_id(long play_id) {
        this.play_id = play_id;
    }
    public int getVol() {
        return vol;
    }
    public void setVol(int vol) {
        this.vol = vol;
    }
    public long getShow_time() {
        return show_time;
    }
    public void setShow_time(long show_time) {
        this.show_time = show_time;
    }
    public String getShow_url() {
        return show_url;
    }
    public void setShow_url(String show_url) {
        this.show_url = show_url;
    }
    public String getBigcover() {
        return bigcover;
    }
    public void setBigcover(String bigcover) {
        this.bigcover = bigcover;
    }
    public long getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
    
    @Override
    public int compareTo(Play another) {
        if (this.play_id > another.play_id) {
            return 1;
        } else if (this.play_id < another.play_id) {
            return -1;
        } else {
            return 0;
        }
    }
}
