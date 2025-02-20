package vn.tapbi.zazip.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.tapbi.zazip.data.model.Recent;

@Dao
public interface RecentDao {

    @Query("SELECT EXISTS(SELECT * FROM recent WHERE path = :path)")
    Boolean exists(String path);

    @Query("SELECT * FROM recent ORDER BY time_open DESC")
    List<Recent> getRecent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recent recent);

    @Delete
    void delete(Recent recent);

    @Query("UPDATE recent SET time_open = :timeOpen WHERE path =:path")
    void update(String path, long timeOpen);
}
