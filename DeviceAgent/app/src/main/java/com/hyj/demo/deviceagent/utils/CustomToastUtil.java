package com.hyj.demo.deviceagent.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 消息提示工具
 *
 * @author hyj
 */
public class CustomToastUtil {

    private Toast mToast;

    private static CustomToastUtil mToastUtil;
    private static TextView mTextView;

    private CustomToastUtil() {
        // cannot be instantiated
    }

    public static synchronized CustomToastUtil getInstance() {
        if (mToastUtil == null) {
            mToastUtil = new CustomToastUtil();
        }

        return mToastUtil;
    }

    public static void releaseInstance() {
        if (mToastUtil != null) {
            mToastUtil = null;
        }
    }

    public void showToast(Context context, CharSequence message, int duration) {
        if (mToast == null) {
            mToast = new Toast(context);
            if (mTextView == null) {
                mTextView = new TextView(context);
            }
            initToastView(context, mToast, mTextView, message);
            mToast.setDuration(duration);
        } else {
            mTextView.setText(message);
        }
        mToast.show();
    }

    public void showToast(Context context, int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, duration);
            mToast = new Toast(context);
            if (mTextView == null) {
                mTextView = new TextView(context);
            }
            mTextView.setBackgroundResource(resId);
            initToastView(context, mToast, mTextView, null);
            mToast.setDuration(duration);
        } else {
            mTextView.setBackgroundResource(resId);
        }
        mToast.show();
    }

    public void hideToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private void initToastView(Context context, Toast toast, TextView view, CharSequence msg) {
//        view.setBackgroundResource(R.drawable.bg_toast);
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        view.setText(msg);
        view.setPadding(DensityUtil.getInstance(context).dp2px(36), DensityUtil.getInstance(context).dp2px(6), DensityUtil.getInstance(context).dp2px(36), DensityUtil.getInstance(context).dp2px(6));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
    }

}
