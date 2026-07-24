# ---- General ----
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes SourceFile, LineNumberTable
-keep public class * extends java.lang.Exception
# ---- Firebase ----
-keep class com.google.firebase.** { *; }
-keep class com.firebase.** { *; }

-dontwarn com.firebase.**
-keepnames class com.firebase.** { *; }
# ---- Hilt / Dagger ----
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$F
# ---- Room ----
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class com.cynorra.kpssarena.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**
# ---- Glide ----
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewin
    *** rewind();
}
# ---- Retrofit ----
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# ---- OkHttp ----
-dontwarn okhttp3.**
-dontwarn okio.**
# ---- MPAndroidChart ----
-keep class com.github.mikephil.charting.** { *; }

# ---- Lottie ----
-keep class com.airbnb.lottie.** { *; }
# ---- AdMob ----
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
# ---- Timber ----
-dontwarn timber.log.**
# ---- RxJava ----
-dontwarn sun.misc.**
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
# ---- Data Models ----
-keep class com.cynorra.kpssarena.domain.model.** { *; }
-keep class com.cynorra.kpssarena.data.remote.model.** { *; }
# ---- ViewModels ----
-keep class * extends androidx.lifecycle.ViewModel { *; }
# ---- Navigation ----
-keep class androidx.navigation.** { *; }
# ---- Remove logging in release ----
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);

    public static int v(...);
    public static int d(...);
    public static int i(...);
}
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
}
