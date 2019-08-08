package anticlimacticteleservices.sic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Comment.class}, version = 1, exportSchema = false)
public abstract class CommentDatabase extends RoomDatabase {
    private static anticlimacticteleservices.sic.CommentDatabase INSTANCE;

    public abstract CommentDao CommentDao();

    public static anticlimacticteleservices.sic.CommentDatabase getCommentDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),CommentDatabase.class, "comment")
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