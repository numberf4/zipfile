package vn.tapbi.zazip.ui.archive;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.ui.base.BaseViewModel;

@HiltViewModel
public class ArchiveViewModel extends BaseViewModel {
    public FileDataRepository fileDataRepository;

    @Inject
    public ArchiveViewModel(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

}
