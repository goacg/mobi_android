package mobi.hubtech.goacg.bean;

import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "album")  
public class Album {
    
    public static final String ALBUM_ID = "album_id";
    public static final String TITLE = "title";
    public static final String ICON_32X32 = "icon_32x32";
    public static final String BIGCOVER = "bigcover";
    public static final String SUB = "sub";
    public static final String START_TIME = "start_time";
    public static final String UPDATE_TIME = "update_time";
    
    @DatabaseField(columnName = ALBUM_ID, id = true)
    private long id;
    
    @DatabaseField(columnName = TITLE)
    private String title;
    
    @DatabaseField(columnName = ICON_32X32)
    private String icon_32x32;
    
    @DatabaseField(columnName = BIGCOVER)
    private String bigcover;
    
    @DatabaseField(columnName = SUB)
    private boolean sub;
    
    @DatabaseField(columnName = START_TIME)
    private long start_time;

    @DatabaseField(columnName = UPDATE_TIME)
    private long update_time;
    
    private ArrayList<Play> plays;
    
    public Album() {
    }
    
    public long getId() {
        return id;
    }
    public void setId(long album_id) {
        this.id = album_id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getIcon_32x32() {
        return icon_32x32;
    }
    public void setIcon_32x32(String icon_32x32) {
        this.icon_32x32 = icon_32x32;
    }
    public String getBigcover() {
        return bigcover;
    }
    public void setBigcover(String bigcover) {
        this.bigcover = bigcover;
    }
    public boolean isSub() {
        return sub;
    }
    public void setSub(boolean sub) {
        this.sub = sub;
    }
    public long getStart_time() {
        return start_time;
    }
    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }
    public long getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    public ArrayList<Play> getPlays() {
        return plays;
    }
    public void setPlays(ArrayList<Play> plays) {
        this.plays = plays;
    }
}
