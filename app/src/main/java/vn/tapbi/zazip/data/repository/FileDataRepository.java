package vn.tapbi.zazip.data.repository;

import static vn.tapbi.zazip.common.Constant.ARCHIVE_7ZIP;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_BZ2;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_GZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_LZ4;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_RAR;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR_BZ2;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR_GZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR_LZ4;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR_XZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TAR_ZST;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TBZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TBZ2;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TGZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TLZ4;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_TXZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_XZ;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_ZIP;
import static vn.tapbi.zazip.common.Constant.ARCHIVE_ZST;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_DOC;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_DOC_X;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_HTML;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PDF;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_POT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_POTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPS;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPSX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPTM;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PUB;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_TEXT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_TXT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLS;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLSM;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLXS;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XML;
import static vn.tapbi.zazip.common.Constant.IMAGE_AI;
import static vn.tapbi.zazip.common.Constant.IMAGE_BMP;
import static vn.tapbi.zazip.common.Constant.IMAGE_EPS;
import static vn.tapbi.zazip.common.Constant.IMAGE_GIF;
import static vn.tapbi.zazip.common.Constant.IMAGE_HEIC;
import static vn.tapbi.zazip.common.Constant.IMAGE_INDD;
import static vn.tapbi.zazip.common.Constant.IMAGE_JPEG;
import static vn.tapbi.zazip.common.Constant.IMAGE_JPG;
import static vn.tapbi.zazip.common.Constant.IMAGE_PNG;
import static vn.tapbi.zazip.common.Constant.IMAGE_PSD;
import static vn.tapbi.zazip.common.Constant.IMAGE_TGA;
import static vn.tapbi.zazip.common.Constant.IMAGE_TIFF;
import static vn.tapbi.zazip.common.Constant.IMAGE_WEBP;
import static vn.tapbi.zazip.common.Constant.SOUND_MP3;
import static vn.tapbi.zazip.common.Constant.TYPE_APK;
import static vn.tapbi.zazip.common.Constant.VIDEO_3GP;
import static vn.tapbi.zazip.common.Constant.VIDEO_AVI;
import static vn.tapbi.zazip.common.Constant.VIDEO_DLVX;
import static vn.tapbi.zazip.common.Constant.VIDEO_FLV;
import static vn.tapbi.zazip.common.Constant.VIDEO_MKV;
import static vn.tapbi.zazip.common.Constant.VIDEO_MOV;
import static vn.tapbi.zazip.common.Constant.VIDEO_MP4;
import static vn.tapbi.zazip.common.Constant.VIDEO_MPGE;
import static vn.tapbi.zazip.common.Constant.VIDEO_VOB;
import static vn.tapbi.zazip.common.Constant.VIDEO_WEBV;
import static vn.tapbi.zazip.common.Constant.VIDEO_WMV;
import static vn.tapbi.zazip.common.Constant.VIDEO_XVID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.model.ItemSelectBottom;
import vn.tapbi.zazip.model.PresentationOption;
import vn.tapbi.zazip.utils.FileProperties;
import vn.tapbi.zazip.utils.Utils;
import vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper;

public class FileDataRepository {
    private final File fileDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static final String ANDROID_DATA = "Android%2Fdata";
    private static final String ANDROID_OBB = "Android%2Fobb";
    private FileProperties fileProperties;
    public List<FileData> listDoc = new LinkedList<>();
    public List<FileData> listPhoto = new LinkedList<>();
    public List<FileData> listMusic = new LinkedList<>();
    public List<FileData> listDownload = new LinkedList<>();
    public List<FileData> listVideo = new LinkedList<>();
    public List<FileData> listApk = new LinkedList<>();
    public List<FileData> listArchiver = new LinkedList<>();

    @Inject
    public FileDataRepository(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public Single<List<FileData>> searchFile(Context context, String text) {
        return Single.fromCallable(() -> search(context, text)).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PresentationOption>> getListPresentationViews(Context context) {
        return Single.fromCallable(() -> getListPresentationView(context)).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PresentationOption>> getListOptionSortView(Context context) {
        return Single.fromCallable(() -> listOptionSortView(context)).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<ItemSelectBottom>> getListOptionBottom(Context context) {
        return Single.fromCallable(() -> FileDataRepository.this.listOptionBottom(context)).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> getDocumentFile(Context context) {
        return Single.fromCallable(() -> (getFileDocAndApk(context)) && getFileDownload(fileDownload, context))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> getVideoFile(Context context) {
        return Single.fromCallable(() -> getFileVideo(context))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> getPhotoFile(Context context) {
        return Single.fromCallable(() -> getFilePhoto(context))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> getMusicFile(Context context) {
        return Single.fromCallable(() -> getFileMusic(context))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FileData>> getFileInDevice(String actualPath, String filePath, Context context, boolean onlyFolder) {
        if (actualPath.equals(filePath)) {
            File file1 = new File(filePath);
            return Single.fromCallable(() -> FileDataRepository.this.baseGetListFileData(getAllFile(file1), context, onlyFolder)).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return Single.fromCallable(() -> FileDataRepository.this.baseGetListFileDataInQ(actualPath, filePath, context, onlyFolder)).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private List<ItemSelectBottom> listOptionBottom(Context context) {
        List<ItemSelectBottom> list = new LinkedList<>();
        list.add(new ItemSelectBottom(R.drawable.ic_rename, context.getString(R.string.rename), context.getResources().getColor(R.color.text_blue)));
        list.add(new ItemSelectBottom(R.drawable.ic_coppy, context.getString(R.string.copy), context.getResources().getColor(R.color.text_blue)));
        list.add(new ItemSelectBottom(R.drawable.ic_extract, context.getString(R.string.extract), context.getResources().getColor(R.color.text_blue)));
        list.add(new ItemSelectBottom(R.drawable.ic_compress, context.getString(R.string.compress), context.getResources().getColor(R.color.text_blue)));
        list.add(new ItemSelectBottom(R.drawable.ic_share, context.getString(R.string.share), context.getResources().getColor(R.color.text_blue)));
        list.add(new ItemSelectBottom(R.drawable.ic_delete, context.getString(R.string.delete), context.getResources().getColor(R.color.text_blue)));
        return list;
    }

    private List<PresentationOption> getListPresentationView(Context context) {
        List<PresentationOption> list = new LinkedList<>();
        list.add(new PresentationOption(context.getString(R.string.detailed), R.drawable.option_view_detail, R.drawable.option_view_detail_select));
        list.add(new PresentationOption(context.getString(R.string.compact), R.drawable.option_view_compact, R.drawable.option_view_compact_select));
        list.add(new PresentationOption(context.getString(R.string.grid), R.drawable.option_view_grid, R.drawable.option_view_grid_select));
        return list;
    }

    private List<PresentationOption> listOptionSortView(Context context) {
        List<PresentationOption> list = new LinkedList<>();
        list.add(new PresentationOption(context.getString(R.string.name), R.drawable.option_sort_name, R.drawable.option_sort_name_select));
        list.add(new PresentationOption(context.getString(R.string.date), R.drawable.option_sort_date, R.drawable.option_sort_date_select));
        list.add(new PresentationOption(context.getString(R.string.type), R.drawable.option_sort_type, R.drawable.option_sort_type_select));
        list.add(new PresentationOption(context.getString(R.string.size), R.drawable.option_sort_size, R.drawable.option_sort_size_select));
        list.add(new PresentationOption(context.getString(R.string.desc), R.drawable.option_sort_desc, R.drawable.option_sort_desc_select));
        return list;
    }


    public List<FileData> baseGetListFileData(List<String> fileList, Context context, boolean onlyFolder) {
        List<FileData> listFolder = new ArrayList<>();
        List<FileData> list = new ArrayList<>();
        for (String file : fileList) {
            File directory1 = new File(file);
            int count = getCountItem(directory1, context, onlyFolder);
            if (directory1.isDirectory()) {
                listFolder.add(new FileData(directory1.getName(), directory1.length(), directory1.getAbsolutePath(), directory1.lastModified() / 1000, directory1.length(), directory1.isDirectory(), count));
            } else if (!onlyFolder) {
                list.add(new FileData(directory1.getName(), directory1.length(), directory1.getAbsolutePath(), directory1.lastModified() / 1000, directory1.length(), directory1.isDirectory(), count));
            }
        }
        return getAllFileData(list, listFolder);
    }

    public int getCountItem(File directory1, Context context, boolean onlyFolder) {
        if (fileProperties.checkPathInExcludedDirs(directory1.getAbsolutePath()) && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            String actualPath = fileProperties.remapPathForApi30OrAbove(directory1.getAbsolutePath(), false);
            boolean hasAccessToSpecialFolder = fileProperties.hasAccessToSpecialFolder(context, actualPath);
            if (hasAccessToSpecialFolder) {
                DocumentFile documentFile = getDocumentFile(actualPath, context);
                if (documentFile != null) {
                    return getCountItemInQ(documentFile, onlyFolder);
                }
            }
            return 0;
        } else {
            return getCountItem(directory1, onlyFolder);
        }
    }

    public int getCountItem(File directory1, boolean onlyFolder) {
        if (directory1.isDirectory() && !directory1.isHidden()) {
            File[] f = directory1.listFiles();
            if (f != null) {
                return f.length;
            }
        }
        return 0;
    }

    private DocumentFile getDocumentFile(String actualPath, Context context) {
        String rootActualPath;

        if (actualPath.contains(ANDROID_DATA)) {
            rootActualPath = actualPath.substring(0, actualPath.indexOf(ANDROID_DATA) + ANDROID_DATA.length());
        } else if (actualPath.contains(ANDROID_OBB)) {
            rootActualPath = actualPath.substring(0, actualPath.indexOf(ANDROID_OBB) + ANDROID_OBB.length());
        } else {
            rootActualPath = actualPath;
        }

        DocumentFile rootUri = DocumentFile.fromTreeUri(context, Uri.parse(rootActualPath));

        String part;
        if (actualPath.contains(ANDROID_DATA)) {
            part = actualPath.substring(actualPath.lastIndexOf(ANDROID_DATA) + ANDROID_DATA.length());
        } else if (actualPath.contains(ANDROID_OBB)) {
            part = actualPath.substring(actualPath.lastIndexOf(ANDROID_OBB) + ANDROID_OBB.length());
        } else {
            part = null;
        }
        if (part != null && !TextUtils.isEmpty(part)) {
            String[] listPart = part.split("%2F");
            for (String p : listPart) {
                if (rootUri != null && !TextUtils.isEmpty(p)) {
                    rootUri = rootUri.findFile(p);
                }
            }
        }
        return rootUri;
    }

    private List<FileData> search(Context context, String text) {
        List<FileData> fileData = new ArrayList<>();
        if (text.isEmpty()) {
            return fileData;
        }

        List<FileData> file = new ArrayList<>();
        List<FileData> folder = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.DATE_MODIFIED};
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver()
                .query(uri, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        if (cursor == null) {
            return fileData;
        } else if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                if (path != null && name != null && name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                    fileData.add(getFileDataByPath(context, path));
                }
            }
        }

        for (FileData f : fileData) {
            File file1 = new File(f.getFilePath());
            if (!file1.isHidden()) {
                if (file1.isDirectory()) {
                    folder.add(f);
                } else {
                    file.add(f);
                }
            }
        }

        return getAllFileData(file, folder);
    }

    private Boolean getFileVideo(Context context) {
        List<FileData> listVideo = new LinkedList<>();
        String[] projection = {
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Video.VideoColumns.DURATION
        };
        Uri uriVideo = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor cursorVideo = context.getContentResolver()
                .query(uriVideo, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        if (cursorVideo == null)
            return false;
        else if (cursorVideo.getCount() > 0) {
            while (cursorVideo.moveToNext()) {
                @SuppressLint("Range") String path = cursorVideo.getString(cursorVideo.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                if (path != null) {
                    File f = new File(path);
                    if (f.isFile() && !f.isHidden()) {
                        FileData fileData = new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/);
                        @SuppressLint("Range") long d = cursorVideo.getLong(cursorVideo.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));
                        fileData.setDurationVideo(d);
                        listVideo.add(fileData);
                    }
                }
            }
            if (listVideo.size() > 0 || listVideo.size() != this.listVideo.size())
                this.listVideo = listVideo;
        }
        return true;
    }

    private Boolean getFilePhoto(Context context) {
        List<FileData> listPhoto = new LinkedList<>();
        String[] projection = {
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATA,
        };
        Uri uriPhoto = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor cursorPhoto = context.getContentResolver()
                .query(uriPhoto, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        if (cursorPhoto == null)
            return false;
        else if (cursorPhoto.getCount() > 0) {
            while (cursorPhoto.moveToNext()) {
                @SuppressLint("Range") String path = cursorPhoto.getString(cursorPhoto.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                if (path != null) {
                    File f = new File(path);
                    if (f.isFile() && !f.isHidden()) {
                        listPhoto.add(new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/));
                    }
                }
            }
            if (listPhoto.size() > 0 || listPhoto.size() != this.listPhoto.size())
                this.listPhoto = listPhoto;
        }

        return true;
    }

    private Boolean getFileMusic(Context context) {
        List<FileData> listMusic = new LinkedList<>();
        String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED};
        Uri uriMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        @SuppressLint("Recycle") Cursor cursorMusic = context.getContentResolver()
                .query(uriMusic, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        if (cursorMusic == null)
            return false;
        else if (cursorMusic.getCount() > 0) {
            while (cursorMusic.moveToNext()) {
                @SuppressLint("Range") String path = cursorMusic.getString(cursorMusic.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                if (path != null) {
                    File f = new File(path);
                    if (f.isFile() && !f.isHidden()) {
                        listMusic.add(new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/));
                    }
                }
            }
            if (listMusic.size() > 0) this.listMusic = listMusic;
        }
        return true;
    }


    private Boolean getFileDocAndApk(Context context) {
        List<FileData> listDoc = new LinkedList<>();
        List<FileData> listApk = new LinkedList<>();
        List<FileData> listArchiver = new LinkedList<>();
        String[] projection = {
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATA,
        };
        Uri uriDoc = MediaStore.Files.getContentUri("external");
        @SuppressLint("Recycle") Cursor cursorDoc = context.getContentResolver()
                .query(uriDoc, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        if (cursorDoc == null)
            return false;
        else if (cursorDoc.getCount() > 0) {
            while (cursorDoc.moveToNext()) {
                @SuppressLint("Range") String path = cursorDoc.getString(cursorDoc.getColumnIndex(projection[1]));
                if (path != null) {
                    if (isDocFile(path)) {
                        File f = new File(path);
                        if (f.isFile() && !f.isHidden()) {
                            listDoc.add(new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/));
                        }

                    } else if (isApkFile(path)) {
                        File f = new File(path);
                        if (f.isFile() && !f.isHidden()) {
                            listApk.add(new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/));
                        }
                    } else if (isArchiveFile(path)) {
                        File f = new File(path);
                        if (f.isFile() && !f.isHidden()) {
                            listArchiver.add(new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/));
                        }
                    }
                }
            }
            if (listDoc.size() > 0 || listDoc.size() != this.listDoc.size()) this.listDoc = listDoc;
            if (listApk.size() > 0 || listApk.size() != this.listApk.size()) this.listApk = listApk;
            if (listArchiver.size() > 0 || listArchiver.size() != this.listArchiver.size())
                this.listArchiver = listArchiver;
        }
        return true;
    }

    public Single<List<FileData>> sortFileMediaByDate(Context context, List<FileData> list, boolean desc) {
        return Single.fromCallable(() -> convertDateListMedia(context, list, desc))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FileData>> sortFileMediaByName(List<FileData> list, boolean desc) {
        return Single.fromCallable(() -> convertNameListMedia(list, desc))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FileData>> sortFileMediaByType(List<FileData> list, boolean desc) {
        return Single.fromCallable(() -> convertTypeListMedia(list, desc))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FileData>> sortFileMediaBySize(List<FileData> list, boolean desc, Context context) {
        return Single.fromCallable(() -> convertSizeListMedia(context, list, desc))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private List<FileData> convertDateListMedia(Context context, List<FileData> fileData, boolean desc) {
        List<FileData> fileDataFinal = new ArrayList<>();
        Collections.sort(fileData, new Comparator<FileData>() {
            @Override
            public int compare(FileData o1, FileData o2) {
                if (!desc) {
                    return o1.getFileTime() >= o2.getFileTime() ? -1 : 1;
                } else {
                    return o1.getFileTime() >= o2.getFileTime() ? 1 : -1;
                }
            }
        });
        String time;
        long timeLong;
        if (fileData.size() > 0) {
            timeLong = fileData.get(0).getFileTime();
            time = fileData.get(0).getFileTimeString();
            long date = new Date().getTime() / 1000;
            String dateString = Utils.getDate2(date);

            long yesterday = (new Date().getTime() - 86400000) / 1000;
            String yesterdayString = Utils.getDate2(yesterday);

            if (time.equals(dateString)) {
                fileDataFinal.add(new FileData(context.getString(R.string.today), timeLong));
            } else if (time.equals(yesterdayString)) {
                fileDataFinal.add(new FileData(context.getString(R.string.yesterday), timeLong));
            } else {
                fileDataFinal.add(new FileData(time, timeLong));
            }

            for (int i = 0; i < fileData.size(); i++) {
                if (!fileData.get(i).getFileTimeString().equals(time)) {
                    time = fileData.get(i).getFileTimeString();
                    timeLong = fileData.get(i).getFileTime();
                    fileDataFinal.add(new FileData(time, timeLong));
                }
                fileDataFinal.add(fileData.get(i));
            }
        }
        return fileDataFinal;
    }

    @NonNull
    private List<FileData> convertNameListMedia(List<FileData> fileData, boolean desc) {
        List<FileData> fileDataFinal = new ArrayList<>();
        if (fileData.size() > 0) {
            Collections.sort(fileData, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    if (!desc)
                        return o1.getFileName().toUpperCase(Locale.ROOT).compareTo(o2.getFileName().toUpperCase(Locale.ROOT));
                    else
                        return o2.getFileName().toUpperCase(Locale.ROOT).compareTo(o1.getFileName().toUpperCase(Locale.ROOT));
                }
            });
            String alphabet;
            alphabet = fileData.get(0).getFileName().substring(0, 1).toUpperCase(Locale.ROOT);
            String nameFile = fileData.get(0).getFileName();
            if (nameFile.toUpperCase(Locale.ROOT).startsWith(alphabet)) {
                fileDataFinal.add(new FileData(alphabet));
            }
            for (int i = 0; i < fileData.size(); i++) {
                if (!fileData.get(i).getFileName().toUpperCase(Locale.ROOT).startsWith(alphabet)) {
                    alphabet = fileData.get(i).getFileName().substring(0, 1).toUpperCase(Locale.ROOT);
                    fileDataFinal.add(new FileData(alphabet));
                }
                fileDataFinal.add(fileData.get(i));
            }
        }
        return fileDataFinal;
    }

    @NonNull
    private List<FileData> convertTypeListMedia(List<FileData> fileData, boolean desc) {
        List<FileData> fileDataFinal = new ArrayList<>();
        if (fileData.size() > 0) {
            Collections.sort(fileData, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    if (!desc)
                        return Utils.getMimeTypeFromName(o1.getFileName()).toLowerCase(Locale.ROOT).compareTo(Utils.getMimeTypeFromName(o2.getFileName()).toLowerCase(Locale.ROOT));
                    else
                        return Utils.getMimeTypeFromName(o2.getFileName()).toLowerCase(Locale.ROOT).compareTo(Utils.getMimeTypeFromName(o1.getFileName()).toLowerCase(Locale.ROOT));
                }
            });
            String type;
            type = Utils.getMimeTypeFromName(fileData.get(0).getFileName().toUpperCase(Locale.ROOT));
            String typeFile = Utils.getMimeTypeFromName(fileData.get(0).getFileName().toUpperCase(Locale.ROOT));
            if (typeFile.equals(type)) {
                fileDataFinal.add(new FileData(typeFile));
            }
            for (int i = 0; i < fileData.size(); i++) {
                if (!(Utils.getMimeTypeFromName(fileData.get(i).getFileName().toUpperCase(Locale.ROOT)).equals(type))) {
                    type = Utils.getMimeTypeFromName(fileData.get(i).getFileName().toUpperCase(Locale.ROOT));
                    fileDataFinal.add(new FileData(type));
                }
                fileDataFinal.add(fileData.get(i));
            }
        }
        return fileDataFinal;
    }

    @NonNull
    private List<FileData> convertSizeListMedia(Context context, List<FileData> fileData, boolean desc) {
        List<FileData> fileDataFinal = new ArrayList<>();
        if (fileData.size() > 0) {
            Collections.sort(fileData, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    if (!desc) {
                        return o1.getFileSize() >= o2.getFileSize() ? -1 : 1;
                    } else {
                        return o1.getFileSize() >= o2.getFileSize() ? 1 : -1;
                    }
                }
            });


            long sizeLarge, sizeMedium, sizeCompare;
            sizeCompare = 100 * 1024 * 1024; //size 100MB
            sizeLarge = 100 * 1024 * 1024; //size 100MB
            sizeMedium = 10 * 1024 * 1024; //size 10MB

            boolean checkLarge = false, checkMedium = false, checkSmall = false;

            for (int i = 0; i < fileData.size(); i++) {

                long fileSize = fileData.get(i).getFileSize();
                if (fileSize >= sizeLarge) {
                    if (!checkLarge) {
                        fileDataFinal.add(new FileData(0, context.getString(R.string.size_large), fileSize));
                        checkLarge = true;
                    }
                } else if (fileSize >= sizeMedium) {
                    if (!checkMedium) {
                        fileDataFinal.add(new FileData(0, context.getString(R.string.size_medium), fileSize));
                        checkMedium = true;
                    }
                } else if (!checkSmall) {
                    fileDataFinal.add(new FileData(0, context.getString(R.string.size_small), fileSize));
                    checkSmall = true;
                }
                fileDataFinal.add(fileData.get(i));
            }
        }
        return fileDataFinal;
    }

    public boolean isArchiveNotSupport(String path) {
        if (path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_LZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_ZST)) {
            return !path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_LZ4)
                    && !path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_ZST);
        } else return false;
    }

    public boolean isArchiveFile(String path) {
        return (path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_ZIP)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_RAR)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_7ZIP)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_BZ2)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TLZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TGZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_GZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TBZ2)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TBZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_XZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_LZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_ZST)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TXZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_LZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_GZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_ZST)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_XZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_BZ2)
        );
    }

    public boolean isVideoFile(String path) {
        return (path.toLowerCase(Locale.ROOT).endsWith(VIDEO_MP4)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_3GP)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_AVI)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_DLVX)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_FLV)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_MKV)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_MPGE)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_VOB)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_WEBV)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_WMV)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_MOV)
                || path.toLowerCase(Locale.ROOT).endsWith(VIDEO_XVID)
        );
    }

    public boolean isPhotoFile(String path) {
        return (path.toLowerCase(Locale.ROOT).endsWith(IMAGE_PNG)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_JPG)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_JPEG)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_GIF)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_WEBP)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_HEIC)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_TIFF)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_PSD)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_EPS)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_AI)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_INDD)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_TGA)
                || path.toLowerCase(Locale.ROOT).endsWith(IMAGE_BMP)
        );
    }

    public void clearListDownload() {
        listDownload.clear();
    }

    private Boolean getFileDownload(File fileDownload, Context context) {
        this.listDownload.clear();
        File[] downloadDir = fileDownload.listFiles();
        if (downloadDir != null && downloadDir.length > 0) {
            for (File f : downloadDir) {
                if (f.isFile()) {
                    this.listDownload.add(getFileDataByPath(context, f.getAbsolutePath()));
                } else {
                    getFileDownload(f, context);
                }
            }
        }
        return true;
    }

    public boolean isApkFile(String path) {
        return path.endsWith(TYPE_APK);
    }

    private boolean isFile(String path) {
        File file = new File(path);
        return file.isFile() && file.exists();
    }

    public boolean isMusicFile(String path) {
        return (path.toLowerCase(Locale.ROOT).endsWith(SOUND_MP3)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_WMA)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_WAV)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_FLAC)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_OGG)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_PCM)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_AIF)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_ALAC)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_M4A)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_MP2)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_WMA9)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_MP1)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_AAC)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_AMR)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_AC3)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_AIFF)
                || path.toLowerCase(Locale.ROOT).endsWith(Constant.SOUND_MIDI)
        );
    }

    public boolean isDocFile(String path) {
        return (path.endsWith(DOCUMENT_TXT)
                || path.endsWith(DOCUMENT_PPTX)
                || path.endsWith(DOCUMENT_DOC_X)
                || path.endsWith(DOCUMENT_DOC)
                || path.endsWith(DOCUMENT_PDF)
                || path.endsWith(DOCUMENT_PPT)
                || path.endsWith(DOCUMENT_PPS)
                || path.endsWith(DOCUMENT_PPSX)
                || path.endsWith(DOCUMENT_PUB)
                || path.endsWith(DOCUMENT_XLS)
                || path.endsWith(DOCUMENT_XLSM)
                || path.endsWith(DOCUMENT_XLTX)
                || path.endsWith(DOCUMENT_PPTM)
                || path.endsWith(DOCUMENT_POT)
                || path.endsWith(DOCUMENT_POTX)
                || path.endsWith(DOCUMENT_XML)
                || path.endsWith(DOCUMENT_HTML)
                || path.endsWith(DOCUMENT_TEXT)
                || path.endsWith(DOCUMENT_XLXS));
    }

    private List<FileData> baseGetListFileDataInQ(String actualPath, String filePath, Context context, boolean onlyFolder) {
        List<FileData> list = new ArrayList<>();
        List<FileData> listFolder = new ArrayList<>();
        DocumentFile rootUri = getDocumentFile(actualPath, context);
        if (rootUri != null) {
            DocumentFile[] files = rootUri.listFiles();
            for (DocumentFile f : files) {
                int count = getCountItemInQ(f, onlyFolder);
                if (f.isDirectory()) {
                    listFolder.add(new FileData(f.getName(), f.length(), filePath + File.separator + f.getName(), f.lastModified() / 1000, f.length(), f.isDirectory(), count));
                } else if (!onlyFolder) {
                    list.add(new FileData(f.getName(), f.length(), filePath + File.separator + f.getName(), f.lastModified() / 1000, f.length(), f.isDirectory(), count));
                }
            }
        }
        return getAllFileData(list, listFolder);
    }

    @NonNull
    private ArrayList<FileData> getAllFileData(List<FileData> list, List<FileData> listFolder) {
        Collections.sort(list, new Comparator<FileData>() {
            public int compare(FileData o1, FileData o2) {
                return o1.getFileName().toLowerCase(Locale.ROOT).compareTo(o2.getFileName().toLowerCase(Locale.ROOT));
            }
        });
        Collections.sort(listFolder, new Comparator<FileData>() {
            public int compare(FileData o1, FileData o2) {
                return o1.getFileName().toLowerCase(Locale.ROOT).compareTo(o2.getFileName().toLowerCase(Locale.ROOT));
            }
        });

        ArrayList<FileData> all = new ArrayList<>();
        all.addAll(listFolder);
        all.addAll(list);
        return all;
    }

    private int getCountItemInQ(DocumentFile file, boolean onlyFolder) {
        int count = 0;
        if (file.isDirectory()) {
            DocumentFile[] f = file.listFiles();
            if (onlyFolder) {
                for (DocumentFile d : f) {
                    if (d.isDirectory()) {
                        count++;
                    }
                }
            } else {
                count = f.length;
            }
        }
        return count;
    }

    public List<String> getAllFolder(File dir) {
        List<String> fileList = new LinkedList<>();
        File[] listFile = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    if (!fileList.contains(file.getAbsolutePath())) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
            }
        }
        Collections.sort(fileList);
        return fileList;
    }

    public List<String> getAllFile(File dir) {
        List<String> fileList = new LinkedList<>();

        File[] listFile = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                String temp = file.getAbsolutePath();
                if (!fileList.contains(temp)) {
                    if (!file.isHidden()) {
                        fileList.add(temp);
                    }
                }
            }
        }
//        Collections.sort(fileList);
        return fileList;
    }

    public Single<Boolean> checkFileMedia(String path) {
        return Single.fromCallable(() -> {
            return isVideoFile(path) || isPhotoFile(path);
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> checkListArchive(List<FileData> list) {
        return Single.fromCallable(() -> checkArchiveList(list)).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Boolean checkArchiveList(List<FileData> list) {
        int archive = 0;
        boolean isArchive;
        for (int i = 0; i < list.size(); i++) {
            if (isArchiveFile(list.get(i).getFileName())) {
                archive++;
            }
        }
        isArchive = archive == list.size();
        return isArchive;
    }

    public Single<Drive> getGoogleDriveServices(Context context, GoogleSignInAccount googleSignInAccount, String appName) {
        return Single.fromCallable(() -> getGoogleDriveService(context, googleSignInAccount, appName)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Drive> getGoogleDriveServices(Context context, String account, String type, String appName) {
        return Single.fromCallable(() -> getGoogleDriveService(context, account, type, appName)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public FileData getFileDataByPath(Context context, String path) {
        File f = new File(path);
        return new FileData(f.getName(), f.length(), path, f.lastModified() / 1000, f.length(), f.isDirectory(), getCountItem(f, context, false), null /*getIcon(context, path)*/);
    }

    public Drive getGoogleDriveService(Context context, GoogleSignInAccount googleSignInAccount, String appName) {
        return DriveServiceHelper.getGoogleDriveService(context, googleSignInAccount, appName);
    }

    public Drive getGoogleDriveService(Context context, String account, String type, String appName) {
        return DriveServiceHelper.getGoogleDriveService(context, account, type, appName);
    }

    public Single<Task<List<FileData>>> getListFolderGoogleDrive(DriveServiceHelper driveServiceHelper, String idFolder) {
        return Single.fromCallable(new Callable<Task<List<FileData>>>() {
            @Override
            public Task<List<FileData>> call() {
                return driveServiceHelper.queryFiles(idFolder);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
