package anticlimacticteleservices.sic;


import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

public class Channel implements Serializable{
    private String title;
    private String author;
    private String url;
    private String thumbnailurl;
    private String description;
    private String profileImage;
    private String subscribers;
    private String ID;
    private Date joined;   
    private Date lastsync;
    private ArrayList<Video> videos;
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    final SimpleDateFormat bdf2 = new SimpleDateFormat( "                   hh:mm zzz    MMMM dd    yyyy");
    
 //         Constructors   
    public Channel(){
        this.title="";
        this.author="";
        this.url="";
        this.thumbnailurl="";
        this.description="";
        this.profileImage="";
        this.ID="";
        this.videos=new ArrayList<Video>();
        this.lastsync=new Date();
    }
    public Channel(String url) {
        final String location = url;
        this.url = url;
        this.description="";
        this.thumbnailurl="";
        this.profileImage="";
        this.author="";
        int counter = 0;
        if (url.indexOf("youtube.com/feeds") > 0)
        {
            this.ID = location.substring(location.lastIndexOf("id=") + 3);
        }
        //if(url.indexOf("bitchute.com")>0)
        else
        {
            String[] segments = location.split("/");
            this.ID = segments[segments.length - 1];
        }
        this.lastsync = new Date();
        videos=new ArrayList<Video>();
        System.out.println("starting scrape of channel:"+url);
    }
    
//          Getters 
    public String getID() {
        return ID; 
    }
    public Date getJoined() {
        return joined;
    }    
    public Date getLastsync() {
        return lastsync;
    }    
    public String getSubscribers() {
        return subscribers;
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
    public String getBitchuteRssFeedUrl(){
        if (url.indexOf("bitchute")>0) {
            return "https://www.bitchute.com/feeds/rss/channel/" + ID;
        }
        else{
            return "";
        }
    }
    public String getBitchuteUrl() {
        if (url.indexOf("bitchute") > 0) {
            return "https://www.bitchute.com/channel/" + ID;
        }
        else{
            return "";
        }
    }
    public String getYoutubeRssFeedUrl() {
        if (url.indexOf("youtube") > 0) {
            return "https://www.youtube.com/feeds/videos.xml?channel_id=" + ID;
        } else {
            return "";
        }
    }
    public String getYoutubeUrl(){
        if (url.indexOf("youtube") > 0) {
            return "https://www.youtube.com/channel/"+ID;
        } else {
            return "";
        }
    }

   //           setters 
    public void setJoined(Date joined) {
        this.joined = joined;
    }
    public void setLastsync(Date lastsync) {
        this.lastsync = lastsync;
    }
    public void setUrl(String value){
        this.url=value;
       /* if (this.ID.isEmpty()){
            if (value.indexOf("youtube.com/feeds") > 0) {
                this.ID = value.substring(value.lastIndexOf("id=") + 3);
            }
        }
        else {
            String[] segments = value.split("/");
            this.ID = segments[segments.length - 1];
        }
    */
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
    public void setID(String value){
        this.ID = value;
    }
    
    //          OTher bits
    public void addVideo(Video vid){
        videos.add(vid);
        if (author.isEmpty()){
            this.author = vid.getAuthor();
        }
    }
    public String toString(){
        return("title:"+this.title+"\n"+
                "ID:"+this.ID+"\n"+
                "url:"+this.url+"\n"+
                "thumbnail:"+this.thumbnailurl+"\n"+
                "author:"+this.author+"\n"+
                "profile image"+this.profileImage+"\n"+
                "description:"+this.description+"\n");
    }


}
