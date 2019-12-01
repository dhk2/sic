package anticlimacticteleservices.sic;

import android.app.DownloadManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
//TODO move dissenter check outside of site specific sections
//TODO transverse comment subthreads
//TODO pull more useful data

public class VideoScrape extends AsyncTask<Video,Video,Video> {
    static CommentDao commentDao;
    static VideoDao videoDao;
    static ChannelDao channelDao;
    Video vid;
    SicDatabase sicDatabase;
    CommentDatabase commentDatabase;
    ChannelDatabase channelDatabase;
    Context context;
    Boolean headless=true;
    private static Long feedAge;
    private boolean useComments;
    public static SharedPreferences preferences;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("Video-Scrape","Pre-execute");
    }
    @Override
    protected void onPostExecute(Video video) {
        super.onPostExecute(video);
        Log.v("Video-Scrape","Post-execute");
    }
    @Override
    protected Video doInBackground(Video... videos) {

        System.out.println("vid-scrape elapsed minutes"+(new Date().getTime()-videos[0].getLastScrape())/60000);
        if (((new Date().getTime()-videos[0].getLastScrape())/60000)<5) {
            return null;
        }
        else {
            System.out.println("made it past the scrape time check");
        }
        if (null==context) {
            if (null == MainActivity.masterData) {
                context = SicSync.context;
            }
            else{
                context=MainActivity.masterData.context;
                headless=false;
            }
        }
        //TODO handle an array of videos
        Log.d("Videoscrape","headless:"+headless);
        vid = videos[0];
        if (headless){
            preferences = context.getSharedPreferences( "anticlimacticteleservices.sic" + "_preferences", MODE_PRIVATE);
            feedAge = preferences.getLong("feedAge",7);
            useComments = preferences.getBoolean("useComments",false);
        }
        else
        {
            feedAge = MainActivity.masterData.feedAge;
            useComments = MainActivity.masterData.useComments;
        }
        Log.d("Videoscrape","preferences loaded:"+feedAge);
        if (null == videoDao){
            if (null == MainActivity.masterData){
                sicDatabase = Room.databaseBuilder(context, SicDatabase.class, "mydb")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                videoDao = sicDatabase.videoDao();
                channelDatabase = Room.databaseBuilder(context , ChannelDatabase.class, "channel")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                channelDao = channelDatabase.ChannelDao();
                commentDatabase = Room.databaseBuilder(context , CommentDatabase.class, "comment")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                commentDao = commentDatabase.CommentDao();
            }
            else {
                videoDao = MainActivity.masterData.getVideoDao();
                commentDao = MainActivity.masterData.getCommentDao();
                channelDao = MainActivity.masterData.getChannelDao();
            }
            Log.d("Videoscrape","database connections made");
        }
        Document doctest = null;
        Long pd = vid.getDate();

        vid.setLastScrape(new Date().getTime());
        videoDao.update(vid);
        Log.e("Videoscrape","initialized data for scrape:"+vid.toCompactString());
        if ((pd+(feedAge*24*60*60*1000)<new Date().getTime()) && !vid.getKeep()) {
            Log.e("Videoscrape","Removing expired video from feed \n"+vid.toCompactString());
            if (!(null == vid.getLocalPath())){
                File file = new File(vid.getLocalPath());
                file.delete();
            }
            if (headless) {
                videoDao.delete((vid));
            }
            else{
                MainActivity.masterData.removeVideo(vid);
            }
            return null;
        }
        //probably not needed after fixing the id assignment issue, but shouldn't hurt.
        if (vid.getAuthorID()==0){
            for (Channel Cdog : MainActivity.masterData.getChannels()){
                if (vid.getAuthor().equals(Cdog.getAuthor())){
                    vid.setAuthorID(Cdog.getID());
                    Log.e("Videoscrape","Setting author id to "+vid.getAuthorID()+" because it matches channel "+vid.getAuthor());

                    if (headless){
                        videoDao.update(vid);
                    }
                    else{
                        MainActivity.masterData.updateVideo(vid);
                    }
                    break;
                }
            }
        }
        //TODO add a way to manage retrying after error condition instead of just giving up
        if (vid.isBitchute() && !vid.isYoutube() && (headless || vid.getErrors()<1)){
            try {
                Log.e("Videoscrape","attempting to load youtube version of bitchute video "+vid.toCompactString());
                doctest = Jsoup.connect(vid.getYoutubeEmbeddedUrl()).get();

                if (doctest.title().equals("YouTube")){
                    //means page does not exist
                }
                else {
                    vid.setYoutubeID(vid.getSourceID());
                    if (headless){
                        videoDao.update(vid);
                    }
                    else{
                        MainActivity.masterData.updateVideo(vid);
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape","unable to load youtube version of bitchute video "+vid.toCompactString());
                vid.incrementErrors();
            }

        }
        if (vid.isYoutube() && !vid.isBitchute() && vid.getAuthorID()>0 && (headless || vid.getErrors()<1)) {
            String testID="";
            try {
                Log.e("Videoscrape","attempting to load bitchute version of youtube video "+vid.toCompactString());
                doctest = Jsoup.connect(vid.getBitchuteTestUrl()).get();
                //System.out.println(doctest);
                 vid.setBitchuteID(vid.getSourceID());
                 if (headless){
                     videoDao.update(vid);
                 }
                 else{
                     MainActivity.masterData.updateVideo(vid);
                 }
                 Channel parent = channelDao.getChannelById(vid.getAuthorID());
                 if (parent.getBitchuteID().isEmpty()){
                     System.out.println("need to set bitchute id for channel imported from youtube");
                     testID = doctest.getElementsByClass("image-container").first().getElementsByTag("a").first().attr("href");
                     System.out.println(testID.length()+">"+testID);
                     testID=testID.substring(0,testID.length()-1);
                     System.out.println(testID.length()+">"+testID);
                    testID = testID.substring(testID.lastIndexOf("/")+1);
                     System.out.println(testID.length()+">"+testID);
                    parent.setBitchuteID(testID);
                    channelDao.update(parent);
                     Log.e("Videoscrape","setting bitchute ID "+parent.getBitchuteID()+" on "+parent.getAuthor());
                    if (headless){
                        channelDao.update(parent);
                    }
                    else {
                        MainActivity.masterData.updateChannel(parent);
                    }
                 }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape", "unable to load bitchute version of youtube video");
                vid.incrementErrors();
            }
        }
        Log.e("Videoscrape", "Starting bitchute processing");
        if (vid.isBitchute()){
            Document doc = null;
            int commentcounter=0;
            try {
                doc = Jsoup.connect(vid.getBitchuteUrl()).get();

           //     System.out.println(doc);
/*                Elements hunks = doc.getAllElements();
                for (Element h : hunks){
                    System.out.println(h.get+"]<-=->["+h.text());
                }
*/

                vid.setCategory(doc.getElementsByClass("video-detail-list").first().getElementsByTag("a").first().text());
                vid.setDescription(doc.getElementsByClass("full hidden").toString());
                vid.setMagnet(doc.getElementsByClass("video-actions").first().getElementsByAttribute("href").first().attr("href"));
                vid.setMp4(doc.getElementsByTag("source").attr("src"));
                if (useComments) {
                    String dissent = "https://dissenter.com/discussion/begin?url=" + vid.getBitchuteUrl() + "/&cpp=69";
                    doc = Jsoup.connect(dissent).get();
                    Elements posts = doc.getElementsByClass("comment-container");
                    for (Element p : posts) {
                        Comment com = new Comment(p.attr("data-comment-id"));
                        com.setText(p.getElementsByClass("comment-body").text());
                        com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                        com.setAuthor(p.getElementsByClass("profile-name").text());
                        com.setFeedID(vid.getID());
                        Comment test = commentDao.dupeCheck(vid.getID(), com.getText(), com.getAuthor());
                        if (null == test) {
                            commentDao.insert(com);
                            commentcounter++;
                        }
                    }
                    Log.v("Videoscrape", vid.getTitle() + " added " + commentcounter + " comments from bitchute url");
                }
                if (vid.getAuthorID()>0) {
                    boolean isDownloading = false;
                    Channel parent = channelDao.getChannelById(vid.getAuthorID());

                    if (null != parent) {
                        if ((parent.isArchive()) && !vid.getMp4().isEmpty() && (null == vid.getLocalPath())) {
                            Log.v("Videoscrape","downloading "+vid.getMp4()+" to "+vid.getLocalPath());
                            Uri target = Uri.parse(vid.getMp4());
                            File fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            vid.setLocalPath(fpath.getAbsolutePath() + "/" + vid.getSourceID() + ".mp4");
                            DownloadManager downloadManager = (DownloadManager) MainActivity.masterData.context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(target);
                            request.allowScanningByMediaScanner();
                            //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            //request.setAllowedOverRoaming(false);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setTitle(vid.getAuthor());
                            request.setDescription(vid.getTitle());
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, vid.getSourceID() + ".mp4");
                            request.setVisibleInDownloadsUi(true);
                            MainActivity.masterData.downloadVideoID = vid.getID();
                            MainActivity.masterData.downloadSourceID = vid.getSourceID();
                            MainActivity.masterData.downloadID = downloadManager.enqueue(request);
                        }

                    }
                }
                if (headless) {
                    videoDao.update(vid);
                } else {
                    MainActivity.masterData.updateVideo(vid);
                }
            } catch (IOException e) {
                vid.incrementErrors();
                e.printStackTrace();
                Log.e("Videoscrape","network failure in bitchute scrape for "+vid.toCompactString());
                vid.setBitchuteID("");
                if (headless) {
                    videoDao.update(vid);
                } else {
                    MainActivity.masterData.updateVideo(vid);
                }
                //return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        Log.e("Videoscrape", "starting youtube processing");
        if (vid.isYoutube()){
            int commentcounter=0;
            Document doc = null;
            try {
                doc = Jsoup.connect(vid.getYoutubeUrl()).get();
                Elements hack =doc.getElementsByTag("button");
                String liked="";
                String unliked="";
                for (Element h : hack){
                    String foo = h.getElementsByClass("like-button-renderer-like-button").text();
                    String bar = h.getElementsByClass ("like-button-renderer-dislike-button").text();
                    if (!foo.isEmpty()) {
                        vid.setUpCount(foo);
                    }
                    if (!bar.isEmpty()){
                        vid.setDownCount(bar);
                    }
                }
                if (useComments) {
                    doc = Jsoup.connect("https://dissenter.com/discussion/begin?url=" + vid.getYoutubeUrl() + "&cpp=69").get();
                    Elements posts = doc.getElementsByClass("comment-container");
                    for (Element p : posts) {
                        Comment com = new Comment(p.attr("data-comment-id"));
                        com.setText(p.getElementsByClass("comment-body").text());
                        com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                        com.setAuthor(p.getElementsByClass("profile-name").text());
                        com.setFeedID(vid.getID());
                        Comment test = commentDao.dupeCheck(vid.getID(), com.getText(), com.getAuthor());
                        if (null == test) {
                            commentDao.insert(com);
                        }
                    }
                    Log.v("Videoscrape", vid.getTitle() + " added " + commentcounter + " comments from youtube url");
                }
                if (headless) {
                    videoDao.update(vid);
                } else {
                    MainActivity.masterData.updateVideo(vid);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape","network failure in youtube background video updater. aborting this run "+vid.getYoutubeEmbeddedUrl()+" "+vid.getTitle());
                vid.incrementErrors();
                if (vid.getErrors()>5){
                    vid.setYoutubeID("");
                    if (headless) {
                        videoDao.update(vid);
                    } else {
                        MainActivity.masterData.updateVideo(vid);
                    }
                }
                return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return null;
    }
}
