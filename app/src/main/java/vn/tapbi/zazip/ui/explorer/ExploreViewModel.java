package vn.tapbi.zazip.ui.explorer;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import vn.tapbi.zazip.common.LiveEvent;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.ui.base.BaseViewModel;
import vn.tapbi.zazip.ui.dialog.MyProgressDialog;

@HiltViewModel
public class ExploreViewModel extends BaseViewModel {
    private FileDataRepository fileDataRepository;
    public LiveEvent<List<FileData>> listFileIInDevice = new LiveEvent<>();
    public MutableLiveData<List<FileData>> listSearch = new MutableLiveData<>();
    private Disposable currentDisposable = null;

    @Inject
    public ExploreViewModel(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    public void searchFile(Context context, String text) {
        fileDataRepository.searchFile(context, text).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (currentDisposable != null) {
                    currentDisposable.dispose();
                    compositeDisposable.clear();
                }
                currentDisposable = d;
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<FileData> fileData) {
                listSearch.postValue(fileData);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getFileInDevice(String actualPath, String filePath, Context context) {
        fileDataRepository.getFileInDevice(actualPath, filePath, context, false).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (currentDisposable != null) {
                    currentDisposable.dispose();
                    compositeDisposable.clear();
                }
                currentDisposable = d;
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<FileData> fileData) {
                currentDisposable = null;
                listFileIInDevice.setValue(fileData);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    public boolean cancelDisposable() {
        if (currentDisposable != null) {
            currentDisposable.dispose();
            compositeDisposable.clear();
            currentDisposable = null;
            return true;
        }
        return false;
    }

}
