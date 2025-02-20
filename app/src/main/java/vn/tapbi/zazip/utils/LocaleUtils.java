package vn.tapbi.zazip.utils;


import static vn.tapbi.zazip.utils.Utils.isAtLeastSdkVersion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.util.Locale;

import timber.log.Timber;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.local.SharedPreferenceHelper;
import vn.tapbi.zazip.ui.main.MainActivity;

public class LocaleUtils {

    public static void applyLocale(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String localeString =preferences.getString(Constant.PREF_SETTING_LANGUAGE, Constant.LANGUAGE_EN);
        if(TextUtils.isEmpty(localeString)){
            localeString = Constant.LANGUAGE_EN;
        }
        Timber.e("applyLocale "+ localeString);
        Locale newLocale= new Locale(localeString);
        updateResource(context, newLocale);
        if(context!=context.getApplicationContext()){
            updateResource(context.getApplicationContext(),newLocale);
        }

    }
    @SuppressLint("WrongConstant")
    public static void sendFeedback(Context context) {
        try {

            PackageInfo pInfo;
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            String versionAppRGB = pInfo.versionName;
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            } catch (final PackageManager.NameNotFoundException e) {
                applicationInfo = null;
            }
            String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");


            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getResources().getString(R.string.mail_feedback_zazip)});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback & Help " +
                    applicationName + ", Version: " + versionAppRGB);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(Intent.createChooser(emailIntent, "Send mail for help!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateResource(Context context, Locale locale) {
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Locale current = getLocaleCompat(resources);
        if(current==locale){
            return;
        }
        Configuration configuration = new Configuration(resources.getConfiguration());
        if( isAtLeastSdkVersion(Build.VERSION_CODES.N)){
            configuration.setLocale(locale);
        }else if(isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1)){
            configuration.setLocale(locale);
        }else{
            configuration.locale = locale;
        }
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }

    public  static Locale getLocaleCompat(Resources resources) {
        Configuration configuration = resources.getConfiguration();
        return isAtLeastSdkVersion(Build.VERSION_CODES.N) ? configuration.getLocales().get(0) : configuration.locale;
    }

    public static void applyLocaleAndRestart(Activity activity,String localeString){
        Timber.e("applyLocaleAndRestart "+ localeString);
        SharedPreferenceHelper.storeString(Constant.PREF_SETTING_LANGUAGE,localeString);
        LocaleUtils.applyLocale(activity);
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        ActivityCompat.finishAffinity(activity);
    }


}
