package vn.tapbi.zazip.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.core.v2.fileproperties.PropertyGroup;
import com.dropbox.core.v2.files.ExportInfo;
import com.dropbox.core.v2.files.FileLockMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FileSharingInfo;
import com.dropbox.core.v2.files.MediaInfo;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.SymlinkInfo;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class FileMetadataDropBox implements Parcelable {
    @SerializedName(".tag")
    private String tag = "";
    private String name = "";
    private String id = "";
    private String client_modified = "";
    private String server_modified = "";
    private String rev = "";
    private long size = 0;
    private String path_lower = "";
    private String path_display = "";
    private boolean is_downloadable = true;
    private String content_hash = "";

    protected FileMetadataDropBox(Parcel in) {
        tag = in.readString();
        name = in.readString();
        id = in.readString();
        client_modified = in.readString();
        server_modified = in.readString();
        rev = in.readString();
        size = in.readLong();
        path_lower = in.readString();
        path_display = in.readString();
        is_downloadable = in.readByte() != 0;
        content_hash = in.readString();
    }

    public static final Creator<FileMetadataDropBox> CREATOR = new Creator<FileMetadataDropBox>() {
        @Override
        public FileMetadataDropBox createFromParcel(Parcel in) {
            return new FileMetadataDropBox(in);
        }

        @Override
        public FileMetadataDropBox[] newArray(int size) {
            return new FileMetadataDropBox[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(client_modified);
        dest.writeString(server_modified);
        dest.writeString(rev);
        dest.writeLong(size);
        dest.writeString(path_lower);
        dest.writeString(path_display);
        dest.writeByte((byte) (is_downloadable ? 1 : 0));
        dest.writeString(content_hash);
    }

    public FileMetadataDropBox(String tag, String name, String id, String client_modified, String server_modified, String rev, long size, String path_lower, String path_display, boolean is_downloadable, String content_hash) {
        this.tag = tag;
        this.name = name;
        this.id = id;
        this.client_modified = client_modified;
        this.server_modified = server_modified;
        this.rev = rev;
        this.size = size;
        this.path_lower = path_lower;
        this.path_display = path_display;
        this.is_downloadable = is_downloadable;
        this.content_hash = content_hash;
    }

    public FileMetadataDropBox(String tag, String name, String id, String path_lower, String path_display) {
        this.tag = tag;
        this.name = name;
        this.id = id;
        this.path_lower = path_lower;
        this.path_display = path_display;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient_modified() {
        return client_modified;
    }

    public void setClient_modified(String client_modified) {
        this.client_modified = client_modified;
    }

    public String getServer_modified() {
        return server_modified;
    }

    public void setServer_modified(String server_modified) {
        this.server_modified = server_modified;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath_lower() {
        return path_lower;
    }

    public void setPath_lower(String path_lower) {
        this.path_lower = path_lower;
    }

    public String getPath_display() {
        return path_display;
    }

    public void setPath_display(String path_display) {
        this.path_display = path_display;
    }

    public boolean isIs_downloadable() {
        return is_downloadable;
    }

    public void setIs_downloadable(boolean is_downloadable) {
        this.is_downloadable = is_downloadable;
    }

    public String getContent_hash() {
        return content_hash;
    }

    public void setContent_hash(String content_hash) {
        this.content_hash = content_hash;
    }
}
