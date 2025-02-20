package vn.tapbi.zazip.ui.main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.common.LiveEvent;
import vn.tapbi.zazip.common.models.EventModifyName;
import vn.tapbi.zazip.common.models.EventOnedrive;
import vn.tapbi.zazip.common.models.EventPath;
import vn.tapbi.zazip.common.models.EventPathCompress;
import vn.tapbi.zazip.common.models.EventRename;
import vn.tapbi.zazip.common.models.EventSortView;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.ItemSelectBottom;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.data.repository.RecentRepository;
import vn.tapbi.zazip.model.EventLoginGoogleDrive;
import vn.tapbi.zazip.model.EventShowGoogleDrive;
import vn.tapbi.zazip.ui.base.BaseViewModel;
import vn.tapbi.zazip.utils.FileProperties;
import vn.tapbi.zazip.utils.Utils;

@HiltViewModel
public class MainViewModel extends BaseViewModel {
    private final FileProperties fileProperties;
    private final RecentRepository recentRepository;
    public FileDataRepository fileDataRepository;
    public MutableLiveData<List<FileData>> recent = new MutableLiveData<>();

    public MutableLiveData<EventSortView> positionPresentationView = new MutableLiveData<>();
    public LiveEvent<EventSortView> eventSortView = new LiveEvent<>();
    public LiveEvent<Boolean> eventShare = new LiveEvent<>();

    public MutableLiveData<Boolean> isShowDialog = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isGetFile = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isGetVideoFile = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isGetPhotoFile = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isGetMusicFile = new MutableLiveData<>(false);

    public MutableLiveData<String> archiveFormat = new MutableLiveData<>();

    public MutableLiveData<List<FileData>> listSelect = new MutableLiveData<>();
    public MutableLiveData<List<ItemSelectBottom>> listOptionBottom = new MutableLiveData<>();
    public LiveEvent<EventModifyName> nameFileToChange = new LiveEvent<>();
    public MutableLiveData<Boolean> allowPaste = new MutableLiveData<>(false);
    public LiveEvent<EventPath> pathPaste = new LiveEvent<>();
    public MutableLiveData<Boolean> haveFileSelect = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> reload = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> reloadExplore = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isResetList = new MutableLiveData<>(true);
    public MutableLiveData<Boolean> isResetListHome = new MutableLiveData<>(true);
    public LiveEvent<EventModifyName> isDelete = new LiveEvent<>();
    public MutableLiveData<Boolean> isShowDetailSelect = new MutableLiveData<>(false);
    public LiveEvent<EventPathCompress> pathCompressLiveEvent = new LiveEvent<>();
    public MutableLiveData<String> pathFolder = new MutableLiveData<>();
    public MutableLiveData<Boolean> showBrowse = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isListArchive = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> showDrawer = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isShowDrawerBack = new MutableLiveData<>(true);
    public MutableLiveData<EventRename> renameSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> gotoSetting = new MutableLiveData<>(false);
    public LiveEvent<EventLoginGoogleDrive> getGoogleSignInClient = new LiveEvent<>();
    public MutableLiveData<EventOnedrive> eventOnedrive = new MutableLiveData<>();
    public LiveEvent<Integer> changeScreenLive = new LiveEvent<>();
    public LiveEvent<EventShowGoogleDrive> showGoogleDriveDetailFail = new LiveEvent<>();
    public LiveEvent<Boolean> checkOpenFile = new LiveEvent<>();
    public LiveEvent<Boolean> checkDoubleClick = new LiveEvent<>();

    public LiveEvent<Boolean> checkResetDownload = new LiveEvent<>();
    @Inject
    public MainViewModel(FileDataRepository fileDataRepository, RecentRepository recentRepository, FileProperties fileProperties) {
        this.fileDataRepository = fileDataRepository;
        this.recentRepository = recentRepository;
        this.fileProperties = fileProperties;
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public FileData getFileDataByPath(Context context, String path) {
        return fileDataRepository.getFileDataByPath(context, path);
    }

    public void insertRecent(String path) {
        recentRepository.insertRecent(path);
    }

    public void changStateShow() {
        isShowDetailSelect.setValue(!isShowDetailSelect.getValue());
    }

    public void getAllFile(Context context) {
        getRecent();
        getPhotoFile(context);
        getDocFile(context);
        getVideoFile(context);
        getMusicFile(context);
    }
    public void getRecent() {
        recentRepository.getRecent().subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<FileData> fileData) {
                recent.setValue(fileData);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();            }
        });
    }
    public void clearListDownload(){
        fileDataRepository.clearListDownload();
    }

    public void getDocFile(Context context) {
        fileDataRepository.getDocumentFile(context).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isGetFile.setValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void getVideoFile(Context context) {
        fileDataRepository.getVideoFile(context).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isGetVideoFile.setValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void getPhotoFile(Context context) {
        fileDataRepository.getPhotoFile(context).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isGetPhotoFile.setValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void getMusicFile(Context context) {
        fileDataRepository.getMusicFile(context).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isGetMusicFile.setValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void getListOptionBottom(Context context) {
        fileDataRepository.getListOptionBottom(context).subscribe(new SingleObserver<List<ItemSelectBottom>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<ItemSelectBottom> itemSelectBottoms) {
                listOptionBottom.postValue(itemSelectBottoms);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void checkListArchive(List<FileData> list) {
        fileDataRepository.checkListArchive(list).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {
                isListArchive.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public Single<Boolean> copyPasteTask(Context context, List<FileData> selectList, String pathPaste) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Utils.copyAndPaste(context, selectList, pathPaste);
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> deleteFile(List<FileData> selectList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Utils.deleteFile(selectList);
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
