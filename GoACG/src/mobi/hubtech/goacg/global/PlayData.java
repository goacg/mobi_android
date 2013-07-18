package mobi.hubtech.goacg.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.hubtech.goacg.bean.Play;

public class PlayData {
    
    private static PlayData instance = new PlayData();
    public static PlayData getInstance() {
        return instance;
    }
    
    private PlayData() {
        data = new HashMap<Long, List<Play>>(64);
    }
    
    private HashMap<Long, List<Play>> data;
    
    public synchronized Map<Long, List<Play>> updateData(Map<Long, List<Play>> map) {
        data.putAll(map);
        return data;
    }
    
    public synchronized Map<Long, List<Play>> getData() {
        return data;
    }
    
    public synchronized void clear() {
        data.clear();
    }
}
