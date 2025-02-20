package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import timber.log.Timber;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.DialogPasswordBinding;

public class PasswordDialog extends Dialog {
    private boolean isShowPass = true;
    private OnClickListener onClickListener;
    private DialogPasswordBinding binding;
    private final Context context;
    private String pass;
    private int countExtractFail;

    public void setCountExtractFail(int countExtractFail) {
        this.countExtractFail = countExtractFail;
        if (binding != null){
            if (countExtractFail > 1 ){
                showWarning();
            }else resetView();
        }
    }

    public String getPass() {
        return binding.edtPass.getText().toString();
    }

    public void setPass(String pass) {
        this.pass = pass;
        if (this.pass != null && binding != null)
            binding.edtPass.setText(this.pass);
    }

    public PasswordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_password, null, false);
        setContentView(binding.getRoot());
        setupView();
        eventClick();
        eventBack();
    }

    private void setupView() {
        binding.edtPass.setTransformationMethod(null);
        binding.edtPass.performClick();
        binding.edtPass.setFocusableInTouchMode(true);
        binding.edtPass.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.edtPass, InputMethodManager.SHOW_IMPLICIT);

        binding.edtPass.post(() ->{
            if (pass != null){
                binding.edtPass.setText(pass);
                binding.edtPass.setSelection(pass.length());
            }else {
                binding.edtPass.setText("");
                binding.edtPass.setSelection(0);
            }
            changeStateShowPass();
        });
    }
    private void resetView(){
        binding.edtPass.getText().clear();
        binding.cslPass.setBackgroundResource(R.drawable.custom_bg_edt);
        binding.ivShowPass.setImageResource(R.drawable.ic_show_pass);
        binding.ivShowPass.setEnabled(true);
        binding.tvWarning.setVisibility(View.GONE);
        binding.edtPass.setTransformationMethod(new PasswordTransformationMethod());
    }
    private void showWarning(){
        binding.edtPass.setTransformationMethod(null);
        binding.cslPass.setBackgroundResource(R.drawable.bg_warning_pass);
        binding.ivShowPass.setImageResource(R.drawable.ic_warning);
        binding.ivShowPass.setEnabled(false);
        binding.tvWarning.setVisibility(View.VISIBLE);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        resetView();
    }

    private void eventClick() {
        binding.ivCancelDialog.setOnClickListener(v -> {
            resetView();
            dismiss();
            onClickListener.onBack();

        });
        binding.ivShowPass.setOnClickListener(v -> changeStateShowPass());

        binding.btnOk.setOnClickListener(v -> {
            String password = binding.edtPass.getText().toString();
            Timber.d("password "+password.length());
            if (password.trim().isEmpty()) {
                showWarning();
            } else if (onClickListener != null) {
                resetView();
                dismiss();
                onClickListener.onOkClickListener(password, countExtractFail);
            }
        });
        binding.edtPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.btnOk.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void changeStateShowPass() {
        isShowPass = !isShowPass;
        if (isShowPass) {
            binding.ivShowPass.setImageResource(R.drawable.ic_show_pass);
            binding.edtPass.setTransformationMethod(null);
        } else {
            binding.ivShowPass.setImageResource(R.drawable.ic_hide_pass);
            binding.edtPass.setTransformationMethod(new PasswordTransformationMethod());
        }
        binding.edtPass.setSelection(binding.edtPass.getText().length());
    }

    private void eventBack() {
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    resetView();
                    dismiss();
                    onClickListener.onBack();
                    return true;
                }
                return false;
            }
        });
    }

    public interface OnClickListener {
        void onOkClickListener(String password, int countExtractFail);
        void onBack();
    }
}
