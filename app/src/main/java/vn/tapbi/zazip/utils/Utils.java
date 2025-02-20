package vn.tapbi.zazip.utils;

import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
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
import static vn.tapbi.zazip.common.Constant.KEY_OPEN_FILE;
import static vn.tapbi.zazip.common.Constant.REQUEST_SHARE_FILE;
import static vn.tapbi.zazip.common.Constant.SOUND_MP3;
import static vn.tapbi.zazip.common.Constant.TYPE_DOC;
import static vn.tapbi.zazip.common.Constant.TYPE_DOCS;
import static vn.tapbi.zazip.common.Constant.TYPE_DOC_X;
import static vn.tapbi.zazip.common.Constant.TYPE_FOLDER;
import static vn.tapbi.zazip.common.Constant.TYPE_NULL;
import static vn.tapbi.zazip.common.Constant.TYPE_PDF;
import static vn.tapbi.zazip.common.Constant.TYPE_PPS;
import static vn.tapbi.zazip.common.Constant.TYPE_PPT;
import static vn.tapbi.zazip.common.Constant.TYPE_PPTM;
import static vn.tapbi.zazip.common.Constant.TYPE_PPTX;
import static vn.tapbi.zazip.common.Constant.TYPE_TXT;
import static vn.tapbi.zazip.common.Constant.TYPE_XLS;
import static vn.tapbi.zazip.common.Constant.TYPE_XLSX;
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
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_JPEG;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_DOC;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_EXCEL;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_POWERPOINT;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_POWER_POINT;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_WORD_DOCUMENT;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_MS_XLSX;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_OPEN_OFFICE_PRESENTATION;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_PDF;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_PLAIN_TEXT;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_PNG;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_RAR;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_SHORT_CUT;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_ZIP;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.EXPORT_TYPE_ZIP_COMPRESSED;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DOCS;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DRIVE_FILE;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DRIVE_FOLDER;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_SHEETS;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_SLIDES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.oauth.DbxCredential;
import com.google.android.gms.drive.DriveFolder;
import com.google.api.client.util.DateTime;
import com.hzy.libp7zip.ExitCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn.tapbi.zazip.App;
import vn.tapbi.zazip.BuildConfig;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;

public class Utils {
    public static long lastClickTime = 0;
    public static String blockCharacterSet = "`,~@#^|\\\"$%&*!<>?'{}[]+-():;=•√π÷×¶∆£€¢^°%©®™✓₫/¥₩";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mma, dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat dateFormatViewHolder = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
    private static final SimpleDateFormat dateFormatFileData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat durationHour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat durationMinutes = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public static void resetLastClickTime() {
        lastClickTime = 0;
    }

    public static boolean checkTime() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClickTime) < 800) {
            return false;
        }
        lastClickTime = currentTime;
        return true;
    }

    public static boolean checkTimeShort() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClickTime) < 600) {
            return false;
        }
        lastClickTime = currentTime;
        return true;
    }

    public static boolean checkLongTime() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClickTime) < 1500) {
            return false;
        }
        lastClickTime = currentTime;
        return true;
    }

    public static boolean isPdfFiles(String typeFile) {
        return typeFile.equals(EXPORT_TYPE_PDF)
                || typeFile.equals(TYPE_PDF);
    }

    public static boolean isDocFiles(String typeFile) {
        return typeFile.equals(TYPE_GOOGLE_DOCS)
                || typeFile.equals(TYPE_DOC_X)
                || typeFile.equals(TYPE_DOC)
                || typeFile.equals("application/msword")
                || typeFile.equals(EXPORT_TYPE_MS_WORD_DOCUMENT);
    }

    public static boolean isExcelFiles(String typeFile) {
        return typeFile.equals(TYPE_GOOGLE_SHEETS)
                || typeFile.equals(TYPE_XLSX)
                || typeFile.equals(TYPE_XLS)
                || typeFile.equals(EXPORT_TYPE_MS_EXCEL)
                || typeFile.equals("application/vnd.ms-excel");
    }

    public static boolean isPptFiles(String typeFile) {
        return typeFile.equals(TYPE_GOOGLE_SLIDES)
                || typeFile.equals(TYPE_PPTX)
                || typeFile.equals(TYPE_PPS)
                || typeFile.equals(TYPE_PPTM)
                || typeFile.equals(TYPE_PPT)
                || typeFile.equals(EXPORT_TYPE_MS_POWER_POINT)
                || typeFile.equals("application/vnd.ms-powerpoint");
    }

    public static DbxCredential getLocalCredential() {
        SharedPreferences sharedPreferences = App.getInstance().getApplicationContext().getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE);
        String serializedCredential = sharedPreferences.getString("credential", "null");
        try {
            return DbxCredential.Reader.readFully(serializedCredential);
        } catch (JsonReadException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void storeCredentialLocally(DbxCredential dbxCredential) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("credential", dbxCredential.toString()).apply();
    }

    public static boolean isTxtFiles(String typeFile) {
        return typeFile.equals(EXPORT_TYPE_PLAIN_TEXT)
                || typeFile.equals(TYPE_TXT);
    }

    public static String getMimeTypeFromName(String nameFile) {
        if (nameFile.contains(".")) {
            return nameFile.substring(nameFile.lastIndexOf(".") + 1);
        } else return "0";
    }


    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            for (int index = start; index < end; index++) {
                int type = Character.getType(source.charAt(index));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };

    public static long convertTimeOneDrive(String dateStr) {
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean checkPermissions(Context context) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static File getCacheDir(Context context) {
        if (BuildConfig.DEBUG) {
            return context.getExternalCacheDir();
        }
        return context.getCacheDir();
    }

    public static void deleteCache(Context context) {
        File file = getCacheDir(context);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                FileUtils.delete(f);
            }
        }
    }

    public static String getDateLogin(long longTime, Context context) {
        String timeLogin;
        String time = Utils.getDate2(longTime);
        String dateString = Utils.getDate2(new Date().getTime() / 1000);
        String yesterday = Utils.getDate2(new Date().getTime() / 1000 - 86400);
        if (time.equals(dateString)) {
            timeLogin = context.getString(R.string.today);
        } else if (time.equals(yesterday)) {
            timeLogin = context.getString(R.string.yesterday);
        } else {
            timeLogin = time;
        }
        return timeLogin;
    }

    public static String getPathFolder(ArrayList<FileData> fileData, String folderParent) {
        for (FileData f : fileData) {
            if (!f.getFilePath().contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                return Environment.getExternalStorageDirectory().getAbsolutePath();
            } else if (!f.getFilePath().contains(folderParent)) {
                String folder = folderParent.substring(0, folderParent.lastIndexOf(File.separator));
                return getPathFolder(fileData, folder);
            }
        }
        return folderParent;
    }

    public static String convertTypeFileUpload(String mime) {
        if (mime.equals(TYPE_PDF)
                || mime.equals(EXPORT_TYPE_PDF))
            return EXPORT_TYPE_PDF;
        else if (mime.equals(TYPE_DOCS)
                || mime.equals(TYPE_DOC_X)
                || mime.equals(TYPE_GOOGLE_DOCS)
                || mime.equals("application/msword")
                || mime.equals(EXPORT_TYPE_MS_WORD_DOCUMENT)) {
            return EXPORT_TYPE_MS_WORD_DOCUMENT;
        } else if (mime.equals(EXPORT_TYPE_PLAIN_TEXT)
                || mime.equals(TYPE_TXT))
            return EXPORT_TYPE_PLAIN_TEXT;
        else if (mime.equals(TYPE_GOOGLE_SHEETS)
                || mime.equals(TYPE_XLSX)
                || mime.equals(EXPORT_TYPE_MS_EXCEL)
                || mime.equals("application/vnd.ms-excel"))
            return EXPORT_TYPE_MS_EXCEL;
        else if (mime.equals(TYPE_GOOGLE_SLIDES)
                || mime.equals(TYPE_PPTX)
                || mime.equals(TYPE_PPS)
                || mime.equals(TYPE_PPTM)
                || mime.equals(TYPE_PPT)
                || mime.equals(EXPORT_TYPE_MS_POWER_POINT)
                || mime.equals("application/vnd.ms-powerpoint"))
            return EXPORT_TYPE_MS_POWER_POINT;
        else return "*/*";
    }

    public static boolean checkDocumentFile(String mime) {
        return mime.equals(TYPE_GOOGLE_DRIVE_FOLDER)
                || mime.equals(TYPE_GOOGLE_DOCS)
                || mime.equals(TYPE_GOOGLE_DRIVE_FILE)
                || mime.equals(EXPORT_TYPE_PDF)
                || mime.equals(EXPORT_TYPE_PLAIN_TEXT)
                || mime.equals(TYPE_GOOGLE_SHEETS)
                || mime.equals(EXPORT_TYPE_MS_POWER_POINT)
                || mime.equals(EXPORT_TYPE_MS_EXCEL)
                || mime.equals(EXPORT_TYPE_OPEN_OFFICE_PRESENTATION)
                || mime.equals(TYPE_GOOGLE_SLIDES)
                || mime.equals(EXPORT_TYPE_MS_DOC)
                || mime.equals(EXPORT_TYPE_MS_XLSX)
                || mime.equals(EXPORT_TYPE_MS_POWERPOINT)
                || mime.equals(EXPORT_TYPE_MS_WORD_DOCUMENT)
                || mime.equals(EXPORT_TYPE_ZIP)
                || mime.equals(EXPORT_TYPE_JPEG)
                || mime.equals(EXPORT_TYPE_RAR)
                || mime.equals(EXPORT_TYPE_PNG)
                || mime.equals(EXPORT_TYPE_SHORT_CUT)
                || mime.equals(EXPORT_TYPE_ZIP_COMPRESSED)
                || mime.equals("video/mp4")
                || mime.equals("application/x-rar")
                || mime.equals("application/x-zip");
    }

    public static boolean isMusicFile(String path) {
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

    public static boolean isPhotoFile(String path) {
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

    public static boolean isVideoFile(String path) {
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

    public static String getDateTimeZone(DateTime val/*, SimpleDateFormat simpleDateFormat*/) {
        String time = "";
        try {
            time = simpleDateFormat.format(val.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDates(long val) {
        try {
            long val1 = val;
            val1 *= 1000L;
            return simpleDateFormat.format(new java.util.Date(val1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(val);
        }
    }

    public static int checkSelectFile(List<FileData> selectList, String path) {
        for (int i = 0; i < selectList.size(); i++) {
            if (selectList.get(i).getFilePath().equals(path)) {
                return i;
            }
        }
        return -1;
    }

    public static String getUniqueFolderName(String folder, String searchedFilename, boolean returnFilePath) {
        int num = 1;
        File file;
        file = new File(folder, searchedFilename);
        while (!file.isFile() && file.exists()) {
            searchedFilename = searchedFilename + "(" + (num++) + ")";
            file = new File(folder, searchedFilename);

        }
        if (returnFilePath) {
            return file.getAbsolutePath();
        } else
            return file.getName();
    }


    public static String downloadFile(String uri, String fileDest) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(fileDest);

            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return Constant.SAVE_FAIL;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return fileDest;
    }


    public static String copyFileArchive(String fileCompress, File fileDest) {
        String dest = getUniqueFileName(fileDest.getParent(), fileDest.getName(), true);
        FileUtils.copyFile(fileCompress, dest);
        return dest;
    }

    public static boolean checkListPaste(List<FileData> dataList, String dest) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dest.contains(dataList.get(i).getFilePath())) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkFileArchive(String path) {
        return (path.endsWith(ARCHIVE_TAR_XZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_LZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_BZ2)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_GZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR_ZST)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_ZIP)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_RAR)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_7ZIP)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TLZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TGZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_GZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TAR)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TBZ2)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TBZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_XZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_LZ4)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_ZST)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_TXZ)
                || path.toLowerCase(Locale.ROOT).endsWith(ARCHIVE_BZ2));
    }

    public static String getUniqueFileName(String folderName, String searchedFilename, boolean returnFilePath) {
        int num = 1;
        String extension;
        String filename;
        if (searchedFilename.endsWith(ARCHIVE_TAR_BZ2)
                || searchedFilename.endsWith(ARCHIVE_TAR_GZ)
                || searchedFilename.endsWith(ARCHIVE_TAR_XZ)
                || searchedFilename.endsWith(ARCHIVE_TAR_LZ4)
                || searchedFilename.endsWith(ARCHIVE_TAR_ZST)) {
            extension = searchedFilename.substring(searchedFilename.lastIndexOf(ARCHIVE_TAR));
            filename = searchedFilename.substring(0, searchedFilename.lastIndexOf(ARCHIVE_TAR));
        } else {
            extension = searchedFilename.substring(searchedFilename.lastIndexOf("."));
            filename = searchedFilename.substring(0, searchedFilename.lastIndexOf("."));
        }
        File file;
        file = new File(folderName, searchedFilename);
        while (file.exists()) {
            searchedFilename = filename + "(" + (num++) + ")" + extension;
            file = new File(folderName, searchedFilename);

        }
        if (returnFilePath) {
            return file.getAbsolutePath();
        } else
            return searchedFilename;
    }

    public static void copyAndPaste(Context context, List<FileData> list, String dest) {
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i).getFilePath());
            String fileDest = "";
            if (file.isFile()) {
                fileDest = getUniqueFileName(dest, list.get(i).getFileName(), true);
                MediaScannerConnection.scanFile(context, new String[]{fileDest}, null, null);

            } else if (file.isDirectory()) {
                fileDest = getUniqueFolderName(dest, list.get(i).getFileName(), true);

            }
            if (file.isFile()) {
                FileUtils.copyFile(list.get(i).getFilePath(), fileDest);

            }
            if (file.isDirectory()) {
                FileUtils.copyDir(list.get(i).getFilePath(), fileDest);
            }
        }
    }


    public static boolean userCreateFolder(String directory, String nameFolder) {
        File folder = new File(directory, nameFolder);
        if (folder.exists()) return false;
        else {
            folder.mkdirs();
            return true;
        }
    }

    public static void showResult(int result, Context context) {
        switch (result) {
            case ExitCode.EXIT_OK:
                Toast.makeText(context, context.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                break;
            case ExitCode.EXIT_WARNING:
                Toast.makeText(context, context.getResources().getString(R.string.exit_warning), Toast.LENGTH_SHORT).show();
                break;
            case ExitCode.EXIT_FATAL:
                Toast.makeText(context, context.getResources().getString(R.string.exit_fault), Toast.LENGTH_SHORT).show();
                break;
            case ExitCode.EXIT_CMD_ERROR:
                Toast.makeText(context, context.getResources().getString(R.string.exit_command), Toast.LENGTH_SHORT).show();
                break;
            case ExitCode.EXIT_MEMORY_ERROR:
                Toast.makeText(context, context.getResources().getString(R.string.exit_memmory), Toast.LENGTH_SHORT).show();
                break;
            case ExitCode.EXIT_NOT_SUPPORT:
                Toast.makeText(context, context.getResources().getString(R.string.exit_not_support), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public static void openFile(String filePath, Context context) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                String mime = URLConnection.guessContentTypeFromName(file.getName());
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                newIntent.setDataAndType(uri, mime);
                newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(newIntent);
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.no_application_found_to_open_this_file, Toast.LENGTH_LONG).show();
        }
    }

    public static void openFile(String filePath, Activity activity) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(activity, App.getInstance().getApplicationContext().getPackageName() + ".provider", file);
                try {
                    String mime = URLConnection.guessContentTypeFromName(file.getName());
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    newIntent.setDataAndType(uri, mime);
                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivityForResult(newIntent, KEY_OPEN_FILE);
                } catch (StringIndexOutOfBoundsException ignored) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(App.getInstance().getApplicationContext(), R.string.no_application_found_to_open_this_file, Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(long val) {
        try {
            long val1 = val;
            val1 *= 1000L;
            return dateFormatViewHolder.format(new java.util.Date(val1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(val);
        }

    }

    public static String getMillisecond(long time) {
        return (new SimpleDateFormat("mm:ss")).format(new Date(time));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate2(long val) {
        try {
            long val1 = val;
            val1 *= 1000L;
            return dateFormatFileData.format(new Date(val1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(val);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDuration(long time) {
        long h = time / 1000 / 60 / 60;
        if (h > 0) {
            return durationHour.format(new Date(time));
        } else
            return durationMinutes.format(new Date(time));
    }


    public static String convertByte(long size) {
        if (size < 0) {
            return "shouldn't be less than zero!";
        } else if (size < 1024) {
            return String.format(Locale.getDefault(), "%.2fB", (double) size);
        } else if (size < 1048576) {
            return String.format(Locale.getDefault(), "%.2fKB", (double) size / 1024);
        } else if (size < 1073741824) {
            return String.format(Locale.getDefault(), "%.2fMB", (double) size / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.2fGB", (double) size / 1073741824);
        }
    }

    public static String createFileCompress(List<String> dataList, String pathName) {
        //pathname is name of parent folder contains datalist
        File compressFolder = new File(pathName);
        if (!compressFolder.exists())
            compressFolder.mkdirs();
        for (int i = 0; i < dataList.size(); i++) {
            try {
                File file = new File(dataList.get(i));
                if (file.isFile()) {
                    FileUtils.copyFile(dataList.get(i)
                            , compressFolder + "/" + dataList.get(i).substring(dataList.get(i).lastIndexOf("/")));
                } else {
                    FileUtils.copyDir(dataList.get(i)
                            , compressFolder + "/" + dataList.get(i).substring(dataList.get(i).lastIndexOf("/")));
                }

            } catch (Exception e) {
                Log.d("Number4", "createFileCompress exception: " + e);
            }

        }
        return compressFolder.getAbsolutePath();
    }

    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(
                        targetLocation, children[i]));
            }
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    public static void copyFile(File sourceLocation, File targetLocation)
            throws IOException {
        if (!targetLocation.exists()) {
            targetLocation.createNewFile();
        }
        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String renameFile(String pathOldFile, String newName) {
        File oldFile = new File(pathOldFile);
        File newFile = new File(getUniqueFolderName(oldFile.getParent(), newName, true));
        oldFile.renameTo(newFile);
        return newFile.getAbsolutePath();
    }

    public static void deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            if (directory.isDirectory()) {
                if (directory.listFiles() != null && directory.listFiles().length != 0) {
                    for (File file : directory.listFiles()) {
                        if (file.isFile()) {
                            file.delete();
                        } else {
                            deleteDirectory(file);
                        }
                    }
                }
            }
            directory.delete();
        }
    }

    public static void shareFile(Activity activity, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivityForResult(shareIntent, REQUEST_SHARE_FILE);
        //activity.startActivity(shareIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isNetworkOnline1(Context context) {
        boolean isOnline = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());  // need ACCESS_NETWORK_STATE permission
            isOnline = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOnline;
    }

    public static String convertTypeFile(String typeFile) {
        if (typeFile.equals(TYPE_GOOGLE_DOCS)
                || typeFile.equals(TYPE_DOC_X)
                || typeFile.equals(TYPE_DOCS)
                || typeFile.equals(EXPORT_TYPE_MS_DOC)
                || typeFile.equals(EXPORT_TYPE_MS_WORD_DOCUMENT)) {
            return TYPE_DOC_X;
        } else if (typeFile.equals(TYPE_GOOGLE_SLIDES)
                || typeFile.equals(TYPE_PPTX)
                || typeFile.equals(TYPE_PPS)
                || typeFile.equals(TYPE_PPTM)
                || typeFile.equals(TYPE_PPT)
                || typeFile.equals(EXPORT_TYPE_MS_POWER_POINT)
                || typeFile.equals(EXPORT_TYPE_MS_POWERPOINT)) {
            return TYPE_PPTX;
        } else if (typeFile.equals(TYPE_GOOGLE_SHEETS)
                || typeFile.equals(TYPE_XLSX)
                || typeFile.equals(EXPORT_TYPE_MS_EXCEL)
                || typeFile.equals(EXPORT_TYPE_MS_XLSX)) {
            return TYPE_XLSX;
        } else if (typeFile.equals(DriveFolder.MIME_TYPE)) {
            return TYPE_FOLDER;
        } else if (typeFile.equals(EXPORT_TYPE_PLAIN_TEXT)
                || typeFile.equals(TYPE_TXT)) {
            return TYPE_TXT;
        } else if (typeFile.equals(EXPORT_TYPE_PDF)
                || typeFile.equals(TYPE_PDF)) {
            return TYPE_PDF;
        }
        return TYPE_NULL;
    }


    public static void deleteFile(List<FileData> dataList) {
        if (dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                File file = new File(dataList.get(i).getFilePath());
                if (file.exists()) {
                    FileUtils.deleteAllInDir(file);
                    deleteDirectory(file);
                }
            }
        }
    }

    public static void createFolder(String path, String nameFolder) {
        File directory = new File(path + File.separator + nameFolder);
        directory.mkdirs();
    }

    public static void showInputMethod(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.showSoftInput(view, 0);
        }

    }

    public static void hideInputMethod(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String addSeparatorToPath(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        }
        return path;
    }


    public static String readFileContent(File file) throws IOException {
        BufferedReader reader;
        StringBuffer sbf = new StringBuffer();
        reader = new BufferedReader(new FileReader(file));
        String tempStr;
        while ((tempStr = reader.readLine()) != null) {
            sbf.append(tempStr);
        }
        reader.close();
        return sbf.toString();
    }

    public static String getTimeString(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.heightPixels;
    }

    public static boolean isOnline(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Locale getLocaleCompat() {
        Configuration configuration = Resources.getSystem().getConfiguration();
        return isAtLeastSdkVersion(Build.VERSION_CODES.N) ? configuration.getLocales().get(0) : configuration.locale;
    }

    public static boolean isAtLeastSdkVersion(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public static void showDialogPermissionManageExternalStorage(Context context, String content) {
        new AlertDialog.Builder(context).setMessage(content).setPositiveButton(
                context.getResources().getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        context.startActivity(intent);

                    }
                }).setNegativeButton(context.getResources().getString(R.string.go_back), (dialog, which) -> dialog.dismiss()).show();
    }

    public static void showDialogPermissionWriteExternalStorage(Context context, String content) {
        new AlertDialog.Builder(context).setMessage(content).setPositiveButton(
                context.getResources().getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }).setNegativeButton(context.getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static class DateTimeComparator implements Comparator<FileData> {
        @Override
        public int compare(FileData o1, FileData o2) {
            if (o1.getFileTime() == o2.getFileTime())
                return 0;
            else if (o1.getFileTime() < o2.getFileTime())
                return 1;
            else
                return -1;
        }
    }
}
