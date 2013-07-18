package mobi.hubtech.goacg.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mobi.hubtech.goacg.bean.TimeSegment;

public class DateTimeUtils {
    
    public static TimeSegment getMonthTimeSegment(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1, 0, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long begin = calendar.getTimeInMillis();
        calendar.roll(Calendar.DAY_OF_MONTH, -1);
        long end = calendar.getTimeInMillis();
        return new TimeSegment(begin, end);
    }
    
    public static String formatTimestamp(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        String formatedTime = sdf.format(c.getTime());
        return formatedTime;
    }
}
