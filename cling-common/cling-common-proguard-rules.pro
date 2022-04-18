-keepattributes SourceFile,LineNumberTable, Annotation, InnerClasses, Signature

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class org.fourthline.cling.**{*;}

-dontwarn org.eclipse.jetty.**
-dontwarn org.fourthline.cling.**
-dontwarn org.seamless.**
-keep class org.fourthline.cling.** {*;}

# for media render state machine
-keep class org.seamless.statemachine.** {*;}
-keepclassmembers class * implements org.fourthline.cling.support.avtransport.impl.state.AbstractState {*;}