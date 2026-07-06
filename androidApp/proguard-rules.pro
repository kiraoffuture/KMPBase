# Crashlytics: keep line numbers for deobfuscated stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin / coroutines
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.kira.kmpbase.**$$serializer { *; }
-keepclassmembers class com.kira.kmpbase.** {
    *** Companion;
}
-keep @kotlinx.serialization.Serializable class com.kira.kmpbase.** { *; }

# Ktor / Ktorfit
-dontwarn io.ktor.**
-keep,allowobfuscation interface com.kira.kmpbase.core.network.** { *; }
-keep,allowobfuscation class com.kira.kmpbase.core.network.** { *; }
-keep,allowobfuscation class de.jensklingenberg.ktorfit.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Koin
-keep class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Firebase / Crashlytics / GitLive
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class dev.gitlive.firebase.** { *; }
-dontwarn dev.gitlive.firebase.**

# AndroidX Security (encrypted prefs)
-keep class androidx.security.crypto.** { *; }

# Coil
-dontwarn coil3.**
-keep class coil3.** { *; }

# App entry points
-keep class com.kira.kmpbase.KmpApplication { *; }
-keep class com.kira.kmpbase.MainActivity { *; }
