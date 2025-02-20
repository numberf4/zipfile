package vn.tapbi.zazip.ui.dialog;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.Q;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPathCompress;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.ui.adapter.DirectoryAdapter;
import vn.tapbi.zazip.ui.adapter.FileDataAdapter;
import vn.tapbi.zazip.ui.custom.RecyclerFastScroller;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.main.MainViewModel;
import vn.tapbi.zazip.utils.Utils;

public class BottomSheetBrowse extends Dialog implements FileDataAdapter.OnItemClickedListener, DirectoryAdapter.OnClickPathDirectory, DialogInterface.OnDismissListener {
    private String path;
    public MainViewModel mainViewModel;
    private FileDataAdapter fileDataAdapter;
    private DirectoryAdapter directoryAdapter;
    private List<FileData> list = new LinkedList<>();
    private TextView textViewFolder, tvMessage;
    private EditText editText;
    private ImageView ivAllow, ivCancel, ivCancelCreate;
    private ProgressBar progressBar;
    private final Context context;
    private RecyclerView rcvDirectory, rcvListFolder;
    private RecyclerFastScroller fastScroller;
    private final FileDataRepository fileDataRepository;
    private InputMethodManager imm;
    private String filePathCurrent = "";
    private ActivityResultLauncher<Intent> handleDocumentUriForRestrictedDirectories;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (this.path != null && mainViewModel != null) {
            mainViewModel.pathFolder.postValue(this.path);
        }
    }

    public void createAdapter() {
        hideKeyboard();
        fileDataAdapter = new FileDataAdapter(Constant.TYPE_SINGLE);
        fileDataAdapter.setTypePresentationView(0);
        fileDataAdapter.setOnItemClickedListener(this);
        rcvListFolder.setLayoutManager(new LinearLayoutManager(context));
        rcvListFolder.setAdapter(fileDataAdapter);
        rcvListFolder.scrollToPosition(0);
        fastScroller.attachRecyclerView(rcvListFolder);
        fastScroller.attachAdapter(fileDataAdapter);
        fastScroller.setHandlePressedColor(context.getResources().getColor(R.color.scroll_bar));
        fastScroller.setOnHandleTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    rcvListFolder.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            rcvListFolder.setScrollY(dy);
                            super.onScrolled(recyclerView, dx, dy);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        directoryAdapter = new DirectoryAdapter(context);
        directoryAdapter.setOnClickPathDirectory(this::onclickPath);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvDirectory.setLayoutManager(linearLayoutManager);
        rcvDirectory.setAdapter(directoryAdapter);
        new Handler().post(() -> {
            if (path != null) {
                mainViewModel.pathFolder.postValue(path);
            }
        });
    }

    private boolean checkDialogShow() {
        return this.isShowing();
    }

    public BottomSheetBrowse(@NonNull Context context, FileDataRepository fileDataRepository) {
        super(context);
        this.fileDataRepository = fileDataRepository;
        this.context = context;
        setup();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1.6));
        getWindow().setGravity(/*Gravity.CENTER | */Gravity.BOTTOM);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        setOnDismissListener(this);


    }

    protected void setup() {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mainViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(MainViewModel.class);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_browse);
        setCancelable(true);

        this.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                hideOptionCreate();
            }
        });
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideOptionCreate();
            }
        });
        setupView();
        handleDocumentUriForRestrictedDirectories = ((MainActivity) context).registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (SDK_INT >= Q && result.getData() != null) {
                        context.getContentResolver().takePersistableUriPermission(
                                result.getData().getData(),
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                });
    }

    protected void setupView() {
        editText = this.findViewById(R.id.edt_create_folder);
        editText.clearFocus();
        progressBar = this.findViewById(R.id.processBar);
        tvMessage = this.findViewById(R.id.tvMessage);
        ivAllow = this.findViewById(R.id.iv_allow_create);
        ivCancelCreate = this.findViewById(R.id.iv_cancel_create);
        ivCancel = this.findViewById(R.id.iv_cancel_browse);
        textViewFolder = this.findViewById(R.id.tv_create_folder);
        rcvDirectory = this.findViewById(R.id.rcv_directory_browse);
        rcvListFolder = this.findViewById(R.id.rcv_list_folder_browse);
        fastScroller = this.findViewById(R.id.fast_scroll_browse);
        mainViewModel.pathFolder.observe((LifecycleOwner) context, data -> {
            if (data != null) {
                path = data;
                if (directoryAdapter != null) {
                    directoryAdapter.setPath(data);
                    rcvListFolder.scrollToPosition(0);
                    rcvDirectory.scrollToPosition(directoryAdapter.getItemCount() - 1);
                    onFolderDeviceClick(path, false);
                }
            }
        });
        eventClick(context);
    }

    private void showOptionCreate() {
        textViewFolder.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        ivCancelCreate.setVisibility(View.VISIBLE);
        ivAllow.setVisibility(View.VISIBLE);
        editText.performClick();
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setText(" ");
        editText.setFilters(new InputFilter[]{Utils.filter});
        editText.setSelection(editText.getText().length());
        showKeyBoard();
    }

    private void hideOptionCreate() {
        textViewFolder.setVisibility(View.VISIBLE);
        editText.getText().clear();
        editText.setVisibility(View.GONE);
        ivCancelCreate.setVisibility(View.GONE);
        ivAllow.setVisibility(View.GONE);
    }

    private void eventClick(Context context) {
        this.findViewById(R.id.btn_select).setOnClickListener(v -> {
            mainViewModel.pathCompressLiveEvent.postValue(new EventPathCompress(path));
            this.dismiss();
        });
        ivCancel.setOnClickListener(v -> {
            hideKeyboard();
            this.dismiss();
        });
        textViewFolder.setOnClickListener(v -> showOptionCreate());

        ivCancelCreate.setOnClickListener(v -> {
            hideKeyboard();
            new Handler().postDelayed(() -> {
                if (checkDialogShow()) {
                    editText.clearFocus();
                    hideOptionCreate();
                }
            }, 400);
        });
        ivAllow.setOnClickListener(v -> {
            if (editText.getText().toString().trim().isEmpty()) {
                Toast.makeText(context, context.getString(R.string.warning_name_folder_empty), Toast.LENGTH_SHORT).show();
            } else {
                hideKeyboard();
                if (!Utils.userCreateFolder(path, editText.getText().toString().trim())) {
                    Toast.makeText(context, context.getString(R.string.directory_already_exists), Toast.LENGTH_SHORT).show();
                }
                mainViewModel.reloadExplore.setValue(true);
                onFolderDeviceClick(path, false);

                new Handler().postDelayed(() -> {
                    if (checkDialogShow()) {
                        editText.clearFocus();
                        hideOptionCreate();
                    }
                }, 400);
            }
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                ivAllow.performClick();
                return true;
            }
            return false;
        });
    }

    private void showKeyBoard() {
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        if (editText != null) {
            editText.post(() -> imm.hideSoftInputFromWindow(editText.getWindowToken(), 0));

        }
    }

    @Override
    public void onItemClick(int position, FileData path) {
        this.path = list.get(position).getFilePath();
    }

    @Override
    public void onFolderDeviceClick(String filePath, boolean isClickFolder) {
        String actualPath = mainViewModel.getFileProperties().remapPathForApi30OrAbove(filePath, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q && !filePath.equals(actualPath)) {
            boolean hasAccessToSpecialFolder = mainViewModel.getFileProperties().hasAccessToSpecialFolder(context, actualPath);
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
        if (!filePathCurrent.equals(filePath)) {
            filePathCurrent = filePath;
            hideKeyboard();
            ivCancelCreate.setVisibility(View.GONE);
            ivAllow.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
            textViewFolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clickFolderCloud(int position, String path) {

    }

    private void folderClick(String actualPath, String filePath, boolean isClickFolder) {
        File dir = new File(filePath);
        if (dir.isDirectory()) {
            if (!filePath.isEmpty()) {
                this.path = filePath;
                directoryAdapter.setPath(filePath);
                getFolder(actualPath, filePath);
                rcvDirectory.scrollToPosition(directoryAdapter.getItemCount() - 1);
            }
        }
    }

    private void getFolder(String actualPath, String filePath) {
        list.clear();
        progressBar.setVisibility(View.VISIBLE);
        fileDataRepository.getFileInDevice(actualPath, filePath, context, true).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<FileData> fileData) {
                list.clear();
                list.addAll(fileData);
                fileDataAdapter.setList(list);
                rcvListFolder.scrollToPosition(0);
                rcvDirectory.scrollToPosition(directoryAdapter.getItemCount() - 1);
                tvMessage.setVisibility(fileData.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    @Override
    public void onclickPath(String path) {
        onFolderDeviceClick(path, false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        fileDataAdapter.resetPathBrowse();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideKeyboard();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editText != null) editText.clearFocus();
    }
}
