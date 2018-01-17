# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xuhao/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
#混淆之后保留行号
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

##---------------Begin: proguard configuration for Android  ----------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v7.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.Fragment
-keep class * extends android.**{*;}
-keep class sun.misc.Unsafe.** { *; }

##---------------Begin: proguard configuration for Okhttp  ----------
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-keep class com.squareup.okhttp.** { *;}
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
-keep class org.codehaus.mojo.**{ *;}

##---------------Begin: proguard configuration for Glide  ----------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

##---------------Begin: proguard configuration for Eventbus  ----------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

##---------------Begin: proguard configuration for Rx  ----------
-dontwarn sun.misc.**
 -keep class sun.misc.** { *; }
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
 }
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
 }
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode consumerNode;
 }

 ##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
 -keepattributes Signature
 # For using GSON @Expose annotation
 -keepattributes *Annotation*
 # Gson specific classes
 -keep class sun.misc.Unsafe { *; }
 #-keep class com.google.gson.stream.** { *; }
 # Application classes that will be serialized/deserialized over Gson
 -keep class com.google.gson.examples.android.model.** { *; }
 # Prevent proguard from stripping interface information from TypeAdapterFactory,
 # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer

 ##---------------Begin: proguard configuration for Greendao  ----------
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{ *; }
-dontwarn rx.**

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**

##---------------Begin: proguard configuration for Frameself  ----------
-keep class cn.xuhao.android.lib.http.** { *; }
-keep class cn.xuhao.android.lib.widget.** { *; }
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class NoProguard
-keep class * implements NoProguard {
    *;
}
-keep class **.R$* {*;}
-keep class **.R{*;}
