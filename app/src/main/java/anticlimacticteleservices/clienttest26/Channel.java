package anticlimacticteleservices.clienttest26;


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

public class Channel {
    private String title;
    private String author;
    private String url;
    private String thumbnailurl;
    private String description;
    private String profileImage;
    private String subscribers;
    private String ID;
    

    private ArrayList<Video> videos;
    final SimpleDateFormat bdf = new SimpleDateFormat("MMM dd, yyyy");
    final SimpleDateFormat ydf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    final SimpleDateFormat bdf2 = new SimpleDateFormat( "                   hh:mm zzz    MMMM dd    yyyy");

    public String getID() {
        return ID;
    }

    public void setSourceID(String sourceID) {
        this.ID = sourceID;
    }

    public Channel(){
        this.title="";
        this.author="";
        this.url="";
        this.thumbnailurl="";
        this.description="";
        this.profileImage="";
        this.ID="";
        this.videos=new ArrayList<Video>();

    }
    public Channel(String url) {
        final String location = url;
        this.url = url;
        this.description="";
        this.thumbnailurl="";
        this.profileImage="";
        int counter = 0;
        if (url.indexOf("youtube") > 0)
        {
            this.ID = location.substring(location.lastIndexOf("?v=") + 3);
        }
        else
        {
            String[] segments = location.split("/");
            this.ID = segments[segments.length - 1];
        }

        videos=new ArrayList<Video>();
        System.out.println("starting scrape of channel:"+url);

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
        if (this.ID.isEmpty()){
            if (value.indexOf("youtube") > 0) {
                this.ID = value.substring(value.lastIndexOf("id=") + 3);
            }
        }
        else {
            String[] segments = value.split("/");
            this.ID = segments[segments.length - 1];
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
    public void addVideo(Video vid){
        videos.add(vid);
        if (author.isEmpty()){
            this.author = vid.getAuthor();
        }

    }



}
