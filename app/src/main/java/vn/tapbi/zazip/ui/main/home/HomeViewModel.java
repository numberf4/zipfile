package vn.tapbi.zazip.ui.main.home;


import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.data.repository.RecentRepository;
import vn.tapbi.zazip.ui.base.BaseViewModel;

@HiltViewModel
public class HomeViewModel extends BaseViewModel {
    private FileDataRepository fileDataRepository;
    private final RecentRepository recentRepository;
    public MutableLiveData<List<FileData>> recent = new MutableLiveData<>();


    @Inject
    public HomeViewModel(FileDataRepository fileDataRepository, RecentRepository recentRepository) {
        this.recentRepository = recentRepository;
        this.fileDataRepository = fileDataRepository;
    }

}
