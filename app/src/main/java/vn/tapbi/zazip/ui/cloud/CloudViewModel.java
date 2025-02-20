package vn.tapbi.zazip.ui.cloud;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;
import vn.tapbi.zazip.common.LiveEvent;
import vn.tapbi.zazip.data.model.Account;
import vn.tapbi.zazip.data.repository.AccountRepository;
import vn.tapbi.zazip.ui.cloud.dropbox.DropboxClients;
import vn.tapbi.zazip.model.EventGoogleSignInClient;
import vn.tapbi.zazip.ui.base.BaseViewModel;
import vn.tapbi.zazip.utils.Resource;
import vn.tapbi.zazip.utils.Status;

@HiltViewModel
public class CloudViewModel extends BaseViewModel {
    private final AccountRepository accountRepository;
    public MutableLiveData<Boolean> mutableLiveDataInsert = new MutableLiveData<>();
    public LiveEvent<EventGoogleSignInClient> googleSignInClientLiveEvent = new LiveEvent<>();
    public MutableLiveData<List<Account>> mutableLiveDataAccount = new MutableLiveData<>();
    public LiveEvent<Resource<FullAccount>> liveDataAccountDropBox = new LiveEvent<>();

    @Inject
    public CloudViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    public void loginGoogleDrive(Context context, String accountName) {
        accountRepository.loginGoogleDrives(context, accountName).subscribe(new SingleObserver<GoogleSignInClient>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull GoogleSignInClient list) {
                googleSignInClientLiveEvent.postValue(new EventGoogleSignInClient(list));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Timber.d("loginGoogleDrive");
            }
        });
    }

    public void getAllAccount() {
        accountRepository.getAllAccounts().subscribe(new SingleObserver<List<Account>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<Account> list) {
                mutableLiveDataAccount.postValue(list);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void insertAccountIntoDB(Account account) {
        accountRepository.insertAccount(account).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull Boolean list) {
                mutableLiveDataInsert.postValue(list);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteAccount(int id) {
        accountRepository.deleteAccount(id).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Boolean aBoolean) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getAccountDropBox(DropboxClients dropboxClients) {
        dropboxClients.getAccountDropBox().subscribe(new SingleObserver<FullAccount>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                liveDataAccountDropBox.postValue(new Resource<>(Status.LOADING, null, ""));
            }

            @Override
            public void onSuccess(@NonNull FullAccount fullAccount) {
                if (fullAccount != null)
                    liveDataAccountDropBox.postValue(new Resource<>(Status.SUCCESS, fullAccount, ""));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                liveDataAccountDropBox.postValue(new Resource<>(Status.ERROR, null, "error"));
            }
        });
    }

}
