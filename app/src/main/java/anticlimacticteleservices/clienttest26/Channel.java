package anticlimacticteleservices.clienttest26;


import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import android.os.Bundle;
import android.app.*;
import android.os.*;
import android.util.Log;

public class Channel {
    private String title;
    private String author;
    private String url;
    private String thumbnailurl;
    private String description;
    private String profileImage;
    private String sourceID;

    private ArrayList<Video> videos;
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    final SimpleDateFormat bdf2 = new SimpleDateFormat( "                   hh:mm zzz    MMMM dd    yyyy");
    public Channel(){
        this.title="";
        this.author="";
        this.url="";
        this.thumbnailurl="";
        this.description="";
        this.profileImage="";
        this.sourceID="";
        this.videos=new ArrayList<Video>();
    }
    public Channel(String url) {
        final String location = url;
        this.url = url;
        this.description="";
        this.thumbnailurl="";
        this.profileImage="";
        int counter = 0;

        videos=new ArrayList<Video>();
        System.out.println("starting scrape of channel:"+url);
        if (url.indexOf("youtube.com")>0) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(location).get();
                        title = doc.title();
                        author = doc.getElementsByTag("Author").first().getElementsByTag("name").text();
                        Elements entries = doc.getElementsByTag("entry");
                        for (Element entry : entries) {
                          //  System.out.println("((("+entry+")))");
                           // System.out.println("(("+entry.getElementsByTag("published").first().text());


                            Video nv = new Video(entry.getElementsByTag("link").first().attr("href"));
                            nv.setTitle(entry.getElementsByTag("title").first().html());
                            Elements media = entry.getElementsByTag("media:group");
                            nv.setThumbnail(media.first().getElementsByTag("media:thumbnail").first().attr("url"));
                            //nv.setThumbnail(entry.getElementsByTag("media:group").first().getElementsByTag("media:thumbnail").first().attr("url"));
                            nv.setDescription(media.first().getElementsByTag("media:description").first().text());
                            nv.setAuthor(title);
                            try {
                                Date pd = ydf.parse(entry.getElementsByTag("published").first().text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }





                            videos.add(nv);
                            //                       System.out.println(nv);

                        }
                        System.out.println("finished scraping " + videos.size() + " videos");
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                }
            });
        }
        if (url.indexOf("bitchute.com")>0) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(location).get();
                        String tempAuthor = doc.title();
                        profileImage = (doc.getElementById("fileupload-medium-icon-2").attr("data-src"));
                        description = doc.getElementById("channel-description").text();
                        title=doc.title();
                        System.out.println(doc.getElementsByClass("channel-banner").attr("data-src"));

                        Elements videoList = doc.getElementsByClass("channel-videos-list");
                        Elements entries = videoList.first().getElementsByClass("row");
                        for (Element entry : entries) {
                          //System.out.println("<<<"+entry+">>>");
                           //System.out.println(entry.getElementsByTag("a").first().attr("href"));
                          // System.out.println(entry.getElementsByClass("channel-videos-title").first());
                            Video nv = new Video("https://www.bitchute.com"+entry.getElementsByTag("a").first().attr("href"));
                            nv.setDescription(entry.getElementsByClass("channel-videos-text").first().text());
                            nv.setThumbnail(entry.getElementsByTag("img").first().attr("data-src"));
                            nv.setTitle(entry.getElementsByClass("channel-videos-title").first().text());
                            nv.setAuthor(entry.getElementsByClass("channel-banner").text());
                            //System.out.println(entry.getElementsByClass("channel-videos-details").first().getElementsByTag("span").text());
                             try {
                                Date pd = bdf.parse(entry.getElementsByClass("channel-videos-details").first().getElementsByTag("span").text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            //System.out.println(nv);
                           // System.out.println("trying to get mp4 value form "+nv.getUrl());
                            Document hackDoc = Jsoup.connect(nv.getUrl()).get();
                            nv.setMp4(hackDoc.getElementsByTag("Source").first().attr("src"));
                            try {
                                Date pd = bdf2.parse(entry.getElementsByClass("video-publish-date").text());
                                nv.setDate(pd);
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage());
                            }
                            videos.add(nv);
                        }
                        System.out.println("finished scraping " + videos.size() + " videos");
                    } catch (MalformedURLException e) {
                        System.out.println("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("I/O Error: " + e.getMessage());
                    }
                //System.out.println(this);
                }
            });
        }
    }
    public String toString(){
        return("title:"+this.title+"\n"+
                "url:"+this.url+"\n"+
                "thumbnail:"+this.thumbnailurl+"\n"+
                "author:"+this.author+"\n"+
                "profile image"+this.profileImage+"\n"+
                "description:"+this.description+"\n");
    }
    public String getUrl(){
        return this.url;
    }
    public String getDescription(){
        return this.description;
    }
    public String getTitle(){
        return this.title;
    }
    public String getAuthor(){
        return this.author;
    }
    public String getThumbnail(){
        return this.thumbnailurl;
    }
    public ArrayList<Video> getVideos(){
        return this.videos;
    }
    public void setUrl(String value){
        this.url=value;
    }
    public void setTitle(String value){
        this.title=value;
    }
    public void setAuthor(String value){
        this.author=value;
    }
    public void setThumbnail(String value){
        this.thumbnailurl=value;
    }
    public void setDescription(String value) {
        this.description = value;
    }
    public void addVideo(Video vid){
        videos.add(vid);
    }



}
