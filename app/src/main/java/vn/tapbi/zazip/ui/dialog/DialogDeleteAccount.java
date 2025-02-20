package vn.tapbi.zazip.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.DialogDeleteAccountBinding;

public class DialogDeleteAccount extends Dialog implements View.OnClickListener {
    private DialogDeleteAccountBinding binding;

    private OnClickDeleteAccount onClickDeleteAccount;
    private int position = 0;
    private String type = "";

    public DialogDeleteAccount(@NonNull Context context, OnClickDeleteAccount onClickDeleteAccount, int position, String type) {
        super(context);
        this.onClickDeleteAccount = onClickDeleteAccount;
        this.position = position;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DialogDeleteAccountBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());
        setUpClick();
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    private void setUpClick() {
        binding.tvAllowDelete.setOnClickListener(this);
        binding.tvCancelDelete.setOnClickListener(this);
        binding.tvContentDelete.setText(getContext().getResources().getString(vn.tapbi.zazip.R.string.you_will_no_longers) + " " + type + " " + getContext().getResources().getString(vn.tapbi.zazip.R.string.account_in_azip));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_allow_delete:
                onClickDeleteAccount.allowDeleteAccount(position);
                dismiss();
                break;
            case R.id.tv_cancel_delete:
                onClickDeleteAccount.cancelDeleteAccount();
                dismiss();
                break;
        }
    }

    public interface OnClickDeleteAccount {
        void allowDeleteAccount(int position);

        void cancelDeleteAccount();
    }
}
