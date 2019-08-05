package anticlimacticteleservices.sic;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class SicSync extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("woohoo, starting sync in background");
        new ChannelUpdate().execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}