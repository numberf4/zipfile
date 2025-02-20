package vn.tapbi.zazip.ui.main.home;

import static android.content.Context.MODE_PRIVATE;
import static vn.tapbi.zazip.utils.Utils.checkTime;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ironman.trueads.admob.interstital.InterstitialAdAdmob;
import com.ironman.trueads.admob.interstital.ShowInterstitialAdsAdmobListener;
import com.ironman.trueads.admob.nativead.NativeAdAdmob;
import com.ironman.trueads.admob.nativead.ShowNativeAdsAdmobListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import company.librate.RateDialog;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPath;
import vn.tapbi.zazip.common.models.EventRename;
import vn.tapbi.zazip.common.models.EventSortView;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.FragmentHomeBinding;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.LocaleUtils;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding, HomeViewModel> implements FileDataAdapter.OnItemClickedListener, View.OnClickListener {
    @Inject
    public FileDataRepository fileDataRepository;
    private final List<FileData> fileRecentList = new LinkedList<>();
    private FileDataAdapter fileRecentAdapter;
    private RateDialog rateDialog;
    private boolean isRate;
    private boolean isShowRate;
    private SharedPreferences sharedPrefs;
    private float ratePoint;
    private boolean isShowAll;
    private int typePresentationView = 0, typeSortView = 0;
    private boolean stateSortDesc;
    private PopupWindow popupWindow;
    private boolean isResetList = false;
    private boolean isShowRecent = false;
    private boolean isChangePosition = true;

    @Override
    protected Class<HomeViewModel> getViewModel() {
        return HomeViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_RATE, isShowRate);
        if (isShowRate) {
            outState.putFloat(Constant.SAVE_RATE_POINT, rateDialog.getRateBar().getRating());
        }
        outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_ALL, isShowAll);
        //if(binding.tvHomeShowRecent.getV)
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isResetList = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvHomeRecent.setVisibility(View.INVISIBLE);
        isShowRecent = false;
        binding.tvHomeShowRecent.setVisibility(View.GONE);
        if (isShowAll) {
//            if (fileRecentList.size() > 0) {
//                binding.tvHomeShowRecentFake.setVisibility(View.VISIBLE);
//            }
            binding.tvHomeShowRecentFake.setVisibility(View.VISIBLE);
            binding.ctlRecentFake.setVisibility(View.VISIBLE);
        } else {
            binding.ctlRecentFake.setVisibility(View.GONE);
            binding.frameLayoutAdsNative.setVisibility(View.VISIBLE);
            binding.shimerId.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isShowRate = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_RATE);
            ratePoint = savedInstanceState.getFloat(Constant.SAVE_RATE_POINT);
            isShowAll = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_ALL);
        }
        NativeAdAdmob.INSTANCE.showNativeAdAdmob((AppCompatActivity) requireActivity(), getString(R.string.id_admob_native),
                binding.frameLayoutAdsNative, 2, true, new ShowNativeAdsAdmobListener() {

                    @Override
                    public void onLoadAdsNativeAdmobCompleted() {
                        binding.frameLayoutAdsNative.setVisibility(View.VISIBLE);
                        binding.shimerId.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadAdsNativeAdmobFail() {
                        binding.frameLayoutAdsNative.setVisibility(View.GONE);
                        binding.shimerId.setVisibility(View.GONE);
                        collapsed();
                        binding.ctlRecentFake.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadAdsNativeAdmobNotShow() {
                        binding.frameLayoutAdsNative.setVisibility(View.GONE);
                        binding.shimerId.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAlreadyLoaderAdsAndShowAgain() {
                        binding.frameLayoutAdsNative.setVisibility(View.VISIBLE);
                        binding.shimerId.setVisibility(View.GONE);
                    }
                });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                binding.frameLayoutAdsNative.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (!isShowAll) {
                            expanded();
                        }
                    }
                });
                if (isShowAll) {
                    collapse(0);
                } else {
                    expand(0);
                }
            }
        });
        setupView();
        observerData();
        listener();
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_DELETE_ALl_ITEM) {
            fileRecentAdapter.resetAllSelect();
        } else if (event.getType() == Constant.EVENT_DELETE_ITEM) {
            fileRecentAdapter.resetList(event.getPath());
        }
    }

    private void setupView() {
        sharedPrefs = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        setScreen(Constant.SCREEN_HOME);

        isRate = sharedPrefs.getBoolean(Constant.KEY_IS_RATE, false);
        if (isRate) {
            binding.lnNavRate.setVisibility(View.GONE);
        }
        typePresentationView = sharedPrefs.getInt(Constant.TYPE_PRESENTATION_VIEW_HOME, 0);
        typeSortView = sharedPrefs.getInt(Constant.TYPE_OPTION_VIEW_HOME, 0);
        stateSortDesc = sharedPrefs.getBoolean(Constant.TYPE_DESC_VIEW_HOME, false);
        fileRecentAdapter = new FileDataAdapter(Constant.TYPE_MULTI);
        fileRecentAdapter.setFileDataRepository(fileDataRepository);
        fileRecentAdapter.setOnItemClickedListener(this);
        setUpSortView(typePresentationView);

        binding.fastScroll.attachRecyclerView(binding.recyclerHomeRecent);
        binding.fastScroll.setHandlePressedColor(getResources().getColor(R.color.scroll_bar));
        binding.fastScroll.attachAdapter(fileRecentAdapter);
        binding.fastScroll.setOnHandleTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.recyclerHomeRecent.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            binding.recyclerHomeRecent.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowRate) {
            if (rateDialog != null) {
                rateDialog.dismiss();
            }
            createRateDialog();
            rateDialog.getRateBar().setRating(ratePoint);
            rateDialog.show();
        }
        Utils.resetLastClickTime();
        if (!Utils.checkPermissions(requireActivity())) {
            binding.groupPermission.setVisibility(View.VISIBLE);
            binding.recyclerHomeRecent.setVisibility(View.INVISIBLE);
            binding.tvMessEmpty.setVisibility(View.GONE);
            binding.ctlRecentFake.setVisibility(View.GONE);
            expand(0);
        } else {
            if (((MainActivity) requireActivity()).isShowDialogRequestPermission() && ((MainActivity) requireActivity()).requestPermissionDialog != null) {
                ((MainActivity) requireActivity()).requestPermissionDialog.dismiss();
            }
            if (fileRecentList.size() > 0) {
                binding.tvHomeShowRecent.setVisibility(View.VISIBLE);
            } else binding.tvHomeShowRecent.setVisibility(View.GONE);
            binding.recyclerHomeRecent.setVisibility(View.VISIBLE);
            mainViewModel.allowPaste.setValue(false);
            binding.groupPermission.setVisibility(View.GONE);
        }
        if (((MainActivity) requireActivity()).selectList.size() > 0) {
            if (App.getInstance().checkShowOptions)
                ((MainActivity) requireActivity()).showOptions();
            if (((MainActivity) requireActivity()).isShowPaste) {
                ((MainActivity) requireActivity()).showOptionPaste();
            }
        }
    }

    private void createRateDialog() {
        rateDialog = new RateDialog(requireContext(), requireContext().getString(R.string.mail_feedback_zazip), false, new RateDialog.IListenerRate() {
            @Override
            public void stateRate() {
                isRate = sharedPrefs.getBoolean(Constant.KEY_IS_RATE, false);
                binding.lnNavRate.setVisibility(View.GONE);
            }

            @Override
            public void stateNotNow() {
                isShowRate = false;
            }

            @Override
            public void onChangeRateIndex(float rateIndex) {
                ratePoint = rateIndex;
            }
        });
    }

    private void setUpSortType(int typeSortView, boolean sortDesc) {
        fileRecentAdapter.setTypeSortView(typeSortView, sortDesc, "");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listener() {
        binding.ivOptionView.setOnClickListener(v -> {
            binding.imgHomeMenu.setEnabled(false);
            if (Utils.checkTime()) {
                popupWindow = createPopUpSortView();
                popupWindow.showAsDropDown(v, 0, -50, Gravity.END);
                setIdPresentationViewInBase(typePresentationView);
                setIdSortViewInBase(typeSortView);
                setSortDescInBase(stateSortDesc);
                if (stateSortDesc) setIdSortDescInBase(4);
            }
        });

        binding.viewHome.setOnClickListener(v ->
                mainViewModel.changStateShow());

        binding.lnNavRate.setOnClickListener(v -> {
            if (checkTime()) {
                if (!isRate) {
                    binding.navHomeDrawer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.navHomeDrawer.closeDrawer(GravityCompat.START, true);
                            mainViewModel.showDrawer.postValue(true);
                            isShowRate = true;
                            createRateDialog();
                            rateDialog.show();
                        }
                    }, 200);

                }
            }
        });
        binding.lnNavFeedback.setOnClickListener(v -> {
            if (checkTime()) {
                LocaleUtils.sendFeedback(requireContext());
            }
        });


        binding.lnNavPolicy.setOnClickListener(v -> {
            if (checkTime()) {
                binding.navHomeDrawer.close();
                mainViewModel.showDrawer.postValue(true);
                ((MainActivity) requireActivity()).changeExplorerScreen(R.id.policy_fragment);

            }
        });
        binding.tvHomeShowRecent.setOnClickListener(v -> {
            collapse(300);
            binding.ctlRecentFake.setVisibility(View.VISIBLE);
            binding.tvHomeShowRecent.setEnabled(false);
        });
        binding.tvHomeShowRecentFake.setOnClickListener(v -> {
            binding.ctlRecentFake.setVisibility(View.GONE);
            binding.tvHomeShowRecent.setEnabled(true);
            expand(300);
        });
        binding.rlHomeExplorer.setOnClickListener(v -> {
            if (!Utils.checkTime()) return;
            showAdsAndChangeScreen(R.id.explorerFragment);
        });
        binding.rlHomeArchive.setOnClickListener(v -> {
            if (!Utils.checkTime()) return;
            showAdsAndChangeScreen(R.id.archiveFragment);
        });
        binding.cloud.setOnClickListener(v -> {
            if (!Utils.checkTime()) return;
            showAdsAndChangeScreen(R.id.cloudFragment);
        });
        binding.imgHomeMenu.setOnClickListener(v -> {
            if (!Utils.checkTime()) return;
            binding.ivOptionView.setEnabled(false);
            binding.navHomeDrawer.open();
        });
        binding.navHomeDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                mainViewModel.showDrawer.setValue(true);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mainViewModel.showDrawer.setValue(true);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mainViewModel.showDrawer.setValue(false);
                binding.ivOptionView.setEnabled(true);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        binding.tvNeedPermission.setOnClickListener(this);
        binding.tvGotoSetting.setOnClickListener(this);
    }

    private void showAdsAndChangeScreen(int idScreen) {
        InterstitialAdAdmob.INSTANCE.showAdInterstitialAdmob(requireActivity(), new ShowInterstitialAdsAdmobListener() {
            @Override
            public void onLoadFailInterstitialAdsAdmob() {
                mainViewModel.changeScreenLive.setValue(idScreen);
            }

            @Override
            public void onInterstitialAdsAdmobClose() {
                mainViewModel.changeScreenLive.setValue(idScreen);
            }

            @Override
            public void onInterstitialAdsNotShow() {
                mainViewModel.changeScreenLive.setValue(idScreen);
            }
        });
    }

    private void expand(long time) {
        isShowAll = false;
        if (fileRecentAdapter != null) fileRecentAdapter.changeShowAll(isShowAll);
        binding.cslExpandView.post(() -> binding.cslExpandView.setVisibility(View.VISIBLE));
        ValueAnimator valueAnimator = ValueAnimator.ofInt(Utils.dpToPx(20), binding.cslExpandView.getHeight() + Utils.dpToPx(20));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.tvHomeRecent.getLayoutParams();
                params.topMargin = value;
                binding.tvHomeRecent.setLayoutParams(params);
            }


        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isShowRecent) binding.tvHomeRecent.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isShowRecent = true;
                binding.tvHomeRecent.setVisibility(View.VISIBLE);
                if (fileRecentList.size() > 0) {
                    binding.tvHomeShowRecent.setVisibility(View.VISIBLE);
                    binding.tvHomeShowRecentFake.setVisibility(View.VISIBLE);
                } else {
                    if (Utils.checkPermissions(requireContext()))
                        binding.tvMessEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(time);
        valueAnimator.start();
    }

    private void collapsed() {
        if (fileRecentAdapter != null) fileRecentAdapter.changeShowAll(isShowAll);
        binding.cslExpandView.setVisibility(View.VISIBLE);
        binding.frameLayoutAdsNative.post(new Runnable() {
            @Override
            public void run() {
                if (!isShowAll) binding.ctlRecentFake.setVisibility(View.GONE);
                //  else binding.ctlRecentFake.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.tvHomeRecent.getLayoutParams();
                params.topMargin = Math.max(binding.frameLayoutAdsNative.getHeight(), Utils.dpToPx(240));
                binding.tvHomeRecent.setLayoutParams(params);
            }
        });
    }


    private void expanded() {
        if (fileRecentAdapter != null) fileRecentAdapter.changeShowAll(isShowAll);
        binding.cslExpandView.post(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.tvHomeRecent.getLayoutParams();
                params.topMargin = binding.cslExpandView.getHeight() + Utils.dpToPx(20);
                binding.tvHomeRecent.setLayoutParams(params);
            }
        });

    }

    private void collapse(long time) {
        isShowAll = true;
        if (fileRecentAdapter != null) fileRecentAdapter.changeShowAll(isShowAll);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(binding.cslExpandView.getHeight(), Utils.dpToPx(20));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.tvHomeRecent.getLayoutParams();
                params.topMargin = value;
                binding.tvHomeRecent.setLayoutParams(params);
            }
        });
        valueAnimator.setDuration(time);
        valueAnimator.start();
    }

    private void renameInList(EventRename data) {
        for (int i = 0; i < fileRecentList.size(); i++) {
            if (fileRecentList.get(i).getFilePath().equals(data.getOldPath())) {
                fileRecentList.get(i).setFilePath(data.getNewPath());
                fileRecentList.get(i).setFileName(new File(data.getNewPath()).getName());
                fileRecentAdapter.resetAllSelect();
                fileRecentAdapter.setOnlyList(fileRecentList);
                setUpSortType(typeSortView, stateSortDesc);
            }
        }
    }

    private void setUpSortView(int typePresentation) {
        fileRecentAdapter.setTypePresentationView(typePresentation);
        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerHomeRecent.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_4);
            binding.recyclerHomeRecent.setLayoutManager(layoutManager);
        }
        binding.recyclerHomeRecent.setAdapter(fileRecentAdapter);
    }

    private void observerData() {
        mainViewModel.changeScreenLive.observe(this, o -> {
            if ((Integer) o != 0) {
                ((MainActivity) requireActivity()).changeScreen((Integer) o, null);
                mainViewModel.changeScreenLive.setValue(0);
            }
        });

        mainViewModel.eventSortView.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof EventSortView) {
                if ((typeSortView != ((EventSortView) data).getPositionSortView() && ((EventSortView) data).getPositionSortView() != -1)
                        || (stateSortDesc != ((EventSortView) data).isSortDesc())) {
                    stateSortDesc = ((EventSortView) data).isSortDesc();
                    typeSortView = ((EventSortView) data).getPositionSortView();
                    sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_HOME, typeSortView).apply();
                    sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_HOME, stateSortDesc).apply();
                    setUpViewInBase();
                    setUpSortType(typeSortView, stateSortDesc);
                }
            }
        });
        mainViewModel.positionPresentationView.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getScreen() == Constant.SCREEN_HOME && data.getPositionPresentationView() != -1) {
                    typePresentationView = data.getPositionPresentationView();
                    sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_HOME, typePresentationView).apply();
                    setIdPresentationViewInBase(typePresentationView);
                    setUpSortView(typePresentationView);
                }

            }
        });

        mainViewModel.checkDoubleClick.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.imgHomeMenu.setEnabled(true);
                    mainViewModel.checkDoubleClick.postValue(false);
                }
            }
        });
        mainViewModel.haveFileSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.recyclerHomeRecent.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(110));
            } else {
                binding.recyclerHomeRecent.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(12));
            }

        });
        mainViewModel.isShowDrawerBack.observe(getViewLifecycleOwner(), data -> {
            if (data) binding.navHomeDrawer.close();
        });
        mainViewModel.renameSuccess.observe(getViewLifecycleOwner(), this::renameInList);

        mainViewModel.isShowDetailSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.viewHome.setClickable(true);
                binding.viewHome.setVisibility(View.VISIBLE);
            } else {
                binding.viewHome.setVisibility(View.GONE);
            }
        });
        mainViewModel.reload.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                fileRecentAdapter.resetAllSelect();
                if (Utils.checkPermissions(requireContext())) {
                    mainViewModel.getRecent();
                }
            }
        });

        mainViewModel.checkOpenFile.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.cloud.setEnabled(true);
                    binding.rlHomeArchive.setEnabled(true);
                    binding.rlHomeExplorer.setEnabled(true);
                    mainViewModel.checkOpenFile.postValue(false);
                }
            }
        });
        mainViewModel.isResetListHome.observe(getViewLifecycleOwner(), isResetList -> {
            if (!isResetList)
                this.isResetList = false;
        });
        mainViewModel.recent.observe(getViewLifecycleOwner(), fileData -> {
            if (fileData != null && fileRecentAdapter != null) {
                fileRecentList.clear();
                fileRecentList.addAll(fileData);

                if (fileRecentList.size() > 0) {
                    binding.ivOptionView.setVisibility(View.VISIBLE);
                    binding.tvMessEmpty.setVisibility(View.GONE);
                    if (isShowRecent) {
                        binding.tvHomeShowRecent.setVisibility(View.VISIBLE);
                        binding.tvHomeShowRecentFake.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.ivOptionView.setVisibility(View.INVISIBLE);
                    binding.tvHomeShowRecent.setVisibility(View.GONE);
                    binding.tvHomeShowRecentFake.setVisibility(View.GONE);
                    if (isShowRecent) {
                        if (Utils.checkPermissions(requireContext()))
                            binding.tvMessEmpty.setVisibility(View.VISIBLE);
                        expand(0);
                    }
                    binding.tvHomeShowRecent.setChecked(false);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            if (!isResetList) {
                                fileRecentAdapter.setOnlyList(fileRecentList);
                                fileRecentAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
                                setUpSortType(typeSortView, stateSortDesc);
                                isResetList = true;
                            }
                        }
                    }
                }, 50);

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, FileData dataItem) {
        int p = Utils.checkSelectFile(((MainActivity) requireActivity()).selectList, dataItem.getFilePath());
        if (p == -1) {
            ((MainActivity) requireActivity()).selectList.add(dataItem);
        } else {
            ((MainActivity) requireActivity()).selectList.remove(p);
        }
        mainViewModel.listSelect.postValue(((MainActivity) requireActivity()).selectList);
        if (((MainActivity) requireActivity()).selectList.size() > 0) {
            ((MainActivity) requireActivity()).showOptions();
        } else {
            App.getInstance().checkShowOptions = false;
            ((MainActivity) requireActivity()).hideOptions();
            ((MainActivity) requireActivity()).hideOnlyPaste();
        }
    }

    @Override
    public void onFolderDeviceClick(String filePath, boolean isCLi) {
        binding.cloud.setEnabled(false);
        binding.rlHomeArchive.setEnabled(false);
        binding.rlHomeExplorer.setEnabled(false);
        if (Utils.checkTime()) {
            Utils.openFile(filePath, requireActivity());
        }
    }

    @Override
    public void clickFolderCloud(int position, String path) {

    }

    @Override
    public void onClick(View v) {
        if (v == binding.tvNeedPermission || v == binding.tvGotoSetting) {
            mainViewModel.gotoSetting.postValue(true);
        }
    }
}