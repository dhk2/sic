package anticlimacticteleservices.sic;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class ChannelInit extends AsyncTask <String,String,Integer>{
    final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    protected Integer doInBackground(String[] params) {
        System.out.println("channel count"+MainActivity.masterData.getChannels().size());
//        System.out.println("Starting to init channel"+params[0]);
        Channel chan;


        Document channelPage;
        Document channelRss;
        channels:for (String g : params) {
            chan=new Channel(g);
            for (Channel c : MainActivity.masterData.getChannels())
                if (chan.getID().equals(c.getID())){
                    System.out.println("channel already exists");
                    continue channels;
            }
            try {
                //chan.setUrl(g);
                channelRss = Jsoup.connect(g).get();
                //bitchute rss feeds don't work with the channel UID but only with the text name, need to get text name before parsing rss
                if (g.indexOf("bitchute")>0) {
                    channelPage=channelRss;
                    chan = new Channel("https://www.bitchute.com" + channelPage.getElementsByClass("name").first().getElementsByTag("a").first().attr("href").toString());
                    channelRss= Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                }
                else {
                    chan = new Channel(g);
                    channelPage=Jsoup.connect(chan.getYoutubeUrl()).get();
                }

                chan.setTitle(channelRss.title());

 //               System.out.println("g is:"+g +"\n   id is "+chan.getID()+ "\n    url is "+chan.getUrl()+"\n   youtube rss:"+chan.getYoutubeRssFeedUrl()+"\n  bitchute rss feed"+chan.getBitchuteRssFeedUrl());
                if (chan.isYoutube()) {
                    chan.setTitle(channelRss.title());
                    chan.setAuthor(channelRss.getElementsByTag("name").first().text());
                    chan.setUrl(g);
                    chan.setDescription(channelPage.getElementsByAttributeValue("name", "description").attr("content").toString());
                    chan.setThumbnail(channelPage.getElementsByAttributeValue("itemprop", "thumbnailUrl").attr("href").toString());
                    Elements entries = channelRss.getElementsByTag("entry");
                    for (Element entry : entries) {
                        Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));

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
                        boolean unique = true;
                        for (Video match : MainActivity.masterData.getVideos()) {
                            if (match.getID().equals(nv.getID())) {
                                unique = false;
                            }
                        }
                        if (unique) {
                            MainActivity.masterData.addVideo(nv);
                        }
                        unique = true;
                        for (Video match : chan.getVideos()) {
                            if (match.getID().equals(nv.getID())) {
                                unique = false;
                            }
                        }
                        if (unique) {
                            System.out.println("g is:"+g +"   id is "+chan.getID()+ "    url is "+chan.getUrl());
                            chan.addVideo(nv);
                        }
                    }
                }

                if (!chan.getBitchuteRssFeedUrl().isEmpty()) {
                    try {

                        chan.setDescription(channelRss.getElementsByTag("description").first().text());
                       // System.out.println(doc);
                        chan.setThumbnail(channelPage.getElementsByAttribute("data-src").last().attr("data-src"));
                        Elements videos = channelRss.getElementsByTag("item");
                        System.out.println(videos.size());
                        for (Element video : videos) {
                            Video nv=new Video();
                            nv.setTitle(video.getElementsByTag("title").first().text());
                            nv.setDescription(video.getElementsByTag("description").first().text());
                            nv.setUrl(video.getElementsByTag("link").first().text());
                            System.out.println(nv);
                            nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url").toString());
                            try {
                                Date pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            // Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            //  nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            nv.setAuthor(channelRss.title());
                            System.out.println(nv);
                            boolean unique=true;
                            for (Video match : MainActivity.masterData.getVideos()) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                MainActivity.masterData.addVideo(nv);
                            }
                            unique = true;
                            for (Video match : chan.getVideos()) {
                                if (match.getID().equals(nv.getID())) {
                                    unique = false;
                                }
                            }
                            if (unique) {
                                chan.addVideo(nv);
                            }
                        }
                       // System.out.println("finished scraping videos");

                    } catch (NullPointerException e) {
                        System.out.println("null pointer issue" + e);
                        e.printStackTrace();
                    }
                }
              boolean unique = true;
                for (Channel match : MainActivity.masterData.getChannels()) {
                    if (match.getID().equals(chan.getID())) {
                        unique = false;
                    }
                }
                if (unique) {
                    MainActivity.masterData.addChannel(chan);
                    System.out.println("adding channel "+chan.getTitle());
                }
                else {
                    System.out.println("dupicate channel rejected "+chan.getTitle());
                }
               } catch (IOException e) {
                e.printStackTrace();
            }
     //       System.out.println("finished initting channel"+chan.getVideos().size());
    //        System.out.println(chan);
            MainActivity.masterData.sortVideos();

        }
        return 69;
    }
    //@Override
    protected void onPostExecute(String result){
        MainActivity.masterData.sortVideos();
        System.out.println("sorting"+MainActivity.masterData.getVideos().size());
}

}
