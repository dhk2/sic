package anticlimacticteleservices.sic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "Feed_Item")
class Video implements Serializable,Comparable<Video>
{
    @PrimaryKey(autoGenerate = true)
    private long ID;
    @ColumnInfo(name = "author_id")
    private long authorID;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "watched")
    private boolean watched;
    @ColumnInfo(name = "date")
    private long date;
    @ColumnInfo(name = "currentPosition")
    private long currentPosition;
    @ColumnInfo(name = "thumbnail_url")
    private String thumbnailurl;
    @ColumnInfo(name = "magnet_link")
    private String magnet;
    @ColumnInfo(name = "video_link")
    private String mp4;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "view_count")
    private String viewCount;
    @ColumnInfo(name = "rating")
    private String rating;
    @ColumnInfo(name = "up_count")
    private String upCount;
    @ColumnInfo(name = "down_count")
    private String downCount;
    @ColumnInfo(name = "source_id")
    private String sourceID;
    @ColumnInfo(name = "comment_count")
    private String commentCount;
    @ColumnInfo(name = "hash_tags")
    private String hashtags;
    @ColumnInfo(name = "category")
    private String category;
    public Video()
    {
        this.title = "";
        this.author = "";
        this.url = "";
    //    System.out.println("blank constructor url set to:" + this.url);
        this.watched = false;
        this.date = new Date().getTime();
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "";
        this.viewCount = "";
        this.upCount = "";
        this.downCount = "";
        this.sourceID = "";
        this.commentCount = "0";
        this.hashtags = "";
        this.category = "";
    }

    public Video(String location)
    {
        this.title = "";
        this.author = "";
        this.url = location;
        //System.out.println("new video created with" + this.url);
        if (location.indexOf("youtube") > 0)
        {
            sourceID = location.substring(location.lastIndexOf("?v=") + 3);
        }
        else
        {
            String[] segments = location.split("/");
            sourceID = segments[segments.length - 1];
        }
       // System.out.println("got id "+sourceID+" from "+location);
        this.watched = false;
        this.date = 0;
        this.thumbnailurl = "";
        this.magnet = "";
        this.description = "";
        this.mp4 = "";
        this.rating = "";
        this.viewCount = "";
        this.upCount = "";
        this.downCount = "";
        this.commentCount = "";
        this.hashtags = "";
        this.category = "";
    }

//  	     Getters

    public long getAuthorID() {
        return authorID;
    }
    public String getUpCount() {
        return upCount;
    }

    public void setAuthorID(long authorID) {
        this.authorID = authorID;
    }
    public String getUrl()
    {
        return this.url;

    }
    public long getDate()
    {
        return date;
    }
    public String getDescription()
    {
        return this.description;
    }
    public String getTitle()
    {
        return this.title;
    }
    public String getAuthor()
    {
        return this.author;
    }
    public String getThumbnail()
    {
        return this.thumbnailurl;
    }
    public String getMp4()
    {
        return this.mp4;
    }
    public String getViewCount()
    {
        return this.viewCount;
    }
    public String getRating()
    {
        return this.rating;
    }
    public boolean getWatched()
    {
        return this.watched;
    }
    public String getSourceID()
    {
        return this.sourceID;
    }
    public String getEmbeddedUrl(){
        //update for new video sources
        if (url.indexOf("youtube") > 0) {
            return "https://www.youtube.com/embed/"+this.sourceID +"?autoplay=1&modestbranding=1";
        } else {
            return "https://www.bitchute.com/embed/"+this.sourceID;
        }
    }


//			Setters

    public void setUrl(String value)
    {
        this.url = value;
        if (this.sourceID.isEmpty()) {
            if (value.indexOf("youtube") > 0) {
                sourceID = value.substring(value.lastIndexOf("?v=") + 3);
            } else {
                String[] segments = value.split("/");
                sourceID = segments[segments.length - 1];
            }
        }
    }



    public void setTitle(String value)
    {
        this.title = value;
    }
    public void setAuthor(String value)
    {
        this.author = value;
    }
    public void setThumbnail(String value)
    {
        this.thumbnailurl = value;
    }
    public void setMagnet(String value)
    {
        this.magnet = value;
    }
    public void setDate(long date)
    {
        //System.out.println(date);

        this.date = date;
    }
    public void setDate(Date date){
        this.date=date.getTime();
    }
    public void setMp4(String value)
    {
        this.mp4 = value;
    }
    public void setDescription(String value)
    {
        this.description = value;
    }
    public void setRating(String value)
    {
        this.rating = value;
    }
    public void setUpCount(String value)
    {
        this.upCount = value;
    }
    public void setDownCount(String value)
    {
        this.downCount = value;
    }
    public void setViewCount(String value)
    {
        this.viewCount = value;
    }
    public void setCommentCount(String value)
    {
        this.commentCount = value;
    }
    public void setCategory(String value)
    {
        this.category = value;
    }
    public void setHashtags(String value)
    {
        this.hashtags = value;
    }
    public void setSourceID(String value)
    {
        this.sourceID = value;
    }
//			Functions

    public String toDebugString()  {
        return ("title:" + title + "\n" +
                "url:" + url + "\n" +
                "thumbnail:" + thumbnailurl + "\n" +
                "author:" + author + "\n" +
//			"watched:" + watched.toString() + "\n" +
                "uploaded:" + new Date(date).toString() + "\n" +
                "magnet link:" + magnet + "\n" +
                "description:" + description + "\n" +
                "mp4 file" + mp4 + "\n" +
                "Rating:" + rating + "\n" +
                "Views:" + viewCount + "\n" +
                "Up votes:" + upCount + "\n" +
                "Down votes:" + downCount + "\n" +
                "sourceID:" + sourceID + "\n" +
                "Comments:" + commentCount + "\n" +
                "Hash tags:" + hashtags  + "\n" +
                "Category:" + category+ "\n");
    }

    public String toString() {
        return (new Date(date).toString() + " " + title + "  by" + author);
    }
    @Override
    public int compareTo(Video candidate)
    {
        return (this.getDate()>(candidate.getDate())  ? -1 :
                this.getDate()==(candidate.getDate()) ? 0 : 1);
    }

    public boolean isBitchute(){
        return (this.url.indexOf("bitchute.com") > 0);
    }
    public String getBitchuteUrl() {return "https://www.bitchute.com/video/"+this.sourceID;}
    public boolean isYoutube(){
        return this.url.indexOf("youtube.com") > 0;
    }
    public String getYoutubeUrl(){
        return "https://www.youtube.com/watch?v="+this.sourceID;
    }

 //   public ArrayList<Comment> getComments(){
//        //comments disabled until they can be roomiied
 //       return null;
 //   }
    public boolean match(String matchID){
        return (matchID.equals(sourceID));
    }

    public long getID() {
        return ID;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getMagnet() {
        return magnet;
    }

    public String getDownCount() {
        return downCount;
    }

    public String getHashtags() {
        return hashtags;
    }

    public String getCategory() {
        return category;
    }

    public void addComment(Comment com){
        //comments disabled
        /*
        Boolean unique=true;
        for (int i=1;i<comments.size();i++) {
            Comment match = (Comment) comments.get(i);
            if (match.getSourceID().equals(com.getSourceID())) {
                unique = false;
            }
        }
        if (unique){
            comments.add(com);
        }
        */
    }
}