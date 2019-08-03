package anticlimacticteleservices.sic;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VideoScrape extends AsyncTask<Video,Video,Video> {
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

        if (vid.isBitchute()){

            Document doc = null;
            try {
                doc = Jsoup.connect(vid.getBitchuteUrl()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
 //           System.out.println("starting to scrape bitchute vide"+vid.getTitle());
 //           System.out.println(vid.toString());
            // System.out.println(doc);

            vid.setCategory(doc.getElementsByClass("video-detail-list").first().getElementsByTag("a").first().text());
            vid.setDescription(doc.getElementsByClass("full hidden").toString());
/* appears likes and viewer count are loaded by script and aren't being picked up by jsoup
            System.out.println(doc.getElementById("video-like-count").toString());
            System.out.println(doc.getElementById("video-like-count").toString());
            System.out.println(doc.getElementsByClass("video-view-count").toString());
*/          vid.setMagnet(doc.getElementsByClass("video-actions").first().getElementsByAttribute("href").first().attr("href"));
            vid.setMp4(doc.getElementsByTag("source").attr("src"));
 //           System.out.println(vid.toString());

            //    System.out.println(p);
            String sophlink ="https://dissenter.com/discussion/begin?url=https://www.bitchute.com/video/FNqiV8kL4cc/&cpp=69";
            try {
                doc = Jsoup.connect("https://dissenter.com/discussion/begin?url="+vid.getBitchuteUrl()).get();
             //   System.out.println(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements posts = doc.getElementsByClass("comment-container");
            System.out.println("size of hack "+posts.size());
            //System.out.println(posts.first().toString());
            for (Element p : posts){
                Comment com = new Comment(p.attr("data-comment-id"));
                com.setText(p.getElementsByClass("comment-body").text());
                com.setThumbnail(p.getElementsByClass("profile-picture mr-3").attr("src"));
                com.setAuthor(p.getElementsByClass("profile-name").text());
                System.out.println(p.getElementsByTag("small").text());
                System.out.println((com));
            }


        }
        if (vid.isYoutube()){
            Document doc = null;
            try {
                doc = Jsoup.connect(vid.getYoutubeUrl()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(vid);
            //System.out.println(doc);
            //System.out.println(doc.getElementsByClass("view-count").text());
            Elements hack =doc.getElementsByTag("button");
            System.out.println("size of hack "+hack.size());
         //   System.out.println(doc.getElementsByTag("button").first().getElementsByClass("like-button-renderer-like-button").text());
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

            System.out.println(vid);





        }

        return null;
    }
}
