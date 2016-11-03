package com.huxq17.example.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片处理工具类
 *
 * @author wxl
 */
public class YnBitmapUtils {

    /**
     * app包名
     */
    private static final String APPPACKAGENAME = "com.yueniapp.sns";

    /**
     * 释放bitmap
     *
     * @param bitmap
     */
    public static void freeeBitmap(Bitmap bitmap) {
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 默认的bitmap压缩==》默认保存到sd/yueniapp/下 以原大小压缩
     * @param bitmap
     * @return
     */
    public static String compressBitmapDefault(Bitmap bitmap, int compressSize, CompressFormat type) {
        FileOutputStream fileOutputStream = null;
        String path = YnBitmapUtils.getProjectSDCardPath();
        try {
            fileOutputStream = new FileOutputStream(new File(path));
            bitmap.compress(type, compressSize, fileOutputStream);
            fileOutputStream.flush();
        } catch (Exception e) {
            LogUtil.i("info",e.getMessage(),e);
        } finally {
            try {
                if(null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                LogUtil.i("info",e.getMessage(),e);
            }
        }

        return path;
    }

    /**
     * 获得项目SD卡的位置
     */
    public static String getProjectSDCardPath() {
        String sdcard_path = "";
        // 判断SD卡是否为空
        if (!Environment.MEDIA_REMOVED.equals(Environment.getExternalStorageState())) {
            sdcard_path = FileManager.getFilePath();
            String strDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            sdcard_path = sdcard_path + APPPACKAGENAME + File.separator + "ypcrop" + strDate + ".jpg";
            File file = new File(FileManager.getFilePath() + File.separator + APPPACKAGENAME);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return sdcard_path;
    }

    /**
     * 用来处理图片的太大的问题
     * @param context
     * @param selectedImage 图片的uri
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int width = 1080;
        options.inSampleSize = ImageUtil.computeSampleSize(options, -1, width * width);// 得到缩略图;

        AssetFileDescriptor fileDescriptor ;
        fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedImage, "r");

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
        actuallyUsableBitmap = ThumbnailUtils.extractThumbnail(actuallyUsableBitmap, width, width);
        return actuallyUsableBitmap;
    }

    /*
     * 从数据流中获得数据
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len ;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        inputStream.close();
        return bos.toByteArray();

    }

    /**
     * 网络下载图片
     */
    public static byte[] getImageFromNet(String path)  {
        byte[] b = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inputStream = conn.getInputStream();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                b = readInputStream(inputStream);
            }
        }catch (Exception e){
            LogUtil.i("info",e.getMessage(),e);
        }

        return b;

    }

    /**
     * 拍照时指定Uri
     */
    public static String imagePath = null;

    public static Uri getCameraUri() {
        Uri uri ;
        String state = Environment.getExternalStorageState();
        if (!TextUtils.isEmpty(state) && state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(FileUtil.PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else { // 返回手机的内存地址
            File file = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String strDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // 照片的名字
        String imageName = "yeuniapp_" + strDate + ".jpg";
        // 图片的路径
        imagePath = FileUtil.PATH + imageName;
        uri = Uri.fromFile(new File(imagePath));
        return uri;
    }




    /**
     * 保存图片到相册
     */
    public static void savePic(Context context, String folderName, String fileName, Bitmap bitmap) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        System.out.println("path===="+path.toString());
        File file = new File(path, folderName + "/" + fileName);
        file.getParentFile().mkdirs();
        try {
            bitmap.compress(CompressFormat.JPEG, 90, new FileOutputStream(file));
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, null);
        } catch (FileNotFoundException e) {
            LogUtil.i("info",e.getMessage(),e);
        }
    }
}
