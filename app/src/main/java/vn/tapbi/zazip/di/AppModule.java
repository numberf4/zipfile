package vn.tapbi.zazip.di;

import static vn.tapbi.zazip.common.Constant.DB_NAME;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.data.db.AccountDB;
import vn.tapbi.zazip.data.db.AccountDao;
import vn.tapbi.zazip.data.db.RecentDataBase;
import vn.tapbi.zazip.utils.FileProperties;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public RecentDataBase recentDataBase(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), RecentDataBase.class, RecentDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    @Provides
    @Singleton
    public AccountDB provideRoomDb(Application context) {
        return Room.databaseBuilder(context, AccountDB.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public AccountDao provideMessageThreadDao(AccountDB db) {
        return db.accountDao();
    }

    @Provides
    @Singleton
    public Context getContent() {
        return App.getInstance();
    }

    @Provides
    @Singleton
    public FileProperties getFileProperties() {
        return new FileProperties();
    }
}
