package mobi.hubtech.goacg.global;

public class C {
    // debug
    public static final String TAG = "mobi.hubtech.goacg";
    public static final boolean DEBUG = true;
    
    // request
//    public static final String URL = "http://goacg.mobi/active/";
    public static final String URL = "http://***.***.**.**/active/";
    public static final String GET_ALBUMS = "get_albums";
    public static final String GET_UPDATE_URLS = "get_update_urls";
    public static final String SUB_ALBUM = "sub_album";
    public static final String UNSUB_ALBUM = "unsub_album";
    
    // time out
    public static final int TIME_OUT_CONNECTION = 30000;
    public static final int TIME_OUT_READ = 60000;
    
    // shared pre
    public static final String SHARED_PRE_NAME_USER_INFO = "user_info";
    public static final String SHARED_PRE_KEY_USER_ID = "user_id";
    public static final String SHARED_PRE_KEY_USER_MAC = "user_mac";
}
