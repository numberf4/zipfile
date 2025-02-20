package vn.tapbi.zazip.ui.archive;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
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
import java.util.LinkedList;
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
import vn.tapbi.zazip.databinding.FragmentArchiveBinding;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class ArchiveFragment extends BaseBindingFragment<FragmentArchiveBinding, ArchiveViewModel> implements FileDataAdapter.OnItemClickedListener, View.OnClickListener {
    @Inject
    public FileDataRepository fileDataRepository;
    private FileDataAdapter fileDataAdapter;
    private final List<FileData> list = new LinkedList<>();
    private boolean isShowDetail = false;
    private boolean isShowDialog;
    private int typePresentationView = 0, typeSortView = 0;
    private boolean stateSortDesc;
    private PopupWindow popupWindow;
    private String textSearch = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected Class<ArchiveViewModel> getViewModel() {
        return ArchiveViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_archive;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_DELETE_ALl_ITEM) {
            Timber.e("Number4, onMessageReceived all");
            fileDataAdapter.resetAllSelect();
        }else if (event.getType() == Constant.EVENT_DELETE_ITEM) {
            Timber.e("Number4, onMessageReceived");
            fileDataAdapter.resetList(event.getPath());
        }else if(event.getType() == Constant.EVENT_RELOAD_FILE_DATA){
            mainViewModel.getDocFile(requireContext());
        }
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setupView();
        eventClick();
        observerData();
        eventSearch();
        eventBack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.resetLastClickTime();
        if (!Utils.checkPermissions(requireActivity())) {
            binding.progressArchive.setVisibility(View.GONE);
            binding.groupPermission.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            binding.recyclerArchive.setVisibility(View.INVISIBLE);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
        } else {
            if (((MainActivity)requireActivity()).isShowDialogRequestPermission() && ((MainActivity)requireActivity()).requestPermissionDialog != null){
                ((MainActivity)requireActivity()).requestPermissionDialog.dismiss();
            }
            if (binding.layoutHeaderExplore.searchExplorer.getQuery().toString().isEmpty()) {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
                reloadData(true);
            } else {
                fileDataAdapter.filter(binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            }
            binding.recyclerArchive.setVisibility(View.VISIBLE);
            binding.groupPermission.setVisibility(View.GONE);
            mainViewModel.allowPaste.setValue(false);
        }

        if (textSearch.length() > 0) {
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
        } else {
            if (list.size() > 0)
                binding.layoutHeaderExplore.ivSortView.setEnabled(true);
        }

    }

    private void setupView() {
        setScreen(Constant.SCREEN_ARCHIVE);
        sharedPreferences = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        typePresentationView = sharedPreferences.getInt(Constant.TYPE_PRESENTATION_VIEW_ARCHIVER, 0);
        typeSortView = sharedPreferences.getInt(Constant.TYPE_OPTION_VIEW_ARCHIVER, 0);
        stateSortDesc = sharedPreferences.getBoolean(Constant.TYPE_DESC_VIEW_ARCHIVER, false);
        fileDataAdapter = new FileDataAdapter(Constant.TYPE_MULTI);
        fileDataAdapter.setFileDataRepository(fileDataRepository);
        fileDataAdapter.setOnItemClickedListener(this);
        setUpSortView(typePresentationView);
        binding.fastScroll.attachRecyclerView(binding.recyclerArchive);
        binding.fastScroll.setHandlePressedColor(getResources().getColor(R.color.scroll_bar));
        binding.fastScroll.attachAdapter(fileDataAdapter);
        binding.fastScroll.setOnHandleTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.recyclerArchive.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            binding.recyclerArchive.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.archive_files));
        EditText searchEditText = binding.layoutHeaderExplore.searchExplorer.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(requireContext().getResources().getColor(R.color.text_black));
        searchEditText.setHintTextColor(requireContext().getResources().getColor(R.color.text_black));

        binding.tvGotoSetting.setOnClickListener(this);
        binding.tvNeedPermission.setOnClickListener(this);
    }

    private void eventClick() {
        binding.layoutHeaderExplore.ivSortView.setOnClickListener(v -> {
            if (Utils.checkTime()) {
                binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
                popupWindow = createPopUpSortView();
                popupWindow.showAsDropDown(v, 0, -50, Gravity.END);
                setIdPresentationViewInBase(typePresentationView);
                setIdSortViewInBase(typeSortView);
                setSortDescInBase(stateSortDesc);
                if (stateSortDesc) setIdSortDescInBase(4);
            }
        });
        binding.layoutHeaderExplore.imgExplorerBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).popBackStack();
            Utils.hideInputMethod(binding.getRoot(), requireContext());
        });

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
            binding.recyclerArchive.scrollToPosition(0);
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvHeader.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.onActionViewCollapsed();
        });
        binding.viewArchive.setOnClickListener(v -> mainViewModel.changStateShow());
    }

    private void eventSearch() {
        binding.layoutHeaderExplore.searchExplorer.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) binding.layoutHeaderExplore.searchExplorer.onActionViewCollapsed();
        });
        binding.layoutHeaderExplore.searchExplorer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utils.hideInputMethod(binding.getRoot(), requireContext());
                return true;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    binding.recyclerArchive.scrollToPosition(0);
                }
                fileDataAdapter.filter(newText.trim());
                textSearch = newText;
                if (fileDataAdapter.getListSearch().size() == 0) {
                    binding.tvMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMessage.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void eventBack() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (!isShowDetail && !isShowDialog) {
                    ((MainActivity) requireActivity()).popBackStack();
                } else if (isShowDetail && !isShowDialog) {
                    mainViewModel.changStateShow();
                } else {
                    mainViewModel.isShowDialog.postValue(false);
                }
                return true;
            }
            return false;
        });
    }

    private void setUpSortView(int typePresentation) {
        fileDataAdapter.setTypePresentationView(typePresentation);
        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerArchive.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_4);
            binding.recyclerArchive.setLayoutManager(layoutManager);
        }
        binding.recyclerArchive.setAdapter(fileDataAdapter);
    }

    private void setUpSortType(int typeSortView, boolean sortDesc, String filter) {
        fileDataAdapter.setTypeSortView(typeSortView, sortDesc, filter);
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observerData() {
        mainViewModel.checkDoubleClick.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
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
                    sharedPreferences.edit().putInt(Constant.TYPE_OPTION_VIEW_ARCHIVER, typeSortView).apply();
                    sharedPreferences.edit().putBoolean(Constant.TYPE_DESC_VIEW_ARCHIVER, stateSortDesc).apply();
                    setUpViewInBase();
                    setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
                }
            }
        });
        mainViewModel.positionPresentationView.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getScreen() == Constant.SCREEN_ARCHIVE && data.getPositionPresentationView() != -1) {
                    typePresentationView = data.getPositionPresentationView();
                    sharedPreferences.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_ARCHIVER, typePresentationView).apply();
                    setIdPresentationViewInBase(typePresentationView);
                    setUpSortView(typePresentationView);
                }
            }
        });
        mainViewModel.renameSuccess.observe(getViewLifecycleOwner(), this::renameInList);
        mainViewModel.isShowDialog.observe(getViewLifecycleOwner(), data -> isShowDialog = data);
        mainViewModel.isShowDetailSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                isShowDetail = true;
                binding.viewArchive.setClickable(true);
                binding.viewArchive.setVisibility(View.VISIBLE);
            } else {
                isShowDetail = false;
                binding.viewArchive.setVisibility(View.GONE);
            }
        });
        mainViewModel.isGetFile.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                setList(mainViewModel.fileDataRepository.listArchiver);
            }
        });
        mainViewModel.haveFileSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.recyclerArchive.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(110));
            } else {
                binding.recyclerArchive.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(12));
            }

        });
    }

    private void setList(List<FileData> data) {
        list.clear();
        list.addAll(data);
        showTextViewMessage();

        fileDataAdapter.setOnlyList(list);
        setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
        fileDataAdapter.setListSelect(((MainActivity) requireActivity()).selectList);

        binding.progressArchive.setVisibility(View.GONE);

        ((MainActivity) requireActivity()).startWatchingFile(list);

    }

    private void renameInList(EventRename data) {
        for (int i = list.size() - 1; i >= 0; i--) {
            String path = list.get(i).getFilePath();
            if (path != null && path.equals(data.getOldPath())) {
                if (viewModel.fileDataRepository.isArchiveFile(data.getNewPath())) {
                    list.get(i).setFilePath(data.getNewPath());
                    list.get(i).setFileName(new File(data.getNewPath()).getName());
                } else {
                    list.remove(list.get(i));
                }
                break;
            }
        }
        showTextViewMessage();
        fileDataAdapter.resetAllSelect();
        fileDataAdapter.setList(list);
        setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString().trim());
    }

    private void showTextViewMessage() {
        if (Utils.checkPermissions(requireContext())) {
            if (list.size() > 0) {
                binding.tvMessage.setVisibility(View.GONE);
                binding.layoutHeaderExplore.ivSortView.setEnabled(true);
                binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
                enabledViewSortSearch(true);
                if (binding.layoutHeaderExplore.searchExplorer.getQuery().toString().isEmpty())
                    binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
                else binding.layoutHeaderExplore.ivSortView.setVisibility(View.GONE);
            } else {
                binding.layoutHeaderExplore.ivSortView.setEnabled(false);
                binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
                binding.tvMessage.setVisibility(View.VISIBLE);
                enabledViewSortSearch(false);
            }
        } else {
            binding.tvMessage.setVisibility(View.GONE);
        }
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
    private void reloadData(Boolean data) {
        if (data) {
//            fileDataAdapter.resetAllSelect();
            mainViewModel.getDocFile(requireContext());
        }
    }

    @Override
    protected void onPermissionGranted() {
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
    public void onFolderDeviceClick(String path, boolean isClickFolder) {
        if (Utils.checkTime()) {
            Utils.openFile(path, requireContext());
        }

    }

    @Override
    public void clickFolderCloud(int position, String path) {

    }

    @Override
    public void onClick(View v) {
        if (v == binding.tvGotoSetting || v == binding.tvNeedPermission) {
            mainViewModel.gotoSetting.postValue(true);
        }
    }
}