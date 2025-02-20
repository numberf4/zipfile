package vn.tapbi.zazip.ui.base;

import static vn.tapbi.zazip.common.Constant.CHECK_LOGIN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.clientIdentifier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.models.EventSortView;
import vn.tapbi.zazip.databinding.PopupSortViewBinding;
import vn.tapbi.zazip.interfaces.ClickPresentationView;
import vn.tapbi.zazip.interfaces.ClickSortViewListener;
import vn.tapbi.zazip.model.PresentationOption;
import vn.tapbi.zazip.ui.adapter.PresentationOptionViewAdapter;
import vn.tapbi.zazip.ui.adapter.SortViewAdapter;
import vn.tapbi.zazip.ui.main.MainViewModel;
import vn.tapbi.zazip.utils.MySharePreferences;

public abstract class BaseBindingFragment<B extends ViewDataBinding, T extends BaseViewModel> extends BaseFragment {

    public B binding;
    public T viewModel;
    public MainViewModel mainViewModel;
    private boolean loaded = false;
   // public String APP_KEY = "xs8l6c6mulmh0v5";
    public String APP_KEY = "u0stisbdaaxmd4d";
    private PopupWindow popupWindow;
    private int positionPresentationView = -1;
    private int positionSortView = -1, tempTypePresentationView = 0, tempTypeSortView = 0;
    private int screen;
    private boolean stateSortDesc, tempStateSortDesc;
    private PresentationOptionViewAdapter optionViewAdapter;
    private SortViewAdapter sortViewAdapter;
    private List<PresentationOption> list = new LinkedList<>();
    private List<PresentationOption> listSort = new LinkedList<>();


    protected abstract Class<T> getViewModel();

    public abstract int getLayoutId();


    protected abstract void onCreatedView(View view, Bundle savedInstanceState);

    protected abstract void onPermissionGranted();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!loaded) {
            binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(getViewModel());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        getListOption();
        if (!needToKeepView()) {
            onCreatedView(view, savedInstanceState);
        } else {
            if (!loaded) {
                onCreatedView(view, savedInstanceState);
                loaded = true;
            }
        }
    }


    public void loginDropBox() {
        if (App.getInstance().goToWifiSettingsIfDisconnected()) {
        } else {
            List<String> stringList = new ArrayList<>();
            stringList.add("files.metadata.read");//add permission for drop box
            stringList.add("account_info.read");
            stringList.add("sharing.write");
            stringList.add("sharing.read");
            stringList.add("files.content.read");
            DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier);
            Auth.startOAuth2PKCE(requireContext(), APP_KEY, requestConfig, stringList);
            MySharePreferences.putBoolean(CHECK_LOGIN_DROP_BOX, false, requireContext());
        }
    }
    public static DbxRequestConfig getDbxConfig() {
        return DbxRequestConfig
                .newBuilder(clientIdentifier)
                .withUserLocale(Locale.getDefault().toString())
                .build();
    }
    private void getListOption() {
        mainViewModel.fileDataRepository.getListPresentationViews(requireContext()).subscribe(new SingleObserver<List<PresentationOption>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mainViewModel.compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<PresentationOption> presentationOptions) {
                list.clear();
                list.addAll(presentationOptions);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
            }
        });
        mainViewModel.fileDataRepository.getListOptionSortView(requireContext()).subscribe(new SingleObserver<List<PresentationOption>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mainViewModel.compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<PresentationOption> presentationOptions) {
                listSort.clear();
                listSort.addAll(presentationOptions);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
            }
        });
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public void setIdPresentationViewInBase(int id) {
        this.tempTypePresentationView = id;
        if (optionViewAdapter != null) {
            optionViewAdapter.setId(id);
        }
    }

    public void setIdSortViewInBase(int idSortView) {
        this.tempTypeSortView = idSortView;
        if (sortViewAdapter != null) {
            sortViewAdapter.setId(idSortView);
        }
    }

    public void setSortDescInBase(boolean sortDesc) {
        this.tempStateSortDesc = sortDesc;
        if (sortViewAdapter != null) {
            sortViewAdapter.setSortDesc(sortDesc);
        }
    }

    public void setIdSortDescInBase(int idSortDesc) {
        if (sortViewAdapter != null) {
            sortViewAdapter.setIdDesc(idSortDesc);
        }
    }

    public void performEventPopup() {
        if (positionPresentationView != -1) {
            mainViewModel.positionPresentationView.setValue(new EventSortView(positionPresentationView, screen));
        } else {
            setIdPresentationViewInBase(tempTypePresentationView);
        }
        if (positionSortView != -1) {
            mainViewModel.eventSortView.setValue(new EventSortView(positionSortView, stateSortDesc));
        } else {
            setIdSortViewInBase(tempTypeSortView);
            setSortDescInBase(tempStateSortDesc);
            if (tempStateSortDesc) setIdSortDescInBase(4);
        }
    }

    protected boolean isViewInBounds(View view, int x, int y) {
        Rect outRect = new Rect();
        int[] location = new int[2];
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    @SuppressLint("ClickableViewAccessibility")
    public PopupWindow createPopUpSortView() {
        optionViewAdapter = new PresentationOptionViewAdapter(requireContext());
        if (list != null) optionViewAdapter.setListOption(list);
        optionViewAdapter.setCLickItemListener(new ClickPresentationView() {
            @Override
            public void onClickItemPresentation(int position) {
                positionPresentationView = position;

            }
        });
        sortViewAdapter = new SortViewAdapter(requireContext());
        if (listSort != null) sortViewAdapter.setListSort(listSort);
        sortViewAdapter.setClickSortViewListener(new ClickSortViewListener() {
            @Override
            public void onClickItemSort(int position) {
                positionSortView = position;
            }

            @Override
            public void onClickItemSortDesc(boolean sortDesc) {
                stateSortDesc = sortDesc;
            }
        });
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupSortViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.popup_sort_view, null, false);

        binding.rcvPresentationView.setAdapter(optionViewAdapter);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(requireContext());
        layoutManager1.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcvPresentationView.setLayoutManager(layoutManager1);

        binding.rcvSortView.setAdapter(sortViewAdapter);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext());
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcvSortView.setLayoutManager(layoutManager2);

        popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(20f);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        binding.ivCloseSortView.setOnClickListener(v -> {
            popupWindow.dismiss();
            mainViewModel.checkDoubleClick.postValue(true);
        });
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (!isViewInBounds(v, x, y)) {
                    performEventPopup();
                    popupWindow.dismiss();
                    mainViewModel.checkDoubleClick.postValue(true);
                    return true;
                }
                return false;
            }
        });
        return popupWindow;
    }

    public boolean needToKeepView() {
        return false;
    }
}
