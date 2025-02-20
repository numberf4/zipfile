package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventModifyName;
import vn.tapbi.zazip.databinding.DialogRenameBinding;
import vn.tapbi.zazip.ui.main.MainViewModel;
import vn.tapbi.zazip.utils.Utils;

public class RenameDialogNormal extends Dialog {
    private DialogRenameBinding binding;
    private String fileName, suffixName;
    private boolean isFolder;
    private Dialog dialog;
    private MainViewModel mainViewModel;
    private InputMethodManager imm;
    private boolean isShowRename;


    public boolean isShowRename() {
        return isShowRename;
    }

    public void setShowRename(boolean showRename) {
        isShowRename = showRename;
    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public RenameDialogNormal(@NonNull Context context, int themeResId) {
        super(context, themeResId);

    }

    public void setFolder(boolean folder) {
        this.isFolder = folder;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getFileName() {
        return binding.edtDialogReName.getText().toString().trim();
    }

    private String getSuffixName(String fileName) {
        if (fileName.endsWith(Constant.ARCHIVE_TAR_BZ2)) {
            return Constant.ARCHIVE_TAR_BZ2;
        } else if (fileName.endsWith(Constant.ARCHIVE_TAR_XZ)) {
            return Constant.ARCHIVE_TAR_XZ;
        } else if (fileName.endsWith(Constant.ARCHIVE_TAR_GZ)) {
            return Constant.ARCHIVE_TAR_GZ;
        } else if (fileName.endsWith(Constant.ARCHIVE_TAR_LZ4)) {
            return Constant.ARCHIVE_TAR_LZ4;
        } else if (fileName.endsWith(Constant.ARCHIVE_TAR_ZST)) {
            return Constant.ARCHIVE_TAR_ZST;
        } else {
            return fileName.substring(fileName.lastIndexOf("."));
        }
    }

    private void setUpFileName() {
        binding.edtDialogReName.setText(this.fileName);
        binding.edtDialogReName.setFilters(new InputFilter[]{Utils.filter});
        binding.edtDialogReName.post(new Runnable() {
            @Override
            public void run() {
                if (isFolder) {
                    binding.edtDialogReName.setSelection(fileName.length());
                } else {
                    if (fileName.contains(".")) {
                        suffixName = getSuffixName(fileName);
                        binding.edtDialogReName.setSelection(fileName.indexOf(suffixName));
                    } else {
                        binding.edtDialogReName.setSelection(fileName.length());
                    }
                }
            }
        });
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (this.fileName != null && binding != null) {
            setUpFileName();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_rename, null, false);
        binding.setMainViewModel(mainViewModel);
        setContentView(binding.getRoot());
        setupView();
        eventBack();
    }

    private Dialog createWarningDialog() {
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 24);
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning_change_name_file);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.findViewById(R.id.btn_ok_reformat).setOnClickListener(v -> {
            mainViewModel.nameFileToChange.setValue(new EventModifyName(binding.edtDialogReName.getText().toString().trim()));
            binding.edtDialogReName.getText().clear();
            dialog.dismiss();
            dismiss();
        });
        dialog.findViewById(R.id.btn_cancel_reformat).setOnClickListener(v -> {
            dialog.dismiss();
        });
        return dialog;
    }

    private void setupView() {
        isShowRename = true;
        dialog = createWarningDialog();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.edtDialogReName.post(() -> {
            if (fileName != null) {
                setUpFileName();
            }
        });

        binding.ivCancelDialog.setOnClickListener(v -> {
            binding.edtDialogReName.getText().clear();
            isShowRename = false;
            dismiss();
        });
        binding.btnCancel.setOnClickListener(v -> {
            binding.edtDialogReName.getText().clear();
            isShowRename = false;
            dismiss();
        });
        binding.btnOk.setOnClickListener(v -> {
            if (isFolder) {
                if (!binding.edtDialogReName.getText().toString().trim().trim().equals(fileName)
                        && binding.edtDialogReName.getText().toString().trim().trim().length() != 0
                        && binding.edtDialogReName.getText().toString().trim().trim().replace(".", "").trim().length() != 0) {
                    mainViewModel.nameFileToChange.setValue(new EventModifyName(binding.edtDialogReName.getText().toString().trim().trim()));
                    binding.edtDialogReName.getText().clear();
                    isShowRename = false;
                    dismiss();
                } else if (binding.edtDialogReName.getText().toString().trim().trim().length() == 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.warning_name_file_empty), Toast.LENGTH_SHORT).show();
                } else if (binding.edtDialogReName.getText().toString().trim().trim().replace(".", "").replace(" ", "").length() == 0) {
                    Toast.makeText(getContext(), getContext().getString(R.string.rename_wrong_format), Toast.LENGTH_SHORT).show();
                } else if (binding.edtDialogReName.getText().toString().trim().trim().equals(fileName)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.name_file_need_to_be_changed), Toast.LENGTH_SHORT).show();
                }
            } else {

                if (binding.edtDialogReName.getText() != null && suffixName != null) {
                    if (!binding.edtDialogReName.getText().toString().trim().trim().equals(fileName)
                            && binding.edtDialogReName.getText().toString().trim().trim().endsWith(suffixName)
                            && binding.edtDialogReName.getText().toString().trim().replace(suffixName, "").replace(".", "").trim().length() != 0
                            && binding.edtDialogReName.getText().toString().trim().substring(0, binding.edtDialogReName.getText().toString().trim().lastIndexOf(".")).trim().length() != 0) {
                        mainViewModel.nameFileToChange.setValue(new EventModifyName(binding.edtDialogReName.getText().toString().trim().trim()));
                        isShowRename = false;
                        binding.edtDialogReName.getText().clear();
                        dismiss();
                    } else if (!binding.edtDialogReName.getText().toString().trim().contains(".")
                            || binding.edtDialogReName.getText().toString().trim().endsWith(".")) {
                        Toast.makeText(getContext(), getContext().getString(R.string.missing_suffix_name), Toast.LENGTH_SHORT).show();
                    } else if (binding.edtDialogReName.getText().toString().trim().trim().length() == 0
                            || binding.edtDialogReName.getText().toString().trim().substring(0, binding.edtDialogReName.getText().toString().trim().lastIndexOf(".")).trim().length() == 0) {
                        Toast.makeText(getContext(), getContext().getString(R.string.warning_name_file_empty), Toast.LENGTH_SHORT).show();
                    } else if (!binding.edtDialogReName.getText().toString().trim().substring(binding.edtDialogReName.getText().toString().trim().lastIndexOf(".")).equals(suffixName)) {
                        dialog.show();
                    } else if (binding.edtDialogReName.getText().toString().trim().substring(0, binding.edtDialogReName.getText().toString().trim().lastIndexOf(".")).replace(".", "").replace(" ", "").length() == 0) {
                        Toast.makeText(getContext(), getContext().getString(R.string.rename_wrong_format), Toast.LENGTH_SHORT).show();
                    } else if (binding.edtDialogReName.getText().toString().trim().trim().equals(fileName)) {
                        Toast.makeText(getContext(), getContext().getString(R.string.name_file_need_to_be_changed), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private void eventBack() {
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    isShowRename = false;
                    binding.edtDialogReName.getText().clear();
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public void showKeyboard() {
        if (imm != null && binding != null) {
            binding.edtDialogReName.postDelayed(() -> {
                binding.edtDialogReName.requestFocus();
                imm.showSoftInput(binding.edtDialogReName, InputMethodManager.SHOW_IMPLICIT);
            }, 200);
        }
    }
}
