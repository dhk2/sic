package anticlimacticteleservices.sic;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

public class VideoScrape extends AsyncTask<Video,Video,Video> {
    static CommentDao commentDao;
    static VideoDao videoDao;
    Video vid;
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

        vid = videos[0];
      //  System.out.println(videos.length+" videos passed to scrape=========================");
       // System.out.println(vid);
        if (null == commentDao){
            commentDao=MainActivity.masterData.getCommentDao();
        }
        if (null == videoDao){
            videoDao=MainActivity.masterData.getVideoDao();
        }
        if (vid.isBitchute()){
            Document doc = null;
            int commentcounter=0;
            try {
                doc = Jsoup.connect(vid.getBitchuteUrl()).get();
                System.out.println(doc.getElementsByClass("video-statistics").toString()+"<<<<<<<<<<<<<<<<<<<<<");
                vid.setCategory(doc.getElementsByClass("video-detail-list").first().getElementsByTag("a").first().text());
                vid.setDescription(doc.getElementsByClass("full hidden").toString());
                vid.setMagnet(doc.getElementsByClass("video-actions").first().getElementsByAttribute("href").first().attr("href"));
                vid.setMp4(doc.getElementsByTag("source").attr("src"));
               // vid.setViewCount(doc.getElementsByAttribute("video-view-count").first().text());
      //          vid.setUpCount(doc.getElementsByAttribute("video-like-count").first().text());


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
               // System.out.println("added "+commentcounter+" from bitchute url");
                doc = Jsoup.connect("https://dissenter.com/discussion/begin?url="+vid.getYoutubeUrl()+"&cpp=69").get();
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
                        System.out.println(com);
                        commentcounter++;
                    }
                }
               // System.out.println("added "+commentcounter+" videos after youtube url");
                videoDao.update(vid);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("network failure in background video updater. aborting this run");
                return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        if (vid.isYoutube()){
            Document doc = null;
            try {
                doc = Jsoup.connect(vid.getYoutubeUrl()).get();
                //System.out.println(vid);
                Elements hack =doc.getElementsByTag("button");
               // System.out.println("size of hack "+hack.size());
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
                //System.out.println("https://dissenter.com/discussion/begin?url="+vid.getBitchuteUrl()+"&cpp=69");
                Elements posts = doc.getElementsByClass("comment-container");
                //System.out.println(MainActivity.masterData.getVideos().size()+")"+vid.getTitle()+"  "+posts.size()+"comments");
                //System.out.println(posts.first().toString());
                for (Element p : posts){
                    System.out.println(p);
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
               // System.out.println(vid);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("network failure in background video updater. aborting this run");
                return null;
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
       // System.out.println(vid.toDebugString());

        return null;
    }
}
