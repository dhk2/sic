package anticlimacticteleservices.sic;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;

public class Util {
    public static String getHowLongAgo(Long pointInTime){
        Long diff = new Date().getTime()- pointInTime;
        int minutes = (int) ((diff / (1000*60)) % 60);
        int hours   = (int) ((diff / (1000*60*60)) % 24);
        int days = (int) ((diff / (1000*60*60*24)));
        String timehack="";
        if (minutes ==1) {
            timehack= "1 minute ago";
        }
        if (minutes>1){
            timehack = minutes + " minutes ago";
        }
        if (minutes==0) {
            timehack = " ago";
        }
        if (hours==1){
            timehack="1 hour,"+timehack;
        }
        if (hours>1){
            timehack= hours +" hours,"+timehack;
        }
        if (days==1){
            timehack="yesterday";
        }
        if (days>1){
            timehack= days +" days ago";
        }if (days>18000){
            timehack = "Time im-memorial";
        }
        return timehack;
    }


    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SicSync.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        if (null==MainActivity.masterData){
            //running in background.
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            builder.setMinimumLatency(30 * 1000); // Wait at least 5m
            builder.setOverrideDeadline(60 * 60 * 1000); // Maximum delay 60m
            //TODO figure out repercussions of adding annotiation.
            //builder.setRequiresBatteryNotLow(true);
        }
        else {
            //running while app is running
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setMinimumLatency(30 * 1000); // Wait at least 5m
            builder.setOverrideDeadline(5 * 60 * 1000); // Maximum delay 60m
        }
        builder.setPersisted(true);

        Log.v("Util-Schedule","scheduling sync service job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
    public static String writeSubtitles(Context context, Video video) {
        String sdf = "HH:mm:ss,SS";
        Log.v("Util.subtitles","starting to print subtitles for "+video.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf);
        long t = 8 * 60 * 60 * 1000;
        Date time = new Date(t);
        FileWriter fileWriter = null;
        String fileName = video.getMp4().substring(video.getMp4().lastIndexOf("/"));
        fileName=fileName.substring(0,fileName.indexOf("."));
        String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+fileName;
        try {
            fileWriter = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        int z =0;
        for (Comment c : MainActivity.masterData.getCommentDao().getCommentsByFeedId(video.getID())){

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
                z++;
                printWriter.println(z);
                printWriter.println(time1 + " --> " + time2);
                printWriter.println(c.getAuthor()+":");
                printWriter.println(text);
                printWriter.println("");
                text=left;
            }

        }
        printWriter.close();
    return output;
    }

    public static String writeHtml(String html) {

        FileWriter fileWriter = null;
        String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "webtorrent.html";
        try {
            fileWriter = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(html);
        printWriter.close();
        return output;
    }

    public static class DownloadVideo extends AsyncTask<String, String, String>
    {

        File downloadFolder = null;
        File outputFile = null;

        @Override
        protected String doInBackground(String... strings) {

            try {
                System.out.println("attempt to download url:"+strings[0]);
                java.net.URL url = new URL(strings[0]);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Util-Download", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }
                downloadFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() );

                if (!downloadFolder.exists()) {
                    downloadFolder.mkdir();
                    Log.e("Util-Download", "Directory Created.");
                }
                String fileName = strings[0].substring(strings[0].lastIndexOf("/"));
                outputFile = new File(downloadFolder,fileName);
                Log.e("Util-Download","downloading file:"+fileName);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e("Util-Download", "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e("Util-Download", "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}