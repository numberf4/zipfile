package vn.tapbi.zazip.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.BottomsheetDialogAddAccountBinding;

public class BottomSheetDialogAddAccount extends BottomSheetDialogFragment {
    private BottomsheetDialogAddAccountBinding binding;
    private final OnClickAddAccount onClickAddAccount;

    public BottomSheetDialogAddAccount(OnClickAddAccount onClickAddAccount) {
        this.onClickAddAccount = onClickAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomsheetDialogAddAccountBinding.inflate(getLayoutInflater());
        setUpClick();
        return binding.getRoot();
    }

    private void setUpClick() {
        binding.tvAddAccountGgDrive.setOnClickListener(v -> {
            onClickAddAccount.addAccountGgDrive();
            dismiss();
        });
        binding.tvAddAccountOneDrive.setOnClickListener(v ->{
            onClickAddAccount.addAccountOneDrive();
            dismiss();
        });
        binding.tvAddAccountDropBox.setOnClickListener(v -> {
            onClickAddAccount.addAccountDropBox();
            dismiss();
        });
    }

    public interface OnClickAddAccount {
        void addAccountGgDrive();

        void addAccountOneDrive();

        void addAccountDropBox();
    }
}
