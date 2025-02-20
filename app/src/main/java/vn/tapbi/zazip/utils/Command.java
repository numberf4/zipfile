package vn.tapbi.zazip.utils;

public class Command {
    public static String getExtractCmd(String archivePath, String outPath) {
        return String.format("7z x '%s' '-o%s' -aoa", archivePath, outPath);
    }

    public static String getExtractCmdPassword(String archivePath, String outPath, String password) {
        return String.format("7z x '%s' '-o%s' -aoa -p%s", archivePath, outPath, password);
    }

    public static String getCompressCmd(String filePath, String outPath, String type) {
        return String.format("7z a -t%s '%s' '%s' -mx1", type, outPath, filePath);
    }

    public static String getCompressCmdPassword(String filePath, String outPath, String type, String password) {
        return String.format("7z a -t%s '%s' '%s' -p%s -mx1", type, outPath, filePath, password);
    }
}
