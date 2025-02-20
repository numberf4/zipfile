package vn.tapbi.zazip.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import vn.tapbi.zazip.data.db.RecentDataBase;

@Entity(tableName = RecentDataBase.TABLE_RECENT)
public class Recent {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RecentDataBase.COLUMN_ID)
    public int id;

    @ColumnInfo(name = RecentDataBase.COLUMN_PATH)
    public String path;

    @ColumnInfo(name = RecentDataBase.COLUMN_TIME_OPEN)
    public long timeOpen;

    public Recent(String path, long timeOpen) {
        this.path = path;
        this.timeOpen = timeOpen;
    }

    public long getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(long timeOpen) {
        this.timeOpen = timeOpen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
