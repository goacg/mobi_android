package mobi.hubtech.goacg.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import mobi.hubtech.goacg.bean.Play;
import mobi.hubtech.goacg.global.C;
import android.util.Log;

public class LogUtils {
    
    public static void printPlayMap(Map<Long, List<Play>> map) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        for (Entry<Long, List<Play>> entry: map.entrySet()) {
            long time = entry.getKey();
            calendar.setTimeInMillis(time);
            Log.i(C.TAG, "time: " + sdf.format(calendar.getTime()));
            List<Play> playList = entry.getValue();
            for (Play play: playList) {
                Log.d(C.TAG, "name: " + play.getAlbum().getTitle() + " vol: " + play.getVol());
            }
        }
    }
    
    public static void printTimestamp(long time) {
        Log.i(C.TAG, "time: " + DateTimeUtils.formatTimestamp(time));
    }
}
