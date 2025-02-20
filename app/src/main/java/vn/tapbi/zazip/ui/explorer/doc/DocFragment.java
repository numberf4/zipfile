package vn.tapbi.zazip.ui.explorer.doc;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
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
import vn.tapbi.zazip.databinding.FragmentDocBinding;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class DocFragment extends BaseBindingFragment<FragmentDocBinding, DocViewModel> implements FileDataAdapter.OnItemClickedListener, View.OnClickListener {
    @Inject
    public FileDataRepository fileDataRepository;
    private final List<FileData> list = new ArrayList<>();
    private int type;
    private FileDataAdapter fileDataAdapter;
    private boolean isShowDetail = false;
    private boolean isShowDialog;
    private int typePresentationView = 0, typeSortView = 0;
    private boolean stateSortDesc;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPrefs;
    private String textSearch = "";

    @Override
    protected Class<DocViewModel> getViewModel() {
        return DocViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_doc;
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
            binding.progressDoc.setVisibility(View.GONE);
            binding.groupPermission.setVisibility(View.VISIBLE);
            binding.tvMessage.setVisibility(View.GONE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            binding.recyclerDoc.setVisibility(View.INVISIBLE);
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
                fileDataAdapter.filter(binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
                binding.layoutHeaderExplore.ivSortView.setVisibility(View.INVISIBLE);
                binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.INVISIBLE);
            }
            binding.recyclerDoc.setVisibility(View.VISIBLE);
            binding.groupPermission.setVisibility(View.GONE);
            mainViewModel.allowPaste.setValue(false);
            if (((MainActivity) requireActivity()).selectList.size() > 0) {
                fileDataAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
            }

        }
        if (textSearch.length() > 0) {
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
        } else {
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.GONE);
            if (list.size() > 0)
                binding.layoutHeaderExplore.ivSortView.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_DELETE_ALl_ITEM) {
            Timber.e("Number4, onMessageReceived all");
            fileDataAdapter.resetAllSelect();
        }else if (event.getType() == Constant.EVENT_DELETE_ITEM) {
            Timber.e("Number4, onMessageReceived");
            fileDataAdapter.resetList(event.getPath());
        }
    }

    private void setupView() {
        sharedPrefs = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        assert getArguments() != null;
        type = getArguments().getInt(Constant.TYPE_DOC);
        setScreen(type);
        if (type == Constant.SCREEN_DOC) {
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.doc));
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_DOC, Constant.TYPE_OPTION_VIEW_DOC, Constant.TYPE_DESC_VIEW_DOC);
        } else if (type == Constant.SCREEN_MUSIC) {
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.music));
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_MUSIC, Constant.TYPE_OPTION_VIEW_MUSIC, Constant.TYPE_DESC_VIEW_MUSIC);
        } else if (type == Constant.SCREEN_DOWNLOAD) {
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.download));
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_DOWNLOAD, Constant.TYPE_OPTION_VIEW_DOWNLOAD, Constant.TYPE_DESC_VIEW_DOWNLOAD);
        } else if (type == Constant.SCREEN_APK) {
            binding.layoutHeaderExplore.tvHeader.setText(requireContext().getString(R.string.apk));
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_APK, Constant.TYPE_OPTION_VIEW_APK, Constant.TYPE_DESC_VIEW_APK);
        }
        fileDataAdapter = new FileDataAdapter(Constant.TYPE_MULTI);
        fileDataAdapter.setFileDataRepository(fileDataRepository);
        fileDataAdapter.setOnItemClickedListener(this);
        new Handler().postDelayed(() -> {
            if (isAdded())
                setUpLayoutSortView(typePresentationView);
        }, 10);

        binding.recyclerDoc.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fastScroll.attachRecyclerView(binding.recyclerDoc);
        binding.fastScroll.setHandlePressedColor(getResources().getColor(R.color.scroll_bar));
        binding.fastScroll.attachAdapter(fileDataAdapter);
        binding.fastScroll.setOnHandleTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    binding.recyclerDoc.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            binding.recyclerDoc.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        EditText searchEditText = binding.layoutHeaderExplore.searchExplorer.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(requireContext().getResources().getColor(R.color.text_black));
        searchEditText.setHintTextColor(requireContext().getResources().getColor(R.color.text_black));
    }

    private void getTypeSortView(String typePresentation, String typeSort, String desc) {
        typePresentationView = sharedPrefs.getInt(typePresentation, 0);
        typeSortView = sharedPrefs.getInt(typeSort, 0);
        stateSortDesc = sharedPrefs.getBoolean(desc, false);
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    private void setTypePresentationView(int typePresentationView) {
        if (type == Constant.SCREEN_DOC)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_DOC, typePresentationView).apply();
        else if (type == Constant.SCREEN_MUSIC)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_MUSIC, typePresentationView).apply();
        else if (type == Constant.SCREEN_DOWNLOAD)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_DOWNLOAD, typePresentationView).apply();
        else if (type == Constant.SCREEN_APK)
            sharedPrefs.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_APK, typePresentationView).apply();
    }

    private void setTypeSortDesc(int typeSort, boolean desc) {
        if (type == Constant.SCREEN_DOC) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_DOC, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_DOC, desc).apply();
        } else if (type == Constant.SCREEN_MUSIC) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_MUSIC, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_MUSIC, desc).apply();
        } else if (type == Constant.SCREEN_DOWNLOAD) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_DOWNLOAD, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_DOWNLOAD, desc).apply();
        } else if (type == Constant.SCREEN_APK) {
            sharedPrefs.edit().putInt(Constant.TYPE_OPTION_VIEW_APK, typeSort).apply();
            sharedPrefs.edit().putBoolean(Constant.TYPE_DESC_VIEW_APK, desc).apply();
        }
    }


    private void eventClick() {
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
        binding.layoutHeaderExplore.imgSearchExplorer.setOnClickListener(v -> {
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
            binding.layoutHeaderExplore.ivSortView.setVisibility(View.GONE);
            binding.layoutHeaderExplore.tvHeader.setVisibility(View.INVISIBLE);
            binding.layoutHeaderExplore.imgSearchExplorer.setVisibility(View.GONE);
            binding.layoutHeaderExplore.searchExplorer.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.tvExplorerCancel.setVisibility(View.VISIBLE);
            binding.layoutHeaderExplore.searchExplorer.onActionViewExpanded();
        });
        binding.layoutHeaderExplore.tvExplorerCancel.setOnClickListener(v -> {
            binding.recyclerDoc.scrollToPosition(0);
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
            Utils.hideInputMethod(binding.getRoot(), requireContext());
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
        });
    }

    private void eventBack() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
            }
        });
    }

    private void listener() {
        binding.viewDoc.setOnClickListener(v -> mainViewModel.changStateShow());
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
                    binding.recyclerDoc.scrollToPosition(0);
                }
                fileDataAdapter.filter(newText);
                textSearch = newText;
                if (fileDataAdapter.getListSearch().size() == 0) {
                    if (Utils.checkPermissions(requireActivity())) binding.tvMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.tvMessage.setVisibility(View.GONE);
                }
                return true;
            }
        });
        binding.tvNeedPermission.setOnClickListener(this);
        binding.tvGotoSetting.setOnClickListener(this);
    }

    private void enabledViewSortSearch(boolean enabled) {
        if (enabled) {
            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setEnabled(true);
            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(true);
        } else {
            binding.layoutHeaderExplore.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderExplore.ivSortView.setEnabled(false);
            binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
        }
    }

    private void setList(List<FileData> data) {
        binding.progressDoc.setVisibility(View.GONE);
        list.clear();
        list.addAll(data);
        if (Utils.checkPermissions(requireContext())) {
            if (list.size() > 0) {
                binding.tvMessage.setVisibility(View.GONE);
                enabledViewSortSearch(true);
            } else {
                if (Utils.checkPermissions(requireActivity())) binding.tvMessage.setVisibility(View.VISIBLE);
                //  binding.layoutHeaderExplore.ivSortView.setEnabled(false);
                //  binding.layoutHeaderExplore.imgSearchExplorer.setEnabled(false);
                enabledViewSortSearch(false);
            }
        } else {
            binding.tvMessage.setVisibility(View.GONE);
        }


        fileDataAdapter.setOnlyList(list);
        fileDataAdapter.setListSelect(((MainActivity) requireActivity()).selectList);
        setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderExplore.searchExplorer.getQuery().toString());
        ((MainActivity) requireActivity()).startWatchingFile(list);
    }

    private void setUpLayoutSortView(int typePresentation) {
        fileDataAdapter.setTypePresentationView(typePresentation);
        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.recyclerDoc.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_4);
            binding.recyclerDoc.setLayoutManager(layoutManager);
        }
        binding.recyclerDoc.setAdapter(fileDataAdapter);
    }

    private void setUpSortType(int typeSortView, boolean sortDesc, String filter) {
        fileDataAdapter.setTypeSortView(typeSortView, sortDesc, filter);
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
        mainViewModel.renameSuccess.observe(getViewLifecycleOwner(), this::renameInList);
        mainViewModel.isShowDialog.observe(getViewLifecycleOwner(), data -> isShowDialog = data);
        mainViewModel.isShowDetailSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                isShowDetail = true;
                binding.viewDoc.setClickable(true);
                binding.viewDoc.setVisibility(View.VISIBLE);
            } else {
                isShowDetail = false;
                binding.viewDoc.setVisibility(View.GONE);
            }
        });
        mainViewModel.isGetFile.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                if (type == Constant.SCREEN_DOC) {
                    setList(mainViewModel.fileDataRepository.listDoc);
                } else if (type == Constant.SCREEN_APK) {
                    setList(mainViewModel.fileDataRepository.listApk);
                } else if (type == Constant.SCREEN_DOWNLOAD) {
                    setList(mainViewModel.fileDataRepository.listDownload);
                }
            }
        });
        mainViewModel.isGetMusicFile.observe(getViewLifecycleOwner(), data -> {
            if (data && type == Constant.SCREEN_MUSIC) {
                setList(mainViewModel.fileDataRepository.listMusic);
            }
        });
        mainViewModel.haveFileSelect.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                binding.recyclerDoc.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(110));
            } else {
                binding.recyclerDoc.setPadding(Utils.dpToPx(12), 0, Utils.dpToPx(12), Utils.dpToPx(12));
            }

        });
        mainViewModel.checkOpenFile.observe(getViewLifecycleOwner(), data -> {
            if ((Boolean) data) {
                binding.layoutHeaderExplore.imgExplorerBack.setEnabled(true);
                mainViewModel.checkOpenFile.postValue(false);
            }
        });
    }

    private void renameInList(EventRename data) {
        for (int i = list.size() - 1; i >= 0; i--) {
            String path = list.get(i).getFilePath();
            if (path != null && path.equals(data.getOldPath())) {
                if ((type == Constant.SCREEN_DOC && viewModel.fileDataRepository.isDocFile(data.getNewPath()))
                        || (type == Constant.SCREEN_MUSIC && viewModel.fileDataRepository.isMusicFile(data.getNewPath()))
                        || (type == Constant.SCREEN_APK && viewModel.fileDataRepository.isApkFile(data.getNewPath()))
                        || (type == Constant.SCREEN_DOWNLOAD)
                ) {
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
        if (list.size() > 0) {
            binding.tvMessage.setVisibility(View.GONE);
        } else {
            if (Utils.checkPermissions(requireActivity())) binding.tvMessage.setVisibility(View.VISIBLE);
        }
    }

    private void reloadData(boolean data) {
        if (data) {
            mainViewModel.clearListDownload();
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
    public void onFolderDeviceClick(String filePath, boolean isClickFolder) {
        //open file with other apps
        binding.layoutHeaderExplore.imgExplorerBack.setEnabled(false);
        if (Utils.checkLongTime()) {
            Utils.openFile(filePath, requireActivity());
        }
        //todo: add to recent file
        if (!fileDataRepository.isArchiveFile(filePath)) {
            mainViewModel.insertRecent(filePath);
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