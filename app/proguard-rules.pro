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
##-------------------------基本不用动区域--------------------------
##指定代码的压缩级别
#-optimizationpasses 5
#
##包明不混合大小写
#-dontusemixedcaseclassnames
#
##不去忽略非公共的库类
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
#
##混淆时是否记录日志
#-verbose
#
##优化  不优化输入的类文件
#-dontoptimize
#
##预校验
#-dontpreverify
#
## 保留sdk系统自带的一些内容 【例如：-keepattributes *Annotation* 会保留Activity的被@override注释的onCreate、onDestroy方法等】
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
#
## 记录生成的日志数据,gradle build时在本项根目录输出
## apk 包内所有 class 的内部结构
#-dump proguard/class_files.txt
## 未混淆的类和成员
#-printseeds proguard/seeds.txt
## 列出从 apk 中删除的代码
#-printusage proguard/unused.txt
## 混淆前后的映射
#-printmapping proguard/mapping.txt
#
#
## 避免混淆泛型
-keepattributes Signature
## 抛出异常时保留代码行号,保持源文件以及行号
-keepattributes SourceFile,LineNumberTable
#
##-----------------------------6.默认保留区-----------------------
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# ============忽略警告，否则打包可能会不成功=============
-ignorewarnings
#如果引用了v4或者v7包
-dontwarn android.support.**

## 保持哪些类不被混淆
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep public class com.coder.binauralbeats.beats.**{*;}
-keep public class com.coder.binauralbeats.viz.**{*;}
-keep public class com.coder.binauralbeats.ui.**{*;}
-keep public class com.coder.binauralbeats.graphview.**{*;}
-keep public class com.coder.binauralbeats.event.**{*;}
############EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
