package anticlimacticteleservices.sic;
import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class ChannelUpdate extends AsyncTask<String, String, Boolean> {
    final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    Document doc;
    int dupecount=0;
    int newcount=0;

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (newcount>0)
            Toast.makeText(MainActivity.masterData.context,newcount+" new videos added",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected Boolean doInBackground(String... params) {

        int channelCount = MainActivity.masterData.getChannels().size();
        for (Channel chan : MainActivity.masterData.getChannels()){
            Long diff = new Date().getTime()- chan.getLastsync().getTime();
            int minutes = (int) ((diff / (1000*60)) % 60);
            int hours   = (int) ((diff / (1000*60*60)) % 24);
            int days = (int) ((diff / (1000*60*60*24)));
            System.out.println(chan.getTitle()+"synched days:"+days+" hours:"+hours+" minutes:"+minutes);
            if (hours>0){
                chan.setLastsync(new Date());
                if (chan.isYoutube()){
                    try {
                        doc = Jsoup.connect(chan.getYoutubeRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Elements videos = doc.getElementsByTag("entry");
        youtubeLoop:for (Element entry : videos) {
                        Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                        for (Video match : MainActivity.masterData.getVideos()) {
                            if (match.getID().equals(nv.getID())) {
                                dupecount++;
                                continue youtubeLoop;
                            }
                        }
                        nv.setAuthor(chan.getAuthor());
                        nv.setTitle(entry.getElementsByTag("title").first().html());
                        nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                        nv.setDescription(entry.getElementsByTag("media:description").first().text());
                        nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                        nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                        try {
                            Date pd = ydf.parse(entry.getElementsByTag("published").first().text());
                            nv.setDate(pd);
                        } catch (ParseException ex) {
                            Log.v("Exception parsing date", ex.getLocalizedMessage());
                            System.out.println(entry);
                        }
                        MainActivity.masterData.addVideo(nv);
                        chan.addVideo(nv);
                        System.out.println("adding video "+nv.getTitle()+ " published on:"+nv.getDate());
                    }
                }
                if (chan.isBitchute()) {
                    try {
                        doc = Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Elements videos = doc.getElementsByTag("item");
       bitchuteLoop:for (Element video : videos) {
                        Video nv = new Video(video.getElementsByTag("link").first().text());
                        for (Video match : MainActivity.masterData.getVideos()) {
                            if (match.getID().equals(nv.getID())) {
                                dupecount++;
                                continue bitchuteLoop;
                            }
                        }
                        nv.setTitle(video.getElementsByTag("title").first().text());
                        nv.setDescription(video.getElementsByTag("description").first().text());
                        nv.setUrl(video.getElementsByTag("link").first().text());
                        nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url").toString());
                        try {
                            Date pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                            nv.setDate(pd);
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }
                        nv.setAuthor(chan.getTitle());
                        MainActivity.masterData.addVideo(nv);
                        chan.addVideo(nv);
                        newcount++;
                        System.out.println("adding video " + nv.getTitle() + " published on:" + nv.getDate());
                    }
                }

            }
        }
        System.out.println(dupecount+ "duplicate videos discarded from RSS feeds, "+newcount+" new videos added");
        if (newcount>1){
            MainActivity.masterData.sortVideos();
        }
        return true;
    }

}
