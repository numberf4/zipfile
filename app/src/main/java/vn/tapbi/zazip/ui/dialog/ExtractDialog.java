package vn.tapbi.zazip.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPathCompress;
import vn.tapbi.zazip.databinding.DialogExtractBinding;
import vn.tapbi.zazip.interfaces.OnPassDataCompress;
import vn.tapbi.zazip.ui.main.MainViewModel;
import vn.tapbi.zazip.utils.Utils;

public class ExtractDialog extends Dialog {
    private boolean isShowExtract;
    private int optionExtract;
    private OnPassDataCompress onPassDataExtract;
    private MainViewModel mainViewModel;
    private DialogExtractBinding binding;
    private final Context context;
    private String name, folder;
    private InputMethodManager imm;
    private Dialog dialogOverwrite;


    public void setShowExtract(boolean showExtract) {
        isShowExtract = showExtract;
    }

    public int getOptionExtract() {
        if (binding.radioAskBeforeOverride.isChecked()) {
            optionExtract = Constant.ASK_BEFORE_OVERRIDE;
        } else if (binding.radioExtractOverwrite.isChecked()) {
            optionExtract = Constant.OVERRIDE_WITHOUT_PROMP;
        } else if (binding.radioExtractSkip.isChecked()) {
            optionExtract = Constant.SKIPS_EXISTING_FILES;
        }

        return optionExtract;
    }

    public void setOptionExtract(int optionExtract) {
        this.optionExtract = optionExtract;
    }

    public String getName() {
        return binding.edtExtractFileName.getText().toString().trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return binding.tvExtractFolder.getText().toString().trim();
    }

    public void setFolder(String folder) {
        this.folder = folder;
        if (this.folder != null && binding != null){
            binding.tvExtractFolder.setText(this.folder);
        }
    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void setOnPassDataExtract(OnPassDataCompress onPassDataExtract) {
        this.onPassDataExtract = onPassDataExtract;
    }

    public ExtractDialog(@NonNull Context context, int themeResId) {
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_extract, null, false);
        binding.setMainViewModel(mainViewModel);
        setContentView(binding.getRoot());
        setupView();
        eventClick();
        eventBack();
    }

    private void setupView() {
        isShowExtract = true;
        dialogOverwrite = createDialogAsk();
        binding.edtExtractFileName.setFilters(new InputFilter[] {Utils.filter});
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (optionExtract == 0) {
            binding.radioAskBeforeOverride.setChecked(true);
        } else if (optionExtract == 1) {
            binding.radioExtractOverwrite.setChecked(true);
        } else if (optionExtract == 2) {
            binding.radioExtractSkip.setChecked(true);
        }
        if (name != null) binding.edtExtractFileName.setText(name);
        if (folder != null) binding.tvExtractFolder.setText(folder);
        binding.radioAskBeforeOverride.setButtonDrawable(R.drawable.ic_radio_button);
        binding.radioExtractSkip.setButtonDrawable(R.drawable.ic_radio_button);
        binding.radioExtractOverwrite.setButtonDrawable(R.drawable.ic_radio_button);
        mainViewModel.pathCompressLiveEvent.observe((LifecycleOwner) context, name -> {
            if (name instanceof EventPathCompress) {
                binding.tvExtractFolder.setText(((EventPathCompress) name).getPath());
            }
        });
    }

    private void eventBack() {
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    isShowExtract = false;
                    reset();
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void reset() {
        binding.tvExtractFolder.setText("");
        binding.edtExtractFileName.getText().clear();
        binding.radioAskBeforeOverride.setChecked(true);
        binding.radioExtractOverwrite.setChecked(false);
        binding.radioExtractSkip.setChecked(false);

    }

    private void eventClick() {
        binding.ivCancelExtract.setOnClickListener(v -> {
            reset();
            isShowExtract = false;
            dismiss();
        });
        binding.tvExtractFolder.setOnClickListener(v -> {
            mainViewModel.pathFolder.postValue(binding.tvExtractFolder.getText().toString().trim());
            mainViewModel.showBrowse.postValue(true);
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        });
        binding.btnOk.setOnClickListener(v -> {
            if (binding.edtExtractFileName.getText().toString().trim().isEmpty()) {
                Toast.makeText(context, context.getString(R.string.warning_name_file_empty), Toast.LENGTH_SHORT).show();
            } else {
                if (binding.radioAskBeforeOverride.isChecked()) {
                    optionExtract = Constant.ASK_BEFORE_OVERRIDE;
                    File file = new File(binding.tvExtractFolder.getText().toString().trim() + File.separator + binding.edtExtractFileName.getText().toString().trim());
                    if (!file.exists()) {
                        onPassDataExtract.onPassDataExtract(binding.edtExtractFileName.getText().toString().trim(),
                                binding.tvExtractFolder.getText().toString(),
                                context.getString(R.string.extracting), Constant.OVERRIDE_WITHOUT_PROMP);
                        reset();
                        dismiss();
                    } else
                        dialogOverwrite.show();
                } else if (binding.radioExtractOverwrite.isChecked()) {
                    optionExtract = Constant.OVERRIDE_WITHOUT_PROMP;
                    onPassDataExtract.onPassDataExtract(binding.edtExtractFileName.getText().toString().trim(),
                            binding.tvExtractFolder.getText().toString(),
                            context.getString(R.string.extracting), optionExtract);
                    reset();
                    dismiss();
                } else if (binding.radioExtractSkip.isChecked()) {
                    optionExtract = Constant.SKIPS_EXISTING_FILES;
                    onPassDataExtract.onPassDataExtract(binding.edtExtractFileName.getText().toString().trim(),
                            binding.tvExtractFolder.getText().toString(),
                            context.getString(R.string.extracting), optionExtract);
                    reset();
                    dismiss();
                }
            }
        });
    }

    private Dialog createDialogAsk() {
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 24);
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ask_extract_option);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.findViewById(R.id.tv_allow_overwrite).setOnClickListener(v -> {
            onPassDataExtract.onPassDataExtract(binding.edtExtractFileName.getText().toString().trim(),
                    binding.tvExtractFolder.getText().toString(),
                    context.getString(R.string.extracting), Constant.OVERRIDE_WITHOUT_PROMP);
            dialog.dismiss();
            reset();
            dismiss();
        });
        dialog.findViewById(R.id.tv_denied_overwrite).setOnClickListener(v -> {
            dialog.dismiss();
        });
        return dialog;

    }
}
