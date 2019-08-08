package anticlimacticteleservices.sic;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Comment  ... comment);
    @Update
    public void update(Comment... comment);
    @Delete
    public void delete(Comment  comment);

    @Query("SELECT * FROM comment")
    List<Comment> getComments();


    @Query("SELECT * FROM comment WHERE ID = :id")
    Comment getCommentById(Long id);

    @Query("SELECT * FROM comment WHERE parent_id = :id")
    List<Comment> getCommentByParentId(Long id);

    @Query("SELECT COUNT(*) from comment")
    int countComments();

    @Query("SELECT * FROM comment WHERE feedID = :id")
    List<Comment> getCommentsByFeedId(Long id);

    @Query("SELECT * FROM comment WHERE feedID = :id AND text = :text AND author = :author" )
    Comment dupeCheck(Long id, String text, String author);


    @Insert
    void insertAll(Comment... comment);
}
