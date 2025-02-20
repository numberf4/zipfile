package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.DialogCancelBinding;

public class CancelDialog extends Dialog implements View.OnClickListener {
    private DialogCancelBinding binding;
    private OnDialogCancelListener onDialogCancelListener;

    public CancelDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_cancel, null, false);
        setContentView(binding.getRoot());
        setupView();
    }

    private void setupView() {
        binding.btnYes.setOnClickListener(this);
        binding.imgExit.setOnClickListener(this);
        binding.btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnNo || v == binding.imgExit) {
            dismiss();
        } else if (v == binding.btnYes && onDialogCancelListener != null) {
            dismiss();
            onDialogCancelListener.onYesClick();
        }
    }

    public void show(OnDialogCancelListener onDialogCancelListener) {
        super.show();
        this.onDialogCancelListener = onDialogCancelListener;
    }

    public interface OnDialogCancelListener {
        void onYesClick();
    }
}
