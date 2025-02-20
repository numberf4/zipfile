package company.librate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import company.librate.view.BaseRatingBar;
import company.librate.view.CustomTextViewGradients;
import company.librate.view.RotationRatingBar;

import static android.content.Context.MODE_PRIVATE;

import androidx.fragment.app.FragmentActivity;

public class RateDialog extends Dialog {
    private boolean isBack;
    private Context context;
    private String supportEmail;
    private ImageView imageIcon;
    private RotationRatingBar rateBar;
    private SharedPreferences sharedPrefs;

    public RotationRatingBar getRateBar() {
        return rateBar;
    }

    private static int upperBound = 4;
    public static final String KEY_IS_RATE = "key_is_rate";
    private boolean isRateAppTemp = false;
    private IListenerRate iListenerRate;

    public RateDialog(Context context, String supportEmail, boolean isBack, IListenerRate iListenerRate) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_ios);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        this.context = context;
        this.iListenerRate = iListenerRate;
        this.supportEmail = supportEmail;
        this.isBack = isBack;
        sharedPrefs = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        initDialog();
    }

    private void initDialog() {
        final TextView btnOk = (TextView) findViewById(R.id.btn_ok);
        TextView btnNotNow = (TextView) findViewById(R.id.btn_not_now);
        TextView txtAppName = (TextView) findViewById(R.id.txt_name_app);
        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        imageIcon = (ImageView) findViewById(R.id.img_icon_app);
        rateBar = (RotationRatingBar) findViewById(R.id.simpleRatingBar);
        txtTitle.setTypeface(FontUntil.getTypeface("fonts/" + "ios.otf", context));
        txtAppName.setTypeface(FontUntil.getTypeface("fonts/" + "ios_semi_bold.otf", context));
        String[] temp = txtTitle.getText().toString().split(" ");
        String first = "";
        for (int i = 0; i < temp.length - 2; i++) {
            first += temp[i] + " ";
        }
        txtTitle.setText(first + "\n" + temp[temp.length - 2] + " " + temp[temp.length - 1]);
//        txtAppName.setText(context.getResources().getString(R.string.app_namee));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListenerRate.stateNotNow();

                if (isRateAppTemp && rateBar.getRating() > 0) {
                    iListenerRate.stateRate();
                    if (rateBar.getRating() > upperBound) {
                        openMarket();
                    } else {
                        sendFeedback(context);
                    }
                    notShowDialogWhenUserRateHigh();
                    dismiss();
                } else {
                    FontUntil.customToast(context, context.getString(R.string.please_rate_5_start));
                    //Toast.makeText(context, "please rate 5 stars", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListenerRate.stateNotNow();
                dismiss();
//                if (isBack) {
//                    dismiss();
//                    ((Activity) context).finish();
//                } else
//                    dismiss();
            }
        });
        rateBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                isRateAppTemp = true;
                iListenerRate.onChangeRateIndex(rating);

               /* if (ratingBar.getRating() > upperBound) {
                    //imageIcon.setImageResource(R.drawable.favorite);
                    btnOk.setText(getContext().getResources().getString(R.string.rate_now));
                } else {
                    // imageIcon.setImageResource(R.drawable.favorite2);
                    btnOk.setText(getContext().getResources().getString(R.string.feedback));
                }*/
            }
        });
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                iListenerRate.stateNotNow();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public boolean isRate() {
        return sharedPrefs.getBoolean(KEY_IS_RATE, false);
    }

    /**
     * update share not show rate when user rate this app > 2 *
     */
    private void notShowDialogWhenUserRateHigh() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(KEY_IS_RATE, true);
        editor.apply();
    }

    private void setTextColor(TextView textView) {
        int[] colors = new int[]{Color.parseColor("#4355FF"), Color.parseColor("#933DFE"),
                Color.parseColor("#FF35FD"), Color.parseColor("#FF8E61"), Color.parseColor("#FFE600"), Color.parseColor("#FF8E61")};
        TextPaint textPaint = textView.getPaint();
        float measure = textPaint.measureText(textView.getText().toString());
        Shader shader = new LinearGradient(0, 0, measure * 2, 0, colors, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
        textView.setTextColor(colors[0]);

    }

    private void openMarket() {
        final String appPackageName = context.getPackageName();
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit();
            editor.putBoolean(KEY_IS_RATE, true);
            editor.apply();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void sendEmail() {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + context.getPackageName() + ")");

        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(emailIntent, "Send mail Report App AZip!"));
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
            //  emailIntent.setType("text/html");
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

    public interface IListenerRate {
        void stateRate();
        void stateNotNow();
        void onChangeRateIndex(float rateIndex);
    }
}
