package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.DialogPermissionBinding;

public class RequestPermissionDialog extends Dialog {

    private DialogPermissionBinding binding;
    private OnRequestPermissionListener onRequestPermissionListener;

    public RequestPermissionDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_permission, null, false);
        setContentView(binding.getRoot());
        binding.btnOkPermission.setOnClickListener(v -> {
            dismiss();
            if (onRequestPermissionListener != null) {
                onRequestPermissionListener.onGotoSettings();
            }
        });
        binding.btnCancelSetting.setOnClickListener(v ->{
            dismiss();
        });
    }

    public void show(OnRequestPermissionListener onRequestPermissionListener) {
        this.onRequestPermissionListener = onRequestPermissionListener;
        super.show();
    }

    public interface OnRequestPermissionListener {
        void onGotoSettings();
    }
}
