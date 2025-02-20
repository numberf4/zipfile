package vn.tapbi.zazip.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.util.List;

public class DocumentDrive implements Parcelable {

    private String title;
    private String idFolder;

    private List<FileData> documents;
    private List<Metadata> metadataList;
    private List<FileMetadata> fileMetadataList;
    private List<FolderMetadata> folderMetadataList;


    public DocumentDrive(String title, String idFolder, List<FileData> documents) {
        this.idFolder = idFolder;
        this.title = title;
        this.documents = documents;
    }

    public DocumentDrive(String title, String idFolder, List<FileData> documents, List<Metadata> metadataList) {
        this.title = title;
        this.idFolder = idFolder;
        this.documents = documents;
        this.metadataList = metadataList;
    }

    public DocumentDrive(String title, String idFolder, List<FileData> documents, List<FileMetadata> fileMetadataList, List<FolderMetadata> folderMetadataList) {
        this.title = title;
        this.idFolder = idFolder;
        this.documents = documents;
        this.fileMetadataList = fileMetadataList;
        this.folderMetadataList = folderMetadataList;
    }

    public DocumentDrive(String title, List<FileData> documents) {
        this.title = title;
        this.documents = documents;
    }

    protected DocumentDrive(Parcel in) {
        title = in.readString();
        idFolder = in.readString();
        documents = in.createTypedArrayList(FileData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(idFolder);
        dest.writeTypedList(documents);
    }

    public List<FileMetadata> getFileMetadataList() {
        return fileMetadataList;
    }

    public void setFileMetadataList(List<FileMetadata> fileMetadataList) {
        this.fileMetadataList = fileMetadataList;
    }

    public List<FolderMetadata> getFolderMetadataList() {
        return folderMetadataList;
    }

    public void setFolderMetadataList(List<FolderMetadata> folderMetadataList) {
        this.folderMetadataList = folderMetadataList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DocumentDrive> CREATOR = new Creator<DocumentDrive>() {
        @Override
        public DocumentDrive createFromParcel(Parcel in) {
            return new DocumentDrive(in);
        }

        @Override
        public DocumentDrive[] newArray(int size) {
            return new DocumentDrive[size];
        }
    };

    public List<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public String getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(String idFolder) {
        this.idFolder = idFolder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FileData> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FileData> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "DocumentDrive{" +
                "title='" + title + '\'' +
                ", idFolder='" + idFolder + '\'' +
                ", documents=" + documents +
                ", metadataList=" + metadataList +
                '}';
    }
}
