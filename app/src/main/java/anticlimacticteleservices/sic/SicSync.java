package anticlimacticteleservices.sic;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

public class SicSync extends JobService {
   public static Context context;
    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("woohoo, starting sync in background");
        context = getApplication().getApplicationContext();
        new ChannelUpdate().execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}