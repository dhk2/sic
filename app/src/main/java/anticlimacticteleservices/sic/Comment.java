package anticlimacticteleservices.sic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "comment")
public class Comment implements Serializable {
    @PrimaryKey(autoGenerate = true)
    Long ID;
    @ColumnInfo(name = "feedID")
    Long feedID;
    @ColumnInfo(name = "source_id")
    String sourceID;
    @ColumnInfo(name = "url")
    String url;
    @ColumnInfo(name = "text")
    String text;
    @ColumnInfo(name = "time_stamp")
    String timestamp;
    @ColumnInfo(name = "thumbnail")
    String thumbnail;
    @ColumnInfo(name = "parent_id")
    String parent;
    @ColumnInfo(name = "upvote")
    String upVote;
    @ColumnInfo(name = "downvote")
    String downVote;
    @ColumnInfo(name = "author")
    String author;

    public Comment(String sourceid) {
        feedID =0l;
        this.sourceID = sourceid;
        url = "";
        text="";
        timestamp="";
        thumbnail="";
        parent = "";
        upVote = "";
        downVote = "";
        author = "";
    }
    public Comment() {
        feedID =0l;
        this.sourceID = "";
        url = "";
        text="";
        timestamp="";
        thumbnail="";
        parent = "";
        upVote = "";
        downVote = "";
        author = "";
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getUpVote() {
        return upVote;
    }

    public void setUpVote(String upVote) {
        this.upVote = upVote;
    }

    public String getDownVote() {
        return downVote;
    }
    public String getSourceID(){
        return this.sourceID;
    }
    public void setDownVote(String downVote) {
        this.downVote = downVote;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public Long getFeedID() {
        return feedID;
    }

    public void setFeedID(Long feedID) {
        this.feedID = feedID;
    }

    @Override
    public String toString() {

        return this.author+": "+this.text;
    }
    public String toHtml() {
        String html = "<img src=\""+thumbnail+"\" height=\"30\" width=\"30\" style=\"float:left\">"+
                "<b> "+author+"</b><p>"+text+"<p>";
        //System.out.println(html);
        return html;
    }

}
