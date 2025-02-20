package vn.tapbi.zazip.utils;

import android.content.Context;
import android.content.UriPermission;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import java.io.File;
import java.util.List;

public class FileProperties {
    private String STORAGE_PRIMARY = "primary";
    private String COM_ANDROID_EXTERNAL_STORAGE_DOCUMENTS =
            "com.android.externalstorage.documents";

    private String[] EXCLUDED_DIRS = new String[]{
            new File(Environment.getExternalStorageDirectory(), "Android/data").getAbsolutePath(),
            new File(Environment.getExternalStorageDirectory(), "Android/obb").getAbsolutePath()
    };

    public boolean checkPathInExcludedDirs(String path) {
        for (String excluded_dir : EXCLUDED_DIRS) {
            if (path.contains(excluded_dir)) {
                return true;
            }
        }
        return false;
    }

    public String remapPathForApi30OrAbove(String path, boolean openDocumentTree) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && checkPathInExcludedDirs(path)) {
            String suffix = path.substring(path.indexOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + Environment.getExternalStorageDirectory().getAbsolutePath().length());
            String documentId = STORAGE_PRIMARY + ":" + suffix.substring(1);
            if (openDocumentTree) {
                return DocumentsContract.buildDocumentUri(COM_ANDROID_EXTERNAL_STORAGE_DOCUMENTS,
                        documentId
                ).toString();
            } else {
                return DocumentsContract.buildTreeDocumentUri(
                        COM_ANDROID_EXTERNAL_STORAGE_DOCUMENTS,
                        documentId
                ).toString();
            }
        } else {
            return path;
        }
    }

    public boolean hasAccessToSpecialFolder(Context context, String actualPath) {
        boolean hasAccessToSpecialFolder = false;
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            List<UriPermission> uriPermissions =
                    context.getContentResolver().getPersistedUriPermissions();

            if (uriPermissions != null && uriPermissions.size() > 0) {
                for (UriPermission p : uriPermissions) {
                    if (p.isReadPermission() && actualPath.startsWith(p.getUri().toString())) {
                        hasAccessToSpecialFolder = true;
                        break;
                    }
                }
            }
        }
        return hasAccessToSpecialFolder;
    }
}
