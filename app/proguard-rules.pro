# Default ProGuard rules. Minification is disabled for this student project,
# so no custom rules are required. Kept because the release build type
# references this file.

# Keep Firebase Firestore model classes intact for reflection-based mapping.
-keepclassmembers class com.ozodbek.booknest.data.** {
    <init>();
    <fields>;
}
