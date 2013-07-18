package mobi.hubtech.goacg.global;

import java.util.Hashtable;
import java.util.Map;

import mobi.hubtech.goacg.request.BaseResponse;

public class RequestCache {
    
    private static RequestCache sInstance;
    
    public static RequestCache getInstance() {
        if (sInstance == null) {
            sInstance = new RequestCache();
        }
        return sInstance;
    }
    
    private RequestCache() {
    }
    
    private Map<String, BaseResponse> mCache = new Hashtable<String, BaseResponse>(64);
    
    public void put(String key, BaseResponse value) {
        mCache.put(key, value);
    }
    
    public BaseResponse get(String key) {
        return mCache.get(key);
    }
    
    public void clear() {
        mCache.clear();
    }
}
