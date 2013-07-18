package mobi.hubtech.goacg.receiver;

import mobi.hubtech.goacg.service.PushService;
import mobi.hubtech.goacg.utils.AlarmUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmUtils.setAlarm(context);
        Intent service = new Intent(context, PushService.class);
        context.startService(service);
    }
}
