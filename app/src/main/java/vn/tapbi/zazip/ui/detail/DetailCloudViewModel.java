package vn.tapbi.zazip.ui.detail;

import static vn.tapbi.zazip.common.Constant.ACCURACY;
import static vn.tapbi.zazip.common.Constant.CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX;
import static vn.tapbi.zazip.common.Constant.ERROR_INTERNET;
import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DRIVE_FOLDER;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.LiveEvent;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.onedrive.FileOneDrive;
import vn.tapbi.zazip.data.model.onedrive.ResponseOneDrive;
import vn.tapbi.zazip.data.model.onedrive.ResponseShareLink;
import vn.tapbi.zazip.data.repository.AccountRepository;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.ui.base.BaseViewModel;
import vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper;
import vn.tapbi.zazip.ui.cloud.dropbox.DropboxClients;
import vn.tapbi.zazip.ui.cloud.onedrive.OneDriveClients;
import vn.tapbi.zazip.utils.MySharePreferences;
import vn.tapbi.zazip.utils.Resource;
import vn.tapbi.zazip.utils.Status;
import vn.tapbi.zazip.utils.Utils;

@HiltViewModel
public class DetailCloudViewModel extends BaseViewModel {
    private AccountRepository accountRepository;
    private FileDataRepository fileDataRepository;
    private OneDriveClients oneDriveClients;
    public MutableLiveData<String> linkShareOneDrive = new MutableLiveData<>();
    public MutableLiveData<String> linkShareGoogle = new MutableLiveData<>();
    public MutableLiveData<String> linkShareDropBox = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginOneDriveFail = new MutableLiveData<>(false);
    public MutableLiveData<Resource<List<FileData>>> liveDataFileDocumentCloud = new MutableLiveData<>();


    public MutableLiveData<Resource<List<FileMetadata>>> fileMetadataList = new MutableLiveData<>();


    @Inject
    public DetailCloudViewModel(AccountRepository accountRepository, FileDataRepository fileDataRepository, OneDriveClients oneDriveClients) {
        this.accountRepository = accountRepository;
        this.fileDataRepository = fileDataRepository;
        this.oneDriveClients = oneDriveClients;
    }

    public void getFileOnedrive(String token, String id) {
        oneDriveClients.getOneDriveFolder(token, id).subscribe(new SingleObserver<ResponseOneDrive>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ResponseOneDrive responseOneDrive) {
                oneDriveToDocument(responseOneDrive.getFileOneDrive());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                loginOneDriveFail.postValue(true);
            }
        });
    }

    static SSLHandshakeException toSSLHandshakeException(Throwable e) {
        if (e instanceof SSLHandshakeException) {
            return (SSLHandshakeException) e;
        }

        return (SSLHandshakeException) new SSLHandshakeException(e.getMessage()).initCause(e);
    }

    public void getLinkShare(String token, String id) {
        oneDriveClients.getLinkOnedrive(token, id).subscribe(new SingleObserver<ResponseShareLink>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull ResponseShareLink responseShareLink) {
                linkShareOneDrive.postValue(responseShareLink.getLink().getWebUrl());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getLinkShareDropBox(String pathFolder, DropboxClients dropboxClients) {
        dropboxClients.getLinkDropBox(pathFolder).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull String s) {
                linkShareDropBox.postValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getLinkGoogle(DriveServiceHelper driveServiceHelper, String id) {
        accountRepository.getLinkShareGoogle(driveServiceHelper, id).subscribe(new SingleObserver<Task<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Task<String> stringTask) {
                stringTask.addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        linkShareGoogle.setValue(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void oneDriveToDocument(List<FileOneDrive> fileOneDrives) {
        convertOneDriveToDocument(fileOneDrives).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.LOADING, null, ""));
            }

            @Override
            public void onSuccess(@NonNull List<FileData> documents) {
                if (documents != null)
                    liveDataFileDocumentCloud.postValue(new Resource<>(Status.SUCCESS, documents, ""));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR_ONE_DRIVE, null, "error"));
            }
        });
    }

    private Single<List<FileData>> convertOneDriveToDocument(List<FileOneDrive> fileOneDrives) {
        return Single.fromCallable(() -> {
            List<FileData> documents = new ArrayList<>();
            for (FileOneDrive f : fileOneDrives) {
                FileData document = new FileData(Constant.TYPE_ONE_DRIVE_FILE, f.getName(), f.getSize(), f.getMicrosoftGraphDownloadUrl(), Utils.convertTimeOneDrive(f.getLastModifiedDateTime()) / 1000);
                document.setFileId(f.getId());
                document.setCountFolder(f.getFolder() != null ? f.getFolder().getChildCount() : 0);
                if (f.getFile() == null) {
                    document.setCategory(DriveFolder.MIME_TYPE);
                    document.setFolder(true);
                    documents.add(document);
                } else if (Utils.checkDocumentFile(f.getFile().getMimeType())) {
                    document.setCategory(f.getFile().getMimeType());
                    documents.add(document);
                }
            }
            Collections.sort(documents, (o1, o2) -> o1.getTimeModified() > o2.getTimeModified() ? -1 : 1);
            return documents;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<FileData>> convertFileDropBoxToDocument(List<Metadata> metadataList, DropboxClients dropboxClients) {
        return Single.fromCallable(() -> {
            List<FileData> documentList = new ArrayList<>();
            for (Metadata metadata : metadataList) {
                if (metadata instanceof FileMetadata) {
                    FileData document = new FileData(TYPE_DROP_BOX_FILE, metadata.getName(), ((FileMetadata) metadata).getSize(),
                            metadata.getPathLower(), ((FileMetadata) metadata).getServerModified().getTime() / 1000,
                            ((FileMetadata) metadata).getPathDisplay(), ((FileMetadata) metadata).getId(), ((FileMetadata) metadata).getRev(),
                            "");
//                    MimeTypeMap mime = MimeTypeMap.getSingleton();
//                    String ext = metadata.getName().substring(metadata.getName().indexOf(".") + 1);
//                    String type = mime.getMimeTypeFromExtension(ext);
//                    document.setCategory(type);
                    document.setFolder(false);
                    document.setCategory(Utils.getMimeTypeFromName(((FileMetadata) metadata).getName()));
                    documentList.add(document);
                } else if (metadata instanceof FolderMetadata) {
                    FileData document = new FileData(TYPE_DROP_BOX_FILE, metadata.getName(), 0,
                            metadata.getPathLower(), 0, ((FolderMetadata) metadata).getPathDisplay(), ((FolderMetadata) metadata).getId(), "", "");
                    document.setFolder(true);
                    document.setCategory(TYPE_GOOGLE_DRIVE_FOLDER);
                    documentList.add(document);
                }
            }
            return documentList;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void fileDropBoxToDocument(List<Metadata> metadata, DropboxClients dropboxClients) {
        convertFileDropBoxToDocument(metadata, dropboxClients).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<FileData> documents) {
                if (documents != null)
                    liveDataFileDocumentCloud.postValue(new Resource<>(Status.SUCCESS, documents, ""));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR_DROP_BOX, null, "error get data drop box"));
            }
        });
    }

    public void getAllFileFromDropBox(String folderPath, DropboxClients dropboxClients) {
        dropboxClients.getAllFile(folderPath).subscribe(new SingleObserver<List<Metadata>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.LOADING, null, ""));
            }

            @Override
            public void onSuccess(@NonNull List<Metadata> metadata) {
                List<FileMetadata> list = new ArrayList<>();
                if (metadata != null) {
                    for (Metadata metadata1 : metadata) {
                        if (metadata1 instanceof FileMetadata) {
                            list.add(((FileMetadata) metadata1));
                        }
                    }
                    fileMetadataList.postValue(new Resource<>(Status.SUCCESS, list, "", null));
                    fileDropBoxToDocument(metadata, dropboxClients);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR_DROP_BOX, null, "error get data drop box"));
                if (MySharePreferences.getBooleanValue(CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX, App.getInstance().getApplicationContext())) {
                    Toast.makeText(App.getInstance().getApplicationContext(), R.string.authorization_has_expired, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getGoogleService(Context context, GoogleSignInAccount googleSignInAccount, String appName, String idFolder) {
        fileDataRepository.getGoogleDriveServices(context, googleSignInAccount, appName).subscribe(new SingleObserver<Drive>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Drive drive) {
                App.getInstance().driveServiceHelper = new DriveServiceHelper(drive);
                getAllFileFromGgDrive(App.getInstance().driveServiceHelper, idFolder);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    public void getGoogleService(Context context, String account, String type, String appName, String idFolder) {
        fileDataRepository.getGoogleDriveServices(context, account, type, appName).subscribe(new SingleObserver<Drive>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Drive drive) {
                App.getInstance().driveServiceHelper = new DriveServiceHelper(drive);
                getAllFileFromGgDrive(App.getInstance().driveServiceHelper, idFolder);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }
        });
    }

    public void getAllFileFromGgDrive(DriveServiceHelper driveServiceHelper, String idFolder) {
        fileDataRepository.getListFolderGoogleDrive(driveServiceHelper, idFolder).subscribe(new SingleObserver<Task<List<FileData>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.LOADING, null, "", null));
            }

            @Override
            public void onSuccess(@NonNull Task<List<FileData>> listTask) {
                listTask.addOnSuccessListener(data -> {
                    if (data != null)
                        liveDataFileDocumentCloud.postValue(new Resource<>(Status.SUCCESS, data, ""));
                }).addOnFailureListener(errors -> {
                    if (errors instanceof UserRecoverableAuthIOException) {
                        liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR_GG_DRIVE, null, ACCURACY, ((UserRecoverableAuthIOException) errors).getIntent()));
                    } else {
                        liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR_GG_DRIVE, null, ERROR_INTERNET, null));
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {
                liveDataFileDocumentCloud.postValue(new Resource<>(Status.ERROR, null, "error get data from google drive", null));
            }
        });
    }

}
