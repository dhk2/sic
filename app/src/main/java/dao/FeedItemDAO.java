package dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import anticlimacticteleservices.sic.FeedItem;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FeedItemDAO {
    @Insert
    public void insert(FeedItem ... feeditems);
    @Update
    public void update(FeedItem... feeditems);
    @Delete
    public void delete(FeedItem feeditem);

    @Query("SELECT * FROM feeditems")
    public default ArrayList<FeedItem> getFeedItems() {
        return null;
    }

    @Query("SELECT * FROM feeditems WHERE ID = :id")
    public FeedItem getFeedItemById(Long id);

    @Query("SELECT url From feeditems where ID = :id")
    public String getUrlById(Long id);

    @Query("SELECT title From feeditems where ID = :id")
    public String getTitleById(Long id);

    @Query("SELECT author From feeditems where ID = :id")
    public String getAuthorById(Long id);

    @Query("SELECT thumbnailurl From feeditems where ID = :id")
    public String getThumbnailUrlById(Long id);

    @Query("SELECT magnet From feeditems where ID = :id")
    public String getMagnetById(Long id);
}
