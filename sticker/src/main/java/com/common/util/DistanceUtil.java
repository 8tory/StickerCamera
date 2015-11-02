package com.common.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DistanceUtil {

    private DisplayMetrics     displayMetrics = null;
    private static DistanceUtil sInstance;
    private Context context;

    public static DistanceUtil init(Context context) {
        if (sInstance == null) {
            sInstance = new DistanceUtil(context);
        }
        return sInstance;
    }

    public static DistanceUtil getInstance() {
        return sInstance;
    }

    public static DistanceUtil getInstance(Context context) {
        return init(context);
    }

    private DistanceUtil(Context context) {
        this.context = context;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    public int getCameraAlbumWidth() {
        return (getScreenWidth() - dp2px(10)) / 4 - dp2px(4);
    }
    
    // 相机照片列表高度计算 
    public int getCameraPhotoAreaHeight() {
        return getCameraPhotoWidth() + dp2px(4);
    }
    
    public int getCameraPhotoWidth() {
        return getScreenWidth() / 4 - dp2px(2);
    }

    //活动标签页grid图片高度
    public int getActivityHeight() {
        return (getScreenWidth() - dp2px(24)) / 3;
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(context.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(context.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(context.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

}
