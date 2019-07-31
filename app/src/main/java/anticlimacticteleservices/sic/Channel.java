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

class Channel implements Serializable{
    private String title;
    private String author;
    private String url;
    private ArrayList<String> urls;
    private String thumbnailurl;
    private String description;
    private String profileImage;
    private String subscribers;
    private String ID;
    private String bitchuteID;
    private String youtubeID;
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
        this.youtubeID="";
        this.bitchuteID="";
        this.urls=new ArrayList<String>();
        this.videos=new ArrayList<Video>();
        this.lastsync=new Date();
        this.joined=lastsync;
        this.subscribers="";
    }
    public Channel(String url) {
        this.url = url;
        urls = new ArrayList<String>();
        urls.add(url);
        description="";
        thumbnailurl="";
        profileImage="";
        author="";
        subscribers="";
        if (url.indexOf("youtube.com/feeds") > 0)
        {
            this.ID = url.substring(url.lastIndexOf("id=") + 3);
        }
        //if(url.indexOf("bitchute.com")>0)
        else
        {
            String[] segments = url.split("/");
            ID = segments[segments.length - 1];
        }
        if (url.indexOf("youtube.com")>0){
            youtubeID=ID;
            bitchuteID="";
        }
        if (url.indexOf("bitchute.com")>0) {
            bitchuteID = ID;
            youtubeID="";
        }
        lastsync = new Date();
        joined = lastsync;
        videos=new ArrayList<Video>();
        toString();
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
        for (String u : urls) {
            if (u.indexOf("bitchute") > 0) {
                return "https://www.bitchute.com/feeds/rss/channel/" + bitchuteID;
            }
        }
        return "";
    }
    public String getBitchuteUrl() {
        for (String u : urls){
            if (u.indexOf("bitchute") > 0) {
                return "https://www.bitchute.com/channel/" + bitchuteID;
            }
        }
        return "";
    }
    public String getYoutubeRssFeedUrl() {
        for (String u : urls) {
            if (url.indexOf("youtube") > 0) {
                return "https://www.youtube.com/feeds/videos.xml?channel_id=" + youtubeID;
            }
        }
        return "";
    }
    public String getYoutubeUrl(){
        for (String u : urls) {
            if (u.indexOf("youtube") > 0) {
                return "https://www.youtube.com/channel/" + youtubeID;
            }
        }
        return "";
    }

   //           setters 
    public void setJoined(Date joined) {
        this.joined = joined;
    }
    public void setSubscribers(String value){
        this.subscribers=value;
    }
    public void setLastsync(Date lastsync) {
        this.lastsync = lastsync;
    }
    public void setUrl(String value){
        if (value.indexOf("youtube.com")>0 && youtubeID.isEmpty()){
            if (value.indexOf("youtube.com/feeds") > 0) {
                youtubeID = value.substring(value.lastIndexOf("id=") + 3);
            }
            else {
                String[] segments = value.split("/");
                youtubeID = segments[segments.length - 1];
            }
            urls.add(value);
        }
        if (value.indexOf("bitchute.com")>0 && bitchuteID.isEmpty()){
            String[] segments = value.split("/");
            bitchuteID = segments[segments.length - 1];
            urls.add(value);
        }
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
                "youtube id:"+youtubeID+"\n"+
                "bitchute id:"+bitchuteID+"\n"+
                "url:"+url+"\n"+
                "url count"+urls.size()+"\n"+
                "thumbnail:"+this.thumbnailurl+"\n"+
                "author:"+this.author+"\n"+
                "profile image"+this.profileImage+"\n"+
                "Subscribers:"+this.subscribers+"\n"+
                "date joined"+this.joined+"\n"+
                "Last Sync"+this.lastsync+"\n"+
                "description:"+this.description+"\n");


    }
    public boolean isBitchute(){
        return this.url.indexOf("bitchute.com") > 0;
    }

    public boolean isYoutube(){
        return this.url.indexOf("youtube.com") > 0;
    }
    public boolean matches(String value){
        System.out.println("trying to match:"+value);
        toString();
        if (youtubeID.equals(value) || bitchuteID.equals(value)){
            return true;
        }
        return false;
    }
}
