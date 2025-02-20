package vn.tapbi.zazip.ui.process;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.databinding.FragmentProcessBinding;
import vn.tapbi.zazip.services.NotificationService;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.dialog.CancelDialog;
import vn.tapbi.zazip.ui.dialog.PasswordDialog;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.main.MainViewModel;

public class ProcessFragment extends BaseBindingFragment<FragmentProcessBinding, MainViewModel> implements View.OnClickListener {
    private String[] path;
    private String folder, pass, type, name, content_process, action, title;
    private final ArrayList<String> list = new ArrayList<>();
    private IntentFilter intentFilter;
    private int optionExtract = -1, countExtractFail;
    private int id;
    private CancelDialog cancelDialog;

    private final BroadcastReceiver compressExtractReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int process = intent.getIntExtra(Constant.KEY_PROCESS, 0);
            countExtractFail = intent.getIntExtra(Constant.KEY_COUNT_EXTRACT, 0);
            String type = intent.getStringExtra(Constant.KEY_TYPE);
            int idNoty = intent.getIntExtra(Constant.KEY_ID, NotificationService.NOTIFICATION_ID);
            mainViewModel.isShowDetailSelect.postValue(false);
            ((MainActivity) requireActivity()).hideOptions();
            ((MainActivity) requireActivity()).hideOnlyPaste();
            switch (type) {
                case Constant.TYPE_ERROR:
                    Toast.makeText(requireActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    if (isAdded()) {
                        popBackStack();
                    }
                    break;
                case Constant.TYPE_PROCESS:
                    if (idNoty == id) {
                        binding.tvProgress.setText(process + "%");
                        if (process == 100 && isAdded()) {
                            mainViewModel.reload.setValue(true);
                            if (cancelDialog.isShowing()) {
                                cancelDialog.dismiss();
                            }

                            popBackStack();
                        }
                    }

                    break;
                case Constant.TYPE_FATAL:
                    ((MainActivity) requireActivity()).passwordDialog.setCountExtractFail(countExtractFail);
                    ((MainActivity) requireActivity()).passwordDialog.show();

                    break;

                case Constant.TYPE_CANCEL:
                    if (idNoty == id && isAdded()) {
                        popBackStack();
                    }
                    break;
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_process;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        intentFilter = new IntentFilter(Constant.ACTION_BACKGROUND_PROCESS);
        setupView(savedInstanceState);
        eventClick();
        eventBack();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(compressExtractReceiver);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(compressExtractReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(compressExtractReceiver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString(Constant.ACTION_PROCESS, action);
        outState.putInt(Constant.KEY_ID, id);
        outState.putString(Constant.KEY_TITLE, title);
        outState.putString(Constant.KEY_PASS, pass);
        outState.putString(Constant.KEY_TYPE, type);
        outState.putString(Constant.KEY_NAME, name);
        outState.putString(Constant.KEY_FOLDER, folder);
        outState.putStringArray(Constant.KEY_PATH, path);
        outState.putInt(Constant.KEY_OPTION_EXTRACT, optionExtract);
        outState.putString(Constant.KEY_CONTENT_PROGRESS, content_process);
        outState.putInt(Constant.KEY_COUNT_EXTRACT, countExtractFail);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    private void setupView(Bundle savedInstanceState) {
        cancelDialog = new CancelDialog(requireActivity());

        if (savedInstanceState != null) {
            action = savedInstanceState.getString(Constant.ACTION_PROCESS);
            id = savedInstanceState.getInt(Constant.KEY_ID, new Random().nextInt());
            title = savedInstanceState.getString(Constant.KEY_TITLE);
            pass = savedInstanceState.getString(Constant.KEY_PASS);
            type = savedInstanceState.getString(Constant.KEY_TYPE);
            name = savedInstanceState.getString(Constant.KEY_NAME);
            folder = savedInstanceState.getString(Constant.KEY_FOLDER);
            path = savedInstanceState.getStringArray(Constant.KEY_PATH);
            optionExtract = savedInstanceState.getInt(Constant.KEY_OPTION_EXTRACT);
            content_process = savedInstanceState.getString(Constant.KEY_CONTENT_PROGRESS);
            countExtractFail = savedInstanceState.getInt(Constant.KEY_COUNT_EXTRACT);
        } else {
            assert getArguments() != null;
            action = getArguments().getString(Constant.ACTION_PROCESS);
            id = getArguments().getInt(Constant.KEY_ID, new Random().nextInt());
            title = getArguments().getString(Constant.KEY_TITLE);
            pass = getArguments().getString(Constant.KEY_PASS);
            type = getArguments().getString(Constant.KEY_TYPE);
            name = getArguments().getString(Constant.KEY_NAME);
            folder = getArguments().getString(Constant.KEY_FOLDER);
            path = getArguments().getStringArray(Constant.KEY_PATH);
            optionExtract = getArguments().getInt(Constant.KEY_OPTION_EXTRACT);
            content_process = getArguments().getString(Constant.KEY_CONTENT_PROGRESS);
            countExtractFail = getArguments().getInt(Constant.KEY_COUNT_EXTRACT, 0);
        }

        binding.tvProgress.setText("0%");
        if (path != null) {
            list.addAll(Arrays.asList(path));
        }
        binding.tvContentProcess.setText(content_process);

        if (action.equals(Constant.ACTION_SHOW)) {
            binding.tvPath.setText(title);
        } else {
            if (content_process.equals(requireContext().getString(R.string.adding))) {
                binding.tvPath.setText(folder + File.separator + name + "." + type);
            } else {
                binding.tvPath.setText(folder + File.separator + name);
            }
        }

        if (action.equals(Constant.ACTION_START)) {
            startService(Constant.ACTION_START_SERVICE);
        }
    }

    private void startService(String action) {
        if (isAdded()) {
            Intent intent = new Intent(requireActivity(), NotificationService.class);
            intent.setAction(action);
            switch (action) {
                case Constant.ACTION_START_SERVICE:
                    intent.putExtra(Constant.KEY_PASS, pass);
                    intent.putExtra(Constant.KEY_TYPE, type);
                    intent.putExtra(Constant.KEY_NAME, name);
                    intent.putExtra(Constant.KEY_FOLDER, folder);
                    intent.putExtra(Constant.KEY_CONTENT_PROGRESS, content_process);
                    intent.putStringArrayListExtra(Constant.KEY_PATH, list);
                    intent.putExtra(Constant.KEY_OPTION_EXTRACT, optionExtract);
                    intent.putExtra(Constant.KEY_ID, id);
                    intent.putExtra(Constant.KEY_COUNT_EXTRACT, countExtractFail);
                    break;
                case Constant.ACTION_UPDATE_PASSWORD:
                    intent.putExtra(Constant.KEY_PASS, pass);
                    break;
                case Constant.ACTION_SHOW_NOTY:
                    intent.putExtra(Constant.KEY_ID, id);
                    break;
            }
            requireActivity().startService(intent);
        }
    }

    private void eventClick() {
        ((MainActivity) requireActivity()).passwordDialog.setOnClickListener(new PasswordDialog.OnClickListener() {
            @Override
            public void onOkClickListener(String password, int countExtractFails) {
                countExtractFail = countExtractFails;
                pass = password;
                startService(Constant.ACTION_START_SERVICE);
            }

            @Override
            public void onBack() {
                if (isAdded()) {
                    popBackStack();
                }
            }
        });
        binding.ivBackPrecess.setOnClickListener(this);
        binding.btnRunInBackground.setOnClickListener(this);
        binding.tvCancelRunInBackground.setOnClickListener(this);
    }

    @Override
    protected void onPermissionGranted() {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_precess:
            case R.id.tvCancelRunInBackground:
                showDialogCancel();
                break;
            case R.id.btn_run_in_background:
                startService(Constant.ACTION_SHOW_NOTY);
                popBackStack();
                break;
        }
    }

    private void eventBack() {
        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (cancelDialog.isShowing()) {
                    cancelDialog.dismiss();
                } else {
                    showDialogCancel();
                }
            }
        });
    }

    private void showDialogCancel() {
        cancelDialog.show(new CancelDialog.OnDialogCancelListener() {
            @Override
            public void onYesClick() {
                if (isAdded()) {
                    Intent intent = new Intent(requireActivity(), NotificationService.class);
                    intent.setAction(Constant.ACTION_CANCEL);
                    intent.putExtra(Constant.KEY_ID, id);
                    requireActivity().startService(intent);
                    popBackStack();
                }
            }
        });
    }

    private void popBackStack() {
        ((MainActivity) requireActivity()).popBackStack();
    }
}
