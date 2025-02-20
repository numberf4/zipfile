package vn.tapbi.zazip;

import static android.os.Build.VERSION.SDK_INT;

import static vn.tapbi.zazip.utils.Utils.isNetworkOnline1;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import timber.log.Timber;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.services.NotificationService;
import vn.tapbi.zazip.ui.cloud.dropbox.DropboxClients;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.ui.splash.SplashActivity;
import vn.tapbi.zazip.utils.LocaleUtils;
import vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper;

@HiltAndroidApp
public class App extends MultiDexApplication {
    private ConnectivityManager mConnectivityManager;
    private static App instance;
    public Boolean checkShowOptions = false;
    public Boolean checkShowSearch = false;
    public DriveServiceHelper driveServiceHelper;
    public List<FileData> listSelectCloud = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        RxJavaPlugins.setErrorHandler(Timber::w);
        initLog();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static App getInstance() {
        return instance;
    }

    private void initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public boolean goToWifiSettingsIfDisconnected() {
        final NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            Toast.makeText(this, getString(R.string.wifi_unavailable_error_message), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!isNetworkOnline1(this)) {
                Toast.makeText(this, getString(R.string.wifi_unavailable_error_message), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.applyLocale(this);
    }
}
