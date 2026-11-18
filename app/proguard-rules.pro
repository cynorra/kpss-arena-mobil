# ============================================================================
# R8 / ProGuard Configuration for KPSS Arena
# Maximizes DEX code optimization, obfuscation, and shrinking for Google Play
# ============================================================================

# ---- R8 Optimization & Obfuscation Enhancements ----
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# ---- Keep Essential Metadata & Annotations ----
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes SourceFile, LineNumberTable

# Make crash reports readable with Crashlytics mapping
-renamesourcefileattribute SourceFile

# ---- General Java / Android Base Rules ----
-keep public class * extends java.lang.Exception
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# ---- Firebase & Play Services ----
# Firebase and Google Play Services AARs include their own consumer ProGuard rules.
# We only protect our own DTO data models parsed via reflection (e.g. Firestore toObject).
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.**

# ---- Data Models & DTOs (Firestore & Room Reflection) ----
-keepclassmembers class com.cynorra.kpssarena.data.remote.model.** {
    <init>();
    <fields>;
    public void set*(...);
    public *** get*();
    public boolean is*();
}

-keepclassmembers class com.cynorra.kpssarena.domain.model.** {
    <init>();
    <fields>;
    public void set*(...);
    public *** get*();
    public boolean is*();
}

# ---- Room Database ----
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep class **_Impl { <init>(); }
-keepclassmembers class * {
    @androidx.room.Dao *;
}
-dontwarn androidx.room.paging.**

# ---- ViewModels ----
# Only keep the constructor for Hilt/ViewModelProvider instantiation.
# Methods and fields are fully obfuscated and optimized.
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ---- Hilt / Dagger & WorkManager ----
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-keep class * extends androidx.work.ListenableWorker {
    <init>(...);
}
-dontwarn dagger.hilt.android.internal.managers.**

# ---- Navigation SafeArgs ----
-keepclassmembers class * implements androidx.navigation.NavDirections {
    public *;
}
-keepclassmembers class * implements androidx.navigation.NavArgs {
    public *;
}

# ---- Glide ----
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# ---- MPAndroidChart ----
-keep class com.github.mikephil.charting.data.** { *; }
-keep class com.github.mikephil.charting.highlight.** { *; }
-keep class com.github.mikephil.charting.interfaces.datasets.** { *; }

# ---- Lottie ----
-keep class com.airbnb.lottie.model.** { *; }
-dontwarn com.airbnb.lottie.**

# ---- Retrofit / OkHttp ----
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

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

# ---- Strip Debug Logging in Release Builds ----
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}
-assumenosideeffects class timber.log.Timber$Tree {
    public void v(...);
    public void d(...);
    public void i(...);
}
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
    public static void i(...);
}
