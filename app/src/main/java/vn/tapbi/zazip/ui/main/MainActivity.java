package vn.tapbi.zazip.ui.main;

import static android.os.Build.VERSION.SDK_INT;
import static vn.tapbi.zazip.common.Constant.KEY_OPEN_FILE;
import static vn.tapbi.zazip.common.Constant.REQUEST_CODE_SIGN_IN;
import static vn.tapbi.zazip.common.Constant.REQUEST_SHARE_FILE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventModifyName;
import vn.tapbi.zazip.common.models.EventPath;
import vn.tapbi.zazip.common.models.EventRename;
import vn.tapbi.zazip.common.models.RecursiveFileObserver;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.ItemSelectBottom;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.ActivityMainBinding;
import vn.tapbi.zazip.interfaces.LoginGoogleFailListener;
import vn.tapbi.zazip.interfaces.LoginGoogleListener;
import vn.tapbi.zazip.interfaces.OnPassDataCompress;
import vn.tapbi.zazip.model.EventLoginGoogleDrive;
import vn.tapbi.zazip.model.EventShowGoogleDrive;
import vn.tapbi.zazip.ui.adapter.DetailSelectAdapter;
import vn.tapbi.zazip.ui.adapter.OptionSelectBottomAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingActivity;
import vn.tapbi.zazip.ui.dialog.BottomSheetBrowse;
import vn.tapbi.zazip.ui.dialog.CompressDialog;
import vn.tapbi.zazip.ui.dialog.DeleteDialog;
import vn.tapbi.zazip.ui.dialog.ExtractDialog;
import vn.tapbi.zazip.ui.dialog.MyProgressDialog;
import vn.tapbi.zazip.ui.dialog.PasswordDialog;
import vn.tapbi.zazip.ui.dialog.RenameDialogNormal;
import vn.tapbi.zazip.ui.dialog.RequestPermissionDialog;
import vn.tapbi.zazip.utils.StatusBarUtils;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> implements OptionSelectBottomAdapter.OnClickItemBottomSelect, DetailSelectAdapter.OnClickItemDetail,
        OnPassDataCompress, PasswordDialog.OnClickListener {
    @Inject
    public FileDataRepository fileDataRepository;
    public GoogleSignInAccount googleSignInAccount;
    public List<FileData> selectList = new ArrayList<>();
    public List<ItemSelectBottom> optionList = new ArrayList<>();
    private RenameDialogNormal renameDialogNormal;
    public PasswordDialog passwordDialog;
    private CompressDialog compressDialog;
    private DeleteDialog deleteDialog;
    private MyProgressDialog progressDialog;
    private ExtractDialog extractDialog;
    public BottomSheetBrowse bottomSheetDialog;
    private DetailSelectAdapter detailSelectAdapter;
    private OptionSelectBottomAdapter optionSelectBottomAdapter;
    public NavController navControllerMain;
    public NavHostFragment navHostFragmentMain;
    private RecursiveFileObserver recursiveFileObserver;
    private int typeCompress, optionExtract, countExtractFail;
    private String fileRename;
    private LoginGoogleFailListener loginGoogleFailListener;
    private LoginGoogleListener loginGoogleListener;
    private String fileNameCompress, folderCompress, passCompress;
    private String pathPaste;
    public String fileNameExtract, folderExtract, passExtract, pathBottomSheet;
    private IntentFilter intentFilter;
    public RequestPermissionDialog requestPermissionDialog;
    public boolean isShowPaste;

    private boolean isListArchive = false;
    private boolean isPaste, isShowDetail, isShowDrawer;

    private boolean isFolderRename;
    private boolean isShowRename, isShowCompress, isShowExtract, isShowBottomSheet, isShowDelete, isShowPassDialog;
    public boolean isKillApp;

    private final static String[] permissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private final BroadcastReceiver downloadCloudReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(Constant.KEY_DOWNLOAD_SUCCESS, true)) {
                Toast.makeText(MainActivity.this, getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                viewModel.checkResetDownload.postValue(true);
                viewModel.reload.setValue(true);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SDK_INT < Build.VERSION_CODES.M) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutId() {
        StatusBarUtils.from(this).setLightStatusBar(true).setTransparentStatusBar(true).process();
        return R.layout.activity_main;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action != null && navControllerMain != null) {
            if (action.equals(Constant.ACTION_SHOW_PROCESS)) {
                int id = intent.getIntExtra(Constant.KEY_ID, 0);
                String title = intent.getStringExtra(Constant.KEY_TITLE);
                String contentProcess = intent.getStringExtra(Constant.KEY_CONTENT_PROGRESS);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ACTION_PROCESS, Constant.ACTION_SHOW);
                bundle.putInt(Constant.KEY_ID, id);
                bundle.putString(Constant.KEY_TITLE, title);
                bundle.putString(Constant.KEY_CONTENT_PROGRESS, contentProcess);

                NavBackStackEntry navBackStackEntry = navControllerMain.getCurrentBackStackEntry();
                if (navBackStackEntry != null) {
                    if (navBackStackEntry.getDestination().getId() == R.id.process_fragment) {
                        popBackStack();
                    }
                    navControllerMain.navigate(R.id.process_fragment, bundle);
                }
            }
        }
    }

    @Override
    public Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setupView(Bundle savedInstanceState) {
        //check KeyHash onedrive
//        try {
//            android.content.pm.PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    android.content.pm.PackageManager.GET_SIGNATURES);
//            for (android.content.pm.Signature signature : info.signatures) {
//                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("Number4", "KeyHash:" + android.util.Base64.encodeToString(md.digest(),
//                        android.util.Base64.DEFAULT));
//            }
//        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
//            Log.d("Number4", "KeyHash:" + "fail");
//        } catch (java.security.NoSuchAlgorithmException e) {
//            Log.d("Number4", "KeyHash:" + "fail 1");
//        }

        if (savedInstanceState != null) {
            optionList = savedInstanceState.getParcelableArrayList(Constant.SAVE_LIST_OPTIONS);
            selectList = savedInstanceState.getParcelableArrayList(Constant.SAVE_LIST_SELECT);
            isShowRename = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_RENAME);
            if (isShowRename) {
                isFolderRename = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_FOLDER_RENAME);
                fileRename = savedInstanceState.getString(Constant.SAVE_FILE_NAME);
            }

            isShowCompress = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_COMPRESS);
            if (isShowCompress) {
                fileNameCompress = savedInstanceState.getString(Constant.SAVE_FILE_NAME_COMPRESS);
                folderCompress = savedInstanceState.getString(Constant.SAVE_FOLDER_NAME_COMPRESS);
                passCompress = savedInstanceState.getString(Constant.SAVE_PASS_COMPRESS);
                typeCompress = savedInstanceState.getInt(Constant.SAVE_TYPE_COMPRESS);
            }

            isShowExtract = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_EXTRACT);
            if (isShowExtract) {
                fileNameExtract = savedInstanceState.getString(Constant.SAVE_FILE_NAME_EXTRACT);
                folderExtract = savedInstanceState.getString(Constant.SAVE_FOLDER_NAME_EXTRACT);
                optionExtract = savedInstanceState.getInt(Constant.SAVE_OPTION_EXTRACT);
            }

            isShowBottomSheet = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_BOTTOM_SHEET);
            if (isShowBottomSheet) {
                pathBottomSheet = savedInstanceState.getString(Constant.SAVE_PATH_BOTTOM_SHEET);
            }
            isShowDelete = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_DELETE);
            isShowPassDialog = savedInstanceState.getBoolean(Constant.SAVE_BOOLEAN_SHOW_PASS_DIALOG);
            passExtract = savedInstanceState.getString(Constant.SAVE_PASS_EXTRACT);

        }
        intentFilter = new IntentFilter(Constant.ACTION_DOWNLOAD_PROCESS);
        requestPermissionDialog = new RequestPermissionDialog(this);
        requestPermission();
        setupView();
        observer();
        eventListener();
        observerData();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        isKillApp = true;
        outState.putParcelableArrayList(Constant.SAVE_LIST_OPTIONS, (ArrayList<? extends Parcelable>) optionList);
        outState.putParcelableArrayList(Constant.SAVE_LIST_SELECT, (ArrayList<? extends Parcelable>) selectList);
        if (renameDialogNormal.isShowing()) {
            outState.putString(Constant.SAVE_FILE_NAME, renameDialogNormal.getFileName());
            outState.putBoolean(Constant.SAVE_BOOLEAN_FOLDER_RENAME, renameDialogNormal.isFolder());
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_RENAME, true);
        } else outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_RENAME, false);
        if (compressDialog.isShowing()) {
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_COMPRESS, true);
            outState.putString(Constant.SAVE_FILE_NAME_COMPRESS, compressDialog.getName());
            outState.putString(Constant.SAVE_FOLDER_NAME_COMPRESS, compressDialog.getFolder());
            outState.putString(Constant.SAVE_PASS_COMPRESS, compressDialog.getPass());
            outState.putInt(Constant.SAVE_TYPE_COMPRESS, compressDialog.getType());
        } else outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_COMPRESS, false);
        if (extractDialog.isShowing()) {
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_EXTRACT, true);
            outState.putString(Constant.SAVE_FILE_NAME_EXTRACT, extractDialog.getName());
            outState.putString(Constant.SAVE_FOLDER_NAME_EXTRACT, extractDialog.getFolder());
            outState.putInt(Constant.SAVE_OPTION_EXTRACT, extractDialog.getOptionExtract());
        } else outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_EXTRACT, false);

        outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_DELETE, deleteDialog.isShowDelete());
        if (passwordDialog.isShowing()) {
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_PASS_DIALOG, true);
            outState.putString(Constant.SAVE_PASS_EXTRACT, passwordDialog.getPass());
        } else {
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_PASS_DIALOG, false);
        }
        if (bottomSheetDialog.isShowing()) {
            outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_BOTTOM_SHEET, true);
            outState.putString(Constant.SAVE_PATH_BOTTOM_SHEET, bottomSheetDialog.getPath());
        } else outState.putBoolean(Constant.SAVE_BOOLEAN_SHOW_BOTTOM_SHEET, false);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void setupData() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(downloadCloudReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(downloadCloudReceiver);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(downloadCloudReceiver, intentFilter);

        Utils.resetLastClickTime();
        if (Utils.checkPermissions(this)) {
//            viewModel.clearListDownload();
            viewModel.getAllFile(this);
        }else {
            selectList.clear();
            hideOptions();
            hideOnlyPaste();
        }
        checkListSelected();
    }

    public boolean isShowDialogRequestPermission() {
        return requestPermissionDialog.isShowing();
    }

    private void checkListSelected() {
        for (int i = selectList.size() - 1; i >= 0; i--) {
            String path = selectList.get(i).getFilePath();
            File file = new File(path);
            if (!file.exists()) {
                selectList.remove(selectList.get(i));
            }
        }
        if (detailSelectAdapter != null) {
            detailSelectAdapter.setFileDataList(selectList);
        }
        if (selectList.size() == 0) {
            hideOptions();
            binding.cslPaste.setVisibility(View.GONE);
            viewModel.isShowDetailSelect.setValue(false);
        }

    }

    private void setupView() {
        if (renameDialogNormal != null) renameDialogNormal.dismiss();
        else renameDialogNormal = new RenameDialogNormal(this, R.style.DialogTheme);
        renameDialogNormal.setMainViewModel(viewModel);
        if (isShowRename) {
            renameDialogNormal.setFolder(isFolderRename);
            renameDialogNormal.setFileName(fileRename);
            renameDialogNormal.show();
            renameDialogNormal.showKeyboard();
        }

        if (compressDialog != null) compressDialog.dismiss();
        else compressDialog = new CompressDialog(this, R.style.DialogTheme2);
        compressDialog.setMainViewModel(viewModel);
        compressDialog.setOnPassDataCompress(this);
        if (isShowCompress) {
            if (fileNameCompress != null)
                compressDialog.setName(fileNameCompress);
            if (folderCompress != null) compressDialog.setFolder(folderCompress);
            compressDialog.setType(typeCompress);
            if (passCompress != null) compressDialog.setPass(passCompress);
            compressDialog.show();
        }

        if (extractDialog != null) extractDialog.dismiss();
        else
            extractDialog = new ExtractDialog(this, R.style.DialogTheme2);
        extractDialog.setOnPassDataExtract(this);
        extractDialog.setMainViewModel(viewModel);
        if (isShowExtract) {
            if (fileNameExtract != null) extractDialog.setName(fileNameExtract);
            if (folderExtract != null) extractDialog.setFolder(folderExtract);
            extractDialog.setOptionExtract(optionExtract);
            extractDialog.show();
        }

        if (bottomSheetDialog != null) bottomSheetDialog.dismiss();
        else
            bottomSheetDialog = new BottomSheetBrowse(this, viewModel.fileDataRepository);
        if (isShowBottomSheet) {
            bottomSheetDialog.setPath(pathBottomSheet);
            bottomSheetDialog.createAdapter();
            bottomSheetDialog.show();
        }

        if (deleteDialog != null) deleteDialog.dismiss();
        else deleteDialog = new DeleteDialog(this, R.style.DialogTheme);
        deleteDialog.setMainViewModel(viewModel);
        if (isShowDelete) {
            if (selectList.size() == 1)
                deleteDialog.setCountListDelete(selectList.get(0).getFileName());
            else if (selectList.size() > 1)
                deleteDialog.setCountListDelete(String.valueOf(selectList.size()).concat(Constant.ITEMS));
            deleteDialog.show();
        }

        passwordDialog = new PasswordDialog(this, R.style.DialogTheme);
        if (isShowPassDialog) {
            if (passExtract != null) passwordDialog.setPass(passExtract);
            passwordDialog.show();
        }

        viewModel.getListOptionBottom(this);
        navHostFragmentMain = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_main_fragment);
        if (navHostFragmentMain != null) {
            navControllerMain = navHostFragmentMain.getNavController();
        }

        optionSelectBottomAdapter = new OptionSelectBottomAdapter(this);
        if (optionList != null && optionList.size() > 0)
            optionSelectBottomAdapter.setList(optionList);
        optionSelectBottomAdapter.setOnClickItemBottomListener(this);
        LinearLayoutManager linearLayoutManagerBottom = new LinearLayoutManager(this);
        linearLayoutManagerBottom.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcvOptionSelect.setLayoutManager(linearLayoutManagerBottom);
        binding.rcvOptionSelect.setAdapter(optionSelectBottomAdapter);

        if (detailSelectAdapter == null)
            detailSelectAdapter = new DetailSelectAdapter();
        detailSelectAdapter.setFileDataRepository(fileDataRepository);
        if (selectList != null && selectList.size() > 0) {
            detailSelectAdapter.setFileDataList(selectList);
            showOptions();
        }

        detailSelectAdapter.setOnClickItemDetail(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rcvDetailItemSelect.setLayoutManager(linearLayoutManager);
        binding.rcvDetailItemSelect.setAdapter(detailSelectAdapter);

        recursiveFileObserver = new RecursiveFileObserver(this::updateSelectedList);

        progressDialog = new MyProgressDialog(this);
    }

    private void updateSelectedList(String oldPath, String newPath) {
        boolean b = MainActivity.this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
        if (b) {
            return;
        }
        for (int i = 0; i < selectList.size(); i++) {
            if (selectList.get(i).getFilePath().equals(oldPath)) {
                selectList.remove(selectList.get(i));
                selectList.add(i, viewModel.getFileDataByPath(MainActivity.this, newPath));
                runOnUiThread(() -> detailSelectAdapter.setFileDataList(selectList));
                break;
            }
        }
    }

    public void startWatchingFile(List<FileData> list) {
        stopWatchingFile();
        List<String> parent = new ArrayList<>();

        for (FileData f : list) {
            if (f.getFilePath() != null) {
                File p = new File(f.getFilePath());
                if (!checkFileInList(parent, p.getParent())) {
                    parent.add(p.getParent());
                }
            }
        }
        recursiveFileObserver.startWatching(parent);
    }

    public void startWatchingFile(String parent) {
        stopWatchingFile();
        recursiveFileObserver.startWatching(parent);
    }

    private boolean checkFileInList(List<String> files, String path) {
        for (String s : files) {
            if (s.equals(path)) {
                return true;
            }
        }
        return false;
    }

    public void stopWatchingFile() {
        if (recursiveFileObserver != null) {
            recursiveFileObserver.stopWatching();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopWatchingFile();
    }

    private void eventListener() {
        binding.tvCancelPaste.setOnClickListener(v -> hideOptionPaste());
        binding.tvPaste.setOnClickListener(v -> {
            if (isPaste) {
                if (Utils.checkListPaste(selectList, pathPaste)) {
                    copyFile();
                } else {
                    Toast.makeText(this, this.getString(R.string.warning_paste_sub_folder), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvDeselect.setOnClickListener(v -> cancelAllItem());
        binding.tvOptionSelect.setOnClickListener(v -> viewModel.changStateShow());
        binding.ivOptionSelect.setOnClickListener(v -> viewModel.changStateShow());
    }

    private void copyFile() {
        progressDialog.show(getString(R.string.copying));
        viewModel.copyPasteTask(MainActivity.this, selectList, pathPaste).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                viewModel.compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean unused) {
                progressDialog.dismiss();
                for (int i = 0; i < selectList.size(); i++) {
                    Log.d("Number4", "onSuccess copy: " );
                    File file = new File(selectList.get(i).getFilePath());
                    if (file.isFile()) viewModel.insertRecent(file.getAbsolutePath());
                }
                viewModel.reload.setValue(true);
                selectList.clear();
                hideOptionPaste();
                hideOptions();
                detailSelectAdapter.clearList();
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observer() {
        viewModel.gotoSetting.observe(this, data -> {
            if (data) {
                gotoSettingRequestPermission();
            }
        });

        viewModel.reload.observe(this, data -> {
            if (data) {
                viewModel.getAllFile(this);
                viewModel.isShowDetailSelect.postValue(false);
                hideOptions();
                binding.cslPaste.setVisibility(View.GONE);
            }
        });

        viewModel.showBrowse.observe(this, show -> {
            if (show) {
                bottomSheetDialog.createAdapter();
                bottomSheetDialog.show();
                viewModel.showBrowse.setValue(false);
            }
        });
        viewModel.isDelete.observe(this, delete -> {
            if (delete instanceof EventModifyName && ((EventModifyName) delete).isDelete()) {
                deleteFile();
            }
        });
        viewModel.pathPaste.observe(this, path -> {
            if (path instanceof EventPath)
                pathPaste = ((EventPath) path).getPath();
        });
        viewModel.allowPaste.observe(this, allowPaste -> {
            isPaste = allowPaste;
            if (allowPaste) {
                binding.tvPaste.setClickable(true);
                binding.tvPaste.setAlpha(1f);

            } else {
                binding.tvPaste.setClickable(false);
                binding.tvPaste.setAlpha(0.5f);
            }
        });

        viewModel.nameFileToChange.observe(this, name -> {
            if (name instanceof EventModifyName) {
                String oldFile = selectList.get(0).getFilePath();
                String newName = Utils.renameFile(oldFile, ((EventModifyName) name).getNewName());
                File newFile = new File(newName);
                MediaScannerConnection.scanFile(MainActivity.this, new String[]{newName, oldFile}, null, null);
                if (newFile.isFile()) viewModel.insertRecent(newName);
                selectList.clear();
                viewModel.listSelect.setValue(selectList);
                hideOptions();
                viewModel.renameSuccess.setValue(new EventRename(oldFile, newFile.getAbsolutePath()));
                Toast.makeText(this, getResources().getString(R.string.rename_success), Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.listOptionBottom.observe(this, data -> {
            if (data != null) {
                optionList.clear();
                optionList.addAll(data);
                optionSelectBottomAdapter.setList(optionList);
            }
        });
        viewModel.listSelect.observe(this, data -> {
            if (data != null) {
                detailSelectAdapter.setFileDataList(data);
                if (selectList.size() != 1)
                    binding.tvNameFilePaste.setText(String.valueOf(selectList.size()).concat(Constant.ITEMS));
                else binding.tvNameFilePaste.setText(selectList.get(0).getFileName());
            } else {
                selectList.clear();
                detailSelectAdapter.setFileDataList(selectList);
            }
        });
        viewModel.isShowDetailSelect.observe(this, data -> {
            if (data) {
                binding.ivOptionSelect.setImageResource(R.drawable.ic_arrow_down);
                animationShowListSelected();
                isShowDetail = true;

            } else {
                isShowDetail = false;
                binding.ivOptionSelect.setImageResource(R.drawable.ic_arrow_up);
                animationHideListSelected();
            }
        });
        viewModel.isListArchive.observe(this, data -> {
            if (data && optionSelectBottomAdapter != null) {
                isListArchive = true;
                if (selectList.size() == 1) {
                    optionSelectBottomAdapter.changeDataOption(true, true);
                } else optionSelectBottomAdapter.changeDataOption(false, false);

            } else {
                isListArchive = false;
                if (selectList.size() == 1) {
                    assert optionSelectBottomAdapter != null;
                    optionSelectBottomAdapter.changeDataOption(true, false);
                } else {
                    assert optionSelectBottomAdapter != null;
                    optionSelectBottomAdapter.changeDataOption(false, false);
                }
            }
        });
        viewModel.showDrawer.observe(this, data -> {
            isShowDrawer = data;
            if (data) {
                hideOptions();
                binding.cslPaste.setVisibility(View.GONE);
            } else {
                if (selectList.size() > 0) {
                    if (isShowPaste) {
                        binding.cslOptionSelect.setVisibility(View.VISIBLE);
                        binding.view.setVisibility(View.VISIBLE);
                        showOptionPaste();
                    } else
                        showOptions();
                }
            }
        });
    }

    private void deleteFile() {
        progressDialog.show(getString(R.string.deleting));
        viewModel.deleteFile(selectList).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                viewModel.compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Boolean unused) {
                progressDialog.dismiss();
                selectList.clear();
                detailSelectAdapter.setFileDataList(selectList);
                hideOptions();
                viewModel.isShowDetailSelect.setValue(false);
                viewModel.isResetList.setValue(false);
                viewModel.isResetListHome.setValue(false);
                viewModel.reload.setValue(true);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animationShowListSelected() {
        binding.rcvDetailItemSelect.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(binding.rcvDetailItemSelect.getHeight(), 0);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            binding.rcvDetailItemSelect.setTranslationY(value);
        });
        valueAnimator.start();
    }

    private void animationHideListSelected() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, binding.rcvDetailItemSelect.getHeight());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            binding.rcvDetailItemSelect.setTranslationY(value);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.rcvDetailItemSelect.setVisibility(View.INVISIBLE);
            }
        });
        valueAnimator.start();
    }

    @SuppressLint("SetTextI18n")
    public void showOptions() {
        if(!Utils.checkPermissions(this)){
            hideOptions();
        }else {
            App.getInstance().checkShowOptions = true;
            viewModel.haveFileSelect.postValue(true);
            binding.cslOptionSelect.setVisibility(View.VISIBLE);
            binding.rcvOptionSelect.setVisibility(View.VISIBLE);
            binding.view.setVisibility(View.VISIBLE);
            binding.tvOptionSelect.setText(Constant.SELECTED + selectList.size() + Constant.ITEMS);
            viewModel.checkListArchive(selectList);
        }
    }

    public void showOptionPaste() {
        isShowPaste = true;
        binding.rcvOptionSelect.setVisibility(View.GONE);
        binding.cslPaste.setVisibility(View.VISIBLE);
    }

    public void hideOptionPaste() {
        isShowPaste = false;
        binding.cslPaste.setVisibility(View.GONE);
        binding.rcvOptionSelect.setVisibility(View.VISIBLE);
    }

    public void hideOnlyPaste() {
        binding.cslPaste.setVisibility(View.GONE);
    }

    public void hideOptions() {
        viewModel.haveFileSelect.postValue(false);
        viewModel.listSelect.postValue(selectList);
        binding.cslOptionSelect.setVisibility(View.GONE);
        binding.rcvOptionSelect.setVisibility(View.GONE);
        binding.view.setVisibility(View.GONE);
    }

    public void backToScreen(int idScreen) {
        navControllerMain.popBackStack(idScreen, false);
    }

    public void popBackStack() {
        navControllerMain.popBackStack();
    }

    public void changeExplorerScreen(int idScreen) {
        changeScreen(idScreen, null);
    }

    public void changeScreen(int idStart, Bundle bundle) {
        navControllerMain.navigate(idStart, bundle);
    }

    public void changeScreenTypeFile(Bundle bundle) {
        navControllerMain.navigate(R.id.docFragment, bundle);
    }

    public void changeScreenTypeMedia(Bundle bundle) {
        navControllerMain.navigate(R.id.photoFragment, bundle);
    }

    private void shareListFile(List<FileData> fileDataList) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, Constant.SHARE_FILE);
        intent.setType("*/*"); /* allow any file type */

        ArrayList<Uri> files = new ArrayList<>();
        for (int i = 0; i < fileDataList.size(); i++ /* List of the files you want to send */) {
            File file = new File(fileDataList.get(i).getFilePath());
            if (!file.isDirectory()) {
                viewModel.insertRecent(file.getAbsolutePath());
            }
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
        new Handler().postDelayed(() -> {
            viewModel.reload.setValue(true);
            selectList.clear();
            hideOptions();
        }, 100);
    }

    @Override
    public void onClickItemBottom(int position) {
        if (position == Constant.POSITION_RENAME) {
            if (selectList.size() == 1) {
                renameDialogNormal.setShowRename(true);
                fileRename = selectList.get(0).getFileName();
                isFolderRename = selectList.get(0).isFolder();
                renameDialogNormal.setFolder(isFolderRename);
                renameDialogNormal.setFileName(fileRename);
                renameDialogNormal.show();
                renameDialogNormal.showKeyboard();
                viewModel.isShowDialog.postValue(true);
            }

        } else if (position == Constant.POSITION_COPY) {
            if (selectList.size() > 0) {
                if (selectList.size() != 1)
                    binding.tvNameFilePaste.setText(String.valueOf(selectList.size()).concat(Constant.ITEMS));
                else binding.tvNameFilePaste.setText(selectList.get(0).getFileName());
                showOptionPaste();
            }

        } else if (position == Constant.POSITION_EXTRACT) {
            if (isListArchive) {
                if (fileDataRepository.isArchiveNotSupport(selectList.get(0).getFileName())) {
                    Toast.makeText(this, MainActivity.this.getString(R.string.the_file_not_support_decompress), Toast.LENGTH_SHORT).show();
                } else {
                    String folder = selectList.get(0).getFilePath().substring(0, selectList.get(0).getFilePath().lastIndexOf(File.separator));
                    extractDialog.setFolder(folder);
                    extractDialog.setShowExtract(true);
                    extractDialog.show();
                    viewModel.isShowDialog.postValue(true);

                }
            }

        } else if (position == Constant.POSITION_COMPRESS) {
            ArrayList<FileData> fileData = new ArrayList<>(selectList);
            Collections.sort(fileData, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    int x1 = o1.getFilePath().length() - o1.getFilePath().replace(File.separator, "").length();
                    int x2 = o2.getFilePath().length() - o2.getFilePath().replace(File.separator, "").length();
                    if (x1 > x2) return 1;
                    else return -1;
                }
            });
            String nameFile;
            if (fileData.size() > 1) {
                nameFile = Constant.DEFAULT_NAME_COMPRESS_LIST;
            } else {
                if (fileData.get(0).isFolder()) {
                    nameFile = fileData.get(0).getFileName();
                } else {
                    nameFile = fileData.get(0).getFileName().substring(0, fileData.get(0).getFileName().indexOf("."));
                }
            }
            String path = fileData.get(0).getFilePath();
            String folderParent = path.substring(0, path.lastIndexOf(File.separator));
            String pathFolderFinal = Utils.getPathFolder(fileData, folderParent);

            compressDialog.setFolder(pathFolderFinal);
            compressDialog.setName(nameFile);
            compressDialog.setShowDialog(true);
            compressDialog.show();
            viewModel.isShowDialog.postValue(true);

        } else if (position == Constant.POSITION_DELETE) {
            deleteDialog.setShowDelete(true);
            if (selectList.size() == 1)
                deleteDialog.setCountListDelete(selectList.get(0).getFileName());
            else if (selectList.size() > 1)
                deleteDialog.setCountListDelete(String.valueOf(selectList.size()).concat(Constant.ITEMS));
            deleteDialog.show();
        } else if (position == Constant.POSITION_SHARE) {
            shareListFile(selectList);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickCancelItem(FileData path1) {
        EventBus.getDefault().post(new EventPath(Constant.EVENT_DELETE_ITEM, path1.getFilePath()));

        int p = Utils.checkSelectFile(selectList, path1.getFilePath());
        if (p != -1) {
            selectList.remove(p);
        }
        detailSelectAdapter.setFileDataList(selectList);

        if (detailSelectAdapter.getItemCount() != 1) {
            binding.tvNameFilePaste.setText(String.valueOf(detailSelectAdapter.getItemCount()).concat(Constant.ITEMS));
        } else
            binding.tvNameFilePaste.setText(detailSelectAdapter.getFileDataList().get(0).getFileName());

        binding.tvOptionSelect.setText(Constant.SELECTED + detailSelectAdapter.getItemCount() + Constant.ITEMS);
        viewModel.checkListArchive(detailSelectAdapter.getFileDataList());
        if (detailSelectAdapter.getItemCount() == 0) {
            hideOptions();
            binding.cslPaste.setVisibility(View.GONE);
            viewModel.isShowDetailSelect.setValue(false);

        }
    }

    private void cancelAllItem() {
        EventBus.getDefault().post(new EventPath(Constant.EVENT_DELETE_ALl_ITEM, ""));
        selectList.clear();
        hideOptions();
        binding.cslPaste.setVisibility(View.GONE);
        viewModel.isShowDetailSelect.setValue(false);
    }

    @Override
    public void onBackPressed() {
        if (isShowDrawer) {
            viewModel.isShowDrawerBack.postValue(true);
        } else if (isShowDetail) {
            viewModel.isShowDetailSelect.postValue(false);
        } else if (selectList.size() > 0
                && navControllerMain.getCurrentBackStackEntry() != null
                && navControllerMain.getCurrentBackStackEntry().getDestination().getId() == R.id.homeFragment) {
            cancelAllItem();
        } else {
            super.onBackPressed();
        }
    }

    private void requestPermission() {
        if (!Utils.checkPermissions(this)) {
            checkForExternalPermission();
        }
    }

    public void checkForExternalPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissionDialog.show(this::gotoSettingRequestPermission);
        } else {
            ActivityCompat.requestPermissions(this, permissions, Constant.WRITE_REQUEST_CODE);
        }
    }

    private void gotoSettingRequestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent =
                        new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                .addCategory("android.intent.category.DEFAULT")
                                .setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, Constant.MANAGE_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, Constant.MANAGE_REQUEST_CODE);
            }
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void setLoginGoogleListener(LoginGoogleListener loginGoogleListener) {
        this.loginGoogleListener = loginGoogleListener;
    }

    public void setLoginGoogleFailListener(LoginGoogleFailListener loginGoogleFailListener) {
        this.loginGoogleFailListener = loginGoogleFailListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    loginGoogleListener.loginDrive(data);
                }
                break;
            case Constant.REQUEST_CODE_SHOW_DETAIL_FAIL:
                loginGoogleFailListener.onLoginFail(resultCode == Activity.RESULT_OK && data != null);
                break;
            case REQUEST_SHARE_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    startActivity(data);
                } else {
                    viewModel.eventShare.postValue(true);
                }
                break;
            case KEY_OPEN_FILE:
                viewModel.checkOpenFile.postValue(true);
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDataCompress(String name, String folder, String pass, String type, String content) {
        Bundle bundle = new Bundle();
        String[] listPath;
        List<String> stringList = new LinkedList<>();
        for (int i = 0; i < selectList.size(); i++) {
            stringList.add(selectList.get(i).getFilePath());
        }
        listPath = stringList.toArray(new String[0]);
        bundle.putString(Constant.ACTION_PROCESS, Constant.ACTION_START);
        bundle.putString(Constant.KEY_PASS, pass);
        bundle.putStringArray(Constant.KEY_PATH, listPath);
        bundle.putString(Constant.KEY_FOLDER, folder);
        bundle.putString(Constant.KEY_NAME, name);
        bundle.putString(Constant.KEY_TYPE, type);
        bundle.putString(Constant.KEY_CONTENT_PROGRESS, content);
        changeScreen(R.id.process_fragment, bundle);
        selectList.clear();
        hideOptions();
    }

    @Override
    public void onPassDataExtract(String name, String folder, String content, int option) {
        List<String> listString = new LinkedList<>();
        String[] listFileArchive;
        for (int i = 0; i < selectList.size(); i++) {
            listString.add(selectList.get(i).getFilePath());
        }
        listFileArchive = listString.toArray(new String[0]);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ACTION_PROCESS, Constant.ACTION_START);
        bundle.putStringArray(Constant.KEY_PATH, listFileArchive);
        bundle.putString(Constant.KEY_NAME, name);
        bundle.putString(Constant.KEY_FOLDER, folder);
        bundle.putString(Constant.KEY_CONTENT_PROGRESS, content);
        bundle.putInt(Constant.KEY_OPTION_EXTRACT, option);
        bundle.putInt(Constant.KEY_COUNT_EXTRACT, countExtractFail);
        changeScreen(R.id.process_fragment, bundle);
        selectList.clear();
        hideOptions();

    }

    @Override
    public void onOkClickListener(String password, int countExtractFail) {
        this.countExtractFail = countExtractFail;
    }

    @Override
    public void onBack() {
        popBackStack();
    }

    private void observerData() {
        viewModel.getGoogleSignInClient.observe(this, data -> {
            if (data != null) {
                startActivityForResult(((EventLoginGoogleDrive) data).getGoogleSignInClient().getSignInIntent(), REQUEST_CODE_SIGN_IN);
                viewModel.getGoogleSignInClient.setValue(null);
            }
        });
        viewModel.showGoogleDriveDetailFail.observe(this, data -> {
            if (data != null) {
                startActivityForResult(((EventShowGoogleDrive) data).getIntent(), Constant.REQUEST_CODE_SHOW_DETAIL_FAIL);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesReceived(EventPath event) {
        if (event.getType() == Constant.EVENT_RELOAD_FILE_DATA) {
            viewModel.getAllFile(this);
        }
    }

}