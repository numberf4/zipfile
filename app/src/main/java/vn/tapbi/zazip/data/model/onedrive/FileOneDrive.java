
package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileOneDrive {

    @SerializedName("createdDateTime")
    @Expose
    private String createdDateTime;
    @SerializedName("cTag")
    @Expose
    private String cTag;
    @SerializedName("eTag")
    @Expose
    private String eTag;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lastModifiedDateTime")
    @Expose
    private String lastModifiedDateTime;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("size")
    @Expose
    private long size;
    @SerializedName("webUrl")
    @Expose
    private String webUrl;
    @SerializedName("reactions")
    @Expose
    private Reactions reactions;
    @SerializedName("createdBy")
    @Expose
    private CreatedBy createdBy;
    @SerializedName("lastModifiedBy")
    @Expose
    private LastModifiedBy lastModifiedBy;
    @SerializedName("parentReference")
    @Expose
    private ParentReference parentReference;
    @SerializedName("fileSystemInfo")
    @Expose
    private FileSystemInfo fileSystemInfo;
    @SerializedName("folder")
    @Expose
    private Folder folder;
    @SerializedName("specialFolder")
    @Expose
    private SpecialFolder specialFolder;
    @SerializedName("@microsoft.graph.downloadUrl")
    @Expose
    private String microsoftGraphDownloadUrl;
    @SerializedName("file")
    @Expose
    private File file;

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getcTag() {
        return cTag;
    }

    public void setcTag(String cTag) {
        this.cTag = cTag;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Reactions getReactions() {
        return reactions;
    }

    public void setReactions(Reactions reactions) {
        this.reactions = reactions;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public LastModifiedBy getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(LastModifiedBy lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ParentReference getParentReference() {
        return parentReference;
    }

    public void setParentReference(ParentReference parentReference) {
        this.parentReference = parentReference;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

    public void setFileSystemInfo(FileSystemInfo fileSystemInfo) {
        this.fileSystemInfo = fileSystemInfo;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public SpecialFolder getSpecialFolder() {
        return specialFolder;
    }

    public void setSpecialFolder(SpecialFolder specialFolder) {
        this.specialFolder = specialFolder;
    }

    public String getMicrosoftGraphDownloadUrl() {
        return microsoftGraphDownloadUrl;
    }

    public void setMicrosoftGraphDownloadUrl(String microsoftGraphDownloadUrl) {
        this.microsoftGraphDownloadUrl = microsoftGraphDownloadUrl;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
