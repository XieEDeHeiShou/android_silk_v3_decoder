package com.ketian.android.silkv3.jni;

/**
 * Created by ketian on 16-10-10.
 */
public class JNI {

    static {
        System.loadLibrary("x");
    }

    // Dynamic linked by JNI_OnLoad
    @SuppressWarnings("JniMissingFunction")
    public static native int x(String src, String dest);
}
