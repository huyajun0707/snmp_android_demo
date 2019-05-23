package com.hyj.demo.deviceagent.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

public class ViewUtil {

    private static ViewUtil mViewUtil;

    private ViewUtil() {
        // cannot be instantiated
    }

    public static synchronized ViewUtil getInstance() {
        if (mViewUtil == null) {
            mViewUtil = new ViewUtil();
        }
        return mViewUtil;
    }

    public static void releaseInstance() {
        if (mViewUtil != null) {
            mViewUtil = null;
        }
    }

    public int dp2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dp(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getScreenWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    public float getDensity(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    public int getDensityDpi(Context ctx) {
        return ctx.getResources().getDisplayMetrics().densityDpi;
    }

    public DisplayMetrics getScreenPixel(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public boolean isSoftKeyAvail(Activity activity) {
        final boolean[] isSoftkey = {false};
        final View rootView = findView((activity).getWindow().getDecorView(), android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootViewHeight = rootView.getRootView().getHeight();
                int viewHeight = rootView.getHeight();
                int heightDiff = rootViewHeight - viewHeight;
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    isSoftkey[0] = true;
                }
            }
        });
        return isSoftkey[0];
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public int getNavigationBarStatus(Context ctx) {
        boolean hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            return ctx.getResources().getDimensionPixelSize(ctx.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        } else {
            return 0;
        }
    }

    public int getStatusBarHeight(Context ctx) {
        int height = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public int getNavigationBarHeight(Context ctx) {
        int height = 0;
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkDeviceHasNavigationBar(ctx)) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public int getStatusAndTitleBarHeight(View view) {
        if (view == null) {
            return 0;
        }
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        return ((Activity) view.getContext()).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop() + frame.top;

    }

    private boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = resources.getBoolean(id);
        }
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            String navBarOverride = (String) method.invoke(clazz, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    public int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    public boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public boolean isInvisible(View view) {
        return view.getVisibility() == View.INVISIBLE;
    }

    public boolean isGone(View view) {
        return view.getVisibility() == View.GONE;
    }

    public void setViewVisible(View view) {
        if (isInvisible(view) || isGone(view)) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void setViewGone(View view) {
        if (isVisible(view)) {
            view.setVisibility(View.GONE);
        }
    }

    public void setViewInvisible(View view) {
        if (isVisible(view)) {
            view.setVisibility(View.INVISIBLE);
        }
    }


    public <V> V findView(Activity activity, @IdRes int resId) {
        return (V) activity.findViewById(resId);
    }

    public <V> V findView(View rootView, @IdRes int resId) {
        return (V) rootView.findViewById(resId);
    }

    public <V> V findViewAttachOnclick(Activity activity, @IdRes int resId, View.OnClickListener onClickListener) {
        View view = activity.findViewById(resId);
        view.setOnClickListener(onClickListener);
        return (V) view;
    }

    public <V> V findViewAttachOnclick(View rootView, @IdRes int resId, View.OnClickListener onClickListener) {
        View view = rootView.findViewById(resId);
        view.setOnClickListener(onClickListener);
        return (V) view;
    }

    public boolean isScrollable(ViewGroup group) {
        int totalHeight = 0;
        for (int i = 0; i < group.getChildCount(); i++) {
            totalHeight += group.getChildAt(i).getMeasuredHeight();
        }
        return group.getMeasuredHeight() < totalHeight;
    }

    public void toggleView(View view, boolean show) {
        if (show) {
            setViewVisible(view);
        } else {
            setViewGone(view);
        }
    }

    public void setText(TextView textView, CharSequence text, Typeface font) {
        if (text != null) {
            textView.setText(text);
            textView.setTypeface(font);
        } else {
            ViewUtil.getInstance().setViewGone(textView);
        }
    }

    public void setText(Button button, CharSequence text, Typeface font, View.OnClickListener listener) {
        setText(button, text, font);
        if (listener != null) {
            button.setOnClickListener(listener);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSystemUiVisibility(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);

    }


    public ProgressDialog showProgressDialog(Activity activity, String title, String message, DialogInterface.OnCancelListener cancelListener, boolean cancelable) {
        if (activity != null && !activity.isFinishing()) {
            lockScreenOrientation(activity);
            Dialog dialog = ProgressDialog.show(activity, title, message, true, true, cancelListener);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
            return (ProgressDialog) dialog;
        } else {
            return null;
        }
    }

    public void hideDialog(Dialog dialog, Activity activity) {
        if (dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing()) {
            dialog.dismiss();
            unLockScreenOrientation(activity);
        }
    }

    public void hideDialog(DialogFragment dialog) {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    public void lockScreenOrientation(Activity activity) {
        Configuration newConfig = activity.getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void unLockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public int getMeasuredWidth(View v) {
        return (v == null) ? 0 : v.getMeasuredWidth();
    }

    public int getWidth(View v) {
        return (v == null) ? 0 : v.getWidth();
    }

    public int getWidthWithMargin(View v) {
        return getWidth(v) + getMarginHorizontally(v);
    }

    public int getStart(View v) {
        return getStart(v, false);
    }

    public int getStart(View v, boolean withoutPadding) {
        if (v == null) {
            return 0;
        }
        if (isLayoutRtl(v)) {
            return (withoutPadding) ? v.getRight() - getPaddingStart(v) : v.getRight();
        } else {
            return (withoutPadding) ? v.getLeft() + getPaddingStart(v) : v.getLeft();
        }
    }

    public int getEnd(View v) {
        return getEnd(v, false);
    }

    public int getEnd(View v, boolean withoutPadding) {
        if (v == null) {
            return 0;
        }
        if (isLayoutRtl(v)) {
            return (withoutPadding) ? v.getLeft() + getPaddingEnd(v) : v.getLeft();
        } else {
            return (withoutPadding) ? v.getRight() - getPaddingEnd(v) : v.getRight();
        }
    }

    public int getPaddingStart(View v) {
        if (v == null) {
            return 0;
        }
        return ViewCompat.getPaddingStart(v);
    }

    public int getPaddingEnd(View v) {
        if (v == null) {
            return 0;
        }
        return ViewCompat.getPaddingEnd(v);
    }

    public int getPaddingHorizontally(View v) {
        if (v == null) {
            return 0;
        }
        return v.getPaddingLeft() + v.getPaddingRight();
    }

    public int getMarginStart(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(lp);
    }

    public int getMarginEnd(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginEnd(lp);
    }

    public int getMarginHorizontally(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(lp) + MarginLayoutParamsCompat.getMarginEnd(lp);
    }

    public boolean isLayoutRtl(View v) {
        return ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public float getDistanceBetween2Points(PointF p0, PointF p1) {
        float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
        return distance;
    }

    public PointF getMiddlePoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f);
    }

    public PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(percent, p1.y, p2.y));
    }

    public float evaluateValue(float fraction, Number start, Number end) {
        return start.floatValue() + (end.floatValue() - start.floatValue()) * fraction;
    }

    public PointF[] getIntersectionPoints(PointF pMiddle, float radius, Double lineK) {
        PointF[] points = new PointF[2];
        float radian, xOffset, yOffset;
        if (lineK != null) {
            radian = (float) Math.atan(lineK);
            xOffset = (float) (Math.sin(radian) * radius);
            yOffset = (float) (Math.cos(radian) * radius);
        } else {
            xOffset = radius;
            yOffset = 0;
        }
        points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y - yOffset);
        points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y + yOffset);

        return points;
    }

    private void checkAppCompatTheme(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorPrimary});
        final boolean failed = !typedArray.hasValue(0);
        if (typedArray != null) {
            typedArray.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use typedArray Theme.AppCompat theme " + "(or descendant) with the design library.");
        }
    }

    public
    @ColorInt
    int getIconColor(float positionOffset, @ColorInt int startColor, @ColorInt int middleColor, @ColorInt int endColor, int step) {
        if (startColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                if (middleColor == Color.TRANSPARENT) {
                    return middleColor;
                }
                return Color.argb((int) (0xff * positionOffset * 2), Color.red(middleColor), Color.green(middleColor), Color.blue(middleColor));
            } else if (positionOffset == 0.5) {
                return middleColor;
            } else {
                if (middleColor == Color.TRANSPARENT) {
                    if (endColor == Color.TRANSPARENT) {
                        return middleColor;
                    }
                    return Color.argb((int) (0xff - (2 * 0xff * positionOffset)), Color.red(endColor), Color.green(endColor), Color.blue(endColor));
                } else {
                    if (endColor == Color.TRANSPARENT) {
                        return Color.argb((int) (0xff - (2 * 0xff * positionOffset)), Color.red(endColor), Color.green(endColor), Color.blue(endColor));
                    }
                    return getOffsetColor((float) ((positionOffset - 0.5) * 2), middleColor, endColor, step);
                }
            }
        } else if (middleColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                return Color.argb((int) (0xff - (2 * 0xff * positionOffset)), Color.red(startColor), Color.green(startColor), Color.blue(startColor));
            } else if (positionOffset == 0.5) {
                return middleColor;
            } else {
                if (endColor == Color.TRANSPARENT) {
                    return Color.TRANSPARENT;
                }
                return Color.argb((int) (0xff - (2 * 0xff * positionOffset)), Color.red(endColor), Color.green(endColor), Color.blue(endColor));
            }
        } else if (endColor == Color.TRANSPARENT) {
            if (positionOffset < 0.5) {
                return getOffsetColor(positionOffset * 2, startColor, middleColor, step);
            } else if (positionOffset == 0.5) {
                return middleColor;
            } else {
                return Color.argb((int) (0xff - (2 * 0xff * positionOffset)), Color.red(middleColor), Color.green(middleColor), Color.blue(middleColor));
            }
        } else {
            if (positionOffset < 0.5) {
                return getOffsetColor(positionOffset * 2, startColor, middleColor, step);
            } else if (positionOffset == 0.5) {
                return middleColor;
            } else {
                return getOffsetColor((float) ((positionOffset - 0.5) * 2), middleColor, endColor, step);
            }
        }
    }

    public
    @ColorInt
    int getOffsetColor(float offset, @ColorInt int startColor, @ColorInt int endColor, int steps) {
        if (offset <= 0.04) {
            return startColor;
        }
        if (offset >= 0.96) {
            return endColor;
        }
        return Color.rgb((int) (Color.red(startColor) + ((Color.red(endColor) - Color.red(startColor)) / steps) * (offset * steps)),
                (int) (Color.green(startColor) + ((Color.green(endColor) - Color.green(startColor))) / steps * (offset * steps)),
                (int) (Color.blue(startColor) + ((Color.blue(endColor) - Color.blue(startColor)) / steps) * (offset * steps)));
    }

    public String getText(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                return ((TextView) view).getText().toString();
            }
            if (view instanceof EditText) {
                return ((EditText) view).getText().toString();
            }
            if (view instanceof Button) {
                return ((Button) view).getText().toString();
            }
        }
        return null;
    }



}
