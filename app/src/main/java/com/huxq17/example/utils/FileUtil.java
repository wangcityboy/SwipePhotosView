package com.huxq17.example.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;

import com.huxq17.example.constants.Constants;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 图片缓存目录
     */
    public static final String IMAGES_FOLD = "images";

    /**
     * 对象缓存目录
     */
    public static final String OBJECTS_FOLD = "objects";

    /**
     * 文本缓存目录
     */
    public static final String TEXT_FOLD = "texts";

    /**
     * APK下载目录
     */
    public static final String APK_DOWNLOAD_FOLD = "apk";

    /**
     * 裁剪过的图片地址
     */
    public static final String CUT_PHOTO = "cut";

    /**
     * 判断是否存在应用外部存储
     *
     * @return
     */
    private static final String SDCARD_MNT = "/mnt/sdcard";

    private static final String SDCARD = "/sdcard";
    /**
     * picasso缓存
     */
    public static final String PICASSO_CACHE = "picasso-cache";


    /* 下载包安装路径 */
    public static final String savePath = FileManager.getFilePath() + "apk/";
    public static final String saveFileName = savePath + "UpdateDemoRelease.apk";

    public static boolean isExistSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private static FileUtil intance;

    public static FileUtil getInstance() {
        if (null == intance) {
            synchronized (FileUtil.class) {
                if (null == intance) {
                    intance = new FileUtil();
                }
            }
        }
        return intance;
    }

    private FileUtil() {

    }


    /**
     * 获取系统相机的路径
     *
     * @return
     */
    public String getSystemPhotoPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    }


    /**
     * 获取裁剪后的照片地址
     *
     * @param context
     * @return
     */
    public static final String getCutPhotoPath(Context context) {
        File file = FileUtil.getDiskCacheDir(context, FileUtil.CUT_PHOTO);
        String path = file.getPath() + File.separator;
        return path;
    }

    /**
     * 获取文本目录 , 如果没有，则生成一个
     *
     * @param context
     * @return
     */
    public static String getTextStorePath(Context context) {
        File file = FileUtil.getDiskCacheDir(context, FileUtil.TEXT_FOLD);
        String path = file.getPath() + File.separator;
        return path;
    }

    /**
     * 删除目录下的所有子目录及文件
     *
     * @param fold
     */
    public static void deleteFold(File fold) {
        if (fold != null && fold.isDirectory()) {
            File[] files = fold.listFiles();
            if (null == files)
                return;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFold(file);
                } else {
                    file.delete();
                }
            }
            LogUtil.d("FileUtil", "path cache cleared");
        }
    }

    /**
     * 获取图片缓存目录 , 如果没有，则生成一个
     *
     * @param context
     * @return
     */
    public static String getImageCachePath(Context context) {
        File file = FileUtil.getDiskCacheDir(context, FileUtil.IMAGES_FOLD);
        String path = file.getPath() + File.separator;
        return path;
    }

    /**
     * 获取对象目录 , 如果没有，则生成一个
     *
     * @param context
     * @return
     */
    public static String getObjectStorePath(Context context) {
        File file = FileUtil.getDiskCacheDir(context, FileUtil.OBJECTS_FOLD);
        String path = file.getPath() + File.separator;
        return path;
    }

    /**
     * 上传对象目录
     *
     * @param context
     */
    public static final void clearObjectStorePath(Context context) {
        File fold = FileUtil.getDiskCacheDir(context, FileUtil.OBJECTS_FOLD);
        deleteFold(fold);
    }

    /**
     * 获取APK下载目录
     *
     * @param context
     * @return
     */
    public static String getApkDownloadPath(Context context) {
        File file = FileUtil.getDiskCacheDir(context, FileUtil.APK_DOWNLOAD_FOLD);
        String path = file.getPath() + File.separator;
        return path;
    }

    /**
     * 打开并安装APK
     *
     * @param context
     * @param apkFile
     */
    public static void openAndInstallApk(Context context, File apkFile) {
        context.startActivity(getInstallApkIntent(apkFile));
    }

    public static Intent getInstallApkIntent(File apkFile) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String mimeType = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(apkFile), mimeType);
        return intent;
    }

    /**
     * 清除APK下载目录的文件
     *
     * @param context
     */
    public static void clearApkDownloadPath(Context context) {
        File fold = FileUtil.getDiskCacheDir(context, FileUtil.APK_DOWNLOAD_FOLD);
        if (fold != null && fold.isDirectory()) {
            File[] files = fold.listFiles();
            if (null == files)
                return;
            for (File file : files) {
                file.delete();
            }
            LogUtil.d("FileUtil", "apk path cache cleared");
        }
    }

    /**
     * @param context
     * @param uniqueName 子目录名,如"images"存图片, "objects"存对象
     * @return
     */
    private static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? getExternalCacheDir(context).getPath()
                : context.getCacheDir().getPath();
        File fold = new File(cachePath + File.separator + uniqueName);
        if (!fold.exists()) {
            fold.mkdirs();
        }
        return fold;
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    private static File getExternalCacheDir(Context context) {
        File fold = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName() + "/cache/");
        if (!fold.exists()) {
            fold.mkdir();
        }
        return fold;
    }

    public static boolean save(InputStream is, String savePath) throws IOException {
        FileOutputStream fos = null;
        try {
            byte[] buff = new byte[1024];
            int len;
            fos = new FileOutputStream(new File(savePath));
            while ((len = is.read(buff)) > 0) {
                fos.write(buff, 0, len);
            }
            return true;
        } catch (IOException e) {
            LogUtil.i("info",e.getMessage());
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.i("info",e.getMessage(),e);
                }
            }
        }
    }

    // 有sd卡的情况下 下载图片
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "yueniapp/";


    // 将剪切 的图片压缩
    public static String saveCropPic(Activity activity, Uri uri, CompressFormat type, int size) {
        String state = Environment.getExternalStorageState();
        if (!TextUtils.isEmpty(state) && state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            File file = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String strDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // 获取图片的绝对地址
        String thePath = getAbsolutePathFromNoStandardUri(uri);
        if (TextUtils.isEmpty(thePath)) {
            // 获取文件的标准路径
            thePath = getAbsoluteImagePath(activity, uri);
        }
        // 获取图片的扩展名
        String picName = getPicName(thePath);
        picName = TextUtils.isEmpty(picName) ? "jpg" : picName;
        // 图片的名字
        String picFileName = "yueniapp_crop" + strDate + "." + picName;
        picFileName = PATH + picFileName;
        // 保存图片到
        if (createFile(picFileName)) {
            FileOutputStream fos =null;
            try {
                fos = new FileOutputStream(picFileName);
                Bitmap bitmap = YnBitmapUtils.decodeUri(activity, uri);
                if (bitmap.compress(type, size, fos)) { // MediaStore.Images.Media.getBitmap(activity.getContentResolver(),
                    YnBitmapUtils.freeeBitmap(bitmap);
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        LogUtil.i("info",e1.getMessage(),e1);
                    }
                }
                LogUtil.i("info",e.getMessage(),e);
            }finally {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        LogUtil.i("info",e.getMessage(),e);
                    }
                }
            }
        }
        return picFileName;

    }

    /**
     * 创建一个新的文件
     *
     * @param path
     * @return
     */
    private static boolean createFile(String path) {

        File file = new File(path);
        if ( !file.exists()) {
            if (!file.getParentFile().exists()) {
                return file.getParentFile().mkdirs();
            }
            try {
                return file.createNewFile();
            } catch (IOException e) {
                LogUtil.i("info",e.getMessage(),e);
            }
        }
        return true;
    }

    /**
     * 获取图片的扩展名
     *
     * @param picPath
     * @return
     */
    private static String getPicName(String picPath) {
        if (TextUtils.isEmpty(picPath)) {
            return "";
        }
        int indext = picPath.lastIndexOf('.');
        return picPath.substring(indext + 1);
    }

    /**
     * 获取文件的标准的路径
     *
     * @return
     */
    private static String getAbsoluteImagePath(Activity activity, Uri uri) {
        String picPath = "";
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (null != cursor && cursor.getCount() > 0 && cursor.moveToFirst()) {
            picPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        }
        return picPath;
    }

    /**
     * 获取图片的绝对地址
     *
     * @param uri 图片的 uri
     * @return 返回图片的地址
     */
    private static String getAbsolutePathFromNoStandardUri(Uri uri) {
        String path = null;
        String picPath = uri.toString();
        picPath = Uri.encode(picPath);
        String file1 = "f://" + SDCARD + File.separator;
        String file2 = "f://" + SDCARD_MNT + File.separator;
        if (!TextUtils.isEmpty(picPath)) {
            if (picPath.startsWith(file1)) {
                path = Environment.getExternalStorageDirectory().getPath() + File.separator + picPath.substring(file1.length());
            } else if (picPath.startsWith(file2)) {
                path = Environment.getExternalStorageDirectory().getPath() + File.separator + picPath.substring(file2.length());
            }
        }
        return path;

    }

    /**
     * 删除创建的图片文件
     *
     * @param url
     */

    public static void detelePic(Context context, String url) {
        File file = new File(url);
        if (file.exists()) {
            file.delete();
            // 清除残留
            context.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "=?", new String[]{url});
        }
    }

    /**
     * 第三方的图片保存
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String fileName) {
        String state = Environment.getExternalStorageState();
        if (!TextUtils.isEmpty(state) && state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            File file = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String strDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // 图片的名字
        String picFileName = "yueniapp_crop" + strDate + "." + fileName + ".jpg";
        picFileName = PATH + picFileName;
        FileOutputStream fos = null;
        if (createFile(picFileName)) {
            try {
                fos =new FileOutputStream(picFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos );
                bitmap.compress(CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        LogUtil.i("info",e1.getMessage(),e1);
                    }
                }
                LogUtil.i("info",e.getMessage(),e);
            }finally {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        LogUtil.i("info",e1.getMessage(),e1);
                    }
                }
            }
        }
        return picFileName;
    }

    /**
     * 下载启动页面图片
     *
     * @param url
     */
    public static void downloadSplashImage(final Context context, final String url) {
        if (!TextUtils.isEmpty(url)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveSplashImage(Picasso.with(context).load(url).get());
                    } catch (Exception e) {
                        LogUtil.i("info",e.getMessage(),e);
                    }
                }
            }).start();

        }
    }

    /**
     * 保存启动页面的图片
     *
     * @param bitmap
     * @return
     */
    public static String saveSplashImage(Bitmap bitmap) {
        // 图片的名字
        String picFileName = "splash.jpg";
        picFileName = FileManager.getFilePath() + picFileName;
        if (createFile(picFileName)) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(picFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bitmap.compress(CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        LogUtil.i("info",e1.getMessage(),e1);
                    }
                }
                LogUtil.i("info",e.getMessage(),e);
            }finally {
                if(null != fos){
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        LogUtil.i("info",e1.getMessage(),e1);
                    }
                }
            }
        }
        return picFileName;

    }

    /**
     *  
     * 判断是否安装目标应用 
     *
     * @param packageName 目标应用安装后的包名 
     * @return 是否已安装目标应用 
     *      
     */
    public static boolean isApkAvailable(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            LogUtil.i("info",e.getMessage(),e);
        }
        return packageInfo != null;
    }

    public static String getSdFile() {
        String state = Environment.getExternalStorageState();
        String dirPath ;
        if (!TextUtils.isEmpty(state) && state.equals(Environment.MEDIA_MOUNTED)) {   //表示有sd卡的
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android/";
        } else {
            dirPath = Environment.getDataDirectory().getAbsolutePath() + File.separator;
        }
        return dirPath + "data/" + Constants.PACKACKNAME + "/file";
    }

    private static final int BUFFER_SIZE = 8192;

    /**
     * 下载文件
     */
    public static void download(String url, File output, File tmpDir) {
        InputStream is = null;
        OutputStream os = null;
        FileOutputStream fos = null;
        File tmp = null;
        try {
            File parentPath = output.getParentFile().getAbsoluteFile();//需要保存的文件的上一级目录
            if (parentPath.exists() && parentPath.isFile()) {
                parentPath.delete();
            }
            if (!parentPath.exists()) {//创建上一级目录
                parentPath.mkdirs();
            }
            tmp = File.createTempFile("download", ".tmp", tmpDir);
            is = new URL(url).openStream();
            fos = new FileOutputStream(tmp);
            os = new BufferedOutputStream(fos );
            copyStream(is, os);
            tmp.renameTo(output);
            tmp = null;
        } catch (IOException e) {
            if(null != fos){
                try {
                    fos.close();
                } catch (IOException e1) {
                    LogUtil.i("info",e1.getMessage(),e1);
                }
            }
            LogUtil.i("info",e.getMessage(),e);
        } finally {
            if (tmp != null) {
                try {
                    tmp.delete();
                } catch (Exception e) {
                    LogUtil.i("info",e.getMessage(),e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    LogUtil.i("info",e.getMessage(),e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    LogUtil.i("info",e.getMessage(),e);
                }
            }
            if(null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.i("info",e.getMessage(),e);
                }
            }
        }
    }

    /**
     * Copy from one stream to another.  Throws IOException in the event of error
     * (for example, SD card is full)
     *
     * @param is Input stream.
     * @param os Output stream.
     */
    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        copyStream(is, os, buffer, BUFFER_SIZE);
    }


    /**
     * Copy from one stream to another.  Throws IOException in the event of error
     * (for example, SD card is full)
     *
     * @param is         Input stream.
     * @param os         Output stream.
     * @param buffer     Temporary buffer to use for copy.
     * @param bufferSize Size of temporary buffer, in bytes.
     */
    public static void copyStream(InputStream is, OutputStream os,
                                  byte[] buffer, int bufferSize) throws IOException {
        for (; ; ) {
            int count = is.read(buffer, 0, bufferSize);
            if (count == -1) {
                break;
            }
            os.write(buffer, 0, count);
        }
    }

    /**
     * 获取用于保存H5文件的路径，如：/mnt/sdcard/yeni/h5/model/public
     * Add by Vincent 2015/08/14
     *
     * @return
     */
    public static String getH5FileSavePath() {
        return FileUtil.getSdFile() + File.separator + "H5" + File.separator + "model";
    }

    public static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    /**
     * 计算目录中的文件大小
     */
    public static double calculateDiskCacheSize(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File file[] = dir.listFiles();
                double size = 0;
                for (File f : file) {
                    size += f.length();
                }
                return  BigDecimal.valueOf(size / 1024 / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            } else {
                return  BigDecimal.valueOf(dir.length() / 1024 / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
        } else {
        }
        return 0;
    }

    /**
     * 根据地址保存图片
     * @param path
     * @param bitmap
     */
    public static void savePic(String path, Bitmap bitmap){
        if(!TextUtils.isEmpty(path)){
            FileOutputStream fileOutputStream = null;
            if(createFile(path +".jpg")){
                try {
                    fileOutputStream  = new FileOutputStream(path +".jpg");
                    BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                    bitmap.compress(CompressFormat.JPEG,100,bos);
                    bos.flush();
                    bos.close();
                }catch (Exception e){
                    if(null != fileOutputStream){
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            LogUtil.i("info",e1.getMessage(),e1);
                        }
                    }
                    LogUtil.i("info",e.getMessage(),e);
                }finally {
                    if(null != fileOutputStream){
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            LogUtil.i("info",e1.getMessage(),e1);
                        }
                    }
                }
            }

        }
    }
    /**
     * 判断下载包是不是已经下载好了
     * @return
     */
    public static boolean isDownReadly(){
        File file  = new File(saveFileName);
        if(file.exists()){
            return true;
        }else {
            return false;
        }
    }
}
