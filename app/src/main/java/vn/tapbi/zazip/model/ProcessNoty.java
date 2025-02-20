package vn.tapbi.zazip.model;

import android.app.Notification;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProcessNoty {
    private int id;
    private String path;
    private String title;
    private RemoteViews remoteViews;
    private RemoteViews remoteViewsBig;
    private long totalSize;
    private Notification notification;
    private boolean isNotyShow;
    private int resultCode = -1;
    private int processType = 0;

    private String contentProcess;
    private String pathFileExtract = "";
    private List<String> listFileCompress = new ArrayList<>();
    private Disposable disposable;

    public ProcessNoty(int id, Notification notification, RemoteViews remoteViews, RemoteViews remoteViewsBig, long totalSize, String title, String contentProcess) {
        this.id = id;
        this.notification = notification;
        this.path = "";
        this.remoteViews = remoteViews;
        this.remoteViewsBig = remoteViewsBig;
        this.totalSize = totalSize;
        this.title = title;
        this.contentProcess = contentProcess;
    }

    public ProcessNoty(int id, String title, RemoteViews remoteViews, RemoteViews remoteViewsBig, long totalSize, Notification notification) {
        this.id = id;
        this.title = title;
        this.remoteViews = remoteViews;
        this.remoteViewsBig = remoteViewsBig;
        this.totalSize = totalSize;
        this.notification = notification;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public boolean isNotyShow() {
        return isNotyShow;
    }

    public void setNotyShow(boolean notyShow) {
        isNotyShow = notyShow;
    }

    public RemoteViews getRemoteViewsBig() {
        return remoteViewsBig;
    }

    public void setRemoteViewsBig(RemoteViews remoteViewsBig) {
        this.remoteViewsBig = remoteViewsBig;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    public String getContentProcess() {
        return contentProcess;
    }

    public void setContentProcess(String contentProcess) {
        this.contentProcess = contentProcess;
    }

    public String getPathFileExtract() {
        return pathFileExtract;
    }

    public void setPathFileExtract(String pathFileExtract) {
        this.pathFileExtract = pathFileExtract;
    }

    public List<String> getListFileCompress() {
        return listFileCompress;
    }

    public void setListFileCompress(List<String> listFileCompress) {
        this.listFileCompress = listFileCompress;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRemoteViews(RemoteViews remoteViews) {
        this.remoteViews = remoteViews;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public RemoteViews getRemoteViews() {
        return remoteViews;
    }
}
