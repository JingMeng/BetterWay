package com.android.betterway.utils;

import android.graphics.Bitmap;

/**
 * @author Jackdow
 * @version 1.0
 *          BetterWay
 */

 public final class BlurUtil {

    private BlurUtil() {
    }
    /**
     * 毛玻璃一张图片
     * @param srcBitmap    原始图片
     * @return  毛玻璃后的图片
     */
    public static Bitmap blur(Bitmap srcBitmap) {
       blurBitmap(srcBitmap, 13);
        return srcBitmap;
    }
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 模糊图片的jni方法
     * @param bitmap 模糊的对象
     * @param r 参数r为对bitmap虚化的程度范围
     */
    static native void blurBitmap(Object bitmap, int r);
}

