package anticlimacticteleservices.sic;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SicSync.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(5 * 60 * 1000); // Wait at least 5m
        builder.setOverrideDeadline(60 * 60 * 1000); // Maximum delay 60m
        System.out.println("scheduling sync service job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
    public static String writeSubtitles(Context context, Video video) {
        String sdf = "HH:mm:ss.SS";
        System.out.println("starting to print subtitles for "+video.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf);
        long t = 8 * 60 * 60 * 1000;
        Date time = new Date(t);
        FileWriter fileWriter = null;
        String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + video.getTitle() + ".srt";
        try {
            fileWriter = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (Comment c : video.getComments()){
            String text = c.getText();
            String left=text;
            while (!text.isEmpty()){
                if (text.length()>200) {
                    left = "..."+(text.substring(200));
                    text = text.substring(0,200);
                    }
                else {
                    left = "";
                }
                String time1 = simpleDateFormat.format(time);
                time.setTime(t += (10 * 1000));
                String time2 = simpleDateFormat.format(time);
                time.setTime(t += 10);
                printWriter.println(time1 + "-->" + time2);
                printWriter.println(c.getAuthor()+":");
                printWriter.println(text);
                text=left;
            }

        }
        printWriter.close();
    return output;
    }
    public static FeedItem makeFeedItem(Video v){
        FeedItem newItem=new FeedItem();
        newItem.setMp4(v.getMp4());
        newItem.setDescription(v.getDescription());
        newItem.setTitle(v.getTitle());
        newItem.setDate(v.getDate().getTime());
        newItem.setAuthor(v.getAuthor());
     //   newItem.setAnnotation(v.getAnnotation());
     //   newItem.setCategory(v.getCategory());
    //    newItem.setMagnet(v.getMagnet());
        newItem.setThumbnailurl(v.getThumbnail());
        newItem.setUrl(v.getUrl());
        newItem.setSourceID(v.getID());


        return newItem;
    }
    public static Video makeVideo(FeedItem f){
        Video v = new Video(f.getUrl());
        v.setAuthor(f.getAuthor());
        v.setDate(new Date(f.getDate()));
        v.setDescription(f.getDescription());
        v.setMp4(f.getMp4());
        v.setTitle(f.getTitle());
        v.setThumbnail(f.getThumbnailurl());
        v.setHashtags(f.getHashtags());
        v.setUpCount(f.getUpCount());
        return v;
    }
}