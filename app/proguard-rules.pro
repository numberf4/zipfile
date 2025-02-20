# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn org.slf4j.**
# fabric and firebase
-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.firebase.FirebaseApp {
   static com.google.firebase.FirebaseApp getInstance();
   boolean isDataCollectionDefaultEnabled();
}
-keep class com.crashlytics.android.ndk.** { *; }
-dontwarn com.crashlytics.android.ndk.**

-keepattributes *Annotation*
   -keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
    }
   -keep enum org.greenrobot.eventbus.ThreadMode { *; }

# default
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keep class android.support.v8.renderscript.** { *; }
-keep class androidx.renderscript.** { *; }

#Các class được bảo mật
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.google.** { *; }
-keep class com.microsoft.** { *; }
-keep class vn.tapbi.zazip.data.model.** { *; }
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
 # Add this global rule
    -keepattributes Signature



