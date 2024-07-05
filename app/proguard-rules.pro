# ProGuard rules:
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keepattributes Signature

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

-keep class com.kappdev.wordbook.core.domain.model.** { *; }
-keep class com.kappdev.wordbook.main_feature.domain.model.** { *; }
-keep class com.kappdev.wordbook.study_feature.domain.model.** { *; }