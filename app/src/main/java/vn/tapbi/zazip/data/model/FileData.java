package vn.tapbi.zazip.data.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import vn.tapbi.zazip.utils.Utils;

public class FileData implements Parcelable {
    private int type;
    private String fileName;
    private long fileSize;
    private String filePath;
    private long fileTime;
    private String category = "";
    private String fileTimeString;
    private long folderSize;
    private String fileId;
    private long timeModified;
    private String time;
    private String linkImageDropBox = "";
    private boolean isChecked = false;
    private boolean isFolder = false;
    private int countItem;
    private String revFileDropBox = "";
    private boolean starred;
    private String createTime;
    private Drawable icon_apk;
    private String pathDisplayDropBox;
    private long durationVideo = 0;
    private int countFolder = 0;
    private String linkThumbnail = "";


    protected FileData(Parcel in) {
        type = in.readInt();
        fileName = in.readString();
        fileSize = in.readLong();
        filePath = in.readString();
        fileTime = in.readLong();
        fileTimeString = in.readString();
        folderSize = in.readLong();
        isChecked = in.readByte() != 0;
        isFolder = in.readByte() != 0;
        countItem = in.readInt();
        durationVideo = in.readLong();
    }

    public FileData(int type, String fileName, long fileSize, String filePath, long fileTime, String pathDisplayDropBox, String fileId, String revFileDropBox, String linkImageDropBox) {
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.time = Utils.getDates(fileTime);
        this.pathDisplayDropBox = pathDisplayDropBox;
        this.fileId = fileId;
        this.revFileDropBox = revFileDropBox;
        this.linkImageDropBox = linkImageDropBox;
    }

    public FileData(int type, String fileName, long fileSize, String filePath, long fileTime) {
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.time = Utils.getDates(fileTime);
    }

    public FileData(String revFileDropBox, int type, String fileName, long fileSize, String filePath, long fileTime, String fileId) {
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.time = Utils.getDates(fileTime);
        this.revFileDropBox = revFileDropBox;
        this.fileId = fileId;
    }

    public static final Creator<FileData> CREATOR = new Creator<FileData>() {
        @Override
        public FileData createFromParcel(Parcel in) {
            return new FileData(in);
        }

        @Override
        public FileData[] newArray(int size) {
            return new FileData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(fileName);
        dest.writeLong(fileSize);
        dest.writeString(filePath);
        dest.writeLong(fileTime);
        dest.writeString(fileTimeString);
        dest.writeLong(folderSize);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isFolder ? 1 : 0));
        dest.writeInt(countItem);
        dest.writeLong(durationVideo);
    }

    public String getLinkImageDropBox() {
        return linkImageDropBox;
    }

    public void setLinkImageDropBox(String linkImageDropBox) {
        this.linkImageDropBox = linkImageDropBox;
    }

    public String getLinkThumbnail() {
        return linkThumbnail;
    }

    public void setLinkThumbnail(String linkThumbnail) {
        this.linkThumbnail = linkThumbnail;
    }

    public String getRevFileDropBox() {
        return revFileDropBox;
    }

    public void setRevFileDropBox(String revFileDropBox) {
        this.revFileDropBox = revFileDropBox;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCreateTime() {
        return createTime;
    }

    public long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCountFolder() {
        return countFolder;
    }

    public void setCountFolder(int countFolder) {
        this.countFolder = countFolder;
    }

    public FileData() {
    }

    public FileData(String fileName, long time) {
        this.fileName = fileName;
        this.fileTime = time;
        this.type = 0;
    }

    public FileData(String fileName) {
        this.fileName = fileName;
        this.type = 0;
    }

    public FileData(int type, String fileName, long fileSize) {
        this.type = 0;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public FileData(String fileName, long fileSize, String filePath, long fileTime, long folderSize, Boolean isFolder, int countItem, Drawable iconApk, long durationVideo) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileTime = fileTime;
        this.folderSize = folderSize;
        this.isFolder = isFolder;
        this.countItem = countItem;
        this.icon_apk = iconApk;
        this.fileTimeString = Utils.getDate2(fileTime);
        this.type = 1;
        this.durationVideo = durationVideo;
    }

    public FileData(String fileName, long fileSize, String filePath, long fileTime, long folderSize, Boolean isFolder, int countItem, Drawable iconApk) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileTime = fileTime;
        this.folderSize = folderSize;
        this.isFolder = isFolder;
        this.countItem = countItem;
        this.icon_apk = iconApk;
        this.fileTimeString = Utils.getDate2(fileTime);
        this.type = 1;
    }

    public FileData(String fileName, long fileSize, String filePath, long fileTime, long folderSize, Boolean isFolder, int countItem) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileTime = fileTime;
        this.folderSize = folderSize;
        this.isFolder = isFolder;
        this.countItem = countItem;
        this.icon_apk = null;
        this.fileTimeString = Utils.getDate2(fileTime);
        this.type = 1;
    }

    public String getPathDisplayDropBox() {
        return pathDisplayDropBox;
    }

    public void setPathDisplayDropBox(String pathDisplayDropBox) {
        this.pathDisplayDropBox = pathDisplayDropBox;
    }

    public String getFileId() {
        return fileId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getDurationVideo() {
        return durationVideo;
    }

    public void setDurationVideo(long durationVideo) {
        this.durationVideo = durationVideo;
    }

    public Drawable getIcon_apk() {
        return icon_apk;
    }

    public void setIcon_apk(Drawable icon_apk) {
        this.icon_apk = icon_apk;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public int getCountItem() {
        return countItem;
    }

    public void setCountItem(int countItem) {
        this.countItem = countItem;
    }

    public long getFolderSize() {
        return folderSize;
    }

    public void setFolderSize(long folderSize) {
        this.folderSize = folderSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileTime() {
        return fileTime;
    }

    public void setFileTime(long fileTime) {
        this.fileTime = fileTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getFileTimeString() {
        return fileTimeString;
    }

    public void setFileTimeString(String fileTimeString) {
        this.fileTimeString = fileTimeString;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "type=" + type +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", filePath='" + filePath + '\'' +
                ", fileTime=" + fileTime +
                ", category='" + category + '\'' +
                ", fileTimeString='" + fileTimeString + '\'' +
                ", folderSize=" + folderSize +
                ", fileId='" + fileId + '\'' +
                ", timeModified=" + timeModified +
                ", time='" + time + '\'' +
                ", isChecked=" + isChecked +
                ", isFolder=" + isFolder +
                ", countItem=" + countItem +
                ", revFileDropBox='" + revFileDropBox + '\'' +
                ", starred=" + starred +
                ", createTime='" + createTime + '\'' +
                ", icon_apk=" + icon_apk +
                ", pathDisplayDropBox='" + pathDisplayDropBox + '\'' +
                ", durationVideo=" + durationVideo +
                ", countFolder=" + countFolder +
                ", linkThumbnail='" + linkThumbnail + '\'' +
                '}';
    }
}