# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/keith/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and fragment by changing the proguardFiles
# directive in collect.gradle.
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

#-keepattributes SourceFile,LineNumberTable

-keep public class com.getui.logful.LoggerFactory { public *; }
-keep public class com.getui.logful.Logger { public *; }
-keep public class com.getui.logful.LoggerConfigurator { public *; }
-keep public class com.getui.logful.annotation.LogProperties { public *; }
-keep public class com.getui.logful.Constants { public *; }
