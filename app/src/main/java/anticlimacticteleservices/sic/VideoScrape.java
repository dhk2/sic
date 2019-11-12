package anticlimacticteleservices.sic;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
//TODO move dissenter check outside of site specific sections
//TODO transverse comment subthreads
//TODO pull more useful data

public class VideoScrape extends AsyncTask<Video,Video,Video> {
    static CommentDao commentDao;
    static VideoDao videoDao;
    Video vid;
    SicDatabase sicDatabase;
    CommentDatabase commentDatabase;
    Context context;
    Boolean headless=true;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(Video video) {
        super.onPostExecute(video);
    }
    @Override
    protected Video doInBackground(Video... videos) {
        if (null==context) {
            if (null == MainActivity.masterData) {
                context = SicSync.context;
            }
            else{
                context=MainActivity.masterData.context;
                headless=false;
            }
        }
        vid = videos[0];
        if (null == videoDao){
            if (null == MainActivity.masterData){
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
                commentDao = MainActivity.masterData.getCommentDao();
            }
        }
        Document doctest = null;
        if (vid.isBitchute() && !vid.isYoutube()){

            try {
                doctest = Jsoup.connect(vid.getYoutubeUrl()).get();
                vid.setBitchuteID(vid.getSourceID());

                vid.setYoutubeID(vid.getSourceID());
                System.out.println("<<>> need to find out if youtube version exists");
                System.out.println((vid.getBitchuteUrl()));
                System.out.println(vid.getBitchuteEmbeddedUrl());
                System.out.println(vid.getEmbeddedUrl());
                System.out.println(vid.getYoutubeEmbeddedUrl());
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape","unable to load youtubve version of bitchute video");
            }

        }
        if (vid.isYoutube() && !vid.isBitchute()) {
            try {
                doctest = Jsoup.connect(vid.getBitchuteEmbeddedUrl()).get();
                vid.setBitchuteID(vid.getSourceID());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape", "unable to load bitchute version of youtube video");
            }
        }
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
                String dissent = "https://dissenter.com/discussion/begin?url="+vid.getBitchuteUrl()+"/&cpp=69";
                doc = Jsoup.connect(dissent).get();
                Elements posts = doc.getElementsByClass("comment-container");
                for (Element p : posts){
                    Comment com = new Comment(p.attr("data-comment-id"));
                    com.setText(p.getElementsByClass("comment-body").text());
                    com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                    com.setAuthor(p.getElementsByClass("profile-name").text());
                    com.setFeedID(vid.getID());
                    Comment test = commentDao.dupeCheck(vid.getID(),com.getText(),com.getAuthor());
                    if (null == test){
                        commentDao.insert(com);
                        commentcounter++;
                    }
                }
                Log.v("Videoscrape",vid.getTitle()+" added "+commentcounter+" comments from bitchute url");
  /*              doc = Jsoup.connect("https://dissenter.com/discussion/begin?url="+vid.getYoutubeUrl()+"&cpp=69").get();
                posts = doc.getElementsByClass("comment-container");
                for (Element p : posts){
                    Comment com = new Comment(p.attr("data-comment-id"));
                    com.setText(p.getElementsByClass("comment-body").text());
                    com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                    com.setAuthor(p.getElementsByClass("profile-name").text());
                    com.setUpVote(p.getElementsByClass("stat-upvotes").text());
                    com.setDownVote(p.getElementsByClass("stat-downvotes").text());
                    com.setFeedID(vid.getID());
                    Comment test = commentDao.dupeCheck(vid.getID(),com.getText(),com.getAuthor());
                    if (null == test){
                        commentDao.insert(com);
                        commentcounter++;
                    }
                }
               Log.v("Videoscrape",vid.getTitle()+" added "+commentcounter+" Comments after youtube url");
               */
                videoDao.update(vid);
                if (!headless){
                    MainActivity.masterData.updateVideo(vid);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape","network failure in background video updater. aborting this run");
                return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        if (vid.isYoutube()){
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
                doc = Jsoup.connect("https://dissenter.com/discussion/begin?url="+vid.getYoutubeUrl()+"&cpp=69").get();
                Elements posts = doc.getElementsByClass("comment-container");
                for (Element p : posts){
                    Comment com = new Comment(p.attr("data-comment-id"));
                    com.setText(p.getElementsByClass("comment-body").text());
                    com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                    com.setAuthor(p.getElementsByClass("profile-name").text());
                    com.setFeedID(vid.getID());
                    Comment test = commentDao.dupeCheck(vid.getID(),com.getText(),com.getAuthor());
                    if (null == test){
                        commentDao.insert(com);
                    }
                }
                videoDao.update(vid);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Videoscrape","network failure in background video updater. aborting this run");
                return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
