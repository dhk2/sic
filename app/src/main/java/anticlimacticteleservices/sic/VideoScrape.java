package anticlimacticteleservices.sic;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VideoScrape extends AsyncTask<Video,Video,Video> {
    static CommentDao commentDao;
    static VideoDao videoDao;
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

        Video vid = videos[0];
        System.out.println(videos.length+" videos passed to scrape");
        System.out.println(vid);
        if (null == commentDao){
            commentDao=MainActivity.masterData.getCommentDao();
        }
        if (null == videoDao){
            videoDao=MainActivity.masterData.getVideoDao();
        }
        if (vid.isBitchute()){
            Document doc = null;
            try {
                doc = Jsoup.connect(vid.getBitchuteUrl()).get();
                vid.setCategory(doc.getElementsByClass("video-detail-list").first().getElementsByTag("a").first().text());
                vid.setDescription(doc.getElementsByClass("full hidden").toString());
                vid.setMagnet(doc.getElementsByClass("video-actions").first().getElementsByAttribute("href").first().attr("href"));
                vid.setMp4(doc.getElementsByTag("source").attr("src"));
                String dissent = "https://dissenter.com/discussion/begin?url="+vid.getBitchuteUrl()+"/&cpp=69";
                doc = Jsoup.connect(dissent).get();
               // System.out.println(dissent);
                Elements posts = doc.getElementsByClass("comment-container");
                System.out.println(vid.getTitle()+"  "+posts.size()+"comments");
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
            } catch (IOException e) {
                e.printStackTrace();
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
               // System.out.println(vid);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        videoDao.update(vid);
        return null;
    }
}
