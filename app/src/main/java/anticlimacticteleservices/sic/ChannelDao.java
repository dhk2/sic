package anticlimacticteleservices.sic;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Channel  ... channel);
    @Update
    public void update(Channel... channel);
    @Delete
    public void delete(Channel  channel);

    @Query("SELECT * FROM channel")
    List<Channel> getChannels();


    @Query("SELECT * FROM channel WHERE ID = :id")
    public Channel getChannelById(Long id);

    @Query("SELECT * FROM channel WHERE bitchute_id= :id OR youtube_id = :id")
    List<Channel> getChannelsBySourceID(String id);

    @Query("SELECT COUNT(*) from channel")
    int countChannels();

    @Insert
    void insertAll(Channel... channel);
}
