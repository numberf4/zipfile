package vn.tapbi.zazip.common.models;

import static android.os.FileObserver.MOVED_FROM;
import static android.os.FileObserver.MOVED_TO;

import android.os.FileObserver;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class RecursiveFileObserver {

    public interface OnFileChangeListener {
        void onRename(String oldPath, String newPath);
    }

    private static final int CHANGES_ONLY = MOVED_FROM | MOVED_TO;

    List<SingleFileObserver> mObservers;
    private final OnFileChangeListener onFileChangeListener;
    private String from, to;

    public RecursiveFileObserver(OnFileChangeListener onFileChangeListener) {
        this.onFileChangeListener = onFileChangeListener;
    }

    public void startWatching(List<String> file) {
        if (mObservers != null) return;

        mObservers = new ArrayList<SingleFileObserver>();
        for (String f : file) {
            mObservers.add(new SingleFileObserver(f, CHANGES_ONLY));
        }

        for (SingleFileObserver sfo : mObservers) {
            sfo.startWatching();
        }
    }

    public void startWatching(String file) {
        if (mObservers != null) return;

        mObservers = new ArrayList<SingleFileObserver>();
        mObservers.add(new SingleFileObserver(file, CHANGES_ONLY));

        for (SingleFileObserver sfo : mObservers) {
            sfo.startWatching();
        }
    }

    public void stopWatching() {
        if (mObservers == null) return;

        for (SingleFileObserver sfo : mObservers) {
            sfo.stopWatching();
        }
        mObservers.clear();
        mObservers = null;
    }

    public void onEvent(int event, String path) {
        switch (event) {
            case MOVED_FROM:
                from = path;
                break;
            case MOVED_TO:
                to = path;
                break;
        }
        if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to) && onFileChangeListener != null) {
            onFileChangeListener.onRename(from, to);
            from = "";
            to = "";
        }
    }

    /**
     * Monitor single directory and dispatch all events to its parent, with full path.
     *
     * @author uestc.Mobius <mobius@toraleap.com>
     * @version 2011.0121
     */
    private class SingleFileObserver extends FileObserver {
        String mPath;

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
        }

        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + "/" + path;
            RecursiveFileObserver.this.onEvent(event, newPath);
        }
    }
}