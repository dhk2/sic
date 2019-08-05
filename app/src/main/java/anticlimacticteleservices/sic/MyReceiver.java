package anticlimacticteleservices.sic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Util.scheduleJob(context);
        System.out.println("scheduling a job thanks to reboot");
    }
}
