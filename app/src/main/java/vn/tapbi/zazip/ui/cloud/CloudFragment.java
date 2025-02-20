package vn.tapbi.zazip.ui.cloud;


import static vn.tapbi.zazip.common.Constant.CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.CHECK_LOGIN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.DROP_BOX;
import static vn.tapbi.zazip.common.Constant.GOOGLE_DRIVE;
import static vn.tapbi.zazip.common.Constant.KEY_NAME_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.KEY_TYPE_GG_DRIVE;
import static vn.tapbi.zazip.common.Constant.ONE_DRIVE;
import static vn.tapbi.zazip.common.Constant.SCREEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.SCREEN_GOOGLE;
import static vn.tapbi.zazip.common.Constant.SCREEN_ONE_DRIVE;
import static vn.tapbi.zazip.common.Constant.clientIdentifier;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.android.Auth;

import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalArgumentException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventOnedrive;
import vn.tapbi.zazip.data.model.Account;
import vn.tapbi.zazip.ui.cloud.dropbox.DropboxClients;
import vn.tapbi.zazip.databinding.FragmentCloudBinding;
import vn.tapbi.zazip.interfaces.LoginGoogleListener;
import vn.tapbi.zazip.model.EventGoogleSignInClient;
import vn.tapbi.zazip.model.EventLoginGoogleDrive;
import vn.tapbi.zazip.ui.adapter.AccountAdapter;
import vn.tapbi.zazip.ui.base.BaseBindingFragment;
import vn.tapbi.zazip.ui.dialog.BottomSheetDialogAddAccount;
import vn.tapbi.zazip.ui.dialog.DialogDeleteAccount;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.MySharePreferences;
import vn.tapbi.zazip.utils.Resource;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class CloudFragment extends BaseBindingFragment<FragmentCloudBinding, CloudViewModel> implements LoginGoogleListener, AccountAdapter.OnItemClickedListener, DialogDeleteAccount.OnClickDeleteAccount {

    private AccountAdapter accountAdapter;
    private IMultipleAccountPublicClientApplication mMultipleAccountApp;
    private final List<Account> accounts = new ArrayList<>();
    private boolean checkAllowAccount = false;
    private DialogDeleteAccount dialogDeleteAccount;
    private String accountLogin = "";
    private boolean clickAccount = false;
    BottomSheetDialogAddAccount bottomSheetDialogAddAccount;
    private final List<IAccount> listAccountOneDrive = new ArrayList<>();
    private final String[] scopes = {"user.read", "files.readwrite.all"};//"offline_access"

    @Override
    protected Class<CloudViewModel> getViewModel() {
        return CloudViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_cloud;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        if (accounts.size() < 3) {
            viewModel.getAllAccount();
        }

        ((MainActivity) requireActivity()).setLoginGoogleListener(this);
        binding.layoutHeaderCloud.tvHeader.setText(getResources().getString(R.string.cloud));
        binding.layoutHeaderCloud.ivSortView.setVisibility(View.GONE);
        binding.layoutHeaderCloud.searchExplorer.setVisibility(View.GONE);
        binding.layoutHeaderCloud.imgSearchExplorer.setVisibility(View.GONE);
        createsPublicClientApplication();
        setUpView();
        setUpAdapter();
        eventEditAccount();
        setUpObserverData();
    }

    @Override
    protected void onPermissionGranted() {

    }

    private void setUpView() {
        binding.layoutHeaderCloud.imgExplorerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkTime())
                    ((MainActivity) requireActivity()).popBackStack();
            }
        });
    }

    private void setUpAdapter() {
        accountAdapter = new AccountAdapter(this, requireContext());
        binding.rcAccount.setAdapter(accountAdapter);
        binding.rcAccount.setItemAnimator(null);
        binding.rcAccount.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
    }

    private void setUpObserverData() {
        viewModel.mutableLiveDataInsert.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) viewModel.getAllAccount();
            }
        });
        viewModel.mutableLiveDataAccount.observe(getViewLifecycleOwner(), account -> {
            if (accountAdapter != null) {
                this.accounts.clear();
                this.accounts.addAll(account);
                accountAdapter.setList(account);
                checkShowAddAccount();
            }
        });
    }

    private void checkShowAddAccount() {
        int countDefault = 0;
        int demGGDrive = 0;
        int demOneDrive = 0;
        int demDropBox = 0;
        for (int i = 0; i < accounts.size(); i++) {
            String type = accounts.get(i).getType();
            switch (type) {
                case GOOGLE_DRIVE:
                    demGGDrive += 1;
                    if (accounts.get(i).getAccountName().equals(GOOGLE_DRIVE)) {
                        countDefault += 1;
                    }
                    break;
                case ONE_DRIVE:
                    demOneDrive += 1;
                    if (accounts.get(i).getAccountName().equals(ONE_DRIVE)) {
                        countDefault += 1;
                    }
                    break;
                case DROP_BOX:
                    demDropBox += 1;
                    if (accounts.get(i).getAccountName().equals(DROP_BOX)) {
                        countDefault += 1;
                    }
                    break;
            }
        }
        if (demGGDrive == 0) {
            addAccountDefault(GOOGLE_DRIVE);
        }
        if (demOneDrive == 0) {
            addAccountDefault(ONE_DRIVE);
        }
        if (demDropBox == 0) {
            addAccountDefault(DROP_BOX);
        }
        if (countDefault == 3) {
            checkAllowAccount = false;
            if (accountAdapter != null) {
                accountAdapter.setCheckShowAddAccountOther(false);
            }
        } else {
            checkAllowAccount = true;
            if (accountAdapter != null) {
                accountAdapter.setCheckShowAddAccountOther(true);
            }
        }
        checkAllowEditAccount();
    }

    private void checkAllowEditAccount() {
        if (checkAllowAccount) {
            binding.editAccount.setTextColor(getResources().getColor(R.color.color_0046DC));
        } else {
            binding.editAccount.setText(getResources().getString(R.string.edit));
            binding.editAccount.setTextColor(getResources().getColor(R.color.color_bdbdbd));
        }
    }

    private void addAccountDefault(String typeAccount) {
        Account account = new Account(typeAccount, 0, typeAccount, null, typeAccount);
        viewModel.insertAccountIntoDB(account);
        new Handler().postDelayed(() -> {
            if (isAdded()) {
                viewModel.getAllAccount();
            }
        }, 70);
    }

    private void loginGgDrive(String accountName) {
        if (App.getInstance().goToWifiSettingsIfDisconnected()) {
        } else {
            viewModel.loginGoogleDrive(requireContext(), accountName);
            viewModel.googleSignInClientLiveEvent.observe(getViewLifecycleOwner(), data -> {
                if (data != null) {
                    ((EventGoogleSignInClient) data).getGoogleSignInClient().signOut();
                    mainViewModel.getGoogleSignInClient.postValue(new EventLoginGoogleDrive(((EventGoogleSignInClient) data).getGoogleSignInClient()));
                    viewModel.googleSignInClientLiveEvent.setValue(null);
                }
            });
        }
    }

    @Override
    public void loginDrive(Intent intent) {
        GoogleSignIn.getSignedInAccountFromIntent(intent)
                .addOnSuccessListener(googleSignInAccount -> {
                    ((MainActivity) requireActivity()).googleSignInAccount = googleSignInAccount;
                    if (clickAccount) {
                        showGGDriveDetail(googleSignInAccount);
                    } else {
                        removeAccount(GOOGLE_DRIVE);
                        setTextDefault();
                        accountAdapter.checkEditAccount(false);
                        addAccount(googleSignInAccount.getEmail(), GOOGLE_DRIVE, R.string.logged_in, null, googleSignInAccount.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), requireContext().getResources().getString(R.string.login_drive_failed), Toast.LENGTH_SHORT).show();
                });

    }

    public void showGGDriveDetail(GoogleSignInAccount googleSignInAccount) {
        mainViewModel.eventOnedrive.postValue(new EventOnedrive(null, null, GOOGLE_DRIVE, googleSignInAccount));
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.TYPE_CLOUD_SCREEN, SCREEN_GOOGLE);
        bundle.putString(KEY_NAME_GG_DRIVE, googleSignInAccount.getAccount().name);
        bundle.putString(KEY_TYPE_GG_DRIVE, googleSignInAccount.getAccount().type);
        ((MainActivity) requireActivity()).changeScreen(R.id.detailCloudFragment, bundle);
        viewModel.googleSignInClientLiveEvent.setValue(null);
    }

    private void addAccount(String email, String typeCloud, int p, String o, String idAccount) {
        boolean checkAccountGGDriveExist = false;
        for (int i = 0; i < accounts.size(); i++) {
            if (!MySharePreferences.getBooleanValue(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, App.getInstance().getApplicationContext())) {
                if (accounts.get(i).getAccountName().equals(email) && accounts.get(i).getType().equals(typeCloud)) {
                    checkAccountGGDriveExist = true;
                    break;
                }
            }
        }
        if (checkAccountGGDriveExist) {
            Toast.makeText(requireContext(), requireContext().getString(p), Toast.LENGTH_SHORT).show();
        } else {
            Account account1 = new Account(email, Calendar.getInstance().getTime().getTime(), typeCloud, o, idAccount);
            viewModel.insertAccountIntoDB(account1);
            new Handler().postDelayed(() -> {
                if (isAdded()) {
                    viewModel.getAllAccount();
                }
            }, 100);
            MySharePreferences.putBoolean(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, false, App.getInstance().getApplicationContext());
            if (accountAdapter != null) {
                accountAdapter.setCheckShowAddAccountOther(true);
            }
        }
    }

    private void removeAccount(String str) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getType().equals(str) && accounts.get(i).getAccountName().equals(str)) {
                viewModel.deleteAccount(accounts.get(i).getId());
                break;
            }
        }
    }

    private void createsPublicClientApplication() {
        PublicClientApplication.createMultipleAccountPublicClientApplication(requireContext(),
                R.raw.msal_config,
                new IPublicClientApplication.IMultipleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(IMultipleAccountPublicClientApplication application) {
                        mMultipleAccountApp = application;
                        loadAccounts();
                    }

                    @Override
                    public void onError(MsalException exception) {
                    }
                });
    }

    private void loadAccounts() {
        if (mMultipleAccountApp == null) {
            return;
        }
        mMultipleAccountApp.getAccounts(new IPublicClientApplication.LoadAccountsCallback() {
            @Override
            public void onTaskCompleted(final List<IAccount> result) {
                // You can use the account data to update your UI or your app database.
                listAccountOneDrive.clear();
                listAccountOneDrive.addAll(result);
                if (isAdded()) {
                }
            }

            @Override
            public void onError(MsalException exception) {
                if (isAdded()) {
                }
            }
        });
    }

    private void loginOneDrive2() {
        if (App.getInstance().goToWifiSettingsIfDisconnected()) {

        } else {
            if (mMultipleAccountApp != null) {
                mMultipleAccountApp.acquireToken(requireActivity(), scopes, new AuthenticationCallback() {
                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        removeAccount(ONE_DRIVE);
                        accountAdapter.checkEditAccount(false);
                        addAccount(authenticationResult.getAccount().getUsername(), ONE_DRIVE, R.string.logged_in, authenticationResult.getAccessToken(), authenticationResult.getAccount().getId());
                        loadAccounts();
                        setTextDefault();
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Toast.makeText(requireContext(), "Login fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Login fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setText() {
        accountAdapter.checkEditAccount(!accountAdapter.getCheckEditAccount());
        if (accountAdapter.getCheckEditAccount()) {
            binding.editAccount.setText(getResources().getString(R.string.done));
        } else {
            binding.editAccount.setText(getResources().getString(R.string.edit));
        }
    }

    private void eventEditAccount() {
        binding.editAccount.setOnClickListener(v -> {
            if (checkAllowAccount) {
                setText();
            }
        });
    }

    private void acquireToken(IAccount iAccount, Account account) {
        mMultipleAccountApp.acquireTokenSilentAsync(scopes,
                iAccount,
                iAccount.getAuthority(),
                new AuthenticationCallback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        Log.d("Number4", "onSuccess acquireToken: " + authenticationResult.getAccessToken());
                        account.setAccessToken(authenticationResult.getAccessToken());
                        mainViewModel.eventOnedrive.postValue(new EventOnedrive(iAccount, account, ONE_DRIVE));
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constant.TYPE_CLOUD_SCREEN, SCREEN_ONE_DRIVE);
                        ((MainActivity) requireActivity()).changeScreen(R.id.detailCloudFragment, bundle);
                    }

                    @Override
                    public void onError(MsalException exception) {
                            Toast.makeText(requireActivity(), R.string.account_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAddClicked(int position) {
        if (isAdded()) {
            switch (accounts.get(position).getType()) {
                case GOOGLE_DRIVE:
                    clickAccount = false;
                    loginGgDrive("");
                    break;
                case ONE_DRIVE:
                    loginOneDrive2();
                    break;
                case DROP_BOX:
                    loginDropBox();
                    break;
            }
        }
    }

    @Override
    public void onAccountClicked(int position) {
        if (bottomSheetDialogAddAccount != null) {
            bottomSheetDialogAddAccount.dismiss();
        }
        switch (accounts.get(position).getType()) {
            case ONE_DRIVE:
                if (App.getInstance().goToWifiSettingsIfDisconnected()) {
                } else {
                    if (listAccountOneDrive.size() > 0) {
                        for (int i = 0; i < listAccountOneDrive.size(); i++) {
                            if (listAccountOneDrive.get(i).getUsername().equals(accounts.get(position).getAccountName())) {
                                acquireToken(listAccountOneDrive.get(i), accounts.get(position));
                                break;
                            }
                        }
                    }
                }
                break;
            case DROP_BOX:
                if (App.getInstance().goToWifiSettingsIfDisconnected()) {
                } else {
                    mainViewModel.eventOnedrive.postValue(new EventOnedrive(null, accounts.get(position), DROP_BOX));
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.TYPE_CLOUD_SCREEN, SCREEN_DROP_BOX);
                    ((MainActivity) requireActivity()).changeScreen(R.id.detailCloudFragment, bundle);
                    Timber.d("onAccountClicked " + accounts.get(position).getAccessToken());
                }
                break;
            case GOOGLE_DRIVE:
                clickAccount = true;
                accountLogin = accounts.get(position).getAccountName();
                loginGgDrive(accountLogin);
                break;
        }
        App.getInstance().listSelectCloud.clear();
    }

    @Override
    public void onDeleteClicked(int position) {
        initDialogDeleteAccount(position);
    }

    private void initDialogDeleteAccount(int position) {
        dialogDeleteAccount = new DialogDeleteAccount(requireContext(), this, position, accounts.get(position).getType());
        dialogDeleteAccount.show();
    }

    @Override
    public void onResume() {
        Utils.resetLastClickTime();
        if (!MySharePreferences.getBooleanValue(CHECK_LOGIN_DROP_BOX, requireContext())) {
            getAccountDropBox();
        }
        ((MainActivity) requireActivity()).hideOptions();
        ((MainActivity) requireActivity()).hideOnlyPaste();
        super.onResume();
    }

    @SuppressWarnings("unchecked")
    private void getAccountDropBox() {
        DbxCredential dbxCredential;
        dbxCredential = Auth.getDbxCredential();
        if (dbxCredential != null) {
            viewModel.getAccountDropBox(new DropboxClients(new DbxClientV2(new DbxRequestConfig(clientIdentifier), dbxCredential)));
            viewModel.liveDataAccountDropBox.observe(getViewLifecycleOwner(), fullAccountResource -> {
                switch (((Resource<FullAccount>) fullAccountResource).getStatus()) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        removeAccount(DROP_BOX);
                        accountAdapter.checkEditAccount(false);
                        addAccount(((Resource<FullAccount>) fullAccountResource).getData().getEmail(), DROP_BOX, R.string.logged_in, Auth.getDbxCredential().getAccessToken(),
                                ((Resource<FullAccount>) fullAccountResource).getData().getAccountId());
                        MySharePreferences.putBoolean(CHECK_LOGIN_DROP_BOX, true, requireContext());
                        setTextDefault();
                        viewModel.liveDataAccountDropBox.removeObservers(getViewLifecycleOwner());
                        break;
                    case ERROR:
                        MySharePreferences.putBoolean(CHECK_LOGIN_DROP_BOX, true, requireContext());
                        break;
                }
            });
        }

    }

    private void setTextDefault() {
        binding.editAccount.setText(getResources().getString(R.string.edit));
        binding.editAccount.setTextColor(getResources().getColor(R.color.color_0046DC));
    }

    @Override
    public void allowDeleteAccount(int position) {
        viewModel.deleteAccount(accounts.get(position).getId());
        new Handler().postDelayed(() -> {
            if (isAdded()) {
                viewModel.getAllAccount();
            }
        }, 100);
    }

    @Override
    public void cancelDeleteAccount() {

    }

    @Override
    public void onClickAddAccountOther() {
        if (!Utils.checkTime()) return;
        setTextDefault();
        accountAdapter.checkEditAccount(false);
        bottomSheetDialogAddAccount = new BottomSheetDialogAddAccount(new BottomSheetDialogAddAccount.OnClickAddAccount() {
            @Override
            public void addAccountGgDrive() {
                clickAccount = false;
                loginGgDrive("");
            }

            @Override
            public void addAccountOneDrive() {
                loginOneDrive2();
            }

            @Override
            public void addAccountDropBox() {
                loginDropBox();
            }
        });
        bottomSheetDialogAddAccount.show(getChildFragmentManager(), "bottom_sheet_account");
    }
}
