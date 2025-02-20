package vn.tapbi.zazip.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.FileUtils;
import com.hzy.libp7zip.ExitCode;
import com.hzy.libp7zip.P7ZipApi;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import timber.log.Timber;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.common.models.EventPath;
import vn.tapbi.zazip.data.repository.RecentRepository;
import vn.tapbi.zazip.model.MyResult;
import vn.tapbi.zazip.model.ProcessNoty;
import vn.tapbi.zazip.model.ResultExtract;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Command;
import vn.tapbi.zazip.utils.Utils;

@AndroidEntryPoint
public class NotificationService extends Service {

    @Inject
    public RecentRepository recentRepository;
    private static final String CHANNEL_NORMAL_ID = "CHANNEL_NORMAL_ID";
    ProcessNoty process = null;
    private int positionFileExtract = 0;
    private String pathFileExtract = "";
    private int optionExtract, countExtract;
    private String pass;
    private String type;
    private String name;
    private String folder;
    private List<String> list;
    private String contentProcess;
    private int id;

    private NotificationManager notificationManager;
    public static int NOTIFICATION_ID = 517;
    private ArrayList<ProcessNoty> processNoty;
    private boolean isRunning;
    private Handler handler;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isRunning = true;
            for (int i = processNoty.size() - 1; i >= 0; i--) {
                ProcessNoty p = processNoty.get(i);
                File file = new File(p.getPath());
                if (file.exists()) {
                    int process = calculateProcess(file, p.getTotalSize(), p.getProcessType());
                    if (p.getResultCode() == -1) {
                        if (p.isNotyShow()) {
                            updateRemoteView(p.getRemoteViews(), /*p.getRemoteViewsBig(),*/ p.getNotification(), p.getId(), p.getTitle(), process, p.getContentProcess());
                        }
                        sendProcess(process, Constant.TYPE_PROCESS, p.getContentProcess(), p.getId(), p.getPathFileExtract());
                    }
                }
            }
            handler.postDelayed(this, 200);
        }
    };

    private int calculateProcess(File file, long total, int typeProcess) {
        float p;
        if (file.isDirectory()) {
            p = FileUtils.getDirLength(file);
        } else {
            p = FileUtils.getFileLength(file);
        }
        int process;
        if (typeProcess == Constant.TYPE_PROCESS_EXTRACT_FILE_FINAL) {
            process = (int) ((p / (float) total) * 50f);
            return Math.min(process * 2, 98);
        } else if (typeProcess == Constant.TYPE_PROCESS_EXTRACT_FILE_TAR) {
            process = (int) ((p / (float) total) * 50f);
            return Math.min(process, 49);
        } else {
            process = (int) ((p / (float) total) * 100f);
            return Math.min(process, 98);
        }
    }

    private void updateRemoteView(RemoteViews remoteViews,/* RemoteViews remoteViewsBig,*/ Notification notification, int id, String title, int process, String contentProcess) {
        remoteViews.setTextViewText(R.id.tvName, title);
        remoteViews.setTextViewText(R.id.tvContentProcess, contentProcess);
        remoteViews.setTextViewText(R.id.tvProcess, process + "%");
        notificationManager.notify(id, notification);
    }

    private void sendProcess(int process, String type, String contentProcess, int id, String name) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_BACKGROUND_PROCESS);
        intent.putExtra(Constant.KEY_ID, id);
        intent.putExtra(Constant.KEY_PROCESS, process);
        intent.putExtra(Constant.KEY_TYPE, type);
        intent.putExtra(Constant.KEY_NAME, name);
        intent.putExtra(Constant.KEY_CONTENT_PROGRESS, contentProcess);
        intent.putExtra(Constant.KEY_COUNT_EXTRACT, countExtract);
        LocalBroadcastManager.getInstance(NotificationService.this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        handler = new Handler();
        processNoty = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        isRunning = false;
        Utils.deleteCache(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;
        if (intent != null) action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Constant.ACTION_START_SERVICE:
                    countExtract = intent.getIntExtra(Constant.KEY_COUNT_EXTRACT, 0);
                    pass = intent.getStringExtra(Constant.KEY_PASS);
                    type = intent.getStringExtra(Constant.KEY_TYPE);
                    name = intent.getStringExtra(Constant.KEY_NAME);
                    folder = intent.getStringExtra(Constant.KEY_FOLDER);
                    list = intent.getStringArrayListExtra(Constant.KEY_PATH);
                    contentProcess = intent.getStringExtra(Constant.KEY_CONTENT_PROGRESS);
                    optionExtract = intent.getIntExtra(Constant.KEY_OPTION_EXTRACT, -1);
                    id = intent.getIntExtra(Constant.KEY_ID, NOTIFICATION_ID);
                    runTask();
                    break;
                case Constant.ACTION_UPDATE_PASSWORD:
                    break;
                case Constant.ACTION_CANCEL:
                    int idCancel = intent.getIntExtra(Constant.KEY_ID, 0);
                    cancelTask(idCancel);
                    break;
                case Constant.ACTION_SHOW_NOTY:
                    int id = intent.getIntExtra(Constant.KEY_ID, 0);
                    showNotyProcess(id);
                    break;
            }
        } else {
            NotificationService.this.stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotyProcess(int id) {
        for (ProcessNoty p : processNoty) {
            if (p.getId() == id && !p.isNotyShow()) {
                p.setNotyShow(true);
                notificationManager.notify(p.getId(), p.getNotification());
            }
        }
    }

    private void cancelTask(int idCancel) {
        for (ProcessNoty p : processNoty) {
            if (p.getId() == idCancel) {
                p.setResultCode(ExitCode.EXIT_CANCEL);
                if (p.isNotyShow()) {
                    notificationManager.cancel(p.getId());
                }
                sendBroadcastCancel(idCancel);
                break;
            }
        }
    }

    private void sendBroadcastCancel(int id) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_BACKGROUND_PROCESS);
        intent.putExtra(Constant.KEY_ID, id);
        intent.putExtra(Constant.KEY_TYPE, Constant.TYPE_CANCEL);
        LocalBroadcastManager.getInstance(NotificationService.this).sendBroadcast(intent);
    }

    private void runTask() {
        setUpNotification();
        if (contentProcess.equals(getString(R.string.adding))) {
            compress(name, type, folder, pass);
        } else {
            extract(name, folder, pass);
        }
    }

    private long calculateTotalSize() {
        long total = 0;
        for (int i = 0; i < list.size(); i++) {
            File f = new File(list.get(i));
            total += f.length();
        }
        return total;
    }

    private void extract(String name, String folder, String pass) {
        pathFileExtract = list.get(positionFileExtract);
        extractTask(name, folder, pass).subscribe(new SingleObserver<MyResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (!isRunning) {
                    handler.post(runnable);
                }
                processNoty.get(processNoty.size() - 1).setDisposable(d);
            }

            @Override
            public void onSuccess(@NonNull MyResult myResult) {
                handlerResultExtract(myResult);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void compress(String name, String type, String folder, String pass) {
        compressTask(name, type, folder, pass).subscribe(new SingleObserver<MyResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (!isRunning) {
                    handler.post(runnable);
                }
                processNoty.get(processNoty.size() - 1).setDisposable(d);
            }

            @Override
            public void onSuccess(@NonNull MyResult myResult) {
                handlerResultCompress(myResult);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void handlerResultExtract(MyResult myResult) {
        setResultCode(myResult);
        switch (myResult.getResult()) {
            case ExitCode.EXIT_FATAL:
            case ExitCode.EXIT_CMD_ERROR:
                countExtract++;
                FileUtils.deleteDir(myResult.getProcessNoty().getPath());
                sendProcess(0,
                        Constant.TYPE_FATAL,
                        myResult.getProcessNoty().getContentProcess(),
                        myResult.getProcessNoty().getId(),
                        myResult.getProcessNoty().getPathFileExtract());
                break;
            case ExitCode.EXIT_OK:
                if (positionFileExtract == list.size() - 1) {
                    updateRemoteView(myResult.getProcessNoty().getRemoteViews(),
                            /*myResult.getProcessNoty().getRemoteViewsBig(),*/
                            myResult.getProcessNoty().getNotification(),
                            myResult.getProcessNoty().getId(),
                            myResult.getProcessNoty().getTitle(),
                            100,
                            myResult.getProcessNoty().getContentProcess());
                    sendProcess(100,
                            Constant.TYPE_PROCESS,
                            myResult.getProcessNoty().getContentProcess(),
                            myResult.getProcessNoty().getId(),
                            myResult.getProcessNoty().getPathFileExtract());
                    MediaScannerConnection.scanFile(NotificationService.this, new String[]{myResult.getProcessNoty().getPath()}, null, null);
                    FileUtils.notifySystemToScan(myResult.getProcessNoty().getPath());
                    insertRecent(myResult.getProcessNoty());
                    showNotySuccess(myResult.getProcessNoty().isNotyShow(), getString(R.string.extract_success), myResult.getProcessNoty().getTitle());
                    Toast.makeText(NotificationService.this, getString(R.string.extract_success), Toast.LENGTH_SHORT).show();
                    //reload file data
                    EventBus.getDefault().post(new EventPath(Constant.EVENT_RELOAD_FILE_DATA));
                } else {
                    positionFileExtract++;
                }
                break;
            default:
                positionFileExtract++;
                break;
        }
        new Handler().postDelayed(this::checkDoneAllTask, 500);
    }

    private void handlerResultCompress(MyResult myResult) {
        setResultCode(myResult);
        if (myResult.getResult() == ExitCode.EXIT_OK) {
            updateRemoteView(myResult.getProcessNoty().getRemoteViews(),
                    /*myResult.getProcessNoty().getRemoteViewsBig(),*/
                    myResult.getProcessNoty().getNotification(),
                    myResult.getProcessNoty().getId(),
                    myResult.getProcessNoty().getTitle(),
                    100,
                    myResult.getProcessNoty().getContentProcess());
            sendProcess(100,
                    Constant.TYPE_PROCESS,
                    myResult.getProcessNoty().getContentProcess(),
                    myResult.getProcessNoty().getId(),
                    myResult.getProcessNoty().getPathFileExtract());
            insertRecent(myResult.getProcessNoty());
            showNotySuccess(myResult.getProcessNoty().isNotyShow(), getString(R.string.compress_success), myResult.getProcessNoty().getTitle());
            Toast.makeText(NotificationService.this, getString(R.string.compress_success), Toast.LENGTH_SHORT).show();

            //reload file data
            EventBus.getDefault().post(new EventPath(Constant.EVENT_RELOAD_FILE_DATA));
        }
        new Handler().postDelayed(this::checkDoneAllTask, 500);
    }

    private void showNotySuccess(boolean isShowNoty, String title, String text) {
        if (!isShowNoty) {
            return;
        }
        int id = new Random().nextInt();

        Intent intent = new Intent(this, NotificationService.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NORMAL_ID);
        builder.setSmallIcon(R.drawable.ic_noti).setColor(getResources().getColor(R.color.blue))
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(String.valueOf(id));
        setMetadata(builder);
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    private void insertRecent(ProcessNoty processNoty) {
        if (!processNoty.getPathFileExtract().isEmpty()) {
            recentRepository.insertRecent(processNoty.getPathFileExtract());
        }
        for (String s : processNoty.getListFileCompress()) {
            recentRepository.insertRecent(s);
        }
    }

    private void checkDoneAllTask() {
        boolean done = true;
        for (ProcessNoty p : processNoty) {
            if (p.getResultCode() == -1) {
                done = false;
            } else {
                notificationManager.cancel(p.getId());
            }
        }
        if (done) {
            for (ProcessNoty p : processNoty) {
                notificationManager.cancel(p.getId());
            }
            NotificationService.this.stopSelf();
        }
    }

    private void setResultCode(MyResult myResult) {
        for (int i = 0; i < processNoty.size(); i++) {
            if (processNoty.get(i).getId() == myResult.getProcessNoty().getId()) {
                processNoty.get(i).setResultCode(myResult.getResult());
            }
        }
    }

    public void copy(InputStream input, OutputStream output, int buffersize) throws IOException {
        if (buffersize < 1) {
            throw new IllegalArgumentException("buffersize must be bigger than 0");
        }
        final byte[] buffer = new byte[buffersize];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    @SuppressLint("TimberArgCount")
    private boolean addToArchiveCompression(TarArchiveOutputStream out, File file, String dir) {
        try {
            String entry = dir + File.separator + file.getName();
            if (file.isFile()) {
                out.putArchiveEntry(new TarArchiveEntry(file, entry));
                out.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
                try (FileInputStream in = new FileInputStream(file)) {
                    if (!isRunning) {
                        handler.post(runnable);
                    }
                    copy(in, out, 1024);
                }
                out.closeArchiveEntry();
            } else if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        addToArchiveCompression(out, child, entry + File.separator);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            Timber.tag("Number4").d(e, "ex addToArchiveCompression: %s");
            return false;
        }
    }

    private TarArchiveOutputStream getTarArchiveOutputStream(String name, String type) {
        try {
            TarArchiveOutputStream taos;
            switch ("." + type) {
                case Constant.ARCHIVE_TAR_BZ2:
                    taos = new TarArchiveOutputStream(new BZip2CompressorOutputStream(new FileOutputStream(name)));
                    break;
                case Constant.ARCHIVE_TAR_GZ:
                    taos = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(name)));
                    break;
                case Constant.ARCHIVE_TAR_XZ:
                    taos = new TarArchiveOutputStream(new XZCompressorOutputStream(new FileOutputStream(name)));
                    break;
                case Constant.ARCHIVE_TAR_LZ4:
                    taos = new TarArchiveOutputStream(new FramedLZ4CompressorOutputStream(new FileOutputStream(name)));
                    break;
                default:
                    taos = new TarArchiveOutputStream(new ZstdCompressorOutputStream(new FileOutputStream(name)));
                    break;
            }

            // TAR has an 8 gig file limit by default, this gets around that
            taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
            // TAR originally didn't support long file names, so enable the support for it
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
//            taos.setAddPaxHeadersForNonAsciiNames(true);
            return taos;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean compressFile(String type, String name, String directory) {
        try (TarArchiveOutputStream out = getTarArchiveOutputStream(name, type)) {
            return addToArchiveCompression(out, new File(directory), "");
        } catch (Exception e) {
            return false;
        }
    }

    private Single<MyResult> compressTask(String name, String type, String folder, String pass) {
        return Single.fromCallable(new Callable<MyResult>() {
            @Override
            public MyResult call() {
                int result;
                String pathTemp = Utils.getCacheDir(NotificationService.this).getAbsolutePath() + "/" + System.currentTimeMillis() + "/" + name;
                String directory = Utils.createFileCompress(list, pathTemp);
                String fileCompress = directory + "." + type;
                File dest = new File(folder + File.separator + name + "." + type);
                boolean checkType = Constant.ARCHIVE_7ZIP.equals("." + type)
                        || Constant.ARCHIVE_ZIP.equals("." + type)
                        || Constant.ARCHIVE_TAR.equals("." + type);
                if (processNoty.size() > 0) {
                    process = processNoty.get(processNoty.size() - 1);
                    String path = pathTemp + "." + type;
                    if (checkType) {
                        process.setPath(path);
                    } else {
                        process.setPath(dest.getAbsolutePath());
                    }
                    processNoty.set(processNoty.size() - 1, process);
                }
                processNoty.get(processNoty.size() - 1).getListFileCompress().add(dest.getAbsolutePath());

                if (checkType) {
                    // compress file with old library zip, 7z, tar
                    result = onCompressFile(directory, type, pass);
                    if (result == ExitCode.EXIT_OK) {
                        MediaScannerConnection.scanFile(NotificationService.this, new String[]{dest.getAbsolutePath()}, null, null);
                    }
                } else {
                    list.remove(list.size() - 1);
                    //compress with apache common compress library
                    boolean success = compressFile(type, dest.getAbsolutePath(), directory);
                    if (success) recentRepository.insertRecent(dest.getAbsolutePath());
                    result = ExitCode.EXIT_OK;
                    MediaScannerConnection.scanFile(NotificationService.this, new String[]{dest.getAbsolutePath()}, null, null);
                }

                if (process != null) {
                    if (process.getResultCode() == -1) {

                        if (checkType) {
                            String fileDest = Utils.copyFileArchive(fileCompress, dest);
                            FileUtils.notifySystemToScan(fileDest);
                        }
                        FileUtils.delete(fileCompress);
                        FileUtils.delete(directory);

                    } else {
                        FileUtils.delete(directory);
                        FileUtils.delete(fileCompress);
                        if (dest.exists()) FileUtils.delete(dest);
                        result = ExitCode.EXIT_CANCEL;
                    }
                }
                return new MyResult(result, process);
            }
        }).unsubscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void unCompress(String pathFileExtract, File tempFileTar) {
        try {
            InputStream fin;
            File fileIn = new File(pathFileExtract);

            fin = new FileInputStream(fileIn);
            BufferedInputStream in = new BufferedInputStream(fin);
            OutputStream out;
            if (!tempFileTar.exists()) {
                tempFileTar.createNewFile();
            }
            out = new FileOutputStream(tempFileTar);
            if (pathFileExtract.endsWith(Constant.ARCHIVE_TAR_LZ4)) {
                FramedLZ4CompressorInputStream gzIn = new FramedLZ4CompressorInputStream(in);
                final byte[] buffer = new byte[4096];
                int n;
                while (-1 != (n = gzIn.read(buffer))) {
                    out.write(buffer, 0, n);
                }
                out.close();
                gzIn.close();
            } else {
                ZstdCompressorInputStream gzIn = new ZstdCompressorInputStream(in);
                final byte[] buffer = new byte[4096];
                int n;
                while (-1 != (n = gzIn.read(buffer))) {
                    out.write(buffer, 0, n);
                }
                out.close();
                gzIn.close();

            }

            this.pathFileExtract = tempFileTar.getAbsolutePath();
        } catch (Exception e) {
        }
    }

    private Single<MyResult> extractTask(String name, String folder, String pass) {
        return Single.fromCallable(new Callable<MyResult>() {
            @Override
            public MyResult call() throws IOException {
                int result = 0;
                File dest = new File(folder + File.separator + name);
                File pathTemp = NotificationService.this.getCacheDir();
                File tempFileTar = File.createTempFile("name", Constant.ARCHIVE_TAR, pathTemp);
                boolean checkTypeExtract = Constant.ARCHIVE_TAR_LZ4.equals("." + type)
                        || Constant.ARCHIVE_TAR_ZST.equals("." + type);
                if (positionFileExtract == 0) {
                    if (processNoty.size() > 0) {
                        process = processNoty.get(processNoty.size() - 1);
                        if (dest.exists()) {
                            String path = Utils.getUniqueFolderName(dest.getAbsolutePath(), dest.getName(), true);
                            if (!checkTypeExtract) {
                                process.setProcessType(Constant.TYPE_PROCESS_HAVE_TEMP_FILE);
                                process.setPath(path);
                            } else {
                                process.setProcessType(Constant.TYPE_PROCESS_EXTRACT_FILE_TAR);
                                process.setPath(tempFileTar.getAbsolutePath());
                            }
                        } else {
                            process.setPath(dest.getAbsolutePath());
                        }
                        processNoty.set(processNoty.size() - 1, process);
                    }
                }
                if (optionExtract == Constant.OVERRIDE_WITHOUT_PROMP) {
                    if (pathFileExtract.endsWith(Constant.ARCHIVE_TAR_LZ4)
                            || pathFileExtract.endsWith(Constant.ARCHIVE_TAR_ZST)) {
                        unCompress(pathFileExtract, tempFileTar);
                    }
                    process.setProcessType(Constant.TYPE_PROCESS_EXTRACT_FILE_FINAL);
                    process.setPath(dest.getAbsolutePath());
                    ResultExtract resultExtract = onExtractFile(pathFileExtract, pass);
                    result = resultExtract.getResult();
                    File files = new File(resultExtract.getFolderExtract());
                    if (process != null) {
                        if (process.getResultCode() == -1) {
                            copyFile(dest, resultExtract.getFolderExtract(), name, folder);
                            for (File file : dest.listFiles()) {
                                if (file.getName().equals("PaxHeaders.X")) {
                                    Utils.deleteDirectory(file);
                                }
                            }
                        } else {
                            if (dest.exists()) FileUtils.delete(dest);
                            result = ExitCode.EXIT_CANCEL;
                        }
                        FileUtils.delete(tempFileTar);
                        FileUtils.delete(files);
                    }

                } else if (optionExtract == Constant.SKIPS_EXISTING_FILES) {
                    if (pathFileExtract.endsWith(Constant.ARCHIVE_TAR_LZ4)
                            || pathFileExtract.endsWith(Constant.ARCHIVE_TAR_ZST)) {
                        unCompress(pathFileExtract, tempFileTar);
                    }
                    ResultExtract resultExtract = onExtractFile(pathFileExtract, pass);
                    result = resultExtract.getResult();
                    File files = new File(resultExtract.getFolderExtract());
                    if (process != null) {
                        if (process.getResultCode() == -1) {
                            //todo: copy file
                            copyFileSkipOverWrite(dest, resultExtract.getFolderExtract(), name, folder);
                            for (File file : Objects.requireNonNull(dest.listFiles())) {
                                if (file.getName().equals("PaxHeaders.X")) {
                                    Utils.deleteDirectory(file);
                                }
                            }
                        } else {
                            result = ExitCode.EXIT_CANCEL;
                        }
                        //todo: delete file cache
                        FileUtils.delete(files);
                        FileUtils.delete(tempFileTar);
                    }
                }
                return new MyResult(result, process);
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private void copyFileSkipOverWrite(File dest, String tempFolderExtract, String name, String folder) throws IOException {
        String fileRename = Utils.renameFile(tempFolderExtract, name);
        if (!dest.exists()) {
            dest.mkdirs();
            FileUtils.copyDir(fileRename, dest.getAbsolutePath());

        } else {
            File fileCopy = new File(fileRename);
            File[] listFileCopy = fileCopy.listFiles();
            if (listFileCopy != null && listFileCopy.length > 0) {
                for (File file2 : listFileCopy) {
                    File fileDest2 = new File(folder + "/" + name + "/" + file2.getName());
                    if (file2.isDirectory()) {
                        if (fileDest2.exists()) {

                            File[] listFileDest = file2.listFiles();
                            if (listFileDest != null && listFileDest.length > 0) {
                                for (File file3 : listFileDest) {
                                    File fileDest3 = new File(folder + "/" + name + "/" + file2.getName() + "/" + file3.getName());
                                    if (fileDest3.isDirectory()) {
                                        if (!fileDest3.exists()) {
                                            fileDest3.mkdirs();
                                            Utils.copyDirectory(file3, fileDest3);
                                        }
                                    } else if (file3.isFile()) {
                                        if (!fileDest3.exists()) {
                                            Utils.copyFile(file3, fileDest3);
                                        }

                                    }
                                }
                            }

                        } else {
                            fileDest2.mkdirs();
                            Utils.copyDirectory(file2, fileDest2);
                        }

                    } else {
                        if (!fileDest2.exists()) {
                            Utils.copyFile(file2, fileDest2);
                        }
                    }
                }
            }
        }
    }

    private void copyFile(File dest, String tempFolderExtract, String name, String folder) {
        try {
            String fileRename = Utils.renameFile(tempFolderExtract, name);
            File fileCopy = new File(fileRename);
            if (!dest.exists()) {
                dest.mkdirs();
                FileUtils.copyDir(fileRename, dest.getAbsolutePath());
                Utils.deleteDirectory(fileCopy);

            } else {

                File[] listFileCopy = fileCopy.listFiles();
                if (listFileCopy != null && listFileCopy.length > 0) {
                    for (File file2 : listFileCopy) {
                        File fileDest2 = new File(folder + "/" + name + "/" + file2.getName());
                        if (file2.isDirectory()) {
                            if (fileDest2.exists()) {
                                File[] listFileDest = file2.listFiles();
                                if (listFileDest != null && listFileDest.length > 0) {
                                    for (File file3 : listFileDest) {
                                        File fileDest3 = new File(folder + "/" + name + "/" + file2.getName() + "/" + file3.getName());
                                        if (fileDest3.isDirectory()) {
                                            if (fileDest3.exists()) {
                                                FileUtils.deleteAllInDir(fileDest3);
                                            } else {
                                                fileDest3.mkdirs();
                                            }
                                            Utils.copyDirectory(file3, fileDest3);
                                        } else if (file3.isFile()) {
                                            if (fileDest3.exists())
                                                FileUtils.deleteFile(fileDest3);
                                            Utils.copyFile(file3, fileDest3);
                                        }
                                    }
                                }
                            } else {
                                fileDest2.mkdirs();
                            }
                            Utils.copyDirectory(file2, fileDest2);
                        } else {
                            if (fileDest2.exists()) {
                                FileUtils.deleteFile(fileDest2);
                            }
                            Utils.copyFile(file2, fileDest2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int onCompressFile(String filePath, String type, String pass) {
        String cmd;
        if (TextUtils.isEmpty(pass)) {
            cmd = Command.getCompressCmd(filePath, filePath + "." + type, type);
        } else {
            cmd = Command.getCompressCmdPassword(filePath, filePath + "." + type, type, pass);
        }
        return P7ZipApi.executeCommand(cmd);
    }

    private ResultExtract onExtractFile(String filePath, String pass) {
        String tempFolderExtract = "";
        int result;
        String fileName = filePath.substring(filePath.lastIndexOf("/"), filePath.lastIndexOf("."));
        String cmd;
        if (TextUtils.isEmpty(pass)) {
            cmd = Command.getExtractCmd(filePath, Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName);
        } else {
            cmd = Command.getExtractCmdPassword(filePath, Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName, pass);
        }
        result = P7ZipApi.executeCommand(cmd);

        File file = new File(Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName);
        File[] listFile = file.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file2 : listFile) {
                if (file2.isFile() && Utils.checkFileArchive(file2.getName())) {
                    String cmd2;
                    String fileName2 = file2.getAbsolutePath().substring(file2.getAbsolutePath().lastIndexOf("/"), file2.getAbsolutePath().lastIndexOf("."));

                    if (TextUtils.isEmpty(pass)) {
                        cmd2 = Command.getExtractCmd(file2.getAbsolutePath(), Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName2);
                    } else {
                        cmd2 = Command.getExtractCmdPassword(file2.getAbsolutePath(), Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName2, pass);
                    }
                    tempFolderExtract = Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName2;
                    result = P7ZipApi.executeCommand(cmd2);
                } else
                    tempFolderExtract = Utils.getCacheDir(NotificationService.this).getAbsolutePath() + fileName;
            }

        } else tempFolderExtract = getExternalCacheDir().getAbsolutePath() + fileName;
        return new ResultExtract(result, tempFolderExtract);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setUpNotification() {
        int id = this.id;
        String contentProcess = this.contentProcess;
        List<String> list = this.list;
        String folder = this.folder;
        String name = this.name;
        String type = this.type;

        RemoteViews remoteViews /*= new RemoteViews(getPackageName(), R.layout.remote_view_noti)*/;
        RemoteViews remoteViewsBig = new RemoteViews(getPackageName(), R.layout.remote_view_noti_big);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_noti);
        } else {
            remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view_noti_limit_view);
        }
        remoteViews.setTextViewText(R.id.tvProcess, "0%");
        remoteViews.setTextViewText(R.id.tvContentProcess, contentProcess);

        remoteViewsBig.setTextViewText(R.id.tvProcess, "0%");
        remoteViewsBig.setTextViewText(R.id.tvContentProcess, contentProcess);

        String title;
        if (contentProcess.equals(getString(R.string.adding))) {
            title = folder + File.separator + name + "." + type;
        } else {
            title = folder + File.separator + name;
        }
        remoteViews.setTextViewText(R.id.tvName, title);
        remoteViewsBig.setTextViewText(R.id.tvName, title);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NORMAL_ID);

        Intent intentCancel = new Intent(this, NotificationService.class);
        intentCancel.setAction(Constant.ACTION_CANCEL);
        intentCancel.putExtra(Constant.KEY_ID, id);
        intentCancel.putExtra(Constant.KEY_TITLE, title);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendIntentCancel = PendingIntent.getService(this, id, intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            remoteViews.setOnClickPendingIntent(R.id.tvCancel, pendIntentCancel);
        }
        remoteViewsBig.setOnClickPendingIntent(R.id.tvCancel, pendIntentCancel);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Constant.ACTION_SHOW_PROCESS);
        intent.putExtra(Constant.KEY_ID, id);
        intent.putExtra(Constant.KEY_CONTENT_PROGRESS, contentProcess);
        intent.putExtra(Constant.KEY_TITLE, title);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.ic_noti).setColor(getResources().getColor(R.color.blue))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle(contentProcess)
                .setContentIntent(pendIntent)
                .setCustomContentView(remoteViews)
                .setGroup(String.valueOf(id))
                .setVibrate(null)
                .setSound(null)
                .setCustomBigContentView(remoteViewsBig)
                .setCustomHeadsUpContentView(remoteViews);

        setMetadata(builder);
        Notification notification = builder.build();
        ProcessNoty p = new ProcessNoty(id, notification, remoteViews, remoteViewsBig, calculateTotalSize(), title, contentProcess);
        if (contentProcess.equals(getString(R.string.adding))) {
            p.setListFileCompress(list);
        } else {
            p.setPathFileExtract(list.get(0));
        }
        processNoty.add(p);
        NOTIFICATION_ID++;
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
}