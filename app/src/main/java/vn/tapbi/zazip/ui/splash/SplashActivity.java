package vn.tapbi.zazip.ui.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;

import com.ironman.trueads.admob.ControlAdsAdmob;
import com.ironman.trueads.admob.interstital.InterstitialAdAdmob;
import com.ironman.trueads.admob.open.AppOpenAdAdmob;
import com.ironman.trueads.admob.open.ShowOpenAdsAdmobListener;
import com.ironman.trueads.common.Common;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.ActivitySplashBinding;
import vn.tapbi.zazip.ui.base.BaseBindingActivity;
import vn.tapbi.zazip.ui.main.MainActivity;
import vn.tapbi.zazip.utils.Utils;

public class SplashActivity extends BaseBindingActivity<ActivitySplashBinding, SplashViewModel> {

    AppOpenAdAdmob appOpenManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public Class<SplashViewModel> getViewModel() {
        return SplashViewModel.class;
    }

    @Override
    public void setupView(Bundle savedInstanceState) {
        binding.ivSplash.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_splash));
        Utils.deleteCache(this);
        ControlAdsAdmob.INSTANCE.initAds(this);
        InterstitialAdAdmob.INSTANCE.loadInterstitialAdmob(this, getString(R.string.admob_interstitial_id));
        if (savedInstanceState == null || !savedInstanceState.getBoolean(Common.IS_SHOWING_AD_OPEN, false)) {
            appOpenManager = new AppOpenAdAdmob(getApplication());
            appOpenManager.loadAndShowOpenAdsAdmob(this, getString(R.string.admob_ads_open_id), true,
                    new ShowOpenAdsAdmobListener() {
                        @Override
                        public void onLoadedAdsOpenApp() {
                        }

                        @Override
                        public void onLoadFailAdsOpenApp() {
                            startMainActivity();
                        }

                        @Override
                        public void onShowAdsOpenAppDismissed() {
                            startMainActivity();
                        }

                        @Override
                        public void onAdsOpenLoadedButNotShow() {
                            startMainActivity();
                        }

                    });

        } else {
            if (!savedInstanceState.getBoolean(Common.IS_LOADED_AD_OPEN)) {
                startMainActivity();
            }
        }
    }

    @Override
    public void setupData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        window.setStatusBarColor(Color.TRANSPARENT);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 200);
    }
}