-dontwarn org.slf4j.**
-dontwarn com.googlecode.javaewah.**
-dontwarn com.googlecode.javaewah32.**

# JGit
-keep class org.eclipse.jgit.** { *; }

# Main program class
-keep class LauncherKt {
    public static void main(java.lang.String[]);
}
