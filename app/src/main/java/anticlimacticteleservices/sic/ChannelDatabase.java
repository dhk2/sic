package anticlimacticteleservices.sic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Channel.class}, version = 4, exportSchema = false)
public abstract class ChannelDatabase extends RoomDatabase {
    private static anticlimacticteleservices.sic.ChannelDatabase INSTANCE;

    public abstract ChannelDao ChannelDao();

    public static anticlimacticteleservices.sic.ChannelDatabase getChannelDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),ChannelDatabase.class, "channel")
        // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                          //  .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}

