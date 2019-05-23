package com.hyj.demo.deviceagent.listener;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/14 9:47
 * @description :
 * =========================================================
 */
public interface OnResponseListener {

    void onSuccess(Object response);

    void onError();

}
