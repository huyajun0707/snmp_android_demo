package com.hyj.demo.deviceagent.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.hyj.demo.deviceagent.utils.CacheUtil;
import com.hyj.demo.deviceagent.utils.LogUtil;
import com.hyj.demo.deviceagent.utils.MountInfo;
import com.onex.system.SystemUtils;

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static BaseApplication application;

    public static BaseApplication getInstance() {
        return application;
    }

    public void releaseInstance() {
        application = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //打开存储开关
        SystemUtils.EnableSATAPowerOn(true);
        LogUtil.getInstance().print(this.getClass().getSimpleName() + " onCreate() invoked!!");
        application = this;
        registerActivityLifecycleCallbacks(this);
        // TODO: 2017/8/4  打包时需要注意此
        LogUtil.getInstance().setDebug(true);
        MountInfo mountInfo = new MountInfo();
        CacheUtil.getInstance().setInnerHddPath(mountInfo.getInnerHddPath());
        CacheUtil.getInstance().setExtraHddPath(mountInfo.getExtraHddPath());
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.getInstance().print(this.getClass().getSimpleName() + " onLowMemory() invoked!!");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.getInstance().print(this.getClass().getSimpleName() + " onTrimMemory() invoked!!" + level);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityCreated() invoked!!");
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " taskId:" + activity.getTaskId());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityStarted() invoked!!");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityResumed() invoked!!");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityPaused() invoked!!");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityStopped() invoked!!");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivitySaveInstanceState() invoked!!");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtil.getInstance().print(activity.getClass().getSimpleName() + " onActivityDestroyed() invoked!!");
    }
}
