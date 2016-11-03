package com.huxq17.example.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 文件的工具类
 *
 * @author admin
 */
public class CommonUtil {
    /**
     * 检测是否存在SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 处理SD卡结果，若存在SD卡，返回SD卡路径，不存在则返回手机内存路径
     *
     * @return
     */
    public static String getRootFilePath() {

        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
            // //
            // /data/data/
        }
    }

    public static boolean checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

    public static void showToask(Context context, String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

}
