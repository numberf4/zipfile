package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.models.EventPathCompress;
import vn.tapbi.zazip.databinding.DialogCompressBinding;
import vn.tapbi.zazip.interfaces.OnPassDataCompress;
import vn.tapbi.zazip.ui.main.MainViewModel;
import vn.tapbi.zazip.utils.Utils;

public class CompressDialog extends Dialog {
    private boolean isShowPass = false;
    private DialogCompressBinding binding;
    private OnPassDataCompress onPassDataCompress;
    private String type;
    private String pass;
    private MainViewModel mainViewModel;
    private ArrayAdapter<CharSequence> adapter;
    private final Context context;
    private InputMethodManager imm;
    private boolean isShowDialog;
    private String name, folder;
    private int idSpinner;

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String convertFileName(String folder, String fileName, String suffix) {
        return Utils.getUniqueFileName(folder, fileName + "." + suffix, false);
    }

    public String getName() {
        return binding.edtCompressFileName.getText().toString().trim();
    }

    public void setFolder(String folder) {
        this.folder = folder;
        if (this.folder != null && binding != null) {
            binding.tvCompressFolder.setText(this.folder);
        }
    }

    public void setOnPassDataCompress(OnPassDataCompress onPassDataCompress) {
        this.onPassDataCompress = onPassDataCompress;
    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public String getFolder() {
        return binding.tvCompressFolder.getText().toString().trim();
    }

    public int getType() {
        return idSpinner;
    }

    public String getPass() {
        return binding.edtPass.getText().toString().trim();
    }

    public void setType(int type) {
        this.idSpinner = type;
        if (binding != null) binding.spinnerFormat.setSelection(this.idSpinner);
    }

    public void setPass(String pass) {
        this.pass = pass;
        if (this.pass != null && binding != null) binding.edtPass.setText(this.pass);
    }

    public CompressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_compress, null, false);
        binding.setMainViewModel(mainViewModel);
        setContentView(binding.getRoot());
        setupView();
        observer();
        eventClick();
        eventBack();
    }

    private void setupView() {
        isShowDialog = true;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.edtCompressFileName.post(() -> {
            if (name != null && folder != null) {
                binding.edtCompressFileName.setText(convertFileName(folder, name, "7z"));
                binding.tvCompressFolder.setText(folder);
                showKeyboard();
            }
            binding.edtCompressFileName.setFilters(new InputFilter[]{Utils.filter});
            if (pass != null)
                binding.edtPass.setText(pass);
            binding.spinnerFormat.setSelection(idSpinner);
        });
        binding.edtPass.setTransformationMethod(new PasswordTransformationMethod());
        adapter = ArrayAdapter.createFromResource(context,
                R.array.archive_format, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item_down);
        binding.spinnerFormat.setAdapter(adapter);


    }

    private void observer() {
        if (mainViewModel != null) {
            mainViewModel.pathCompressLiveEvent.observe((LifecycleOwner) context, namePath -> {
                if (namePath instanceof EventPathCompress) {
                    binding.tvCompressFolder.setText(((EventPathCompress) namePath).getPath());
                    binding.edtCompressFileName.setText(convertFileName(((EventPathCompress) namePath).getPath(), name, type));
                    binding.edtCompressFileName.setSelection(binding.edtCompressFileName.getText().toString().indexOf("."));
                    if (!imm.isAcceptingText()){
                        showKeyboard();
                    }
                }
            });
            mainViewModel.archiveFormat.observe((LifecycleOwner) context, format -> {
                if (format != null){
                    binding.edtCompressFileName.setText(convertFileName(folder, name, format));
                    binding.edtCompressFileName.setSelection(binding.edtCompressFileName.getText().toString().indexOf("."));
                    if (!imm.isAcceptingText()){
                        showKeyboard();
                    }
                }
            });
        }
    }


    private void resetView() {
        if (binding.edtPass.isFocusable()) {
            binding.edtPass.clearFocus();
        }
        binding.edtCompressFileName.getText().clear();
        binding.edtPass.getText().clear();
        binding.spinnerFormat.setAdapter(adapter);
        binding.tvCompressFolder.setText("");
        binding.tvWarningFormatBeta.setVisibility(View.GONE);
    }

    private void eventClick() {
        binding.spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idSpinner = position;
                if (position == 0 || position == 1) {
                    binding.tvCompressPassword.setVisibility(View.VISIBLE);
                    binding.ctlPass.setVisibility(View.VISIBLE);
                    binding.edtPass.setEnabled(true);
                    type = parent.getItemAtPosition(position).toString();
                    binding.tvWarningFormatBeta.setVisibility(View.GONE);
                } else {
                    binding.edtPass.setEnabled(false);
                    binding.tvCompressPassword.setVisibility(View.GONE);
                    binding.ctlPass.setVisibility(View.GONE);
                    binding.edtPass.setText("");
                    if (position != 2){
                        binding.tvWarningFormatBeta.setVisibility(View.VISIBLE);
                    }
                    if (position == 7){
                        type = "tar.zst";
                    }else type = parent.getItemAtPosition(position).toString();
                }
                mainViewModel.archiveFormat.setValue(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.tvCompressFolder.setOnClickListener(v -> {
            mainViewModel.pathFolder.postValue(binding.tvCompressFolder.getText().toString().trim());
            mainViewModel.showBrowse.postValue(true);
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        });
        binding.ivShowPass.setOnClickListener(v -> changeStateShowPass());
        binding.ivCancelDialog.setOnClickListener(v -> {
            resetView();
            dismiss();
            isShowDialog = false;
        });
        binding.btnCancel.setOnClickListener(v -> {
            resetView();
            dismiss();
            isShowDialog = false;
        });

        binding.btnCompress.setOnClickListener(v -> {
            if (binding.edtCompressFileName.getText().toString().trim().isEmpty()) {
                Toast.makeText(context, context.getString(R.string.warning_name_file_empty), Toast.LENGTH_SHORT).show();
            } else {
                if (binding.edtPass.getText().toString().trim().isEmpty()) pass = "";
                else pass = binding.edtPass.getText().toString().trim();
                String fileName = binding.edtCompressFileName.getText().toString().trim();
                onPassDataCompress.onDataCompress(fileName.substring(0, fileName.indexOf(".")),
                        binding.tvCompressFolder.getText().toString().trim(),
                        pass, type, context.getString(R.string.adding));

                resetView();
                isShowDialog = false;
                dismiss();
            }
        });

    }

    private void eventBack() {
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    isShowDialog = false;
                    resetView();
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void changeStateShowPass() {
        isShowPass = !isShowPass;
        if (isShowPass) {
            binding.ivShowPass.setImageResource(R.drawable.ic_hide_pass);
            binding.edtPass.setTransformationMethod(null);
        } else {
            binding.ivShowPass.setImageResource(R.drawable.ic_show_pass);
            binding.edtPass.setTransformationMethod(new PasswordTransformationMethod());
        }
        binding.edtPass.setSelection(binding.edtPass.getText().length());
    }

    public void showKeyboard() {
        if (imm != null && binding != null) {
            binding.edtCompressFileName.postDelayed(() -> {
                binding.edtCompressFileName.requestFocus();
                imm.showSoftInput(binding.edtCompressFileName, InputMethodManager.SHOW_IMPLICIT);
                binding.edtCompressFileName.setSelection(binding.edtCompressFileName.getText().toString().indexOf("."));
            }, 100);
        }
    }
}
