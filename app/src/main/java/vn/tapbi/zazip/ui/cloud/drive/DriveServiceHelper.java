package vn.tapbi.zazip.ui.cloud.drive;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import timber.log.Timber;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.GoogleDriveFileHolder;
import vn.tapbi.zazip.utils.Utils;


public class DriveServiceHelper implements Serializable {
    private static final String TAG = "DriveServiceHelper";
    public static String TYPE_AUDIO = "application/vnd.google-apps.audio";
    public static String TYPE_GOOGLE_DOCS = "application/vnd.google-apps.document";
    public static String TYPE_GOOGLE_DRAWING = "application/vnd.google-apps.drawing";
    public static String TYPE_GOOGLE_DRIVE_FILE = "application/vnd.google-apps.file";
    public static String TYPE_GOOGLE_DRIVE_FOLDER = DriveFolder.MIME_TYPE;
    public static String TYPE_GOOGLE_FORMS = "application/vnd.google-apps.form";
    public static String TYPE_GOOGLE_FUSION_TABLES = "application/vnd.google-apps.fusiontable";
    public static String TYPE_GOOGLE_MY_MAPS = "application/vnd.google-apps.map";
    public static String TYPE_PHOTO = "application/vnd.google-apps.photo";
    public static String TYPE_GOOGLE_SLIDES = "application/vnd.google-apps.presentation";
    public static String TYPE_GOOGLE_APPS_SCRIPTS = "application/vnd.google-apps.script";
    public static String TYPE_GOOGLE_SITES = "application/vnd.google-apps.site";
    public static String TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet";
    public static String TYPE_UNKNOWN = "application/vnd.google-apps.unknown";
    public static String TYPE_VIDEO = "application/vnd.google-apps.video";
    public static String TYPE_3_RD_PARTY_SHORTCUT = "application/vnd.google-apps.drive-sdk";
    public static String EXPORT_TYPE_HTML = "text/html";
    public static String EXPORT_TYPE_HTML_ZIPPED = "application/zip";
    public static String EXPORT_TYPE_PLAIN_TEXT = "text/plain";
    public static String EXPORT_TYPE_RICH_TEXT = "application/rtf";
    public static String EXPORT_TYPE_OPEN_OFFICE_DOC = "application/vnd.oasis.opendocument.text";
    public static String EXPORT_TYPE_PDF = "application/pdf";
    public static String EXPORT_TYPE_MS_WORD_DOCUMENT = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static String EXPORT_TYPE_MS_DOC = "application/msword";
    public static String EXPORT_TYPE_MS_XLSX = "application/vnd.ms-excel";
    public static String EXPORT_TYPE_MS_POWERPOINT = "application/vnd.ms-powerpoint";
    public static String EXPORT_TYPE_EPUB = "application/epub+zip";
    public static String EXPORT_TYPE_MS_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String EXPORT_TYPE_OPEN_OFFICE_SHEET = "application/x-vnd.oasis.opendocument.spreadsheet";
    public static String EXPORT_TYPE_CSV = "text/csv";
    public static String EXPORT_TYPE_TSV = "text/tab-separated-values";
    public static String EXPORT_TYPE_ZIP = "application/zip";
    public static String EXPORT_TYPE_JPEG = "image/jpeg";
    public static String EXPORT_TYPE_RAR = "application/rar";
    public static String EXPORT_TYPE_PNG = "image/png";
    public static String EXPORT_TYPE_SVG = "image/svg+xml";
    public static String EXPORT_TYPE_SHORT_CUT = "application/vnd.google-apps.shortcut";
    public static String EXPORT_TYPE_ZIP_COMPRESSED = "application/x-zip-compressed";
    public static String EXPORT_TYPE_MS_POWER_POINT = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static String EXPORT_TYPE_OPEN_OFFICE_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
    public static String EXPORT_TYPE_JSON = "application/vnd.google-apps.script+json";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    public static Drive getGoogleDriveService(Context context, GoogleSignInAccount account, String appName) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE));
        Account account1 = new Account("yen.nguyenthihai@tapbi.vn", "com.google");
        credential.setSelectedAccount(account1);
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName(appName)
                        .build();
        return googleDriveService;
    }

    public static Drive getGoogleDriveService(Context context, String account, String type, String appName) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE));
        Account account1 = new Account(account, type);
        credential.setSelectedAccount(account1);
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName(appName)
                        .build();
        return googleDriveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */

    public Task<String> createFile(final String fileName) {
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                File metadata = new File()
                        .setParents(Collections.singletonList("root"))
                        .setMimeType("text/plain")
                        .setName(fileName);

                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                return googleFile.getId();
            }
        });
    }

    public Task<String> createFile(final String fileName, @Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {

                    root = Collections.singletonList(folderId);
                }

                File metadata = new File()
                        .setParents(root)
                        .setMimeType("text/plain")
                        .setName(fileName);

                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                return googleFile.getId();
            }
        });
    }

    public Task<GoogleDriveFileHolder> createTextFile(final String fileName, final String content, @Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {

                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {

                    root = Collections.singletonList(folderId);
                }

                File metadata = new File()
                        .setParents(root)
                        .setMimeType("text/plain")
                        .setName(fileName);
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                File googleFile = mDriveService.files().create(metadata, contentStream).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(googleFile.getId());
                return googleDriveFileHolder;
            }
        });
    }

    public Task<GoogleDriveFileHolder> createTextFileIfNotExist(final String fileName, final String content, @Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();

                FileList result = mDriveService.files().list()
                        .setQ("mimeType = 'text/plain' and name = '" + fileName + "' ")
                        .setSpaces("drive")
                        .execute();

                if (result.getFiles().size() > 0) {
                    googleDriveFileHolder.setId(result.getFiles().get(0).getId());
                    return googleDriveFileHolder;
                } else {

                    List<String> root;
                    if (folderId == null) {
                        root = Collections.singletonList("root");
                    } else {

                        root = Collections.singletonList(folderId);
                    }

                    File metadata = new File()
                            .setParents(root)
                            .setMimeType("text/plain")
                            .setName(fileName);
                    ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                    File googleFile = mDriveService.files().create(metadata, contentStream).execute();
                    if (googleFile == null) {
                        throw new IOException("Null result when requesting file creation.");
                    }

                    googleDriveFileHolder.setId(googleFile.getId());

                    return googleDriveFileHolder;
                }
            }
        });
    }

    public Task<GoogleDriveFileHolder> createFolder(final String folderName, @Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();

                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {

                    root = Collections.singletonList(folderId);
                }
                File metadata = new File()
                        .setParents(root)
                        .setMimeType(DriveFolder.MIME_TYPE)
                        .setName(folderName);

                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }
                googleDriveFileHolder.setId(googleFile.getId());
                return googleDriveFileHolder;
            }
        });
    }

    public Task<GoogleDriveFileHolder> createFolderIfNotExist(final String folderName, @Nullable final String parentFolderId) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                FileList result = mDriveService.files().list()
                        .setQ("mimeType = '" + DriveFolder.MIME_TYPE + "' and name = '" + folderName + "' ")
                        .setSpaces("drive")
                        .execute();

                if (result.getFiles().size() > 0) {
                    googleDriveFileHolder.setId(result.getFiles().get(0).getId());
                    googleDriveFileHolder.setName(result.getFiles().get(0).getName());
                    googleDriveFileHolder.setId(result.getFiles().get(0).getId());

                } else {
                    List<String> root;
                    if (parentFolderId == null) {
                        root = Collections.singletonList("root");
                    } else {

                        root = Collections.singletonList(parentFolderId);
                    }
                    File metadata = new File()
                            .setParents(root)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setName(folderName);

                    File googleFile = mDriveService.files().create(metadata).execute();
                    if (googleFile == null) {
                        throw new IOException("Null result when requesting file creation.");
                    }
                    googleDriveFileHolder.setId(googleFile.getId());
                }
                return googleDriveFileHolder;
            }
        });
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, String>> readFile(final String fileId) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                // Retrieve the metadata as a File object.
                File metadata = mDriveService.files().get(fileId).execute();
                String name = metadata.getName();

                // Stream the file contents to a String.
                try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();

                    return Pair.create(name, contents);
                }
            }
        });
    }

    public Task<Void> deleteFolderFile(final String fileId) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Retrieve the metadata as a File object.
                if (fileId != null) {
                    mDriveService.files().delete(fileId).execute();
                }

                return null;
            }
        });
    }

    public Task<GoogleDriveFileHolder> uploadFile(final File googleDiveFile, final AbstractInputStreamContent content) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                // Retrieve the metadata as a File object.
                File fileMeta = mDriveService.files().create(googleDiveFile, content).execute();
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(fileMeta.getId());
                googleDriveFileHolder.setName(fileMeta.getName());
                return googleDriveFileHolder;
            }
        });
    }

    public Task<Boolean> uploadFile(final java.io.File localFile, final String mimeType, @Nullable final String folderId, String name) {
        return Tasks.call(mExecutor, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // Retrieve the metadata as a File object.
                try {
                    List<String> root = new ArrayList<>();
                    root.add(folderId);
                    File fileMetadata = new File();
                    fileMetadata.setParents(root);
                    fileMetadata.setName(name + "." + mimeType);
                    fileMetadata.setMimeType(mimeType);
                    String type = Utils.convertTypeFileUpload(mimeType);
                    FileContent fileContent = new FileContent(type, localFile);
                    File file = mDriveService.files().create(fileMetadata, fileContent)/*.setFields("folderId")*/.execute();
                } catch (Exception e) {
                }

                return true;
            }
        });
    }

    public Task<List<GoogleDriveFileHolder>> searchFolder(final String folderName) {
        return Tasks.call(mExecutor, new Callable<List<GoogleDriveFileHolder>>() {
            @Override
            public List<GoogleDriveFileHolder> call() throws Exception {
                List<GoogleDriveFileHolder> googleDriveFileHolderList = new ArrayList<>();
                // Retrive the metadata as a File object.
                FileList result = mDriveService.files().list()
                        .setQ("mimeType = '" + DriveFolder.MIME_TYPE + "' and name = '" + folderName + "' ")
                        .setSpaces("drive")
                        .execute();

                for (int i = 0; i < result.getFiles().size(); i++) {

                    GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                    googleDriveFileHolder.setId(result.getFiles().get(i).getId());
                    googleDriveFileHolder.setName(result.getFiles().get(i).getName());

                    googleDriveFileHolderList.add(googleDriveFileHolder);
                }

                return googleDriveFileHolderList;
            }
        });
    }

    public Task<List<GoogleDriveFileHolder>> searchFile(final String fileName, final String mimeType) {
        return Tasks.call(mExecutor, new Callable<List<GoogleDriveFileHolder>>() {
            @Override
            public List<GoogleDriveFileHolder> call() throws Exception {
                List<GoogleDriveFileHolder> googleDriveFileHolderList = new ArrayList<>();
                // Retrive the metadata as a File object.
                FileList result = mDriveService.files().list()
                        .setQ("name = '" + fileName + "' and mimeType ='" + mimeType + "'")
                        .setSpaces("drive")
                        .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
                        .execute();


                for (int i = 0; i < result.getFiles().size(); i++) {
                    GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                    googleDriveFileHolder.setId(result.getFiles().get(i).getId());
                    googleDriveFileHolder.setName(result.getFiles().get(i).getName());
                    googleDriveFileHolder.setModifiedTime(result.getFiles().get(i).getModifiedTime());
                    googleDriveFileHolder.setSize(result.getFiles().get(i).getSize());
                    googleDriveFileHolderList.add(googleDriveFileHolder);

                }

                return googleDriveFileHolderList;
            }
        });
    }

    public Task<Boolean> downloadFile(String fileSaveLocation, final String fileId, String mime) {
        return Tasks.call(mExecutor, new Callable<Boolean>() {
            @Override
            public Boolean call(){
                try {
                    OutputStream outputStream = new FileOutputStream(fileSaveLocation);
                    if (mime.equals(TYPE_GOOGLE_SHEETS)) {
                        mDriveService.files().export(fileId, EXPORT_TYPE_MS_EXCEL).executeMediaAndDownloadTo(outputStream);
                    } else if (mime.equals(TYPE_GOOGLE_SLIDES)) {
                        mDriveService.files().export(fileId, EXPORT_TYPE_MS_POWER_POINT).executeMediaAndDownloadTo(outputStream);
                    } else if (mime.equals(TYPE_GOOGLE_DOCS)) {
                        mDriveService.files().export(fileId, EXPORT_TYPE_MS_WORD_DOCUMENT).executeMediaAndDownloadTo(outputStream);
                    } else {
                        mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                    }
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        });
    }

    public Task<InputStream> downloadFile(final String fileId) {
        return Tasks.call(mExecutor, new Callable<InputStream>() {
            @Override
            public InputStream call() throws Exception {
                // Retrieve the metadata as a File object.
                return mDriveService.files().get(fileId).executeMediaAsInputStream();
            }
        });
    }

    public Task<Void> exportFile(final java.io.File fileSaveLocation, final String fileId, final String mimeType) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Retrieve the metadata as a File object.
                OutputStream outputStream = new FileOutputStream(fileSaveLocation);
                mDriveService.files().export(fileId, mimeType).executeMediaAndDownloadTo(outputStream);
                return null;
            }
        });
    }

    public Task<String> getLinkShare(String id) {
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return mDriveService.files().get(id).setFields("webViewLink").execute().getWebViewLink();
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(final String fileId, final String name, final String content) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Create a File containing any metadata changes.
                File metadata = new File().setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                return null;
            }
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<List<FileData>> queryFiles() {
        return Tasks.call(mExecutor,
                () -> {
                    List<FileData> googleDriveFileHolderList = new ArrayList<>();
                    FileList result;
                    result = mDriveService.files().list().setQ("'root' in parents and trashed=false").setFields("files(id,name,size,createdTime,modifiedTime,starred,mimeType)").setSpaces("drive").execute();
                    if (result != null) {
                        googleDriveFileHolderList = getGoogleDriveFiles(result);
                    }
                    return googleDriveFileHolderList;
                }
        );
    }

    public Task<List<FileData>> queryFiles(@Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<List<FileData>>() {
                    @Override
                    public List<FileData> call() throws Exception {
                        String parent = "root";
                        if (folderId != null) {
                            parent = folderId;
                        }
                        FileList mockupList = mDriveService.files().list()
                                .setPageSize(100)
                                .setQ("'" + folderId + "' in parents")
                                .setFields("nextPageToken, files(id, name, thumbnailLink, createdTime, hasThumbnail)")
                                .execute();
                        FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents and trashed=false").
                                setFields("files(id, name,size,createdTime,modifiedTime,starred,mimeType,thumbnailLink,hasThumbnail)").setSpaces("drive").execute();
                        return getGoogleDriveFiles(result);
                    }
                }
        );
    }

    public Task<List<FileData>> queryFolders() {
        return Tasks.call(mExecutor,
                () -> {
                    List<FileData> googleDriveFileHolderList = new ArrayList<>();
                    FileList result;
                    result = mDriveService.files().list().setQ("'root' in parents and mimeType='application/vnd.google-apps.folder' and trashed=false").setFields("files(id,name,size,createdTime,modifiedTime,starred,mimeType)").setSpaces("drive").execute();
                    if (result != null) {
                        googleDriveFileHolderList = getGoogleDriveFiles(result);
                    }
                    return googleDriveFileHolderList;
                }
        );
    }

    public Task<List<FileData>> queryFolders(String folderId) {
        return Tasks.call(mExecutor, new Callable<List<FileData>>() {
                    @Override
                    public List<FileData> call() throws Exception {
                        String parent = "root";
                        if (folderId != null) {
                            parent = folderId;
                        }
                        FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents and mimeType='application/vnd.google-apps.folder' and trashed=false").setFields("files(id, name,size,createdTime,modifiedTime,starred,mimeType)").setSpaces("drive").execute();
                        return getGoogleDriveFiles(result);
                    }
                }
        );
    }

    private List<FileData> getGoogleDriveFiles(FileList result) {
        List<FileData> googleDriveFileHolderList2 = new ArrayList<>();
        for (int i = 0; i < result.getFiles().size(); i++) {
                FileData document = new FileData();
                if (result.getFiles().get(i).getMimeType().equals(TYPE_GOOGLE_DRIVE_FOLDER)) {
                    document.setFolder(true);
                }

                if (result.getFiles().get(i).getThumbnailLink() != null) {
                    document.setFilePath(result.getFiles().get(i).getThumbnailLink());
                }

                document.setType(Constant.TYPE_GG_DRIVE_FILE);
                document.setFileId(result.getFiles().get(i).getId());
                document.setFileName(result.getFiles().get(i).getName());

                if (result.getFiles().get(i).getSize() != null) {
                    document.setFileSize(result.getFiles().get(i).getSize());
                }

                if (result.getFiles().get(i).getModifiedTime() != null) {
                    document.setTime(Utils.getDateTimeZone(result.getFiles().get(i).getModifiedTime()));
                }

                if (result.getFiles().get(i).getCreatedTime() != null) {
                    document.setCreateTime(Utils.getDateTimeZone(result.getFiles().get(i).getCreatedTime()));
                }

                if (result.getFiles().get(i).getStarred() != null) {
                    document.setStarred(result.getFiles().get(i).getStarred());
                }
                if (result.getFiles().get(i).getMimeType() != null) {
                    document.setCategory(result.getFiles().get(i).getMimeType());
                }
                googleDriveFileHolderList2.add(document);
        }
        Collections.sort(googleDriveFileHolderList2, new Comparator<FileData>() {
            public int compare(FileData o1, FileData o2) {
                return o1.getTimeModified() > o2.getTimeModified() ? -1 : 1;
            }
        });
        return googleDriveFileHolderList2;
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            final ContentResolver contentResolver, final Uri uri) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                // Retrieve the document's display name from its metadata.
                String name;
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        name = cursor.getString(nameIndex);
                    } else {
                        throw new IOException("Empty cursor returned for file.");
                    }
                }

                // Read the document's contents as a String.
                String content;
                try (InputStream is = contentResolver.openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    content = stringBuilder.toString();
                }

                return Pair.create(name, content);
            }
        });
    }

}
