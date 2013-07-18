package mobi.hubtech.goacg.utils;

import java.util.Calendar;

import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.service.NotifyService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmUtils {
    
    public static void setAlarm(Context context) {
        Calendar c = Calendar.getInstance();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Log.d(C.TAG, "setAlarm: " + DateTimeUtils.formatTimestamp(c.getTimeInMillis()));
        
        Intent intent = new Intent(context, NotifyService.class);
        PendingIntent pending = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC, c.getTimeInMillis(), pending);
    }
    
    public static void setAlarmTomorrow(Context context) {
        Calendar c = Calendar.getInstance();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Log.d(C.TAG, "setAlarmTomorrow: " + DateTimeUtils.formatTimestamp(c.getTimeInMillis()));
        
        Intent intent = new Intent(context, NotifyService.class);
        PendingIntent pending = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC, c.getTimeInMillis(), pending);
    }
}
