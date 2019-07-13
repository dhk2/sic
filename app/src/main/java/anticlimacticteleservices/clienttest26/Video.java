package anticlimacticteleservices.clienttest26;

import java.util.Date;

public class Video implements Comparable<Video>  {
    private String title;
    private String author;
    private String url;
    private boolean watched;
    private Date date;
    private String thumbnailurl;
    private String magnet;
    private String mp4;
    private String description;
    private String views;
    private String rating;

    public Video() {
        this.title = "";
        this.author = "";
        this.url = "";
        System.out.println("blank constructor url set to:"+this.url);
        this.watched = false;
        this.date = new Date();
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "0";
        this.views = "0";
    }

    public Video(String location) {
        this.title = "";
        this.author = "";
        this.url = location;
        System.out.println("new video created with"+this.url);
        this.watched = false;
        this.date = new Date();
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "0";
        this.views = "0";
    }

    public String toString() {
        return ("title:" + this.title + "\n" +
                "url:" + this.url + "\n" +
                "thumbnail:" + this.thumbnailurl + "\n" +
                "author:" + this.author + "\n" +
                "watched:" + this.watched + "\n" +
                "uploaded:"+ this.date.toString()+"\n"+
                "magnet link:" + this.magnet + "\n" +
                "description:" + this.description + "\n" +
                "views:" + this.views + "\n" +
                "rating:" + this.rating + "\n" +
                "mp4 file" + this.mp4);

    }

    public String getUrl() {
        return this.url;
    }
    public Date getDate(){
        return this.date;
    }
    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getThumbnail() {
        return this.thumbnailurl;
    }

    public String getMp4() {
        return this.mp4;
    }

    public String getViews() {
        return this.views;
    }

    public String getRating() {
        return this.rating;
    }

    public boolean getWatched() {
        return this.watched;
    }

    public void setUrl(String value) {
        this.url = value;
 //       System.out.println("url explicitly set to:"+this.url);

    }

    public void setTitle(String value) {
        this.title = value;
    }

    public void setAuthor(String value) {
        this.author = value;
    }

    public void setThumbnail(String value) {
        this.thumbnailurl = value;
    }

    public void setMagnet(String value) {
        this.magnet = value;
    }
    public void setDate(Date date){
        this.date=date;
    }
    public void setMp4(String value) {
        this.mp4 = value;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setViews(String value) {
        this.views = value;
    }

    public void setRating(String value) {
        this.rating = value;
    }


@Override
    public int compareTo(Video candidate) {
        return (this.getDate().after(candidate.getDate())  ? -1 :
                this.getDate().equals(candidate.getDate()) ? 0 : 1);
    }

}