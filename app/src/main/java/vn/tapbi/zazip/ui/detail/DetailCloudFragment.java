package vn.tapbi.zazip.ui.detail;

import static android.content.Context.MODE_PRIVATE;
import static vn.tapbi.zazip.common.Constant.ACCESS_TOKEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.ACCESS_TOKEN_ONEDRIVE;
import static vn.tapbi.zazip.common.Constant.CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.DROP_BOX;
import static vn.tapbi.zazip.common.Constant.ERROR_INTERNET;
import static vn.tapbi.zazip.common.Constant.FOLDER_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.FOLDER_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.FOLDER_ONEDRIVE;
import static vn.tapbi.zazip.common.Constant.FOLDER_ROOT;
import static vn.tapbi.zazip.common.Constant.GOOGLE_DRIVE;
import static vn.tapbi.zazip.common.Constant.KEY_NAME_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.KEY_TEXT_SEARCH_CLOUD;
import static vn.tapbi.zazip.common.Constant.KEY_TYPE_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.LIST_DATA_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.NAME_ACCOUNT_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.NAME_DROPBOX_FOLDER;
import static vn.tapbi.zazip.common.Constant.NAME_GG_DRIVE_FOLDER;
import static vn.tapbi.zazip.common.Constant.NAME_ONEDRIVE_FOLDER;
import static vn.tapbi.zazip.common.Constant.ONE_DRIVE;
import static vn.tapbi.zazip.common.Constant.TYPE_ACCOUNT_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_GG_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_ONE_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_SCREEN_CLOUD;
import static vn.tapbi.zazip.common.Constant.clientIdentifier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPathCompress;
import vn.tapbi.zazip.common.models.EventSortView;
import vn.tapbi.zazip.data.model.DocumentDrive;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.AccountRepository;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.FragmentDetailCloudBinding;
import vn.tapbi.zazip.interfaces.LoginGoogleFailListener;
import vn.tapbi.zazip.model.EventShowGoogleDrive;
import vn.tapbi.zazip.model.FileCloudDownload;
import vn.tapbi.zazip.services.DownloadFileCloudService;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.cloud.dropbox.DropboxClients;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.MySharePreferences;
import vn.tapbi.zazip.utils.PicassoClient;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class DetailCloudFragment extends BaseBindingFragment<FragmentDetailCloudBinding, DetailCloudViewModel> implements LoginGoogleFailListener, FileDataAdapter.OnItemClickedListener, View.OnClickListener {
    @Inject
    public AccountRepository accountRepository;
    @Inject
    public FileDataRepository fileDataRepository;
    private FileDataAdapter fileDataAdapter;
    private String accessTokenOneDrive = "";
    private String accessTokenDropBox = "";
    private final List<FileData> dataDocuments = new ArrayList<>();
    OnBackPressedCallback callback;
    private boolean checkDoubleClickFolderAndBackPress = false;
    private ArrayList<DocumentDrive> dataDrives = new ArrayList<>();
    private String idOneDriver = "";
    private SharedPreferences sharedPreferences;
    private String folderGgDrive = FOLDER_ROOT;
    private PopupWindow popupWindow;
    private int typePresentationView = 0, typeSortView = 0;
    private boolean stateSortDesc, clickDownload;
    private int type;
    String pathFolderDropBox = "";
    private String nameMailGgDrive = "";
    private String typeGGDrive = "";
    private String textSearch = "";
    DropboxClients dropboxClients;
    private final String pathDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    @Override
    protected Class<DetailCloudViewModel> getViewModel() {
        return DetailCloudViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_detail_cloud;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            nameMailGgDrive = savedInstanceState.getString(NAME_ACCOUNT_GG_DRIVE);
            typeGGDrive = savedInstanceState.getString(TYPE_ACCOUNT_GG_DRIVE);
            folderGgDrive = savedInstanceState.getString(FOLDER_GG_DRIVE);
            accessTokenDropBox = savedInstanceState.getString(ACCESS_TOKEN_DROP_BOX);
            accessTokenOneDrive = savedInstanceState.getString(ACCESS_TOKEN_ONEDRIVE);
            idOneDriver = savedInstanceState.getString(FOLDER_ONEDRIVE);
            pathFolderDropBox = savedInstanceState.getString(FOLDER_DROP_BOX);
            textSearch = savedInstanceState.getString(KEY_TEXT_SEARCH_CLOUD);
            if (textSearch.length() > 0) {
                setUpShowSearch();
            }
            String list = savedInstanceState.getString(LIST_DATA_GG_DRIVE);
            type = savedInstanceState.getInt(TYPE_SCREEN_CLOUD);
            if (type == Constant.SCREEN_GOOGLE) {
                viewModel.getGoogleService(requireContext(), nameMailGgDrive, typeGGDrive, requireContext().getString(R.string.app_name), folderGgDrive);
            } else if (type == Constant.SCREEN_ONE_DRIVE) {
                viewModel.getFileOnedrive(accessTokenOneDrive, idOneDriver);
            } else if (type == Constant.SCREEN_DROP_BOX) {
                DbxClientV2 dbxClientV2 = new DbxClientV2(new DbxRequestConfig(clientIdentifier), accessTokenDropBox);
                PicassoClient.init(requireContext(), dbxClientV2);
                if (fileDataAdapter != null) {
                    fileDataAdapter.setPicasso(PicassoClient.getPicasso());
                }
                dropboxClients = new DropboxClients(dbxClientV2);
                viewModel.getAllFileFromDropBox(pathFolderDropBox, new DropboxClients(dbxClientV2));
            }
            ArrayList<DocumentDrive> list1 = new Gson().fromJson(list, new TypeToken<ArrayList<DocumentDrive>>() {
            }.getType());
            dataDrives.addAll(list1);
        }
        enabledViewSortSearch(false);
        setUpView();
        setUpAdapter();
        eventObserver();
        eventBack();
        eventClick();
        listenerSearch();
        eventClickDownLoadCloud();
        ((MainActivity) requireActivity()).setLoginGoogleFailListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.resetLastClickTime();
        if (Utils.checkPermissions(requireContext())) {
            if (clickDownload) {
                showDialogPickFolderDownload();
            }
        }
        if (textSearch.length() > 0) {
            setUpShowSearch();
        } else {
            setUpCancelSearch();
            if (dataDocuments.size() > 0)
                binding.layoutHeaderDetailCloud.ivSortView.setEnabled(true);
        }
    }

    private void listenerSearch() {
        binding.layoutHeaderDetailCloud.searchExplorer.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) binding.layoutHeaderDetailCloud.searchExplorer.onActionViewCollapsed();
        });
        binding.layoutHeaderDetailCloud.searchExplorer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utils.hideInputMethod(binding.getRoot(), requireContext());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    binding.rcDetailCloud.scrollToPosition(0);
                }
                textSearch = newText;
                fileDataAdapter.filter(newText);
                return true;
            }
        });
    }

    private void setUpShowSearch() {
        binding.layoutHeaderDetailCloud.ivSortView.setVisibility(View.GONE);
        binding.layoutHeaderDetailCloud.tvHeader.setVisibility(View.INVISIBLE);
        binding.layoutHeaderDetailCloud.imgSearchExplorer.setVisibility(View.GONE);
        binding.layoutHeaderDetailCloud.searchExplorer.setVisibility(View.VISIBLE);
        binding.layoutHeaderDetailCloud.tvExplorerCancel.setVisibility(View.VISIBLE);
        binding.layoutHeaderDetailCloud.searchExplorer.onActionViewExpanded();
    }

    private void setUpCancelSearch() {
        binding.rcDetailCloud.scrollToPosition(0);
        binding.layoutHeaderDetailCloud.ivSortView.setVisibility(View.VISIBLE);
        binding.layoutHeaderDetailCloud.searchExplorer.setVisibility(View.GONE);
        binding.layoutHeaderDetailCloud.tvExplorerCancel.setVisibility(View.GONE);
        binding.layoutHeaderDetailCloud.tvHeader.setVisibility(View.VISIBLE);
        binding.layoutHeaderDetailCloud.imgSearchExplorer.setVisibility(View.VISIBLE);
        binding.layoutHeaderDetailCloud.searchExplorer.onActionViewCollapsed();
    }

    private void eventClick() {
        binding.layoutHeaderDetailCloud.imgSearchExplorer.setOnClickListener(v -> {
            binding.layoutHeaderDetailCloud.ivSortView.setEnabled(false);
            setUpShowSearch();
        });
        binding.layoutHeaderDetailCloud.tvExplorerCancel.setOnClickListener(v -> {
            setUpCancelSearch();
            binding.layoutHeaderDetailCloud.ivSortView.setEnabled(true);
        });
        binding.layoutHeaderDetailCloud.imgExplorerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.handleOnBackPressed();
            }
        });
        binding.layoutHeaderDetailCloud.ivSortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutHeaderDetailCloud.imgSearchExplorer.setEnabled(false);
                popupWindow = createPopUpSortView();
                popupWindow.showAsDropDown(v, 0, -50, Gravity.END);
                setIdPresentationViewInBase(typePresentationView);
                setIdSortViewInBase(typeSortView);
                setSortDescInBase(stateSortDesc);
                if (stateSortDesc) setIdSortDescInBase(4);
            }
        });
    }

    private void setUpView() {
        sharedPreferences = requireContext().getSharedPreferences(requireContext().getPackageName(), MODE_PRIVATE);
        assert getArguments() != null;
        type = getArguments().getInt(Constant.TYPE_CLOUD_SCREEN);
        nameMailGgDrive = getArguments().getString(KEY_NAME_GG_DRIVE);
        typeGGDrive = getArguments().getString(KEY_TYPE_GG_DRIVE);
        setScreen(type);
        if (type == Constant.SCREEN_GOOGLE) {
            binding.layoutHeaderDetailCloud.tvHeader.setText(GOOGLE_DRIVE);
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_GOOGLE, Constant.TYPE_OPTION_VIEW_GOOGLE, Constant.TYPE_DESC_VIEW_GOOGLE);
        } else if (type == Constant.SCREEN_ONE_DRIVE) {
            binding.layoutHeaderDetailCloud.tvHeader.setText(ONE_DRIVE);
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_ONE_DRIVE, Constant.TYPE_OPTION_VIEW_ONE_DRIVE, Constant.TYPE_DESC_VIEW_ONE_DRIVE);
        } else if (type == Constant.SCREEN_DROP_BOX) {
            binding.layoutHeaderDetailCloud.tvHeader.setText(DROP_BOX);
            getTypeSortView(Constant.TYPE_PRESENTATION_VIEW_DROP_BOX, Constant.TYPE_OPTION_VIEW_DROP_BOX, Constant.TYPE_DESC_VIEW_DROP_BOX);
        }
        enableViewDownload(false);
        enableViewShare(false);
    }

    private void getTypeSortView(String typePresentation, String typeSort, String desc) {
        typePresentationView = sharedPreferences.getInt(typePresentation, 0);
        typeSortView = sharedPreferences.getInt(typeSort, 0);
        stateSortDesc = sharedPreferences.getBoolean(desc, false);
    }

    private void eventClickDownLoadCloud() {
        binding.tvDownload.setOnClickListener(this);
        binding.tvShare.setOnClickListener(this);
    }

    private void startService(int typeCloud, int totalFile, String pathSaveTo, List<FileCloudDownload> listFileDownload) {
        Gson gson = new Gson();
        String list = gson.toJson(listFileDownload);
        Intent intent = new Intent(requireActivity(), DownloadFileCloudService.class);
        intent.setAction(Constant.ACTION_START_DOWNLOAD);
        intent.putExtra(Constant.TYPE_CLOUD, typeCloud);
        intent.putExtra(Constant.KEY_TOTAL_FILE_DRIVE, totalFile);
        intent.putExtra(Constant.KEY_ID_DRIVE, new Random().nextInt());
        intent.putExtra(Constant.KEY_PATH_SAVE_TO, pathSaveTo);
        intent.putExtra(Constant.KEY_FILE_DOWNLOAD, list);
        requireActivity().startService(intent);
    }

    private void startDownloadService(String pathSaveTo) {
        List<FileCloudDownload> downloadList = new LinkedList<>();
        for (int i = 0; i < App.getInstance().listSelectCloud.size(); i++) {
            downloadList.add(new FileCloudDownload(App.getInstance().listSelectCloud.get(i).getFileName(),
                    App.getInstance().listSelectCloud.get(i).getCategory(),
                    App.getInstance().listSelectCloud.get(i).getFileId(),
                    App.getInstance().listSelectCloud.get(i).getFilePath(),
                    App.getInstance().listSelectCloud.get(i).getRevFileDropBox(),
                    accessTokenDropBox,
                    App.getInstance().listSelectCloud.get(i).getPathDisplayDropBox()));
        }
        Toast.makeText(requireContext(), R.string.toast_start_download_file, Toast.LENGTH_SHORT).show();
        startService(type, App.getInstance().listSelectCloud.size(), pathSaveTo, downloadList);
    }

    private void showDialogPickFolderDownload() {
        ((MainActivity) requireActivity()).bottomSheetDialog.setPath(pathDownload);
        ((MainActivity) requireActivity()).bottomSheetDialog.createAdapter();
        ((MainActivity) requireActivity()).bottomSheetDialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDownload:
                if (Utils.checkLongTime()) {
                    if (Utils.checkPermissions(requireContext())) {
                        showDialogPickFolderDownload();
                    } else {
                        clickDownload = true;
                        ((MainActivity) requireActivity()).checkForExternalPermission();
                    }
                }
                break;

            case R.id.tvShare:
                if (Utils.checkLongTime()) {
                    Toast.makeText(requireContext(), getString(R.string.get_file_link_share), Toast.LENGTH_SHORT).show();
                    if (fileDataAdapter != null) {
                        fileDataAdapter.setCheckEventShare(true);
                    }
                    if (type == Constant.SCREEN_GOOGLE) {
                        viewModel.getLinkGoogle(App.getInstance().driveServiceHelper, App.getInstance().listSelectCloud.get(0).getFileId());
                        break;
                    } else if (type == Constant.SCREEN_ONE_DRIVE) {
                        viewModel.getLinkShare(accessTokenOneDrive, App.getInstance().listSelectCloud.get(0).getFileId());
                        break;
                    } else if (type == Constant.SCREEN_DROP_BOX) {
                        viewModel.getLinkShareDropBox(App.getInstance().listSelectCloud.get(0).getPathDisplayDropBox(), dropboxClients);
                        break;
                    }
                }
        }
    }

    private void setTypePresentationView(int typePresentationView) {
        if (type == Constant.SCREEN_GOOGLE)
            sharedPreferences.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_GOOGLE, typePresentationView).apply();
        else if (type == Constant.SCREEN_ONE_DRIVE)
            sharedPreferences.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_ONE_DRIVE, typePresentationView).apply();
        else if (type == Constant.SCREEN_DROP_BOX)
            sharedPreferences.edit().putInt(Constant.TYPE_PRESENTATION_VIEW_DROP_BOX, typePresentationView).apply();
    }

    private void setTypeSortDesc(int typeSort, boolean desc) {
        if (type == Constant.SCREEN_GOOGLE) {
            sharedPreferences.edit().putInt(Constant.TYPE_OPTION_VIEW_GOOGLE, typeSort).apply();
            sharedPreferences.edit().putBoolean(Constant.TYPE_DESC_VIEW_GOOGLE, desc).apply();
        } else if (type == Constant.SCREEN_ONE_DRIVE) {
            sharedPreferences.edit().putInt(Constant.TYPE_OPTION_VIEW_ONE_DRIVE, typeSort).apply();
            sharedPreferences.edit().putBoolean(Constant.TYPE_DESC_VIEW_ONE_DRIVE, desc).apply();
        } else if (type == Constant.SCREEN_DROP_BOX) {
            sharedPreferences.edit().putInt(Constant.TYPE_OPTION_VIEW_DROP_BOX, typeSort).apply();
            sharedPreferences.edit().putBoolean(Constant.TYPE_DESC_VIEW_DROP_BOX, desc).apply();
        }
    }

    private void setUpViewInBase() {
        setIdSortDescInBase(typeSortView);
        setSortDescInBase(stateSortDesc);
    }

    private void eventBack() {
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    int size = dataDrives.size();
                    if (size > 0) {
                        DocumentDrive documentDrive = dataDrives.get(size - 1);
                        dataDocuments.clear();
                        dataDocuments.addAll(documentDrive.getDocuments());
                        if (documentDrive.getDocuments().isEmpty()) {
                            binding.tvShowFolderEmpty.setVisibility(View.VISIBLE);
                            enabledViewSortSearch(false);
                        } else {
                            binding.tvShowFolderEmpty.setVisibility(View.INVISIBLE);
                            enabledViewSortSearch(true);
                        }
                        fileDataAdapter.setList(documentDrive.getDocuments());
                        if (type == Constant.SCREEN_DROP_BOX) {
                            fileDataAdapter.setFileMetadataList(documentDrive.getFileMetadataList());
                        }
                        fileDataAdapter.notifyChanged();
                        if (!checkDoubleClickFolderAndBackPress) {
                            dataDrives.remove(size - 1);
                        }
                    } else {
                        setEnabled(false);
                        requireActivity().onBackPressed();
                    }
                    setUpCancelSearch();
                } else {
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onPermissionGranted() {

    }

    private void setUpAdapter() {
        fileDataAdapter = new FileDataAdapter(Constant.TYPE_MULTI);
        fileDataAdapter.setFileDataRepository(fileDataRepository);
        binding.rcDetailCloud.setItemAnimator(null);
        fileDataAdapter.setOnItemClickedListener(this);
        setUpLayoutSortView(typePresentationView);
    }

    private void eventObserver() {
        mainViewModel.pathCompressLiveEvent.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof EventPathCompress) {
                startDownloadService(((EventPathCompress) data).getPath());
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
                    setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderDetailCloud.searchExplorer.getQuery().toString());
                }
            }
        });
        mainViewModel.positionPresentationView.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getScreen() == type && data.getPositionPresentationView() != -1) {
                    typePresentationView = data.getPositionPresentationView();
                    setTypePresentationView(typePresentationView);
                    setIdPresentationViewInBase(typePresentationView);
                    setUpLayoutSortView(typePresentationView);
                }
            }
        });
        mainViewModel.checkDoubleClick.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    binding.layoutHeaderDetailCloud.imgSearchExplorer.setEnabled(true);
                }
            }
        });
        mainViewModel.eventShare.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if (((Boolean) data)) {
                    if (fileDataAdapter != null) {
                        fileDataAdapter.setCheckEventShare(false);
                    }
                }
            }
        });
        viewModel.loginOneDriveFail.observe(getViewLifecycleOwner(), data -> {
            if (data) {
                Toast.makeText(requireContext(),  R.string.account_error, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    if (isAdded()) {
                        requireActivity().onBackPressed();
                    }
                }, 400);
            }
        });
        mainViewModel.eventOnedrive.observe(getViewLifecycleOwner(), eventOnedrive -> {
            switch (eventOnedrive.getType()) {
                case ONE_DRIVE:
                    accessTokenOneDrive = eventOnedrive.getAccount().getAccessToken();
                    viewModel.getFileOnedrive(accessTokenOneDrive, idOneDriver);
                    break;
                case DROP_BOX:
                    accessTokenDropBox = eventOnedrive.getAccount().getAccessToken();
                    DbxClientV2 dbxClientV2 = new DbxClientV2(new DbxRequestConfig(clientIdentifier), eventOnedrive.getAccount().getAccessToken());
                    dropboxClients = new DropboxClients(dbxClientV2);
                    PicassoClient.init(requireContext(), dbxClientV2);
                    viewModel.getAllFileFromDropBox("", dropboxClients);
                    break;
                case GOOGLE_DRIVE:
                    break;
            }
        });
        viewModel.getGoogleService(requireContext(), nameMailGgDrive, typeGGDrive, requireContext().getString(R.string.app_name), folderGgDrive);
        viewModel.liveDataFileDocumentCloud.observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.getStatus()) {
                case LOADING:
                    if (fileDataAdapter != null) {
                        fileDataAdapter.setCheckEventShare(true);
                    }
                    checkDoubleClickFolderAndBackPress = true;
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.layoutHeaderDetailCloud.imgExplorerBack.setEnabled(false);
                    enabledViewSortSearch(false);
                    break;
                case SUCCESS:
                    checkDoubleClickFolderAndBackPress = false;
                    binding.layoutHeaderDetailCloud.imgExplorerBack.setEnabled(true);
                    if (fileDataAdapter != null) {
                        fileDataAdapter.setCheckEventShare(false);
                    }
                    if (App.getInstance().listSelectCloud.size() == 1) {
                        binding.tvShare.setEnabled(true);
                    }
                    if (listResource.getData() != null) {
                        if (listResource.getData().isEmpty()) {
                            binding.tvShowFolderEmpty.setVisibility(View.VISIBLE);
                            enabledViewSortSearch(false);
                        } else {
                            binding.tvShowFolderEmpty.setVisibility(View.INVISIBLE);
                            enabledViewSortSearch(true);
                        }
                        setListForAdapter(listResource.getData());
                    }
                    break;
                case ERROR_DROP_BOX:
                    errorCloud();
                    if (MySharePreferences.getBooleanValue(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, App.getInstance().getApplicationContext())) {
                        if (isAdded()) {
                            requireActivity().onBackPressed();
                        }
                        loginDropBox();
                    }
                    break;
                case ERROR_GG_DRIVE:
                    errorCloud();
                    if (listResource.getS().equals(Constant.ACCURACY)) {
                        mainViewModel.showGoogleDriveDetailFail.postValue(new EventShowGoogleDrive(listResource.getIntent()));
                    } else if (listResource.getS().equals(ERROR_INTERNET)) {
                        Toast.makeText(requireContext(), getString(R.string.check_connect_internet), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            if (isAdded()) {
                                requireActivity().onBackPressed();
                            }
                        }, 300);
                    }
                    break;
                case ERROR_ONE_DRIVE:
                    errorCloud();
                    break;
            }
        });
        viewModel.fileMetadataList.observe(getViewLifecycleOwner(), listResource -> {
            switch (listResource.getStatus()) {
                case LOADING:
                case ERROR:
                    break;
                case SUCCESS:
                    if (fileDataAdapter != null) {
                        if (PicassoClient.getPicasso() != null) {
                            fileDataAdapter.setPicasso(PicassoClient.getPicasso());
                        }
                        fileDataAdapter.setFileMetadataList(listResource.getData());
                    }
                    break;
            }
        });
        viewModel.linkShareOneDrive.observe(getViewLifecycleOwner(), link -> {
            if (link != null) {
                Utils.shareFile(requireActivity(), link);
            }
        });
        viewModel.linkShareGoogle.observe(getViewLifecycleOwner(), link -> {
            if (link != null) {
                Utils.shareFile(requireActivity(), link);
            }
        });
        viewModel.linkShareDropBox.observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                Utils.shareFile(requireActivity(), s);
            }
        });
        mainViewModel.checkResetDownload.observe(getViewLifecycleOwner(), data -> {
            if (data instanceof Boolean) {
                if ((Boolean) data) {
                    if (fileDataAdapter != null) {
                        fileDataAdapter.clearListSelectDownload();
                        mainViewModel.checkResetDownload.postValue(false);
                        enableViewDownload(false);
                        enableViewShare(false);
                    }
                }
            }
        });
    }

    private void errorCloud() {
        if (fileDataAdapter != null) {
            fileDataAdapter.setCheckEventShare(false);
        }
        checkDoubleClickFolderAndBackPress = false;
        binding.layoutHeaderDetailCloud.imgExplorerBack.setEnabled(true);
        binding.progressBar.setVisibility(View.INVISIBLE);
        if (App.getInstance().listSelectCloud.size() == 1) {
            binding.tvShare.setEnabled(true);
        }
    }

    private void setListForAdapter(List<FileData> fileDataList) {
        binding.progressBar.setVisibility(View.INVISIBLE);
        dataDocuments.clear();
        dataDocuments.addAll(fileDataList);
        fileDataAdapter.setOnlyList(fileDataList);
        setUpSortType(typeSortView, stateSortDesc, binding.layoutHeaderDetailCloud.searchExplorer.getQuery().toString());
    }

    private void setUpSortType(int typeSortView, boolean sortDesc, String filter) {
        fileDataAdapter.setTypeSortView(typeSortView, sortDesc, filter);
    }

    private void setUpLayoutSortView(int typePresentation) {
        fileDataAdapter.setTypePresentationView(typePresentation);
        if (typePresentation == Constant.SORT_VIEW_DETAIL || typePresentation == Constant.SORT_VIEW_COMPACT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            binding.rcDetailCloud.setLayoutManager(layoutManager);
        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), Constant.SPAN_COUNT_GRID_4);
            binding.rcDetailCloud.setLayoutManager(layoutManager);
        }
        binding.rcDetailCloud.setAdapter(fileDataAdapter);
    }

    private void setColorFilterTextView(int color) {
        for (Drawable drawable : binding.tvShare.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void setColorFilterDownload(int color) {
        for (Drawable drawable : binding.tvDownload.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void enableViewDownload(boolean enable) {
        binding.tvDownload.setEnabled(enable);
        if (enable) {
            binding.tvDownload.setTextColor(getResources().getColor(R.color.text_blue));
            setColorFilterDownload(getResources().getColor(R.color.text_blue));
        } else {
            binding.tvDownload.setTextColor(getResources().getColor(R.color.text_gray));
            setColorFilterDownload(getResources().getColor(R.color.text_gray));
        }
    }

    private void enableViewShare(boolean enable) {
        binding.tvShare.setEnabled(enable);
        if (enable) {
            binding.tvShare.setTextColor(getResources().getColor(R.color.text_blue));
            setColorFilterTextView(getResources().getColor(R.color.text_blue));
        } else {
            binding.tvShare.setTextColor(getResources().getColor(R.color.text_gray));
            setColorFilterTextView(getResources().getColor(R.color.text_gray));
        }
    }

    private void enabledViewSortSearch(boolean enabled) {
        if (enabled) {
            binding.layoutHeaderDetailCloud.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderDetailCloud.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_enabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderDetailCloud.ivSortView.setEnabled(true);
            binding.layoutHeaderDetailCloud.imgSearchExplorer.setEnabled(true);
        } else {
            binding.layoutHeaderDetailCloud.imgSearchExplorer.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderDetailCloud.ivSortView.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.bg_item_sort_search_disabled), PorterDuff.Mode.SRC_IN));
            binding.layoutHeaderDetailCloud.ivSortView.setEnabled(false);
            binding.layoutHeaderDetailCloud.imgSearchExplorer.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(int position, FileData fileData) {
        if (App.getInstance().listSelectCloud.size() == 0) {
            enableViewDownload(false);
            enableViewShare(false);
        } else if (App.getInstance().listSelectCloud.size() == 1) {
            enableViewDownload(true);
            enableViewShare(true);
        } else {
            enableViewDownload(true);
            enableViewShare(false);
        }
    }

    @Override
    public void onFolderDeviceClick(String path, boolean isClickFolder) {

    }

    @Override
    public void clickFolderCloud(int position, String path) {
        binding.tvShare.setEnabled(false);
        setUpCancelSearch();
        if (dataDocuments.get(position).getType() == TYPE_ONE_DRIVE_FILE) {
            idOneDriver = fileDataAdapter.getList().get(position).getFileId();
            dataDrives.add(new DocumentDrive(NAME_ONEDRIVE_FOLDER, idOneDriver, fileDataAdapter.getList()));
            viewModel.getFileOnedrive(accessTokenOneDrive, idOneDriver);
        } else if (dataDocuments.get(position).getType() == TYPE_DROP_BOX_FILE) {
            pathFolderDropBox = fileDataAdapter.getList().get(position).getPathDisplayDropBox();
            dataDrives.add(new DocumentDrive(NAME_DROPBOX_FOLDER, fileDataAdapter.getList().get(position).getPathDisplayDropBox(), fileDataAdapter.getList(), fileDataAdapter.getFileMetadataList(), null));
            viewModel.getAllFileFromDropBox(fileDataAdapter.getList().get(position).getPathDisplayDropBox(), dropboxClients);
        } else if (dataDocuments.get(position).getType() == TYPE_GG_DRIVE_FILE) {
            dataDrives.add(new DocumentDrive(NAME_GG_DRIVE_FOLDER, fileDataAdapter.getList().get(position).getFileId(), fileDataAdapter.getList()));
            folderGgDrive = fileDataAdapter.getList().get(position).getFileId();
            viewModel.getGoogleService(requireContext(), nameMailGgDrive, typeGGDrive, requireContext().getString(R.string.app_name), folderGgDrive);
        }

    }

    @Override
    public void onLoginFail(boolean accept) {
        if (accept) {
            if (isAdded()) {
                viewModel.getGoogleService(requireContext(), nameMailGgDrive, typeGGDrive, requireContext().getString(R.string.app_name), folderGgDrive);
            }
        } else {
            if (isAdded()) {
                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCESS_TOKEN_ONEDRIVE, accessTokenOneDrive);
        outState.putString(ACCESS_TOKEN_DROP_BOX, accessTokenDropBox);
        outState.putString(FOLDER_ONEDRIVE, idOneDriver);
        outState.putString(FOLDER_DROP_BOX, pathFolderDropBox);
        outState.putString(NAME_ACCOUNT_GG_DRIVE, nameMailGgDrive);
        outState.putString(TYPE_ACCOUNT_GG_DRIVE, typeGGDrive);
        outState.putString(FOLDER_GG_DRIVE, folderGgDrive);
        outState.putInt(TYPE_SCREEN_CLOUD, type);
        outState.putString(KEY_TEXT_SEARCH_CLOUD, textSearch);
        Gson gson = new Gson();
        String list = gson.toJson(dataDrives);
        outState.putString(LIST_DATA_GG_DRIVE, list);
    }

}

