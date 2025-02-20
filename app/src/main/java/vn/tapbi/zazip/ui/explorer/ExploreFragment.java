package vn.tapbi.zazip.ui.explorer;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.Q;
import static vn.tapbi.zazip.common.Constant.KEY_TEXT_SEARCH;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPath;
import vn.tapbi.zazip.common.models.EventRename;
import vn.tapbi.zazip.common.models.EventSortView;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.FragmentExplorerBinding;
import vn.tapbi.zazip.ui.adapter.DirectoryAdapter;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class ExploreFragment extends BaseBindingFragment<FragmentExplorerBinding, ExploreViewModel> implements FileDataAdapter.OnItemClickedListener, DirectoryAdapter.OnClickPathDirectory, View.OnClickListener {
    @Inject
    public FileDataRepository fileDataRepository;
    private FileDataAdapter fileDataAdapter;
    private DirectoryAdapter directoryAdapter;
    private boolean isSearching;
    private boolean isLoading;
    private String querySearch;
    private int queryLength;
    private String fileScroll = "";
    private int typePresentationView = 0, typeSortView = 0;
    private boolean stateSortDesc;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    private final ArrayList<String> paths = new ArrayList<>();
    private List<FileData> list = new ArrayList<>();
    private boolean isShowDetail = false;
    private String textSearch = "";
    private String rootPath;
    private String actualPathCurrent, filePathCurrent;
    private boolean isClickFolderCurrent;

    @Override
    protected Class<ExploreViewModel> getViewModel() {
        return ExploreViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_explorer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (bundle == null) {
            paths.add(rootPath);
        } else {
            String path = bundle.getString(Constant.KEY_PATH);
            initPaths(path);
        }
    }

    private void initPaths(String path) {
        if (path.equals(rootPath)) {
            paths.add(rootPath);
        } else {
            File file = new File(path);
            String pathFinal;
            if (file.isDirectory()) {
                pathFinal = path;
            } else {
                pathFinal = path.substring(0, path.lastIndexOf(File.separator) + 1);
                fileScroll = path.substring(path.lastIndexOf(File.separator) + 1);
            }
            setupPaths(pathFinal, rootPath);
        }
    }

    private void setupPaths(String pathFinal, String rootPath) {
        String temp = pathFinal;
        if (temp != null && !temp.isEmpty() && rootPath != null && !rootPath.isEmpty()) {
            if (temp.contains(rootPath)) {
                while (!temp.equals(rootPath)) {
                    paths.add(0, temp);
                    temp = temp.substring(0, Math.max(temp.lastIndexOf(File.separator),1));
                }
            }
            paths.add(0, rootPath);
        }
    }

    @Override
    public void onStop() {
        querySearch = binding.layoutHeaderExplore.searchExplorer.getQuery().toString();
        queryLength = binding.layoutHeaderExplore.searchExplorer.getQuery().length();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (querySearch != null || queryLength != 0) {
            isSearching = true;
        } else isSearching = false;
        outState.putParcelableArrayList(Constant.SAVE_LIST_EXPLORE, (ArrayList<? extends Parcelable>) list);
        outState.putBoolean(Constant.SAVE_BOOLEAN_SEARCH_EXPLORE, isSearching);
        outState.putString(KEY_TEXT_SEARCH, textSearch);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SEARCH_EXPLORE);
            list = savedInstanceState.getParcelableArrayList(Constant.SAVE_LIST_EXPLORE);
            textSearch = savedInstanceState.getString(KEY_TEXT_SEARCH);
            if (textSearch.length() > 0) {
                binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
                App.getInstance().checkShowSearch = false;
            } else {
                App.getInstance().checkShowSearch = true;
            }
        }
        if (App.getInstance().checkShowSearch) {
            textSearch = "";
        }
        setupView();
        eventClick();
        listener();
        observerData();
        eventBack();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_DELETE_ALl_ITEM) {
            fileDataAdapter.resetAllSelect();
        }else if (event.getType() == Constant.EVENT_DELETE_ITEM) {
            fileDataAdapter.resetList(event.getPath());
        }else if(event.getType() == Constant.EVENT_RELOAD_FILE_DATA){
            if(actualPathCurrent!=null && filePathCurrent != null){
                folderClick(actualPathCurrent, filePathCurrent, isClickFolderCurrent);
            }
        }
    }

    @Override
    public void onResume() {
        if (App.getInstance().checkShowSearch) {
            textSearch = "";
            App.getInstance().checkShowSearch = false;
        }
        super.onResume();
        Utils.resetLastClickTime();
        if (!Utils.checkPermissions(requireActivity())) {
            binding.progressExplorer.setVisibility(View.GONE);
            binding.groupPermission.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            binding.rcvDirectory.setVisibility(View.INVISIBLE);
            binding.recyclerExplorer.setVisibility(View.INVISIBLE);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
        } else {
            if (((MainActivity)requireActivity()).isShowDialogRequestPermission() && ((MainActivity)requireActivity()).requestPermissionDialog != null){
                ((MainActivity)requireActivity()).requestPermissionDialog.dismiss();
            }
            if (binding.layoutHeaderExplore.searchExplorer.getQuery().toString().isEmpty()) {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
            } else {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            }
            binding.rcvDirectory.setVisibility(View.VISIBLE);
            binding.recyclerExplorer.setVisibility(View.VISIBLE);
            binding.tvMessage.setVisibility(View.GONE);
            binding.groupPermission.setVisibility(View.GONE);
            mainViewModel.allowPaste.setValue(true);
            binding.recyclerExplorer.scrollToPosition(0);
            if (textSearch.length() > 0) {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.GONE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.GONE);
                viewModel.searchFile(requireContext(), textSearch);
            } else {
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
                binding.layoutHeaderExplore.ivSortView.setEnabled(true);
                reloadData();
            }
            if (((MainActivity) requireActivity()).selectList.size() > 0) {
                fileDataAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
            }
        }
    }

    @Override
    public void onDestroyView() {
        textSearch = "";
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (viewModel.cancelDisposable() && paths.size() > 1) {
            removePath();
            directoryAdapter.setPath(paths.get(paths.size() - 1));
        }
        App.getInstance().checkShowSearch = true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    private void setupView() {
        setScreen(Constant.SCREEN_EXPLORE);
        sharedPreferences = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        typePresentationView = sharedPreferences.getInt(Constant.TYPE_PRESENTATION_VIEW_EXPLORE, 0);
        typeSortView = sharedPreferences.getInt(Constant.TYPE_OPTION_VIEW_EXPLORE, 0);
        stateSortDesc = sharedPreferences.getBoolean(Constant.TYPE_DESC_VIEW_EXPLORE, false);
        mainViewModel.pathPaste.postValue(new EventPath(paths.get(paths.size() - 1)));
        binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.file_explorer));
        ((MainActivity) requireActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        fileDataAdapter = new FileDataAdapter(Constant.TYPE_MULTI);
        fileDataAdapter.setFileDataRepository(fileDataRepository);
        fileDataAdapter.setOnItemClickedListener(this);
        setUpSortView(typePresentationView);

        binding.fastScroll.attachRecyclerView(binding.recyclerExplorer);
        binding.fastScroll.setHandlePressedColor(getResources().getColor(R.color.scroll_bar));
        binding.fastScroll.attachAdapter(fileDataAdapter);
        binding.fastScroll.setOnHandleTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.recyclerExplorer.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            binding.recyclerExplorer.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        directoryAdapter = new DirectoryAdapter(requireContext());
        directoryAdapter.setOnClickPathDirectory(this);
        directoryAdapter.setPath(paths.get(paths.size() - 1));
        LinearLayoutManager layoutManagerDirectory = new LinearLayoutManager(requireContext());
        layoutManagerDirectory.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcvDirectory.setLayoutManager(layoutManagerDirectory);
        binding.rcvDirectory.setAdapter(directoryAdapter);

        EditText searchEditText = binding.layoutHeaderExplore.searchExplorer.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(requireContext().getResources().getColor(R.color.text_black));
        searchEditText.setHintTextColor(requireContext().getResources().getColor(R.color.text_gray));
    }

    private void setUpSortView(int typePresentation) {
        fileDataAdapter.setTypePresentationView(typePresentation);
        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerExplorer.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_4);
            binding.recyclerExplorer.setLayoutManager(layoutManager);
        }
        binding.recyclerExplorer.setAdapter(fileDataAdapter);
    }

    private void setUpSortType(int typeSortView, boolean sortDesc) {
        fileDataAdapter.setTypeSortView(typeSortView, sortDesc, "");
    }

    private void eventClick() {
        binding.layoutHeaderExplore.imgSearchExplorer.setOnClickListener(v -> {
            showSearchView();
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
        });
        binding.layoutHeaderExplore.tvExplorerCancel.setOnClickListener(v -> {
            hideSearchView();
            setListData(View.VISIBLE, list);
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
        });

        binding.layoutHeaderExplore.imgExplorerBack.setOnClickListener(v -> {
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
            ((MainActivity) requireActivity()).popBackStack();
            Utils.hideInputMethod(binding.getRoot(), requireContext());
        });

        binding.layoutHeaderExplore.ivSortView.setOnClickListener(v -> {
            if (Utils.checkTime()) {
                binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
                binding.layoutHeaderExplore.imgExplorerBack.setEnabled(false);
                popupWindow = createPopUpSortView();
                popupWindow.showAsDropDown(v, 0, -50, Gravity.END);
                setIdPresentationViewInBase(typePresentationView);
                setIdSortViewInBase(typeSortView);
                setSortDescInBase(stateSortDesc);
                if (stateSortDesc) setIdSortDescInBase(4);
            }
        });

        binding.tvDoc.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_DOC, Constant.SCREEN_DOC);
            ((MainActivity) requireActivity()).changeScreenTypeFile(bundle);
        });
        binding.tvMusic.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_DOC, Constant.SCREEN_MUSIC);
            ((MainActivity) requireActivity()).changeScreenTypeFile(bundle);
        });
        binding.tvDownload.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_DOC, Constant.SCREEN_DOWNLOAD);
            ((MainActivity) requireActivity()).changeScreenTypeFile(bundle);
        });
        binding.tvApk.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_DOC, Constant.SCREEN_APK);
            ((MainActivity) requireActivity()).changeScreenTypeFile(bundle);
        });

        binding.tvPhoto.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_PHOTO, Constant.SCREEN_PHOTO);
            ((MainActivity) requireActivity()).changeScreenTypeMedia(bundle);
        });
        binding.tvVideo.setOnClickListener(v -> {
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.TYPE_PHOTO, Constant.SCREEN_VIDEO);
            ((MainActivity) requireActivity()).changeScreenTypeMedia(bundle);
        });
    }

    private void setListData(int visible, List<FileData> list) {
        binding.progressExplorer.setVisibility(View.GONE);
        binding.rcvDirectory.setVisibility(visible);
        fileDataAdapter.setList(list);
        binding.recyclerExplorer.scrollToPosition(0);
        showTextMessage(list);
    }

    private void hideSearchView() {
        binding.layoutHeaderExplore.searchExplorer.setVisibility(View.GONE);
        binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.GONE);
        binding.layoutHeaderExplore.tvHeader.setVisibility(View.VISIBLE);
        binding.layoutHeaderExplore.ivSortView.setVisibility(View.VISIBLE);
        binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.VISIBLE);
        binding.layoutHeaderExplore.searchExplorer.onActionViewCollapsed();
    }

    private void showSearchView() {
        binding.layoutHeaderExplore.tvHeader.setVisibility(View.INVISIBLE);
        binding.layoutHeaderExplore.ivSortView.setVisibility(View.GONE);
        binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.GONE);
        binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
        binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
        binding.layoutHeaderExplore.searchExplorer.performClick();
        binding.layoutHeaderExplore.searchExplorer.onActionViewExpanded();
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
                textSearch = newText;
                if (binding.layoutHeaderExplore.searchExplorer.getVisibility() == View.VISIBLE) {
                    if (!textSearch.isEmpty()) {
                        binding.rcvDirectory.setVisibility(View.GONE);
                    }
                    fileDataAdapter.setList(new ArrayList<>());
                    binding.progressExplorer.setVisibility(View.VISIBLE);
                    binding.tvMessage.setVisibility(View.GONE);
                    viewModel.searchFile(requireActivity(), newText);
                }

                return true;
            }
        });
        binding.viewExplorer.setOnClickListener(v -> mainViewModel.changStateShow());
        binding.tvNeedPermission.setOnClickListener(this);
        binding.tvGotoSetting.setOnClickListener(this);
    }

    private void eventBack() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isShowDetail) {
                    if (paths.size() > 1) {
                        paths.remove(paths.size() - 1);
                        directoryAdapter.setPath(paths.get(paths.size() - 1));
                        reloadData();
                    } else {
                        ((MainActivity) requireActivity()).popBackStack();
                    }
                } else {
                    mainViewModel.changStateShow();
                }
            }
        });
    }

    private void observerData() {
        mainViewModel.checkDoubleClick.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
                    binding.layoutHeaderExplore.imgExplorerBack.setEnabled(true);
                }
            }
        });
        mainViewModel.eventSortView.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof EventSortView) {
                if ((typeSortView != ((EventSortView) data).getPositionSortView() && ((EventSortView) data).getPositionSortView() != -1)
                        || (stateSortDesc != ((EventSortView) data).isSortDesc())) {
                    stateSortDesc = ((EventSortView) data).isSortDesc();
                    typeSortView = ((EventSortView) data).getPositionSortView();
                    sharedPreferences.edit().putInt(Constant.TYPE_OPTION_VIEW_EXPLORE, typeSortView).apply();
                    sharedPreferences.edit().putBoolean(Constant.TYPE_DESC_VIEW_EXPLORE, stateSortDesc).apply();
                    setUpViewInBase();
                    setUpSortType(typeSortView, stateSortDesc);
                }
            }
        });

        mainViewModel.positionPresentationView.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getScreen() == Constant.SCREEN_EXPLORE && data.getPositionPresentationView() != -1) {
                    typePresentationView = data.getPositionPresentationView();
                    sharedPreferences.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_EXPLORE, typePresentationView).apply();
                    setIdSortViewInBase(typePresentationView);
                    setUpSortView(typePresentationView);
                }
            }
        });

        mainViewModel.reload.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                reloadData();
            }
        });
        mainViewModel.reloadExplore.observe(getViewLifecycleOwner(), data -> {
            if (data) reloadData();
        });
        mainViewModel.isShowDetailSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                isShowDetail = true;
                binding.viewExplorer.setClickable(true);
                binding.viewExplorer.setVisibility(View.VISIBLE);
            } else {
                isShowDetail = false;
                binding.viewExplorer.setVisibility(View.GONE);
            }
        });

        viewModel.listFileIInDevice.observe(getViewLifecycleOwner(), fileData -> {
            binding.progressExplorer.setVisibility(View.GONE);
            setListToAdapter((List<FileData>) fileData);
        });

        mainViewModel.renameSuccess.observe(getViewLifecycleOwner(), this::renameInList);
        mainViewModel.haveFileSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.recyclerExplorer.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(110));
            } else {
                binding.recyclerExplorer.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(12));
            }
        });

        viewModel.listSearch.observe(getViewLifecycleOwner(), fileData -> {
            if (binding.layoutHeaderExplore.searchExplorer.getVisibility() == View.VISIBLE) {
                if (textSearch.isEmpty()) {
                    setListData(View.VISIBLE, list);
                } else {
                    setListData(View.GONE, fileData);
                }
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
    }

    private void renameInList(EventRename data) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFilePath() != null && list.get(i).getFilePath().equals(data.getOldPath())) {
                list.get(i).setFilePath(data.getNewPath());
                list.get(i).setFileName(new File(data.getNewPath()).getName());
                break;
            }
        }
        fileDataAdapter.resetAllSelect();
        fileDataAdapter.setList(list);
        setUpSortType(typeSortView, stateSortDesc);
    }

    private void reloadData() {
        isLoading = true;
        onFolderDeviceClick(paths.get(paths.size() - 1), false);
    }

    private void setListToAdapter(List<FileData> dataList) {
        list.clear();
        list.addAll(dataList);
        fileDataAdapter.setOnlyList(list);
        fileDataAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
        setUpSortType(typeSortView, stateSortDesc);
        isLoading = false;
        ((MainActivity) requireActivity()).startWatchingFile(paths.get(paths.size() - 1));
        scrollToFileCompress();
        showTextMessage(list);
    }

    private void showTextMessage(List<FileData> fileData) {
        if (fileData.size() > 0) {
            enabledViewSortSearch(true);
            binding.tvMessage.setVisibility(View.GONE);
        } else {
            enabledViewSortSearch(false);
            binding.tvMessage.setVisibility(View.VISIBLE);
        }
    }

    private void enabledViewSortSearch(boolean enabled) {
        if (enabled) {
            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
//            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
        } else {
//            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
//            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
        }
    }

    private void scrollToFileCompress() {
        if (!fileScroll.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (fileScroll.equals(list.get(i).getFileName())) {
                    binding.recyclerExplorer.scrollToPosition(i);
                    fileScroll = "";
                    break;
                }
            }
        }
    }

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    public void onItemClick(int position, FileData dataItem) {
        if (isLoading) {
            return;
        }

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

    private final ActivityResultLauncher<Intent> handleDocumentUriForRestrictedDirectories = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (SDK_INT >= Q && result.getData() != null) {
                    requireActivity().getContentResolver().takePersistableUriPermission(
                            result.getData().getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            });


    @Override
    public void onFolderDeviceClick(String filePath, boolean isClickFolder) {
        binding.rcvDirectory.setVisibility(View.VISIBLE);
        File file = new File(filePath);
        if (file.isDirectory()) {
            String actualPath = mainViewModel.getFileProperties().remapPathForApi30OrAbove(filePath, false);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q && !filePath.equals(actualPath)) {
                boolean hasAccessToSpecialFolder = mainViewModel.getFileProperties().hasAccessToSpecialFolder(requireActivity(), actualPath);
                if (!hasAccessToSpecialFolder) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).putExtra(
                            DocumentsContract.EXTRA_INITIAL_URI,
                            Uri.parse(mainViewModel.getFileProperties().remapPathForApi30OrAbove(filePath, true)));
                    handleDocumentUriForRestrictedDirectories.launch(intent);
                } else {
                    folderClick(actualPath, filePath, isClickFolder);
                }
            } else {
                folderClick(actualPath, filePath, isClickFolder);
            }
            actualPathCurrent = actualPath;
            filePathCurrent = filePath;
            isClickFolderCurrent = isClickFolder;
        } else {
            if (Utils.checkTime()) {
                binding.layoutHeaderExplore.imgExplorerBack.setEnabled(false);
                Utils.openFile(filePath, requireActivity());
            }
            if (!fileDataRepository.isArchiveFile(filePath)) {
                mainViewModel.insertRecent(filePath);
            }
        }

    }

    @Override
    public void clickFolderCloud(int position, String path) {

    }

    private void folderClick(String actualPath, String filePath, boolean isClickFolder) {
        binding.progressExplorer.setVisibility(View.VISIBLE);
        if (!textSearch.isEmpty()) {
            paths.clear();
            initPaths(filePath);
        } else if (isClickFolder) {
            paths.add(filePath);
        }
        if (!filePath.isEmpty()) {
            if (isClickFolder) {
                binding.recyclerExplorer.scrollToPosition(0);
            }
            mainViewModel.pathPaste.postValue(new EventPath(filePath));
            directoryAdapter.setPath(filePath);
            binding.layoutHeaderExplore.searchExplorer.setQuery("", true);
            hideSearchView();
            viewModel.getFileInDevice(actualPath, filePath, requireActivity().getApplicationContext());
            binding.rcvDirectory.scrollToPosition(directoryAdapter.getItemCount() - 1);
        }

    }

    @Override
    public void onclickPath(String path2) {
        updatePaths(path2);
        onFolderDeviceClick(path2, false);
    }

    private void updatePaths(String path) {
        for (int i = paths.size() - 1; i >= 0; i--) {
            if (!paths.get(i).equals(path)) {
                paths.remove(i);
            } else {
                break;
            }
        }
    }

    private void removePath() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.tvNeedPermission || v == binding.tvGotoSetting) {
            mainViewModel.gotoSetting.postValue(true);
        }
    }

}