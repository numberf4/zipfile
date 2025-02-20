package vn.tapbi.zazip.data.db;



import static vn.tapbi.zazip.common.Constant.DB_VERSION;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import vn.tapbi.zazip.data.model.Account;


@Database(entities = {Account.class}, version = DB_VERSION, exportSchema = false)
public abstract class AccountDB extends RoomDatabase {
    public abstract AccountDao accountDao();

}
