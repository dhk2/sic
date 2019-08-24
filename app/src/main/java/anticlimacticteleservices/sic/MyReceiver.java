package anticlimacticteleservices.sic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Receiver","received intent:"+intent.toString());
        Util.scheduleJob(context);
        System.out.println("scheduling a job thanks to reboot");
    }
}
