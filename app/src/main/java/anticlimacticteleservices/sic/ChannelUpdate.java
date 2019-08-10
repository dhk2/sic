package anticlimacticteleservices.sic;
import android.app.ActivityManager;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
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

class ChannelUpdate extends AsyncTask<String, String, Boolean> {
    private final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    private final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Document doc;
    private int dupecount=0;
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
    @Override
    protected void onPreExecute() {
        // load these settings into static variables in case Mainactivity closes and the background app is still running.
        super.onPreExecute();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> am i actually visiable <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(videoDao);
        if (null==context) {
            if (null == MainActivity.masterData) {
                context = SicSync.context;
            }
            else{
                context=MainActivity.masterData.context;
            }
        }
        System.out.println("context"+context);
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
        //TODO get this out of preferences or something.
        if (null==feedAge){
            feedAge=30l;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (newcount>0) {
         //   Toast.makeText(context, newcount + " new videos added", Toast.LENGTH_SHORT).show();
        }
        Util.scheduleJob(context);

    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (null != videoDao){
            allVideos =(ArrayList)videoDao.getVideos();
            System.out.println("loaded videos from database"+allVideos.size());
        }
        else {
            System.out.println("error trying to access databse from background asynctask");
        }
        if (null != channelDao){
            System.out.println("loading channels from sql");
            allChannels = (ArrayList<Channel>) channelDao.getChannels();
        }
        else{
            System.out.println("failed to load channel list from sql");
        }

channelloop:for (Channel chan :allChannels){
            Long diff = new Date().getTime()- chan.getLastsync();
            int minutes = (int) ((diff / (1000*60)) % 60);
            int hours   = (int) ((diff / (1000*60*60)) % 24);
            int days = (int) ((diff / (1000*60*60*24)));
            System.out.println(chan.getTitle()+"synched days:"+days+" hours:"+hours+" minutes:"+minutes);
            //TODO implement variable refresh rate by channel here
            if (minutes>5 ){
                chan.setLastsync(new Date());
                channelDao.update(chan);
                if (chan.isYoutube()){
                    try {
                        doc = Jsoup.connect(chan.getYoutubeRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Elements videos = doc.getElementsByTag("entry");
        youtubeLoop:for (Element entry : videos) {
                        Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                        //TODO use sql query on source id to find duplicate instead of iterating.
                        for (Video match : allVideos) {
                            if (match.getSourceID().equals(nv.getSourceID()) && match.isYoutube()) {
                                dupecount++;
                                System.out.println("video duped "+match.getAuthor()+ " "+nv.getSourceID());
                                continue channelloop;
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
                            System.out.println("out of feed range for " + chan.getTitle());
                            break;
                        }
                        nv.setDate(pd);
                        nv.setAuthorID(chan.getID());
                        nv.setAuthor(chan.getAuthor());
                        nv.setTitle(entry.getElementsByTag("title").first().html());
                        nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                        nv.setDescription(entry.getElementsByTag("media:description").first().text());
                        nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                        nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                        videoDao.insert(nv);
                       // MainActivity.masterData.setDirtydata(1);
                        System.out.println("adding video "+nv.getTitle()+ " published on:"+nv.getDate());
                    }
                }
                if (chan.isBitchute()) {
                    try {
                        doc = Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null==doc){
                        System.out.println("this is where network failure shows up");
                        return false;
                    }
                    Elements videos = doc.getElementsByTag("item");
       bitchuteLoop:for (Element video : videos) {
                        Video nv = new Video(video.getElementsByTag("link").first().text());
                        for (Video match : allVideos) {
                            if (match.getSourceID().equals(nv.getSourceID())&& match.isBitchute()) {
                                System.out.println("video duped "+nv.getSourceID()+"\n"+match.toDebugString());

                                dupecount++;
                                continue channelloop;
                            }
                        }
                        Date pd=new Date(1);
                        try {
                           pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                        } catch (ParseException ex) {
                           Log.v("Exception", ex.getLocalizedMessage());
                        }
                        if (pd.getTime()+(feedAge*24*60*60*1000)<new Date().getTime()) {
                           System.out.println("out of feed range for " + chan.getTitle());
                           break;
                        }
                        nv.setDate(pd);
                        nv.setAuthorID(chan.getID());
                        nv.setTitle(video.getElementsByTag("title").first().text());
                        nv.setDescription(video.getElementsByTag("description").first().text());
                        nv.setUrl(video.getElementsByTag("link").first().text());
                        nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url"));
                        nv.setAuthor(chan.getTitle());
                        videoDao.insert(nv);
                        newcount++;
                        System.out.println("adding video " + nv.getTitle() + " published on:" + nv.getDate());

                    }
                }

            }
        }
  //      database.close();
 //       channelDatabase.close();
        System.out.println(dupecount+ "duplicate videos discarded from RSS feeds, "+newcount+" new videos added");
        return true;
    }
}
