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
import java.util.Date;

public class ChannelInit extends AsyncTask <String,String,Integer>{
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Override
    protected Integer doInBackground(String[] params) {
        System.out.println("channel count"+MainActivity.masterData.getChannels().size());
        System.out.println("Starting to init channel"+params[0]);
        for (String g : params) {
            Channel chan = new Channel(g);
            try {

                Document doc = Jsoup.connect(g).get();
                chan.setTitle(doc.title());
                chan.setUrl(g);
                if (g.indexOf("youtube.com") > 0) {
                    chan.setTitle(doc.title());
                    chan.setAuthor(doc.getElementsByTag("name").first().text());
                    chan.setUrl(g);

                    Elements entries = doc.getElementsByTag("entry");
                    for (Element entry : entries) {
                        Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));

                        nv.setAuthor(chan.getAuthor());
                        nv.setTitle(entry.getElementsByTag("title").first().html());
                        nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                        try { Document doc2 = Jsoup.connect("https://www.youtube.com/channel/" + chan.getID()).get();
                            chan.setDescription(doc2.getElementsByAttributeValue("name", "description").attr("content").toString());
                            chan.setThumbnail(doc2.getElementsByAttributeValue("itemprop", "thumbnailUrl").attr("href").toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to load youtube channel page for " + chan.getTitle() + " at " + g +" id:" +chan.getID());
                        }
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
                            chan.addVideo(nv);
                        }
                    }
                }

                if (g.indexOf("bitchute.com") > 0) {
                    try {
                        Elements metaElements =doc.getElementsByAttribute("name");
                        System.out.println(metaElements.first().getElementsByClass("channel-videos-text").text());
                        chan.setDescription(doc.getElementsByClass("channel-videos-text").text());
                        chan.setThumbnail(doc.getElementsByClass("image lazyload").attr("data-src"));
                        Elements videoList = doc.getElementsByClass("channel-videos-list");
                        Elements entries = videoList.first().getElementsByClass("row");
                        for (Element entry : entries) {
                            //System.out.println("<<<<entry<<<<"+entry);
                            Video nv = new Video("https://www.bitchute.com" + entry.getElementsByTag("a").first().attr("href"));
                            nv.setDescription(entry.getElementsByClass("channel-videos-text").first().text());
                            nv.setThumbnail(entry.getElementsByTag("img").first().attr("data-src"));
                            nv.setTitle(entry.getElementsByClass("channel-videos-title").first().text());
                            nv.setViewCount(entry.getElementsByClass("video-views").first().text());

                            try {
                                Date pd = bdf.parse(entry.getElementsByClass("channel-videos-details").first().getElementsByTag("span").text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            // Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            //  nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            nv.setAuthor(doc.title());
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
                        System.out.println("finished scraping videos");

                    } catch (NullPointerException e) {
                        System.out.println("null pointer issue" + e);
                    }
                }
                if (chan.getDescription().isEmpty()) {
                        //need to load more channel info since it wasn't cached
                    if (chan.getUrl().indexOf("youtube") > 1) {
                        try {
                            Document doc2 = Jsoup.connect("https://www.youtube.com/channel/"+chan.getID()).get();
                            chan.setDescription(doc2.getElementsByAttributeValue("name","description").attr("content").toString());
                            chan.setThumbnail(doc2.getElementsByAttributeValue("itemprop","thumbnailUrl").attr("href").toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Failed to load youtube channel page for " + chan.getTitle()+" at "+"https://www.youtube.com/channel/"+chan.getID());
                        }
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
            System.out.println("finished initting channel"+chan.getVideos().size());
            System.out.println(chan);
        }
        return null;
    }


}
