package com.huxq17.example.utils;


import com.huxq17.example.constants.Constants;

public class FileManager {

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getSaveFilePath() {
        if (CommonUtil.hasSDCard()) {
            return CommonUtil.getRootFilePath() + "com.vito/files/";
        } else {
            return CommonUtil.getRootFilePath() + "com.vito/files";
        }
    }

    /**
     * 获取日志缓存路径
     * 图片缓存的源码在StorageUtils.getCacheDirectory
     *
     * @return
     */
    public static String getLogFilePath() {
        if (CommonUtil.hasSDCard()) {
            return CommonUtil.getRootFilePath() + "com.vito/Crash/";
        } else {
            return CommonUtil.getRootFilePath() + "com.vito/Crash";
        }
    }

    /**
     * 获取数据路径
     *
     * @return
     */
    public static String getFilePath() {
        if (CommonUtil.hasSDCard()) {
            return CommonUtil.getRootFilePath() + "Android/data/" + Constants.PACKACKNAME + "/cache/";
        } else {
            return CommonUtil.getRootFilePath() + "data/" + Constants.PACKACKNAME + "/cache/";
        }
    }
}
