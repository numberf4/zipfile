package vn.tapbi.zazip.interfaces;

public interface OnPassDataCompress {
    void onPassDataExtract(String name, String folder, String content, int option);
    void onDataCompress(String name, String folder, String pass, String type, String content);
}
