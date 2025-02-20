package vn.tapbi.zazip.data.repository;


import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.data.db.RecentDataBase;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.Recent;

public class RecentRepository {
    private RecentDataBase dataBase;
    private FileDataRepository fileDataRepository;
    private Context context;
    private final long ONE_MONTH_MILLISECONDS = 30L * 24L * 60L * 60L * 1000L;

    @Inject
    public RecentRepository(Context context, RecentDataBase dataBase, FileDataRepository fileDataRepository) {
        this.context = context;
        this.dataBase = dataBase;
        this.fileDataRepository = fileDataRepository;
    }

    public Single<List<FileData>> getRecent() {
        return Single.fromCallable(this::getListRecent).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private List<FileData> getListRecent() {
        List<FileData> fileData = new ArrayList<>();
        List<Recent> recent = dataBase.recentDao().getRecent();

        long currentTime = System.currentTimeMillis();
        for (Recent r : recent) {
            File file = new File(r.getPath());
            if (file.exists() && checkTimeOpenFile(r.getTimeOpen(), currentTime)) {
                int count = fileDataRepository.getCountItem(file, false);
                fileData.add(new FileData(file.getName(),
                        file.length(),
                        file.getAbsolutePath(),
                        file.lastModified() / 1000,
                        file.length(),
                        file.isDirectory(),
                        count)
                );
            } else {
                dataBase.recentDao().delete(r);
            }
        }
        return fileData;
    }

    private boolean checkTimeOpenFile(long timeOpen, long currentTime) {
        return !(currentTime - timeOpen > ONE_MONTH_MILLISECONDS);
    }

    public void insertRecent(String path) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                File file = new File(path);
                if (!file.isDirectory()) {
                    if (dataBase.recentDao().exists(path)) {
                        dataBase.recentDao().update(path, System.currentTimeMillis());
                    } else {
                        dataBase.recentDao().insert(new Recent(path, System.currentTimeMillis()));
                    }
                }
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

}
