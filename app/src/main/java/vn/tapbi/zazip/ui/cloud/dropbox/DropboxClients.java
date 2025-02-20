package vn.tapbi.zazip.ui.cloud.dropbox;

import static vn.tapbi.zazip.common.Constant.CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.clientIdentifier;

import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.utils.MySharePreferences;

public class DropboxClients {
    public DbxClientV2 dbxClientV2;

    public DropboxClients(DbxClientV2 dbxClientV2) {
        this.dbxClientV2 = dbxClientV2;
    }

    public DropboxClients() {
        dbxClientV2 = new DbxClientV2(new DbxRequestConfig(clientIdentifier), Auth.getDbxCredential());
    }

    public FullAccount getAccountInfo() throws DbxException {
        try {
            return dbxClientV2.users().getCurrentAccount();
        } catch (InvalidAccessTokenException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLinkShareDropBox(String pathFile) throws DbxException {
        List<SharedLinkMetadata> list = dbxClientV2.sharing().listSharedLinks().getLinks();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SharedLinkMetadata linkMetadata = list.get(i);
                if (pathFile.toLowerCase().equals(linkMetadata.getPathLower())) {
                    return linkMetadata.getUrl();
                }
            }
        }
        return dbxClientV2.sharing().createSharedLinkWithSettings(pathFile).getUrl();
    }

    public Single<String> getLinkDropBox(String pathFolder) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getLinkShareDropBox(pathFolder);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public ListFolderResult getFileForFolder(String folderPath) throws DbxException {
        return dbxClientV2.files().listFolder(folderPath);
    }

    public List<Metadata> getFileForFolderInThread(String folderPath) throws DbxException {
        ListFolderResult listFolderResult = getFileForFolder(folderPath);

        return new ArrayList<>(listFolderResult.getEntries());
    }

    public ListFolderResult getFileForFolderContinue(String cursor) throws DbxException {
        return dbxClientV2.files().listFolder(cursor);
    }

    public Single<FullAccount> getAccountDropBox() {
        return Single.fromCallable(new Callable<FullAccount>() {
            @Override
            public FullAccount call() throws Exception {
                return getAccountInfo();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public boolean revokeDropboxAuthorization() throws DbxException {
        try {
            dbxClientV2.auth().tokenRevoke();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Single<Boolean> removeAccount() {
        return Single.fromCallable(this::revokeDropboxAuthorization).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Metadata>> getAllFile(String folderPath) {
        return Single.fromCallable(new Callable<List<Metadata>>() {
            @Override
            public List<Metadata> call() throws Exception {
                try {
                    MySharePreferences.putBoolean(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, false, App.getInstance().getApplicationContext());
                    return getFileForFolderInThread(folderPath);
                } catch (InvalidAccessTokenException ex) {
                    if (ex.getAuthError().isExpiredAccessToken()) {
                        MySharePreferences.putBoolean(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, true, App.getInstance().getApplicationContext());
                    }
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String> downLoadFileDropBoxInThread(String pathDisplayDropBox, String rev) {
        return Single.fromCallable(() -> {
            return downLoadFileDropBox(pathDisplayDropBox, rev);
        });

    }

    private String downLoadFileDropBox(String pathDisplayDropBox, String rev) {
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, pathDisplayDropBox.substring(1));
            try (OutputStream outputStream = new FileOutputStream(file)) {
                dbxClientV2.files().download(pathDisplayDropBox, rev)
                        .download(outputStream);
                return file.getAbsolutePath();
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return Constant.SAVE_FAIL;
        }
    }
}
