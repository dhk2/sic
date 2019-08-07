package anticlimacticteleservices.sic;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.io.*;
@Entity(tableName = "channel")
class Channel implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private long ID;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "urls")
    private ArrayList<String> urls;
    @ColumnInfo(name = "thumbnail_url")
    private String thumbnailurl;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "profile_image")
    private String profileImage;
    @ColumnInfo(name = "subscribers")
    private String subscribers;
    @ColumnInfo(name = "source_id")
    private String sourceID;
    @ColumnInfo(name = "bitchute_id")
    private String bitchuteID;
    @ColumnInfo(name = "youtube_id")
    private String youtubeID;
    @ColumnInfo(name = "start date")
    private long joined;
    @ColumnInfo(name = "last_sync")
    private long lastsync;



    //private ArrayList<Video> videos;
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
        this.sourceID ="";
        this.youtubeID="";
        this.bitchuteID="";
        this.urls=new ArrayList<String>();
     //   this.videos=new ArrayList<Video>();
        this.lastsync=new Date().getTime();
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
            this.sourceID = url.substring(url.lastIndexOf("id=") + 3);
        }
        //if(url.indexOf("bitchute.com")>0)
        else
        {
            String[] segments = url.split("/");
            sourceID = segments[segments.length - 1];
        }
        if (url.indexOf("youtube.com")>0){
            youtubeID= sourceID;
            bitchuteID="";
        }
        if (url.indexOf("bitchute.com")>0) {
            bitchuteID = sourceID;
            youtubeID="";
        }
        lastsync = new Date().getTime();
        joined = lastsync;
       // videos=new ArrayList<Video>();
        toString();
    }
    
//          Getters 
    public String getSourceID() {
        return sourceID;
    }
    public long getJoined() {
        return joined;
    }    
    public long getLastsync() {
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

    //public ArrayList<Video> getVideos(){return this.videos;}
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
    public ArrayList<String> getUrls(){
        return urls;
    }
   //           setters 
    public void setJoined(Date joined) {
        this.joined = joined.getTime();
    }
    public void setSubscribers(String value){
        this.subscribers=value;
    }
    public void setLastsync(Date lastsync) {
        this.lastsync = lastsync.getTime();
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
    public void setSourceID(String value){
        this.sourceID = value;
    }
    
    //          OTher bits
    /*
    public void addVideo(Video vid){
        boolean unique=true;
        for (Video match : videos) {
            if (match.getSourceID().equals(vid.getSourceID())) {
                unique = false;
                break;
            }
        }
        if (unique) {
            videos.add(vid);
            if (author.isEmpty()) {
                this.author = vid.getAuthor();
            }
        }
    }
    */
    public String toString(){
        return("title:"+this.title+"\n"+
                "sourceID:"+this.sourceID +"\n"+
                "youtube id:"+youtubeID+"\n"+
                "bitchute id:"+bitchuteID+"\n"+
                "url:"+url+"\n"+
                "url count"+urls.size()+"\n"+
                "thumbnail:"+this.thumbnailurl+"\n"+
                "author:"+this.author+"\n"+
                "profile image"+this.profileImage+"\n"+
                "Subscribers:"+this.subscribers+"\n"+
                "date joined"+new Date(this.joined)+"\n"+
                "Last Sync"+new Date(lastsync)+"\n"+
                "description:"+this.description+"\n");


    }
    public boolean isBitchute(){
        return this.url.indexOf("bitchute.com") > 0;
    }

    public boolean isYoutube(){
        return this.url.indexOf("youtube.com") > 0;
    }
    public boolean matches(String value){
      //  System.out.println("trying to match:"+value);
        return youtubeID.equals(value) || bitchuteID.equals(value);
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
}
