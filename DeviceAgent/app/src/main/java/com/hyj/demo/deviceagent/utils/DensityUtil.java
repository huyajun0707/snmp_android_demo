package com.hyj.demo.deviceagent.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

/**
 * 页面显示单位转换工具
 *
 * @author hyj
 */
public class DensityUtil {

    private static DensityUtil mDensityUtil;
    private Context mContext;

    private DensityUtil(Context context) {
        // cannot be instantiated
        this.mContext = context;
    }

    public static synchronized DensityUtil getInstance(Context context) {
        if (mDensityUtil == null) {
            mDensityUtil = new DensityUtil(context);
        }
        return mDensityUtil;
    }

    public static void releaseInstance() {
        if (mDensityUtil != null) {
            mDensityUtil = null;
        }
    }

    public int getScreenWidth() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }

    public float getDensity() {
        return mContext.getResources().getDisplayMetrics().density;
    }

    public int getDensityDpi() {
        return mContext.getResources().getDisplayMetrics().densityDpi;
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dp(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public int px2sp(float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public float sp2px(float sp) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public int getNavigationBarStatus() {
        boolean hasMenuKey = ViewConfiguration.get(mContext).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            return mContext.getResources().getDimensionPixelSize(mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        } else {
            return 0;
        }
    }

    public Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}