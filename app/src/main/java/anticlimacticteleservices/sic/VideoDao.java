package anticlimacticteleservices.sic;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Video  ... feed_item);
    @Update
    public void update(Video... feed_item);
    @Delete
    public void delete(Video  feed_item);


    @Query("SELECT * FROM feed_item ORDER BY date DESC")
    List<Video> getVideos();

    @Query("SELECT * FROM feed_item WHERE ID = :id")
    public Video getvideoById(Long id);

    @Query("SELECT * FROM feed_item WHERE author_id = :id")
    List<Video> getVideosByAuthorId(Long id);

    @Query("SELECT COUNT(*) from feed_item")
    int countVideos();

    @Query("Select * FROM feed_item WHERE source_id = :sid")
    List<Video> getVideosBySourceID(String sid);

    @Insert
    void insertAll(Video... feed_item);

    @Query("SELECT * FROM feed_item WHERE watched=1 ORDER BY date DESC")
    List<Video> getWatchedVideos();

    @Query("SELECT * FROM feed_item WHERE watched=0 ORDER BY date DESC")
    List<Video> getUnWatchedVideos();




}
