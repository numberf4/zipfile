package vn.tapbi.zazip.data.repository;

import static vn.tapbi.zazip.common.Constant.DROP_BOX;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.data.db.AccountDB;
import vn.tapbi.zazip.data.model.Account;
import vn.tapbi.zazip.data.model.onedrive.ResponseOneDrive;
import vn.tapbi.zazip.data.model.onedrive.ResponseShareLink;
import vn.tapbi.zazip.interfaces.onedriveApi.OnedriveApi;
import vn.tapbi.zazip.data.model.onedrive.BodyShareFile;
import vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper;

public class AccountRepository {
    AccountDB accountDB;
    OnedriveApi onedriveApi;

    @Inject
    public AccountRepository(AccountDB accountDB, OnedriveApi onedriveApi) {
        this.accountDB = accountDB;
        this.onedriveApi = onedriveApi;
    }

    private GoogleSignInClient loginGoogleDrive(Context context, String accountName) {
        GoogleSignInOptions signInOptions;

        if (accountName.isEmpty()) {
            signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(/*new Scope(Scopes.DRIVE_APPFOLDER)*/
                                    new Scope(DriveScopes.DRIVE_APPDATA),
                                    new Scope(Scopes.DRIVE_FILE)
                            )
                            .requestEmail()
                            .build();
        } else {
            signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(/*new Scope(Scopes.DRIVE_APPFOLDER)*/
                                    new Scope(DriveScopes.DRIVE_APPDATA),
                                    new Scope(Scopes.DRIVE_FILE))
                            .requestEmail()
                            .setAccountName(accountName)
                            .build();
        }


        return GoogleSignIn.getClient(context, signInOptions);
    }

    public Single<GoogleSignInClient> loginGoogleDrives(Context context, String accountName) {
        return Single.fromCallable(new Callable<GoogleSignInClient>() {
            @Override
            public GoogleSignInClient call() throws Exception {
                return AccountRepository.this.loginGoogleDrive(context, accountName);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Account>> getAllAccounts() {
        return Single.fromCallable(new Callable<List<Account>>() {
            @Override
            public List<Account> call() throws Exception {
                return AccountRepository.this.getAllAccount();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> insertAccount(Account account) {
        return Single.fromCallable((Callable<Boolean>) () -> addAccount(account)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Boolean addAccount(Account account) {
        try {
            accountDB.accountDao().insert(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Single<ResponseOneDrive> getOneDriveFolder(String token, String id) {
        String author = "Bearer " + token;
        if (id != null && !id.isEmpty()) {
            return onedriveApi.getFolderById(author, id);
        }
        return onedriveApi.getFolderRoot(author);
    }

    public Single<ResponseShareLink> getLinkOnedrive(String token, String id) {
        return onedriveApi.shareFileById(token, id, new BodyShareFile());
    }

    public Single<Task<String>> getLinkShareGoogle(DriveServiceHelper driveServiceHelper, String id) {
        return Single.fromCallable(() -> driveServiceHelper.getLinkShare(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> deleteAccount(int id) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return deleteById(id);
            }
        });
    }

    public Boolean deleteById(int id) {
        try {
            accountDB.accountDao().deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Account> getAllAccount() {
        return accountDB.accountDao().getAllAccount();
    }
}
