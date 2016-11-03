package com.huxq17.example.utils;

import android.util.Log;

public class LogUtil {

    public static int DISABLE = 0x0;

    /**
     * 所有等级日志
     */
    public static int ALL = 0x3f;

    /**
     * 详细日志
     */
    public static int VERBOSE = 0x1;

    /**
     * 调试日志
     */
    public static int DEBUG = 0x2;

    /**
     * 信息日志
     */
    public static int INFO = 0x4;

    /**
     * 警告日志
     */
    public static int WARN = 0x8;

    /**
     * 错误日志
     */
    public static int ERROR = 0x10;

    /**
     * 断言日志
     */
    public static int ASSERT = 0x20;

    /**
     * 当前启用的日志等级(默认打开所有)
     */
    private static int LEVEL = ALL;

    private  static  boolean isShowLog = true;

    /**
     * 设置日志等级 例： LogUtil.setLevel(LogUtil.ALL); LogUitl.setLevel(LogUtil.VERBOSE);
     *
     * @param level
     */
    public static void setLevel(int level) {
        LEVEL = level;
    }

    public static void v(String tag, String msg, Object... args) {
        if ((LEVEL & VERBOSE) > 0 && isShowLog)
            log(Log.VERBOSE, tag, msg, args);
    }

    public static void d(String tag, String msg, Object... args) {
        if ((LEVEL & DEBUG) > 0 &&isShowLog) // 内测环境才显示日志信息
            log(Log.DEBUG, tag, msg, args);
    }

    public static void i(String tag, String msg, Object... args) {
        if ((LEVEL & INFO) > 0 && isShowLog)
            log(Log.INFO, tag, msg, args);
    }

    public static void w(String tag, String msg, Object... args) {
        if ((LEVEL & WARN) > 0 && isShowLog)
            log(Log.WARN, tag, msg, args);
    }

    public static void e(String tag, String msg, Object... args) {
        if ((LEVEL & ERROR) > 0 && isShowLog)
            log(Log.ERROR, tag, msg, args);
    }

    public static void a(String tag, String msg, Object... args) {
        if ((LEVEL & ASSERT) > 0)
            log(Log.ASSERT, tag, msg, args);
    }

    private static void log(int priority, String tag, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        Log.println(priority, tag, message);
    }
}
