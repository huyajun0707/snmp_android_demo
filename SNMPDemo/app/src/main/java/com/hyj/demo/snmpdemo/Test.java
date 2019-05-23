package com.hyj.demo.snmpdemo;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/11/7 16:57
 * @description :
 * =========================================================
 */
public class Test {
    public static void main(String[] vargs) {
        String string = "2018-11-08T07:30:00";
        System.out.print(string.substring(string.indexOf("T") + 1));
    }

}
