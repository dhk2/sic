package anticlimacticteleservices.sic;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import dao.FeedItemDAO;
import anticlimacticteleservices.sic.FeedItem;

@Database(entities = {FeedItem.class}, version = 2, exportSchema = false)
public abstract class SicDatabase extends RoomDatabase {
    public abstract FeedItemDAO getFeedItemDAO();

}
