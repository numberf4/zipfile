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
import vn.tapbi.zazip.databinding.DialogProgressBinding;

public class MyProgressDialog extends Dialog {
    private DialogProgressBinding binding;

    public MyProgressDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_progress, null, false);
        setContentView(binding.getRoot());
    }


    public void show(String message) {
        super.show();
        binding.tvMessage.setText(message);
    }

}
