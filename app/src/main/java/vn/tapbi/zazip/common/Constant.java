package vn.tapbi.zazip.common;

import android.os.Environment;

import com.google.android.gms.drive.DriveFolder;

import java.util.Arrays;
import java.util.List;

public class Constant {
    public static final int SPAN_COUNT_GRID_3 = 3;
    public static final int SPAN_COUNT_GRID_4 = 4;
    public static String TYPE_GOOGLE_DRIVE_FOLDER = DriveFolder.MIME_TYPE;
    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_MULTI = 2;
    public static final int TYPE_PROCESS_HAVE_TEMP_FILE = 0;
    public static final int TYPE_PROCESS_EXTRACT_FILE_TAR = 1;
    public static final int TYPE_PROCESS_EXTRACT_FILE_FINAL = 2;
    public static final String ONE_DRIVE = "One Drive";
    public static final String DROP_BOX = "Drop Box";
    public static final String SAVE_FAIL = "save_fail";
    public static final int REQUEST_CODE_SIGN_IN = 1010;
    public static final String clientIdentifier = "Zazip/devPhase2";
    public static final String TYPE_FILE = "type_file";
    public static final String ACCOUNT_NAME = "accountName";
    public static final String DEFAULT_NAME_COMPRESS_LIST = "0";
    public static final int WRITE_REQUEST_CODE = 1;
    public static final int READ_REQUEST_CODE = 2;
    public static final int MANAGE_REQUEST_CODE = 3;
    public static final int DB_VERSION = 1;
    public static final int POSITION_RENAME = 0;
    public static final int ADD_ACCOUNT_GG_DRIVE_DEFAULT = 111;
    public static final int ADD_ACCOUNT_ONE_DRIVE_DEFAULT = 222;
    public static final int NONE_ADD_ACCOUNT = 333;
    public static final int POSITION_COPY = 1;
    public static final String ACCURACY = "accuracy";
    public static final String ERROR_INTERNET = "ERROR_INTERNET";
    public static final int POSITION_EXTRACT = 2;
    public static final int POSITION_COMPRESS = 3;
    public static final int POSITION_SHARE = 4;
    public static final int KEY_OPEN_FILE = 60;
    public static final int POSITION_DELETE = 5;
    public static final int REQUEST_CODE_SHOW_DETAIL_FAIL = 2;
    public static final String DB_NAME = "ACCOUNT_DB";
    public static final String SELECTED = "Selected ";
    public static final String KEY_TEXT_SEARCH = "KEY_TEXT_SEARCH";
    public static final String KEY_TEXT_SEARCH_CLOUD = "KEY_TEXT_SEARCH_CLOUD";
    public static final String ITEMS = " items";
    public static final String ITEMSS = " item(s)";
    public static final String TYPE_SHARE = "text/plain";
    public static final String VALUE_SHARE = "My application name";
    public static final String BASE_URL = "https://graph.microsoft.com/v1.0/me/";
    public static final int CONNECT_S = 10;
    public static final int READ_S = 10;
    public static final String CHECK_LOGIN_DROP_BOX = "CHECK_LOGIN_DROP_BOX";
    public static final int WRITE_S = 10;
    public static final int TYPE_ONE_DRIVE_FILE = 2222;
    public static final int TYPE_DROP_BOX_FILE = 3333;
    public static final String TYPE_PDF = "pdf";
    public static final String TYPE_DOC_X = "docx";
    public static final String TYPE_DOCS = "doc";
    public static final String TYPE_XLSX = "xlsx";
    public static final String TYPE_XLS = "xls";
    public static final String TYPE_TXT = "txt";
    public static final String TYPE_PPT = "ppt";
    public static final String TYPE_PPTX = "pptx";
    public static final String TYPE_PPS = "pps";
    public static final String TYPE_PPTM = "pptm";
    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_JPEG = "jpeg";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_WEBP = "webp";
    public static final String TYPE_HEIC = "heic";
    public static final String TYPE_TIFF = "tiff";
    public static final String TYPE_PSD = "psd";
    public static final String TYPE_EPS = "eps";
    public static final String TYPE_AI = "ai";
    public static final String TYPE_INDD = "indd";
    public static final String TYPE_TGA = "tga";
    public static final String TYPE_AC3 = "ac3";
    public static final String TYPE_AMR = "amr";
    public static final String TYPE_MP3 = "mp3";
    public static final String TYPE_AIFF = "aiff";
    public static final String TYPE_OGG = "ogg";
    public static final String TYPE_WMA = "wma";
    public static final String TYPE_FLAC = "flac";
    public static final String TYPE_WAV = "wav";
    public static final String TYPE_M4A = "m4a";
    public static final String TYPE_AAC = "aac";
    public static final String TYPE_MIDI = "midi";
    public static final String TYPE_PCM = "pcm";
    public static final String TYPE_AIF = "aif";
    public static final String TYPE_ALAC = "alac";
    public static final String TYPE_WMA9 = "wma9";
    public static final String TYPE_MP2 = "mp2";
    public static final String TYPE_MP1 = "mp1";
    public static final String TYPE_MP4 = "mp4";
    public static final String TYPE_MKV = "mkv";
    public static final String TYPE_WMV = "wmv";
    public static final String TYPE_VOB = "vob";
    public static final String TYPE_FLV = "flv";
    public static final String TYPE_DLVX = "dlvx";
    public static final String TYPE_AVI = "avi";
    public static final String TYPE_XVID = "xvid";
    public static final String TYPE_MPGE = "mpeg";
    public static final String TYPE_WEBV = "webv";
    public static final String TYPE_3GP = "3gp";
    public static final String TYPE_MOV = "mov";
    public static final String TYPE_NULL = "null";
    public static final String TYPE_FOLDER = "FOLDER";
    public static final String GOOGLE_DRIVE = "Google Drive";
    public static final String TYPE_DOC = "type_doc";
    public static final String TYPE_CLOUD_SCREEN = "type_cloud_screen";
    public static final String TYPE_PHOTO = "type_photo";
    public static final String KEY_NAME_GG_DRIVE = "KEY_NAME_GG_DRIVE";
    public static final String KEY_TYPE_GG_DRIVE = "KEY_TYPE_GG_DRIVE";
    public static final String CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX = "CHECK_EXPIRED_ACCESS_TOKEN_DROP_BOX";

    public static final String NAME_ACCOUNT_GG_DRIVE = "NAMEACCOUNTGGDRIVE";
    public static final String TYPE_ACCOUNT_GG_DRIVE = "TYPEACCOUNTGGDRIVE";
    public static final String FOLDER_GG_DRIVE = "FOLDER_GG_DRIVE";
    public static final String LIST_DATA_GG_DRIVE = "LIST_DATA_DRIVE";

    public static final String ACCESS_TOKEN_ONEDRIVE = "ACCESS_TOKEN_ONEDRIVE";
    public static final String ACCESS_TOKEN_DROP_BOX = "ACCESS_TOKEN_DROP_BOX";
    public static final String FOLDER_DROP_BOX = "FOLDER_DROP_BOX";
    public static final String FOLDER_ONEDRIVE = "FOLDER_ONEDRIVE";
    public static final String TYPE_SCREEN_CLOUD = "TYPE_SCREEN_CLOUD";
    public static final int SCREEN_HOME = 1;
    public static final int SCREEN_DOC = 2;
    public static final int SCREEN_PHOTO = 3;
    public static final int SCREEN_MUSIC = 4;
    public static final int SCREEN_DOWNLOAD = 5;
    public static final int SCREEN_VIDEO = 6;
    public static final int SCREEN_APK = 7;
    public static final int SCREEN_ARCHIVE = 8;
    public static final int SCREEN_EXPLORE = 9;
    public static final int SCREEN_GOOGLE = 10;
    public static final int SCREEN_ONE_DRIVE = 11;
    public static final int SCREEN_DROP_BOX = 12;

    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_VN = "vi";
    public static final int TYPE_GG_DRIVE_FILE = 1111;
    public static final String TYPE_APK = ".apk";
    public static final String FOLDER_ROOT = "root";

    public static final String NAME_ONEDRIVE_FOLDER = "nameOneDriveFolder";
    public static final String LIST_FILE_METADATA = "LIST_FILE_METADATA";
    public static final String LIST_FOLDER_METADATA = "LIST_FOLDER_METADATA";
   // public static final String LIST_FILE_METADATA = "LIST_FILE_METADATA";
    public static final String NAME_DROPBOX_FOLDER = "nameDropBoxFolder";
    public static final String NAME_GG_DRIVE_FOLDER = "nameGgDriveFolder";
    //type document
    public static final int REQUEST_SHARE_FILE = 9999;
    public static final String DOCUMENT_PDF = ".pdf";
    public static final String DOCUMENT_PPT = ".ppt";
    public static final String DOCUMENT_PPS = ".pps";
    public static final String DOCUMENT_PPSX = ".ppsx";
    public static final String DOCUMENT_PPTX = ".pptx";
    public static final String DOCUMENT_PPTM = ".pptm";
    public static final String DOCUMENT_POT = ".pot";
    public static final String DOCUMENT_POTX = ".potx";
    public static final String DOCUMENT_DOC = ".doc";
    public static final String DOCUMENT_DOC_X = ".docx";
    public static final String DOCUMENT_XLXS = ".xlsx";
    public static final String DOCUMENT_XLS = ".xls";
    public static final String DOCUMENT_XLSM = ".xlsm";
    public static final String DOCUMENT_XLTX = ".xltx";
    public static final String DOCUMENT_TXT = ".txt";
    public static final String DOCUMENT_TEXT = ".text";
    public static final String DOCUMENT_PUB = ".pub";
    public static final String DOCUMENT_XML = ".xml";
    public static final String DOCUMENT_HTML = ".html";


    //type achiver
    public static final String ARCHIVE_ZIP = ".zip";
    public static final String ARCHIVE_7ZIP = ".7z";
    public static final String ARCHIVE_TAR = ".tar";
    public static final String ARCHIVE_BZ2 = ".bz2";
    public static final String ARCHIVE_GZ = ".gz";
    public static final String ARCHIVE_XZ = ".xz";
    public static final String ARCHIVE_TGZ = ".tgz";
    public static final String ARCHIVE_TBZ2 = ".tbz2";
    public static final String ARCHIVE_TBZ = ".tbz";
    public static final String ARCHIVE_TXZ = ".txz";
    public static final String ARCHIVE_RAR = ".rar";
    public static final String ARCHIVE_LZ4 = ".lz4";
    public static final String ARCHIVE_TLZ4 = ".tlz4";
    public static final String ARCHIVE_ZST = ".zst";
    public static final String ARCHIVE_TAR_LZ4 = ".tar.lz4";
    public static final String ARCHIVE_TAR_ZST = ".tar.zst";
    public static final String ARCHIVE_TAR_GZ = ".tar.gz";
    public static final String ARCHIVE_TAR_XZ = ".tar.xz";
    public static final String ARCHIVE_TAR_BZ2 = ".tar.bz2";
    public static final List<String> FORMAT = Arrays.asList(
            ARCHIVE_ZIP,
            ARCHIVE_TGZ,
            ARCHIVE_GZ,
            ARCHIVE_RAR,
            ARCHIVE_7ZIP,
            ARCHIVE_TAR,
            ARCHIVE_TBZ2,
            ARCHIVE_TBZ,
            ARCHIVE_XZ,
            ARCHIVE_TXZ,
            ARCHIVE_ZST,
            ARCHIVE_TAR_LZ4,
            ARCHIVE_LZ4,
            ARCHIVE_BZ2);

    //type sound
    public static final String SOUND_AC3 = ".ac3";
    public static final String SOUND_AMR = ".amr";
    public static final String SOUND_MP3 = ".mp3";
    public static final String SOUND_AIFF = ".aiff";
    public static final String SOUND_OGG = ".ogg";
    public static final String SOUND_WMA = ".wma";
    public static final String SOUND_FLAC = ".flac";
    public static final String SOUND_WAV = ".wav";
    public static final String SOUND_M4A = ".m4a";
    public static final String SOUND_AAC = ".aac";
    public static final String SOUND_MIDI = ".midi";
    public static final String SOUND_PCM = ".pcm";
    public static final String SOUND_AIF = ".aif";
    public static final String SOUND_ALAC = ".alac";
    public static final String SOUND_WMA9 = ".wma9";
    public static final String SOUND_MP2 = ".mp2";
    public static final String SOUND_MP1 = ".mp1";

    public static final String[] SOUND_TYPE = {".mp3", ".wma", ".wav", ".flac", ".ogg", ".pcm", ".aif", ".alac", ".amr", ".midi", ".wma9", ".ac3", ".aac", ".mp2", ".m4a"};


    //type video
    public static final String VIDEO_MP4 = ".mp4";
    public static final String VIDEO_MKV = ".mkv";
    public static final String VIDEO_WMV = ".wmv";
    public static final String VIDEO_VOB = ".vob";
    public static final String VIDEO_FLV = ".flv";
    public static final String VIDEO_DLVX = ".dlvx";
    public static final String VIDEO_AVI = ".avi";
    public static final String VIDEO_XVID = ".xvid";
    public static final String VIDEO_MPGE = ".mpeg";
    public static final String VIDEO_WEBV = ".webv";
    public static final String VIDEO_3GP = ".3gp";
    public static final String VIDEO_MOV = ".mov";
    public static final String[] VIDEO_TYPE = {".mp4", ".mkv", ".wmv", ".vob", ".flv", ".dlvx", ".avi", ".mpge", ".webv", ".3gp"};

    //type image
    public static final String IMAGE_JPG = ".jpg";
    public static final String IMAGE_JPEG = ".jpeg";
    public static final String IMAGE_PNG = ".png";
    public static final String IMAGE_GIF = ".gif";
    public static final String IMAGE_BMP = ".bmp";
    public static final String IMAGE_WEBP = ".webp";
    public static final String IMAGE_HEIC = ".heic";
    public static final String IMAGE_TIFF = ".tiff";
    public static final String IMAGE_PSD = ".psd";
    public static final String IMAGE_EPS = ".eps";
    public static final String IMAGE_AI = ".ai";
    public static final String IMAGE_INDD = ".indd";
    public static final String IMAGE_TGA = ".tga";
    public static final String[] IMAGE_TYPE = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

    //folder hide
    public static final String PATH_THUMBNAILS = "thumbnails";
    public static final String PATH_ICON = "icon";
    public static final String PATH_COVER = "cover";
    public static final String PATH_CACHE = "cache";
    public static final String PATH_TRASH = "Trash";


    /*SharePreference constant*/
    public static final String PREF_SETTING_LANGUAGE = "pref_setting_language";

    public static final String SHARE_FILE = "Here are some files.";

    //event bus
    public static final int EVENT_DELETE_ITEM = 2;
    public static final int EVENT_DELETE_ALl_ITEM = 3;
    public static final int EVENT_RELOAD_FILE_DATA = 4;

    //state sort view
    public static final int SORT_VIEW_DETAIL = 0;
    public static final int SORT_VIEW_COMPACT = 1;
    public static final int SORT_VIEW_GRID = 2;
    public static final int SORT_VIEW_TYPE_NAME = 0;
    public static final int SORT_VIEW_TYPE_DATE = 1;
    public static final int SORT_VIEW_TYPE_TYPE = 2;
    public static final int SORT_VIEW_TYPE_SIZE = 3;
    public static final int SORT_VIEW_TYPE_DESC = 4;

    public static final String TYPE_CLOUD = "type_cloud";
    public static final String KEY_TOTAL_FILE_DRIVE = "key_total_file_drive";
    public static final String KEY_ID_DRIVE = "key_id_drive";
    public static final String KEY_FILE_DOWNLOAD = "key_file_download";
    public static final String KEY_PATH_SAVE_TO = "key_path_save_to";

    //bundle progress
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_UPDATE_PASSWORD = "ACTION_UPDATE_PASSWORD";
    public static final String ACTION_SHOW_NOTY = "ACTION_SHOW_NOTY";
    public static final String ACTION_CANCEL = "ACTION_CANCEL";
    public static final String ACTION_SHOW_PROCESS = "ACTION_SHOW_PROCESS";
    public static final String ACTION_SHOW_FILE_SUCCESS = "ACTION_SHOW_FILE_SUCCESS";

    public static final String ACTION_DOWNLOAD_SUCCESS = "action_download_success";
    public static final String ACTION_START_DOWNLOAD = "action_start_download";
    public static final String ACTION_CANCEL_DOWNLOAD = "action_cancel_download";
    public static final String ACTION_DOWNLOAD_PROCESS = "action_download_process";

    public static final String ACTION_BACKGROUND_PROCESS = "ACTION_BACKGROUND_PROCESS";
    public static final String TYPE_FATAL = "TYPE_FATAL";
    public static final String TYPE_ERROR = "TYPE_ERROR";
    public static final String TYPE_PROCESS = "TYPE_SUCCESS";
    public static final String TYPE_CANCEL = "TYPE_CANCEL";

    public static final String ACTION_PROCESS = "ACTION_PROCESS";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_SHOW = "ACTION_SHOW";

    public static final String KEY_PROCESS = "KEY_PROCESS";
    public static final String KEY_PASS = "key_pass";
    public static final String KEY_ID = "key_id";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_PATH = "key_path";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_FOLDER = "key_folder";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_CONTENT_PROGRESS = "key_content_progress";
    public static final String KEY_OPTION_EXTRACT = "key_option_extract";
    public static final String KEY_COUNT_EXTRACT = "key_count_extract";
    public static final String KEY_TITLE_DOWNLOAD = "key_title_download";
    public static final String KEY_DOWNLOAD_SUCCESS = "key_download_success";
    public static final String KEY_RESULT_DOWNLOAD_SUCCESS = "key_result_download_success";
    public static final String KEY_TYPE_DOWNLOAD_SUCCESS = "key_type_download_success";
    public static final String KEY_TOTAL_DOWNLOAD_SUCCESS = "key_total_download_success";

    // option extract
    public static final int ASK_BEFORE_OVERRIDE = 0;
    public static final int OVERRIDE_WITHOUT_PROMP = 1;
    public static final int SKIPS_EXISTING_FILES = 2;
    //rate
    public static final String KEY_IS_RATE = "key_is_rate";

    //save instanceState
    public static final String SAVE_LIST_SELECT = "save_list_select";
    public static final String SAVE_LIST_OPTIONS = "save_list_option";
    public static final String SAVE_LIST_EXPLORE = "save_list_explore";

    public static final String SAVE_BOOLEAN_SHOW_RENAME = "save_boolean_show_rename";
    public static final String SAVE_BOOLEAN_SHOW_PASS_DIALOG = "save_boolean_show_pass_dialog";
    public static final String SAVE_BOOLEAN_SHOW_COMPRESS = "save_boolean_show_compress";
    public static final String SAVE_BOOLEAN_SHOW_EXTRACT = "save_boolean_show_extract";
    public static final String SAVE_BOOLEAN_SEARCH_EXPLORE = "save_boolean_search_explore";
    public static final String SAVE_BOOLEAN_FOLDER_RENAME = "save_boolean_folder_rename";
    public static final String SAVE_BOOLEAN_SHOW_BOTTOM_SHEET = "save_boolean_show_bottom_sheet";
    public static final String SAVE_BOOLEAN_SHOW_DELETE = "save_boolean_show_delete";
    public static final String SAVE_BOOLEAN_SHOW_RATE = "save_boolean_show_rate";

    public static final String SAVE_PATH_BOTTOM_SHEET = "save_path_bottom_sheet";

    public static final String SAVE_FILE_NAME = "save_file_name";
    public static final String SAVE_FILE_NAME_COMPRESS = "save_file_name_compress";
    public static final String SAVE_FOLDER_NAME_COMPRESS = "save_folder_name_compress";
    public static final String SAVE_PASS_COMPRESS = "save_pass_compress";
    public static final String SAVE_TYPE_COMPRESS = "save_type_compress";

    public static final String SAVE_FILE_NAME_EXTRACT = "save_file_name_extract";
    public static final String SAVE_FOLDER_NAME_EXTRACT = "save_folder_name_extract";
    public static final String SAVE_OPTION_EXTRACT = "save_option_extract";

    public static final String SAVE_PASS_EXTRACT = "save_pass_extract";
    public static final String SAVE_RATE_POINT = "save_rate_point";
    public static final String SAVE_ENABLE_SHOW_ALL = "save_rate_point";
    public static final String SAVE_COLLAPSE = "save_collapse";
    public static final String SAVE_BOOLEAN_SHOW_ALL = "save_boolean_show_all";
    public static final String SAVE_HEIGHT_VIEW = "save_height_view";

    //type sort view
    public static final String TYPE_PRESENTATION_VIEW_ARCHIVER = "type_presentation_view_archiver";
    public static final String TYPE_PRESENTATION_VIEW_HOME = "type_presentation_view_home";
    public static final String TYPE_PRESENTATION_VIEW_EXPLORE = "type_presentation_view_explore";
    public static final String TYPE_PRESENTATION_VIEW_DOC = "type_presentation_view_doc";
    public static final String TYPE_PRESENTATION_VIEW_PHOTO = "type_presentation_view_photo";
    public static final String TYPE_PRESENTATION_VIEW_MUSIC = "type_presentation_view_music";
    public static final String TYPE_PRESENTATION_VIEW_DOWNLOAD = "type_presentation_view_download";
    public static final String TYPE_PRESENTATION_VIEW_VIDEO = "type_presentation_view_video";
    public static final String TYPE_PRESENTATION_VIEW_APK = "type_presentation_view_apk";
    public static final String TYPE_PRESENTATION_VIEW_GOOGLE = "type_presentation_view_google";
    public static final String TYPE_PRESENTATION_VIEW_ONE_DRIVE = "type_presentation_view_one_drive";
    public static final String TYPE_PRESENTATION_VIEW_DROP_BOX = "type_presentation_view_drop_box";

    public static final String TYPE_OPTION_VIEW_APK = "type_option_view_apk";
    public static final String TYPE_OPTION_VIEW_ARCHIVER = "type_option_view_archiver";
    public static final String TYPE_OPTION_VIEW_EXPLORE = "type_option_view_explore";
    public static final String TYPE_OPTION_VIEW_HOME = "type_option_view_home";
    public static final String TYPE_OPTION_VIEW_DOC = "type_option_view_doc";
    public static final String TYPE_OPTION_VIEW_PHOTO = "type_option_view_photo";
    public static final String TYPE_OPTION_VIEW_MUSIC = "type_option_view_music";
    public static final String TYPE_OPTION_VIEW_DOWNLOAD = "type_option_view_download";
    public static final String TYPE_OPTION_VIEW_VIDEO = "type_option_view_video";
    public static final String TYPE_OPTION_VIEW_GOOGLE = "type_option_view_google";
    public static final String TYPE_OPTION_VIEW_ONE_DRIVE = "type_option_view_one_drive";
    public static final String TYPE_OPTION_VIEW_DROP_BOX = "type_option_view_one_drop_box";

    public static final String TYPE_DESC_VIEW_ARCHIVER = "type_desc_view_archiver";
    public static final String TYPE_DESC_VIEW_APK = "type_desc_view_apk";
    public static final String TYPE_DESC_VIEW_EXPLORE = "type_desc_view_explore";
    public static final String TYPE_DESC_VIEW_HOME = "type_desc_view_home";
    public static final String TYPE_DESC_VIEW_DOC = "type_desc_view_doc";
    public static final String TYPE_DESC_VIEW_PHOTO = "type_desc_view_photo";
    public static final String TYPE_DESC_VIEW_MUSIC = "type_desc_view_music";
    public static final String TYPE_DESC_VIEW_DOWNLOAD = "type_desc_view_download";
    public static final String TYPE_DESC_VIEW_VIDEO = "type_desc_view_video";
    public static final String TYPE_DESC_VIEW_GOOGLE = "type_desc_view_google";
    public static final String TYPE_DESC_VIEW_ONE_DRIVE = "type_desc_view_one_drive";
    public static final String TYPE_DESC_VIEW_DROP_BOX = "type_desc_view_drop_box";


}
