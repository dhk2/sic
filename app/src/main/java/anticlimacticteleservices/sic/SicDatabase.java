package anticlimacticteleservices.sic;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Video.class}, version = 5, exportSchema = false)
public abstract class SicDatabase extends RoomDatabase {
    private static SicDatabase INSTANCE;

    public abstract VideoDao videoDao();

    public static SicDatabase getSicDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SicDatabase.class, "item_-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
