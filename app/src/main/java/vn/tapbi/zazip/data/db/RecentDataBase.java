package vn.tapbi.zazip.data.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;


import vn.tapbi.zazip.data.model.Recent;

@Database(entities = {Recent.class}, version = 1, exportSchema = false)
public abstract class RecentDataBase extends RoomDatabase {

    public abstract RecentDao recentDao();

    public static final String DATABASE_NAME = "utilities.db";
    public static final String TABLE_RECENT = "recent";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_TIME_OPEN = "time_open";
}
