package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.models.EventModifyName;
import vn.tapbi.zazip.databinding.DialogDeleteBinding;
import vn.tapbi.zazip.ui.main.MainViewModel;

public class DeleteDialog extends Dialog {
    private DialogDeleteBinding binding;
    private MainViewModel mainViewModel;
    private String fileName;

    private boolean isShowDelete;

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.isShowDelete = showDelete;
    }

    public void setCountListDelete(String countListDelete) {
        this.fileName = countListDelete;
        if (this.fileName != null && binding != null)
            binding.tvDeleteFileName.setText(this.fileName + " ?");

    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public DeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_delete, null, false);
        binding.setMainViewModel(mainViewModel);
        setContentView(binding.getRoot());
        setupView();
        eventBack();
    }

    private void setupView() {
        isShowDelete = true;
        binding.tvDeleteFileName.post(() ->{
            if (fileName != null)
                binding.tvDeleteFileName.setText(fileName + " ?");
        });

        binding.ivCancelDialog.setOnClickListener(v -> {
            isShowDelete = false;
            dismiss();
        });
        binding.btnDeleteNo.setOnClickListener(v -> {
            isShowDelete = false;
            dismiss();
        });
        binding.btnDeleteYes.setOnClickListener(v -> {
            mainViewModel.isDelete.setValue(new EventModifyName(true));
            isShowDelete = false;
            dismiss();
        });
    }

    private void eventBack() {
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    isShowDelete = false;
                    return true;
                }
                return false;
            }
        });
    }
}
