# Add project specific ProGuard rules here.
# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Keep generic signature and annotations
-keepattributes Signature
-keepattributes *Annotation*

# Keep all classes in the app package
-keep class com.tubearchivist.share.** { *; }

# Keep JSON related classes
-keepclassmembers class * {
    @org.json.** *;
}

# Gson specific rules
# Gson uses generic type information stored in a class file when working with fields.
# Proguard/R8 removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Keep generic signature of TypeToken (Gson)
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Application classes that will be serialized/deserialized over Gson
-keep class me.imjerry.tubearchivistshare.ActivityLog { <fields>; }

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
