package vn.tapbi.zazip.services;

import static vn.tapbi.zazip.common.Constant.clientIdentifier;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DOCS;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_SHEETS;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_SLIDES;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.FileUtils;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.repository.RecentRepository;
import vn.tapbi.zazip.model.FileCloudDownload;
import vn.tapbi.zazip.model.ProcessNoty;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;
import vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper;

@AndroidEntryPoint
public class DownloadFileCloudService extends Service {

    @Inject
    public RecentRepository recentRepository;
    private static final String CHANNEL_NORMAL_ID = "CHANNEL_NORMAL_ID";
    public static int NOTIFICATION_ID = 44;
    private NotificationManager notificationManager;
    private int countFileDownloadSuccess, countFileDownloadFail, countFileDownload;
    private String nameFile, mimeFile, idFile, urlOneDrive, revDropBox, accessTokenDropBox, pathDisplayDropBox, pathSaveTo;
    private int totalFiles;
    private int id;
    private ArrayList<ProcessNoty> processNoty;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        processNoty = new ArrayList<>();
    }

    private void convertNameFile(String fullName, String mime) {
        if (fullName.contains(".")) {
            nameFile = fullName.substring(0, fullName.lastIndexOf("."));
            mimeFile = fullName.substring(fullName.lastIndexOf(".") + 1);
            Log.d("Number4", "convertNameFile: " + nameFile + " mime: " + mimeFile);
        } else {
            nameFile = fullName;
            mimeFile = mime;

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case Constant.ACTION_START_DOWNLOAD:
                countFileDownload = 0;
                countFileDownloadSuccess = 0;
                countFileDownloadFail = 0;
                int typeCloud = intent.getIntExtra(Constant.TYPE_CLOUD, 10);
                totalFiles = intent.getIntExtra(Constant.KEY_TOTAL_FILE_DRIVE, 0);
                pathSaveTo = intent.getStringExtra(Constant.KEY_PATH_SAVE_TO);
                id = intent.getIntExtra(Constant.KEY_ID_DRIVE, NOTIFICATION_ID);
                String listDownload = intent.getStringExtra(Constant.KEY_FILE_DOWNLOAD);
                List<FileCloudDownload> list1 = new Gson().fromJson(listDownload, new TypeToken<ArrayList<FileCloudDownload>>() {
                }.getType());

                for (int i = 0; i < list1.size(); i++) {
                    convertNameFile(list1.get(i).getNameFile(), list1.get(i).getMimeFile());
                    idFile = list1.get(i).getIdFile();
                    urlOneDrive = list1.get(i).getUrlOneDrive();
                    revDropBox = list1.get(i).getRevDropBox();
                    accessTokenDropBox = list1.get(i).getAccessTokenDropBox();
                    pathDisplayDropBox = list1.get(i).getPathDisplayDropBox();

                    setUpNotification();
                    updateRemoteView(processNoty.get(processNoty.size() - 1).getRemoteViews(),
                            processNoty.get(processNoty.size() - 1).getRemoteViewsBig(),
                            id,
                            nameFile + "." + convertMimeGoogle(mimeFile),
                            countFileDownloadSuccess,
                            processNoty.get(processNoty.size() - 1).getNotification()
                    );
                    runDownload(typeCloud);
                }
                break;
            case Constant.ACTION_CANCEL_DOWNLOAD:
                int idCancel = intent.getIntExtra(Constant.KEY_ID_DRIVE, NOTIFICATION_ID);
                cancelTask(idCancel);
                break;

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void cancelTask(int id) {
        notificationManager.cancel(id);
    }

    private void showNotyDownloadSuccess(String title, String text) {
        int id = new Random().nextInt();

        Intent intent = new Intent(this, DownloadFileCloudService.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NORMAL_ID);
        builder.setSmallIcon(R.drawable.ic_noti).setColor(getResources().getColor(R.color.blue))
                .setContentTitle(title + "  " + text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(String.valueOf(id));
        setMetadata(builder);
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    private String convertMimeGoogle(String mime) {
        if (mime.equals(TYPE_GOOGLE_DOCS)
                || mime.equals(TYPE_GOOGLE_SLIDES)
                || mime.equals(TYPE_GOOGLE_SHEETS)) {
            return Utils.convertTypeFile(mimeFile);
        } else {
            return mime;
        }
    }

    private void setUpNotification() {
        int id = this.id;
        String nameFile = this.nameFile;
        String mimeFile = convertMimeGoogle(this.mimeFile);
        int totalFiles = this.totalFiles;
        String fullName = nameFile + "." + mimeFile;
        String dest = Utils.getUniqueFileName(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fullName, false);

        RemoteViews remoteViews /*= new RemoteViews(getPackageName(), R.layout.remote_view_noti)*/;
        RemoteViews remoteViewsBig = new RemoteViews(getPackageName(), R.layout.remote_view_noti_big);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_noti);
        } else {
            remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_noti_limit_view);
        }
        remoteViews.setTextViewText(R.id.tvContentProcess, dest);
        remoteViewsBig.setTextViewText(R.id.tvContentProcess, dest);

        remoteViews.setTextViewText(R.id.tvName, getString(R.string.download_file));
        remoteViewsBig.setTextViewText(R.id.tvName, getString(R.string.download_file));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NORMAL_ID);

        Intent intentCancel = new Intent(this, DownloadFileCloudService.class);
        intentCancel.setAction(Constant.ACTION_CANCEL_DOWNLOAD);
        intentCancel.putExtra(Constant.KEY_ID_DRIVE, id);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendIntentCancel = PendingIntent.getService(this, id, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            remoteViews.setOnClickPendingIntent(R.id.tvCancel, pendIntentCancel);
        }
        remoteViewsBig.setOnClickPendingIntent(R.id.tvCancel, pendIntentCancel);

        Intent intent = new Intent(this, DownloadFileCloudService.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendIntent = PendingIntent.getService(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setSmallIcon(R.drawable.ic_noti).setColor(getResources().getColor(R.color.blue))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendIntent)
                .setVibrate(null)
                .setSound(null)
                .setCustomContentView(remoteViews)
                .setGroup(String.valueOf(id))
                .setCustomBigContentView(remoteViewsBig)
                .setCustomHeadsUpContentView(remoteViews);
        setMetadata(builder);
        Notification notifications = builder.build();
        notificationManager.notify(id, notifications);
        ProcessNoty process = new ProcessNoty(id, fullName, remoteViews, remoteViewsBig, totalFiles, notifications);
        processNoty.add(process);
        NOTIFICATION_ID++;
    }

    private void updateRemoteView(RemoteViews remoteViews, RemoteViews remoteViewsBig, int id, String title, int process, Notification notification) {
        remoteViews.setTextViewText(R.id.tvName, title);
        remoteViews.setTextViewText(R.id.tvContentProcess, getString(R.string.download_file));
        remoteViews.setTextViewText(R.id.tvProcess, process + "/" + totalFiles);

        remoteViewsBig.setTextViewText(R.id.tvName, title);
        remoteViewsBig.setTextViewText(R.id.tvContentProcess, getString(R.string.download_file) );
        remoteViewsBig.setTextViewText(R.id.tvProcess, process + "/" + totalFiles);
        notificationManager.notify(id, notification);
    }

    private void setMetadata(NotificationCompat.Builder notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNormalChannel();
        } else {
            notification.setCategory(Notification.CATEGORY_SERVICE);
            notification.setPriority(Notification.PRIORITY_LOW);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNormalChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_NORMAL_ID) == null) {
            NotificationChannel mChannel =
                    new NotificationChannel(
                            CHANNEL_NORMAL_ID,
                            CHANNEL_NORMAL_ID,
                            NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private void runDownload(int typeCloud) {
        if (typeCloud == Constant.SCREEN_GOOGLE) {
            if (App.getInstance().driveServiceHelper != null)
                downloadGoogleFile(App.getInstance().driveServiceHelper);

        } else if (typeCloud == Constant.SCREEN_ONE_DRIVE) {
            downLoadOneDriveFile(urlOneDrive);

        } else if (typeCloud == Constant.SCREEN_DROP_BOX) {
            if (revDropBox != null && accessTokenDropBox != null && pathDisplayDropBox != null) {
                downloadFileDropBox(pathDisplayDropBox, revDropBox, accessTokenDropBox);
            }
        }
    }

    private void sendProcess(boolean result) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_DOWNLOAD_PROCESS);
        intent.putExtra(Constant.KEY_DOWNLOAD_SUCCESS, result);
        LocalBroadcastManager.getInstance(DownloadFileCloudService.this).sendBroadcast(intent);
    }

    private void handleNoti() {
        if ((countFileDownloadSuccess + countFileDownloadFail) == totalFiles) {
            if (countFileDownloadFail > 0) {
                cancelTask(id);
                showNotyDownloadSuccess(getString(R.string.download_success), countFileDownloadSuccess + "/" + totalFiles);
                countFileDownloadFail = 0;
                countFileDownloadSuccess = 0;
                countFileDownload = 0;
            } else {
                cancelTask(id);
                showNotyDownloadSuccess(getString(R.string.download_success), countFileDownloadSuccess + "/" + totalFiles);
                sendProcess(true);
                countFileDownload = 0;
                countFileDownloadSuccess = 0;
            }
            DownloadFileCloudService.this.stopSelf();
        }
    }

    private void downloadFileDropBox(String pathDisplayDropBox, String revDropBox, String accessTokenDropBox) {
        String nameTemp = pathDisplayDropBox.substring(1).replaceAll("/", "_").replaceAll(":", "_");
        String name = Utils.getUniqueFileName(pathSaveTo, nameTemp, true);
        downloadDropBoxFile(pathDisplayDropBox, revDropBox, accessTokenDropBox, name).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Boolean result) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (countFileDownload == totalFiles) {
                    cancelTask(id);
                }
                Log.d("Number4", "onError downloadFileDropBox: " + e);
            }
        });
    }

    private String downloadDropBox(String pathDisplayDropBox, String revDropBox, String accessTokenDropBox, String pathFileDest) {
        try {
            File file = new File(pathFileDest);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(file);
            new DbxClientV2(new DbxRequestConfig(clientIdentifier), accessTokenDropBox)
                    .files()
                    .download(pathDisplayDropBox, revDropBox)
                    .download(out);
            return file.getName();
        } catch (Exception e) {
            Log.d("Number4", "downloadDropBox exception: " + e);
            return Constant.SAVE_FAIL;
        }
    }

    private Single<Boolean> downloadDropBoxFile(String pathDisplayDropBox, String revDropBox, String accessTokenDropBox, String pathFileDest) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ProcessNoty p = processNoty.get(processNoty.size() - 1);
                countFileDownload++;
                String s = downloadDropBox(pathDisplayDropBox, revDropBox, accessTokenDropBox, pathFileDest);
                if (s.equals(Constant.SAVE_FAIL)) {
                    countFileDownloadFail++;
                    showNotyDownloadSuccess(getString(R.string.download_failed), pathDisplayDropBox.substring(1));
                    FileUtils.delete(pathFileDest);
                    sendProcess(false);
                    if (countFileDownloadFail > 0 && countFileDownloadFail == totalFiles) {
                        cancelTask(id);
                    }
                    return false;
                } else {
                    countFileDownloadSuccess++;
                    FileUtils.notifySystemToScan(pathFileDest);
                    recentRepository.insertRecent(pathFileDest);
                    //update UI
                    updateRemoteView(p.getRemoteViews(),
                            p.getRemoteViewsBig(),
                            id,
                            pathFileDest.substring(pathFileDest.lastIndexOf(File.separator) + 1),
                            countFileDownloadSuccess,
                            p.getNotification());
                    handleNoti();
                    return true;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void downloadGoogleFile(DriveServiceHelper driveServiceHelper) {
        String mime = convertMimeGoogle(mimeFile);
        String dest = Utils.getUniqueFileName(pathSaveTo, nameFile + "." + mime, true);
        downloadFileGoogleDrive(driveServiceHelper, dest, idFile, mimeFile).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Boolean myResult) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (countFileDownload == totalFiles) {
                    cancelTask(id);
                    countFileDownload = 0;
                }
                Log.d("Number4", "onError download GG Drive: " + e);
            }
        });
    }

    private void downLoadOneDriveFile(String url) {
        String mime = convertMimeGoogle(mimeFile);
        String dest = Utils.getUniqueFileName(pathSaveTo, nameFile + "." + mime, true);
        downloadFileOneDrive(url, dest).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull Boolean myResult) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (countFileDownload == totalFiles) {
                    cancelTask(id);
                    countFileDownload = 0;
                }
                Log.d("Number4", "onError download One Drive: " + e);
            }
        });
    }

    private Single<Boolean> downloadFileGoogleDrive(DriveServiceHelper driveServiceHelper, String destDownload, String fileId, String mime) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ProcessNoty p = processNoty.get(processNoty.size() - 1);
                countFileDownload++;
                driveServiceHelper.downloadFile(destDownload, fileId, mime)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Log.d("Number4", "onFailure download GG: " + e);
                                countFileDownloadFail++;
                                showNotyDownloadSuccess(getString(R.string.download_failed), destDownload.substring(destDownload.lastIndexOf(File.separator) + 1));
                                FileUtils.delete(destDownload);
                                sendProcess(false);
                                if (countFileDownloadFail > 0 && countFileDownloadFail == totalFiles) {
                                    cancelTask(id);
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        String title = destDownload.substring(destDownload.lastIndexOf(File.separator) + 1);
                        Log.d("Number4", "onSuccess download GG: " + aBoolean);
                        if (aBoolean) {
                            countFileDownloadSuccess++;
                            FileUtils.notifySystemToScan(destDownload);
                            recentRepository.insertRecent(destDownload);
                            //update UI
                            updateRemoteView(p.getRemoteViews(),
                                    p.getRemoteViewsBig(),
                                    id, title,
                                    countFileDownloadSuccess,
                                    p.getNotification());
                            handleNoti();
                        } else {
                            countFileDownloadFail++;
                            showNotyDownloadSuccess(getString(R.string.download_failed), title);
                            FileUtils.delete(destDownload);
                            sendProcess(false);
                            if (countFileDownloadFail > 0 && countFileDownloadFail == totalFiles) {
                                cancelTask(id);
                            }
                        }
                    }
                });
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<Boolean> downloadFileOneDrive(String url, String filePath) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                countFileDownload++;
                ProcessNoty p = processNoty.get(processNoty.size() - 1);
                String s = Utils.downloadFile(url, filePath);
                String name = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                if (s.equals(Constant.SAVE_FAIL)) {
                    countFileDownloadFail++;
                    showNotyDownloadSuccess(getString(R.string.download_failed), name);
                    FileUtils.delete(filePath);
                    sendProcess(false);
                    if (countFileDownloadFail > 0) {
                        cancelTask(id);
                    }
                    return false;
                } else {
                    countFileDownloadSuccess++;
                    FileUtils.notifySystemToScan(filePath);
                    recentRepository.insertRecent(filePath);
                    updateRemoteView(p.getRemoteViews(),
                            p.getRemoteViewsBig(),
                            id,
                            name,
                            countFileDownloadSuccess,
                            p.getNotification());
                    handleNoti();
                    return true;
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
