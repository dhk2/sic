package anticlimacticteleservices.sic;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

class ChannelUpdate extends AsyncTask<String, String, Boolean> {
    private final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    private final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Document doc;
    private int dupecount=0;
    private int mirror=0;
    private int newcount=0;
    private ArrayList<Video> allVideos;
    private ArrayList<Channel> allChannels;
    private static Context context;
    private static Long feedAge;
    private static ChannelDao channelDao;
    private static VideoDao videoDao;
    private static CommentDao commentDao;
    SicDatabase sicDatabase;
    ChannelDatabase channelDatabase;
    CommentDatabase commentDatabase;
    String updateError="";
    boolean headless=true;
    boolean backgroundSync;
    boolean wifiOnly;
    boolean wifiConnected;
    boolean mobileConnected;
    boolean forceRefresh;
    boolean muteErrors;
    boolean hideWatched;
    int youtubePlayerChoice;
    int bitchutePlayerChoice;

    public static SharedPreferences preferences;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (!headless && null != MainActivity.masterData.getSwipeRefreshLayout()) {
            MainActivity.masterData.getSwipeRefreshLayout().setRefreshing(false);
            if (null==videoDao){
                updateError="Network offline";
            }
            else {
                if (hideWatched) {
                    MainActivity.masterData.setVideos(videoDao.getUnWatchedVideos());
                } else {
                    MainActivity.masterData.setVideos(videoDao.getVideos());
                }
            }
        }
        if (newcount>0 || forceRefresh) {
            Toast.makeText(context, newcount + " new videos added", Toast.LENGTH_SHORT).show();
        }
        if (updateError !="" && !muteErrors){
            Toast.makeText(context, newcount + updateError, Toast.LENGTH_LONG).show();
        }
        if (!headless ||  backgroundSync) {
            Util.scheduleJob(context);
        }

    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.v("Channel-Update","starting on channel update");
        Boolean videoExists = false;
        Video nv = null;
        if (null == MainActivity.masterData) {
            context = SicSync.context;
            headless=true;
            forceRefresh=false;
        }
        else{
            headless=false;
            context=MainActivity.masterData.context;
            forceRefresh=MainActivity.masterData.isForceRefresh();
            MainActivity.masterData.setForceRefresh(false);
        }
        System.out.println("headless:"+headless+" forceRefresh:"+forceRefresh);
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
            Log.e("Channel-Update","no network connectivity");
            return null;
        }
        if (headless){
            preferences = context.getSharedPreferences( "anticlimacticteleservices.sic" + "_preferences", MODE_PRIVATE);
            feedAge = preferences.getLong("feedAge",7);
            backgroundSync = preferences.getBoolean("backgroundSync",true);
            wifiOnly = preferences.getBoolean("wifiOnly",false);
            youtubePlayerChoice = preferences.getInt("youtubePlayerChoice", 4);
            bitchutePlayerChoice = preferences.getInt("bitchutePlayerChoice", 8);
            muteErrors = preferences.getBoolean("muteErrors",true);
            hideWatched = preferences.getBoolean("hideWatched",false);
        }
        else
        {
            feedAge = MainActivity.preferences.getLong("feedAge",7);
            backgroundSync = MainActivity.preferences.getBoolean("backgroundSync",true);
            wifiOnly = MainActivity.preferences.getBoolean("wifiOnly",false);
            youtubePlayerChoice = MainActivity.preferences.getInt("bitchutePlayerChoice", 8);
            bitchutePlayerChoice = MainActivity.preferences.getInt("bitchutePlayerChoice", 8);
            muteErrors = MainActivity.preferences.getBoolean("muteErrors",true);
            hideWatched = MainActivity.preferences.getBoolean("hideWatched",false);
        }
        Log.v("Update-Channel", "status loaded wifi:"+wifiConnected+" mobile:"+mobileConnected+" background sync:"+backgroundSync+" wifi only:"+wifiOnly+" headless"+headless);
        if (headless && wifiOnly && !wifiConnected){
            return false;
        }
        if (headless && !backgroundSync){
            return false;
        }
        if (null == videoDao){
            if (null == MainActivity.masterData){
                channelDatabase = Room.databaseBuilder(context , ChannelDatabase.class, "channel")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                channelDao = channelDatabase.ChannelDao();
                sicDatabase = Room.databaseBuilder(context, SicDatabase.class, "mydb")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                videoDao = sicDatabase.videoDao();
                commentDatabase = Room.databaseBuilder(context , CommentDatabase.class, "comment")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                commentDao = commentDatabase.CommentDao();

            }
            else {
                videoDao = MainActivity.masterData.getVideoDao();
                channelDao = MainActivity.masterData.getChannelDao();
                commentDao = MainActivity.masterData.getCommentDao();
            }
        }
        if (null != videoDao){
            allVideos =(ArrayList)videoDao.getVideos();
            Log.v("Channel-Update","loaded videos from database"+allVideos.size());
        }
        else {
            Log.e("Channel-Update","Nothing accessing feed database");
            return false;
        }
        if (null != channelDao){

            allChannels = (ArrayList<Channel>) channelDao.getChannels();
            Log.v("Channel-Update","Loaded channel database with "+allChannels.size());
        }
        else{
            Log.e("Channel-Update", "failed to load channel list from sql");
            return false;
        }

channelloop:for (Channel chan :allChannels){
            Long diff = new Date().getTime()- chan.getLastsync();
            int minutes = (int) ((diff / (1000*60)) % 60);
            int hours   = (int) ((diff / (1000*60*60)) % 24);
            int days = (int) ((diff / (1000*60*60*24)));
            //System.out.println(chan.getTitle()+"synched days:"+days+" hours:"+hours+" minutes:"+minutes);
            //TODO implement variable refresh rate by channel here
            if (minutes>(5*(chan.getErrors()+1)) || forceRefresh){
                allVideos =(ArrayList)videoDao.getVideos();
                Log.v("Channel-Update", "Checking "+chan.getAuthor()+" for new videos");
                chan.setLastsync(new Date());
                channelDao.update(chan);
                if (chan.isYoutube()){
                    try {
                        System.out.println(chan.toCompactString());
                        doc = Jsoup.connect(chan.getYoutubeRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        //crash out if unable to reach Youtube
                        if (e.getMessage().indexOf("Unable to resolve host")>0) {
                            chan.incrementErrors();
                            updateError = e.toString();
                            //break channelloop;
                        }
                    }
                    Elements videos = doc.getElementsByTag("entry");
        youtubeLoop:for (Element entry : videos) {
                        videoExists=false;
                        nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                      //  Log.v("Channel-Update","creating new youtube video :"+nv.toCompactString());
                        //TODO use sql query on source id to find duplicate instead of iterating.

                        for (Video match : allVideos) {
                            if (match.getSourceID().equals(nv.getSourceID())) {
                                if (match.isYoutube()) {
                                    dupecount++;
                                    videoExists = true;
                                    //TODO fix the root cause of blank authorIDs
                                    if (match.getAuthorID() == 0) {
                                        match.setAuthorID(chan.getID());
                                        Log.v("Channel-Update","Fixing missing channel ID in youtubevideo :"+match.toCompactString());
                                        if (headless) {
                                            videoDao.update(match);
                                        } else {
                                            MainActivity.masterData.updateVideo(match);
                                        }
                                        continue youtubeLoop;
                                    }
                                    break youtubeLoop;
                                }
                                if (match.isBitchute()) {
                                    nv=match;
                                    videoExists=true;
                                    mirror++;
                                    nv.setYoutubeID(nv.getSourceID());
                                }
                            }
                        }
                        Date pd = new Date(1);
                        try {
                            pd = ydf.parse(entry.getElementsByTag("published").first().text());
                        } catch (ParseException ex) {
                            Log.v("Exception parsing date", ex.getLocalizedMessage());
                            System.out.println(entry);
                        }
                        //TODO put in exception for archived channels here when implemented
                        if (pd.getTime()+(feedAge*24*60*60*1000)<new Date().getTime()) {
                            System.out.println("out of feed range for youtube channel " + chan.getTitle());
                            continue youtubeLoop;
                        }
                        Log.v("Channel-Update", "Starting youtube video parsing for : "+nv.toCompactString()+" for new video");
                        nv.setDate(pd);
                        nv.setAuthorID(chan.getID());
                        nv.setAuthor(chan.getAuthor());
                        nv.setTitle(entry.getElementsByTag("title").first().html());
                        nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                        nv.setDescription(entry.getElementsByTag("media:description").first().text());
                        nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                        nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                        if (nv.getID()>0) {
                            Log.v("Channel-Update", "updating video db "+nv.getID()+ " with : "+nv.toCompactString()+" for new video");
                            for (Video v : allVideos) {
                                if (v.getID() == (nv.getID())){
                                    v = nv;
                                    break;
                                }
                            }
                            if (headless) {
                                videoDao.update(nv);
                            } else {
                                MainActivity.masterData.updateVideo(nv);
                            }
                        } else {
                            Log.v("Channel-Update", "inserting youtube : "+nv.getSourceID()+ " with : "+nv.toCompactString()+" for new video");
                            allVideos.add(nv);
                            if (headless) {
                                videoDao.insert(nv);
                            } else {
                                MainActivity.masterData.addVideo(nv);
                            }
                            newcount++;
                            if (chan.isNotify()) {
                                createNotification(nv);
                            }
                        }

                    }
                }

                if (chan.isBitchute()) {
                    try {
                        doc = Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Channel-Update","network failure tying to get bitchute rss feeds in background "+e.getMessage());
                        //crash out if unable to reach bitchute
                        if (e.getMessage().indexOf("Unable to resolve host")>0) {
                            System.out.println("site appears to be down");
                            chan.incrementErrors();
                            updateError = e.toString();
                           // break channelloop;
                        }
                    }
                    if (null==doc){
                        Log.e("Channel-Update", "null document load for bitchuyte RSS feed");
                        return false;
                    }
                    Elements videos = doc.getElementsByTag("item");
       bitchuteLoop:for (Element video : videos) {

                        nv = new Video(video.getElementsByTag("link").first().text());
                        for (Video match : allVideos) {
                            if (match.getBitchuteID().equals(nv.getSourceID())) {
                              //  System.out.println("video duped "+nv.getSourceID()+"\n"+match.toDebugString());
                                dupecount++;
                                if (match.getAuthorID()==0){
                                    match.setAuthorID(chan.getID());
                                    Log.v("Channel-Update","Fixing missing channel ID in bitchute :"+match.toCompactString());
                                    if (headless){
                                        videoDao.update(match);
                                    }
                                    else{
                                        MainActivity.masterData.updateVideo(match);
                                    }
                                    continue bitchuteLoop;
                                }
                                continue bitchuteLoop;
                            }
                            if (match.getSourceID().equals(nv.getSourceID())&& !match.isBitchute()) {
                                //  System.out.println("video duped "+nv.getSourceID()+"\n"+match.toDebugString());
                                mirror++;
                                videoExists=true;
                                System.out.println("new bitchute video mirrored from existing youtube"+match.getAuthor()+ " "+nv.getSourceID());
                                match.setBitchuteID(nv.getSourceID());
                                if (headless){
                                    videoDao.update(match);
                                }
                                else{
                                    MainActivity.masterData.updateVideo(match);
                                }
                                continue bitchuteLoop;
                            }
                            continue bitchuteLoop;
                        }
                        Date pd=new Date(1);
                        try {
                           pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                        } catch (ParseException e) {
                           Log.e("Channel-Update", "date parsing error "+e.getLocalizedMessage());
                        }
                        if (pd.getTime()+(feedAge*24*60*60*1000)<new Date().getTime()) {
                           continue bitchuteLoop;
                        }
                        nv.setDate(pd);
                        nv.setAuthorID(chan.getID());
                        nv.setTitle(video.getElementsByTag("title").first().text());
                        nv.setDescription(video.getElementsByTag("description").first().text());
                        nv.setUrl(video.getElementsByTag("link").first().text());
                        nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url"));
                        nv.setAuthor(chan.getTitle());
                       /*
                        if (chan.isYoutube()){
                            nv.setYoutubeID(nv.getSourceID());
                        }   */
                        if (nv.getID()>0) {
                            Log.v("Channel-Update", "updating bitchute video : "+nv.toCompactString());
                            for (Video v : allVideos) {
                                if (v.getID() == (nv.getID())){
                                    v = nv;
                                    break;
                                }
                            }
                            if (headless) {
                                videoDao.update(nv);
                            } else {
                                MainActivity.masterData.updateVideo(nv);
                            }
                        } else {
                            Log.v("Channel-Update", "inserting new bitchute video : "+nv.toCompactString());
                            allVideos.add(nv);
                            if (headless) {
                                videoDao.insert(nv);
                            } else {
                                MainActivity.masterData.addVideo(nv);
                            }
                            newcount++;
                            if (chan.isNotify()) {
                               createNotification(nv);
                            }
                        }
                    }
                }

            }
            else{
                Log.e("Channel-Update", "not updating channel at this time"+chan.getAuthor());
            }
        }
        if (headless) {
            for (Video v : allVideos) {
                if (v.getMp4().isEmpty() && v.isBitchute()) {
                    new VideoScrape().execute(v);
                }
            }
        }
        else{
            for (Video v : allVideos) {
                if (v.getMp4().isEmpty() && v.isBitchute()) {
                    new VideoScrape().execute(v);
                }
            }
            MainActivity.masterData.refreshChannels();
        }
        Log.v("Channel-Update",dupecount+" duplicate videos discarded,"+mirror+" videos mirrored," +newcount+" new videos added");
        return true;
    }
    private void createNotification(Video vid){
        String path="";
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int switcher=0;
        Uri uri = Uri.parse(vid.getYoutubeUrl());
        if (vid.isBitchute()) {
            switcher = bitchutePlayerChoice;
            uri = Uri.parse(vid.getMp4());
        }
        if (vid.isYoutube()){
            switcher = youtubePlayerChoice;
        }
        switch(switcher){
            case 1:
                notificationIntent.setPackage("org.videolan.vlc");
                notificationIntent.setDataAndTypeAndNormalize(uri, "video/*");
                notificationIntent.putExtra("title", vid.getTitle());
                break;
            case 32:
                notificationIntent.setPackage("org.schabi.newpipe");
                notificationIntent.setDataAndTypeAndNormalize(uri, "video/*");
                break;
            case 256:
                notificationIntent.setPackage( "com.google.android.youtube" );
                notificationIntent.setData(uri);
                break;
            default:
                notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.putExtra("videoID",vid.getID());
                Bundle bundle = new Bundle();
                bundle.putLong("videoID", vid.getID());
                notificationIntent.putExtras(bundle);




        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notificationBuilder =
                new NotificationCompat.Builder(context, "anticlimacticteleservices.sic")
                        .setSmallIcon(R.mipmap.sic_round)
                        .setContentTitle(vid.getAuthor())
                        .setContentText(vid.getTitle())
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();

        NotificationManager notificationManager = context.getSystemService(
                NotificationManager.class);
        notificationManager.notify(((int) vid.getID()), notificationBuilder);
    }
}
