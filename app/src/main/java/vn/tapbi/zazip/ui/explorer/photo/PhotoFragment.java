package vn.tapbi.zazip.ui.explorer.photo;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
import vn.tapbi.zazip.databinding.FragmentPhotoBinding;
import vn.tapbi.zazip.ui.adapter.PhotoAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class PhotoFragment extends BaseBindingFragment<FragmentPhotoBinding, PhotoViewModel> implements PhotoAdapter.OnItemPhotoClickedListener, View.OnClickListener {

    @Inject
    public FileDataRepository fileDataRepository;
    private PhotoAdapter photoAdapter;
    private boolean isShowDetail;
    private boolean isShowDialog;
    private int type;
    private final List<FileData> list = new ArrayList<>();
    private SharedPreferences sharedPrefs;
    private int typePresentationView = 2, typeSortView = 1;
    private boolean stateSortDesc;
    private PopupWindow popupWindow;
    private String textSearch = "";
    private boolean isResetList = false;

    @Override
    protected Class<PhotoViewModel> getViewModel() {
        return PhotoViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setupView();
        eventClick();
        listener();
        observerData();
        eventBack();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.resetLastClickTime();
        if (!Utils.checkPermissions(requireActivity())) {
            binding.progressMedia.setVisibility(View.GONE);
            binding.groupPermission.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            binding.recyclerPhoto.setVisibility(View.INVISIBLE);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);

        } else {
            if (((MainActivity)requireActivity()).isShowDialogRequestPermission() && ((MainActivity)requireActivity()).requestPermissionDialog != null){
                ((MainActivity)requireActivity()).requestPermissionDialog.dismiss();
            }
            if (binding.layoutHeaderExplore.searchExplorer.getQuery().toString().isEmpty()) {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
//                reloadData(true);
            } else {
                photoAdapter.filter(binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            }
            binding.recyclerPhoto.setVisibility(View.VISIBLE);
            binding.groupPermission.setVisibility(View.GONE);
            if (((MainActivity) requireActivity()).selectList.size() > 0) {
                photoAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
            }
        }
        if (textSearch.length() > 0) {
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
        } else {
            if (list.size() > 0)
                binding.layoutHeaderExplore.ivSortView.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isResetList = false;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_DELETE_ALl_ITEM) {
            photoAdapter.resetAllSelect();
        } else if (event.getType() == Constant.EVENT_DELETE_ITEM) {
            photoAdapter.resetList(event.getPath());
        }
    }

    private void setUpLayoutSortView(int typePresentation) {

        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerPhoto.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_3);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ITEM) {
                        return 1;
                    } else {
                        return 3;
                    }
                }
            });
            binding.recyclerPhoto.setLayoutManager(layoutManager);
        }
        photoAdapter.setTypePresentationView(typePresentation);
        binding.recyclerPhoto.setAdapter(photoAdapter);
    }

    private void setupView() {
        sharedPrefs = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        assert getArguments() != null;
        type = getArguments().getInt(Constant.TYPE_PHOTO);
        setScreen(type);
        if (type == Constant.SCREEN_PHOTO) {
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_PHOTO, Constant.TYPE_OPTION_VIEW_PHOTO, Constant.TYPE_DESC_VIEW_PHOTO);
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.photo));
        } else if (type == Constant.SCREEN_VIDEO) {
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_VIDEO, Constant.TYPE_OPTION_VIEW_VIDEO, Constant.TYPE_DESC_VIEW_VIDEO);
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.video));
        }
        EditText searchEditText = binding.layoutHeaderExplore.searchExplorer.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(requireContext().getResources().getColor(R.color.text_black));
        searchEditText.setHintTextColor(requireContext().getResources().getColor(R.color.text_black));
        photoAdapter = new PhotoAdapter(requireContext());
        photoAdapter.setFileDataRepository(fileDataRepository);
        photoAdapter.setOnItemClickedListener(this);
        setUpLayoutSortView(typePresentationView);

        binding.fastScroll.attachRecyclerView(binding.recyclerPhoto);
        binding.fastScroll.setHandlePressedColor(getResources().getColor(R.color.scroll_bar));
        binding.fastScroll.attachAdapter(photoAdapter);
        binding.fastScroll.setOnHandleTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.recyclerPhoto.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            binding.recyclerPhoto.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        mainViewModel.allowPaste.setValue(false);

    }

    private void getTypeSortView(String typePresentation, String typeSort, String desc) {
        typePresentationView = sharedPrefs.getInt(typePresentation, 2);
        typeSortView = sharedPrefs.getInt(typeSort, 1);
        stateSortDesc = sharedPrefs.getBoolean(desc, false);
    }

    private void setTypePresentationView(int typePresentationView) {
        if (type == Constant.SCREEN_PHOTO)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_PHOTO, typePresentationView).apply();
        else if (type == Constant.SCREEN_VIDEO)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_VIDEO, typePresentationView).apply();
    }

    private void setTypeSortDesc(int typeSort, boolean desc) {
        if (type == Constant.SCREEN_PHOTO) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_PHOTO, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_PHOTO, desc).apply();
        } else if (type == Constant.SCREEN_VIDEO) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_VIDEO, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_VIDEO, desc).apply();
        }
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    private void observerData() {
        mainViewModel.checkDoubleClick.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.layoutHeaderExplore.imgExplorerBack.setEnabled(true);
                    binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
                }
            }
        });
        mainViewModel.eventSortView.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof EventSortView) {
                if ((typeSortView != ((EventSortView) data).getPositionSortView() && ((EventSortView) data).getPositionSortView() != -1)
                        || (stateSortDesc != ((EventSortView) data).isSortDesc())) {
                    stateSortDesc = ((EventSortView) data).isSortDesc();
                    typeSortView = ((EventSortView) data).getPositionSortView();
                    setTypeSortDesc(typeSortView, stateSortDesc);
                    setUpViewInBase();
                    setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
                }
                mainViewModel.eventSortView.setValue(null);
            }
        });

        mainViewModel.positionPresentationView.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getScreen() == type && data.getPositionPresentationView() != -1 && typePresentationView != data.getPositionPresentationView()) {
                    typePresentationView = data.getPositionPresentationView();
                    setTypePresentationView(typePresentationView);
                    setIdPresentationViewInBase(typePresentationView);
                    setUpLayoutSortView(typePresentationView);
                }

            }
        });
        mainViewModel.isGetVideoFile.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                if (type == Constant.SCREEN_VIDEO) {
                    setList(mainViewModel.fileDataRepository.listVideo);
                }
            }
        });
        mainViewModel.isGetPhotoFile.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                if (type == Constant.SCREEN_PHOTO) {
                    setList(mainViewModel.fileDataRepository.listPhoto);
                }
            }
        });
        mainViewModel.renameSuccess.observe(getViewLifecycleOwner(), this::renameInList);
        mainViewModel.isShowDialog.observe(getViewLifecycleOwner(), data -> isShowDialog = data);
        mainViewModel.reload.observe(getViewLifecycleOwner(), this::reloadData);
        mainViewModel.isShowDetailSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                isShowDetail = true;
                binding.viewPhoto.setClickable(true);
                binding.viewPhoto.setVisibility(View.VISIBLE);
            } else {
                isShowDetail = false;
                binding.viewPhoto.setVisibility(View.GONE);
            }
        });
        mainViewModel.haveFileSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.recyclerPhoto.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(110));
            } else {
                binding.recyclerPhoto.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(12));
            }

        });
        mainViewModel.checkOpenFile.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.layoutHeaderExplore.imgExplorerBack.setEnabled(true);
                    mainViewModel.checkOpenFile.postValue(false);
                }
            }
        });
        mainViewModel.isResetList.observe(getViewLifecycleOwner(), isResetList -> {
            if (!isResetList)
                this.isResetList = false;
        });
    }

    private void reloadData(Boolean data) {
        if (data) {
            photoAdapter.resetAllSelect();
            mainViewModel.getPhotoFile(requireContext());
            mainViewModel.getVideoFile(requireContext());
        }
    }

    private void renameInList(EventRename data) {
        for (int i = list.size() - 1; i >= 0; i--) {
            String path = list.get(i).getFilePath();
            if (path != null && path.equals(data.getOldPath())) {
                if ((type == Constant.SCREEN_PHOTO && viewModel.fileDataRepository.isPhotoFile(data.getNewPath()))
                        || (type == Constant.SCREEN_VIDEO && viewModel.fileDataRepository.isVideoFile(data.getNewPath()))) {
                    list.get(i).setFilePath(data.getNewPath());
                    list.get(i).setFileName(new File(data.getNewPath()).getName());
                } else if (checkRemoveTitle(i)) {
                    list.remove(list.get(i));
                    list.remove(list.get(i - 1));
                } else {
                    list.remove(list.get(i));
                }
                break;
            }
        }
        showTextViewMessage();
        photoAdapter.resetAllSelect();
        photoAdapter.setListAndSort(list, typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());

    }

    private boolean checkRemoveTitle(int i) {
        return (i > 0 && i < list.size() - 1 && (list.get(i - 1).getType() == 0 && list.get(i + 1).getType() == 0))
                || (i == list.size() - 1 && (list.get(i - 1).getType() == 0));
    }

    private void showTextViewMessage() {
        if (Utils.checkPermissions(requireContext())) {
            if (list.size() > 0) {
                binding.tvMessage.setVisibility(View.GONE);
            } else {
                binding.tvMessage.setVisibility(View.VISIBLE);
                binding.tvMessage.setText(type == Constant.SCREEN_PHOTO ? getString(R.string.no_photo) : getString(R.string.no_video));
            }
        } else {
            binding.tvMessage.setVisibility(View.GONE);
        }

    }

    private void setList(List<FileData> fileData) {
        if (fileData.size() > 0) {
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
            enabledViewSortSearch(true);
        } else {
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
            enabledViewSortSearch(false);
        }
        if (!isResetList) {
            list.clear();
            list.addAll(fileData);
            photoAdapter.setListAndSort(list, typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
            photoAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
            isResetList = true;
        }
        binding.progressMedia.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).startWatchingFile(list);
        showTextViewMessage();
    }

    private void enabledViewSortSearch(boolean enabled) {
        if (enabled) {
            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
        } else {
            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
        }
    }

    private void eventClick() {
        binding.layoutHeaderExplore.imgSearchExplorer.setOnClickListener(v -> {
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
            binding.layoutHeaderExplore.tvHeader.setVisibility(View.INVISIBLE);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.GONE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.GONE);
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.performClick();
            binding.layoutHeaderExplore.searchExplorer.onActionViewExpanded();
        });
        binding.layoutHeaderExplore.tvExplorerCancel.setOnClickListener(v -> {
            binding.recyclerPhoto.scrollToPosition(0);
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvHeader.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.onActionViewCollapsed();
        });
        binding.layoutHeaderExplore.imgExplorerBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).popBackStack();
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
            Utils.hideInputMethod(binding.getRoot(), requireContext());
        });
        binding.layoutHeaderExplore.ivSortView.setOnClickListener(v -> {
            if (Utils.checkTime()) {
                binding.layoutHeaderExplore.imgExplorerBack.setEnabled(false);
                binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
                popupWindow = createPopUpSortView();
                popupWindow.showAsDropDown(v, 0, -50, Gravity.END);
                setIdPresentationViewInBase(typePresentationView);
                setIdSortViewInBase(typeSortView);
                setSortDescInBase(stateSortDesc);
                if (stateSortDesc) setIdSortDescInBase(4);
            }
        });
        binding.viewPhoto.setOnClickListener(v -> mainViewModel.changStateShow());
    }

    private void setUpSortType(int typeSortView, boolean sortDesc, String filter) {
        photoAdapter.setTypeSortView(typeSortView, sortDesc, filter);
    }

    private void listener() {
        binding.layoutHeaderExplore.searchExplorer.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) binding.layoutHeaderExplore.searchExplorer.onActionViewCollapsed();
        });
        binding.layoutHeaderExplore.searchExplorer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utils.hideInputMethod(binding.getRoot(), requireContext());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    binding.recyclerPhoto.scrollToPosition(0);
                }
                photoAdapter.filter(newText.trim());
                textSearch = newText;
                if (photoAdapter.getListPhoto().size() == 0) {
                    binding.tvMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMessage.setVisibility(View.GONE);
                }
                return true;
            }
        });
        binding.tvNeedPermission.setOnClickListener(this);
        binding.tvGotoSetting.setOnClickListener(this);
    }

    private void eventBack() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                //event back
                if (!isShowDetail && !isShowDialog) {
                    ((MainActivity) requireActivity()).popBackStack();
                } else if (isShowDetail && !isShowDialog) {
                    mainViewModel.changStateShow();
                } else {
                    mainViewModel.isShowDialog.postValue(false);
                }
                Utils.hideInputMethod(binding.getRoot(), requireContext());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onItemClick(int path, FileData dataItem) {
        binding.layoutHeaderExplore.imgExplorerBack.setEnabled(false);
        Utils.openFile(dataItem.getFilePath(), requireActivity());
        //todo: add to recent file
        mainViewModel.insertRecent(dataItem.getFilePath());
    }

    @Override
    public void onItemCheckClick(int position, FileData dataItem) {
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
    public void onClick(View v) {
        if (v == binding.tvNeedPermission || v == binding.tvGotoSetting) {
            mainViewModel.gotoSetting.postValue(true);
        }
    }
}