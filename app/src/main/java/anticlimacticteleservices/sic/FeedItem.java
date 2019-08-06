package anticlimacticteleservices.sic;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.arch.persistence.room.Entity;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "feeditems")
public class FeedItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull private long ID;
    private String title;
    private String author;
    private String url;
    private boolean watched;
    private boolean liked;
    private boolean favorite;
    private boolean boosted;
    private boolean disliked;
    private boolean hidden;
    private boolean shared;
    private Long currentPosition;
    private Long date;
    private String thumbnailurl;
    private String magnet;
    private String mp4;
    private String annotation;
    private String Picture;
    private String description;
    private String viewCount;
    private String rating;
    private String upCount;
    private String downCount;
    private String sourceID;
    private String hashtags;
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isBoosted() {
        return boosted;
    }

    public void setBoosted(boolean boosted) {
        this.boosted = boosted;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public Long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getMp4() {
        return mp4;
    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUpCount() {
        return upCount;
    }

    public void setUpCount(String upCount) {
        this.upCount = upCount;
    }

    public String getDownCount() {
        return downCount;
    }

    public void setDownCount(String downCount) {
        this.downCount = downCount;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }



}
