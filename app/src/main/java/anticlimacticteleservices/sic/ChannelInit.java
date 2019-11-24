package anticlimacticteleservices.sic;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class ChannelInit extends AsyncTask <String,String,Integer>{
    private final SimpleDateFormat bdf = new SimpleDateFormat("EEE', 'dd MMM yyyy HH:mm:SS' 'ZZZZ");
    private final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Channel chan;
    private int newVideoCount=0;
    private int dupeCount=0;
    private int newChannelCount=0;
    @Override
    protected Integer doInBackground(String[] params) {
    Log.v("Channel-Init",MainActivity.masterData.getChannels().size()+" channels, init channel"+params[0]);
        Document channelPage;
        Document channelRss;

        channels:for (String g : params) {
           chan = new Channel(g);
            Log.v("Channel-Init","trying to add channel:"+g);
           for (Channel c : MainActivity.masterData.getChannels()){
               if (((chan.getYoutubeID() == c.getYoutubeID()) && chan.isYoutube() )|| ( chan.isBitchute() &&(chan.getBitchuteID() == c.getBitchuteID()))){
                   dupeCount++;
                   Log.v("Channel-Init","trying to add duplicate channel "+chan.getTitle());
                   continue channels;
               }
           }
           try {
                chan = new Channel(g);
                channelRss=null;
                channelPage=null;
                if (chan.isBitchute()){
                    channelPage = Jsoup.connect(chan.getBitchuteUrl()).get();
                //bitchute rss feeds don't work with the channel UID but only with the text name, need to get text name before parsing rss
                    chan = new Channel("https://www.bitchute.com" + channelPage.getElementsByClass("name").first().getElementsByTag("a").first().attr("href"));
                    channelRss= Jsoup.connect(chan.getBitchuteRssFeedUrl()).get();
                }
                if (chan.isYoutube()) {
                    channelRss = Jsoup.connect(chan.getYoutubeRssFeedUrl()).get();
                    channelPage= Jsoup.connect(chan.getYoutubeUrl()).get();
                }
               System.out.println(chan);
               System.out.println("bitchute rss feed:"+chan.getBitchuteRssFeedUrl());
                chan.setTitle(channelRss.title());
               Log.v("Channel-Init","creating channel with title:"+channelRss.title());
               //System.out.println("g is:"+g +"\n   id is "+chan.getSourceID()+ "\n    url is "+chan.getBitchuteUrl()+"\n   youtube rss: "+chan.getYoutubeRssFeedUrl()+"\n  bitchute rss feed "+chan.getBitchuteRssFeedUrl());
               if (chan.isYoutube()) {
                    chan.setTitle(channelRss.title());
                    chan.setAuthor(channelRss.getElementsByTag("name").first().text());
                    chan.setUrl(g);
                    chan.setDescription(channelPage.getElementsByAttributeValue("name", "description").attr("content"));
                    chan.setThumbnail(channelPage.getElementsByAttributeValue("itemprop", "thumbnailUrl").attr("href"));
                    Elements entries = channelRss.getElementsByTag("entry");
                    Date pd=new Date(1);
                    for (Element entry : entries) {
                        try {
                            pd = ydf.parse(entry.getElementsByTag("published").first().text());
                        } catch (ParseException ex) {
                            Log.e("Exception parsing date", ex.getLocalizedMessage());
                            System.out.println(entry);
                        }
                        if (pd.getTime()+(MainActivity.masterData.getFeedAge()*24*60*60*1000)<new Date().getTime()){
                            System.out.println("out of feed range for "+chan.getTitle()+Long.toString(MainActivity.masterData.getFeedAge()));
                            break;
                        }
                        Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                        nv.setDate(pd);
                        nv.setAuthor(chan.getAuthor());
                        nv.setAuthorID(chan.getID());
                        nv.setTitle(entry.getElementsByTag("title").first().html());
                        nv.setThumbnail(entry.getElementsByTag("media:thumbnail").first().attr("url"));
                        nv.setDescription(entry.getElementsByTag("media:description").first().text());
                        nv.setRating(entry.getElementsByTag("media:starRating").first().attr("average"));
                        nv.setViewCount(entry.getElementsByTag("media:statistics").first().attr("views"));
                        boolean unique = true;
                        for (Video match : MainActivity.masterData.getVideos()) {
                            if (match.getSourceID().equals(nv.getSourceID())) {
                                unique = false;
                                if (match.getYoutubeID()==""){
                                    match.setYoutubeID(nv.getSourceID());
                                }
                                if (match.getAuthorID()>0){
                                    for (Channel c : MainActivity.masterData.getChannels()){
                                        if (match.getAuthorID() == c.getID()){
                                            c.setYoutubeID(chan.getYoutubeID());
                                        }
                                    }
                                }

                                break;
                            }
                        }
                        if (unique) {
                            MainActivity.masterData.addVideo(nv);
                            newVideoCount++;
                        }

                        //  video is already in database
                    }
                }






                if (chan.isBitchute()) {
                    try {
                        chan.setDescription(channelRss.getElementsByTag("description").first().text());
                        chan.setThumbnail(channelPage.getElementsByAttribute("data-src").last().attr("data-src"));
                        Elements videos = channelRss.getElementsByTag("item");
                        System.out.println(videos.size());
                        for (Element video : videos) {
                            Video nv=new Video(video.getElementsByTag("link").first().text());
                            nv.setTitle(video.getElementsByTag("title").first().text());
                            nv.setDescription(video.getElementsByTag("description").first().text());
                           // System.out.println(nv);
                            nv.setThumbnail(video.getElementsByTag("enclosure").first().attr("url"));
                            Date pd=new Date(1);
                            try {
                                pd = bdf.parse(video.getElementsByTag("pubDate").first().text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            //TODO put in exception for archived channels here when implemented
                            if (pd.getTime()+(MainActivity.masterData.getFeedAge()*24*60*60*1000)<new Date().getTime()) {
                                System.out.println("out of feed range for " + chan.getTitle());
                                break;
                            }
                            nv.setAuthor(channelRss.title());
                            nv.setAuthorID(chan.getID());
                            //System.out.println(nv);
                            boolean unique=true;
                            for (Video match : MainActivity.masterData.getVideos()) {
                                if (match.getSourceID().equals(nv.getSourceID())) {
                                    unique = false;
                                    if (match.getBitchuteID()==""){
                                        match.setBitchuteID(nv.getSourceID());
                                    }
                                    if (match.getAuthorID()>0){
                                        for (Channel c : MainActivity.masterData.getChannels()){
                                            if (match.getAuthorID() == c.getID()){
                                                c.setBitchuteID(chan.getBitchuteID());
                                            }
                                        }
                                    }
                                    break;
                                }

                            }
                            if (unique) {
                                MainActivity.masterData.addVideo(nv);
                                newVideoCount++;
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println("null pointer issue" + e);
                        e.printStackTrace();
                    }
                }
                 MainActivity.masterData.addChannel(chan);
                    Log.v("Channel-Init",MainActivity.masterData.getChannels().size()+" added channel "+chan.getTitle());
                    newChannelCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainActivity.masterData.sortVideos();
        }
        return 69;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (newChannelCount ==1){
            Toast.makeText(MainActivity.masterData.context,"added "+chan.getTitle()+ " with "+newVideoCount+" videos",Toast.LENGTH_SHORT).show();
        }
        if (newChannelCount>1){
            Toast.makeText(MainActivity.masterData.context,"added "+newChannelCount+ " channels with "+newVideoCount+" videos",Toast.LENGTH_SHORT).show();
        }
    }
}
