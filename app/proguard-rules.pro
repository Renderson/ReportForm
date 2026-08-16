# R8 / Play Console
# Hilt, Room, Firebase, Glide, CameraX, AdMob e Compose já enviam consumer rules.

-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature,EnclosingMethod,*Annotation*
-renamesourcefileattribute SourceFile

# Entidades Room e extras Serializable (ex.: Report).
-keep class com.rendersoncs.report.model.** { *; }

# Campos usados por java.io.Serializable (Intent da câmera usa File).
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn org.jetbrains.annotations.**
