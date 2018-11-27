package android.lzy.a21habit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.TimeZoneFormat;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class DataUtil {


    private static final String TAG = "DataUtil";
    //核对日期
    public static boolean verifyDate(String endDate){
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        Date nowDate = new Date(System.currentTimeMillis());
        Date endTimeDate = null;
        try {
            if (!endDate.isEmpty()){
                endTimeDate = df.parse(endDate);
            }
        }catch (ParseException e){
            //Toast.makeText(MyApplication.getContext(), R.string.InputLicenseError + "_3", Toast.LENGTH_LONG).show();
        }
        if (nowDate.getTime() > endTimeDate.getTime()){
            return false;
        }else return true;
    }

    //日期加法
    public static String datePlus(String day, int days) {
        Log.w(TAG, "datePlus: " + day);
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        Date base = null;
        try {
            base = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        String dateOK = df.format(cal.getTime());

        return dateOK;
    }

    public static long daysBetween(String one1, String two1) {
        SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        Date one = null;
        Date two = null;
        try {
            one = df.parse(one1);
            two = df.parse(two1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        one = standardDate(one);
        two = standardDate(two);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(one);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(two);
        Log.w(TAG, "daysBetween: " + calendar.compareTo(calendar1));

        long difference =  (one.getTime()-two.getTime())/86400000;
        Log.w(TAG, "daysBetween origin: " + difference);
        return difference;
    }

    public static Date standardDate(Date date){
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    public static int getHour(Date date){
        return date.getHours();
    }

    public static int getMinute(Date date){
        return date.getMinutes();
    }

    //获取图片缩略图
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        //Log.w(TAG, "getImageThumbnail: " + Integer.toString(options.outWidth) + ";" + Integer.toString(options.outHeight) );
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }



    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static String getYMDString() {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return Integer.toString(calendar.get(Calendar.YEAR)) + Integer.toString(calendar.get(Calendar.MONTH) + 1) + Integer.toString(calendar.get(Calendar.DATE));
    }

    //获取音频文件路径
    public static String getRealPathFromUriForAudio(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    static public Bitmap getBitmapFromImg(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            return bitmap;
        }else {
            FileNotFoundException foundException = new FileNotFoundException();
            throw foundException;
        }
    }

    static public byte[] getBitmapByte(Bitmap bitmap, String filePath){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (filePath.contains(".jpg") || filePath.contains(".jpeg"))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        else if (filePath.contains(".png"))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    static public Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

    static public String storeImgFileFromByte(byte[] temp, String fileName){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            //Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/test.png");
            //Log.w(TAG, "sendRequestWithOkHttp: " + bitmap.);
            String savePath;
            File filePic;
            /*if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                savePath = SD_PATH;
            } else {
                savePath = MyApplication.getContext().getApplicationContext().getFilesDir()
                        .getAbsolutePath()
                        + IN_PATH;
            }*/
            savePath = EnumStatus.rootPath + "/";
            try {
                filePic = new File(savePath + fileName);
                if (!filePic.exists()) {
                    filePic.getParentFile().mkdirs();
                    filePic.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(filePic);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return filePic.getAbsolutePath();
        }
        return null;
    }

    static public String storeImgFileFromString(byte[] temp, String fileName){
        if(temp != null){
            String savePath;
            File filePic;
            /*if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                savePath = SD_PATH;
            } else {
                savePath = MyApplication.getContext().getApplicationContext().getFilesDir()
                        .getAbsolutePath()
                        + IN_PATH;
            }*/
            savePath = EnumStatus.rootPath + "/";
            try {
                filePic = new File(savePath + fileName);
                if (!filePic.exists()) {
                    filePic.getParentFile().mkdirs();
                    filePic.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(filePic);
                fos.write(temp);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return filePic.getAbsolutePath();
        }
        return null;
    }

    /// <summary>
    /// 二进制样式的字符串转byte数组
    /// </summary>
    /// <param name="binaryStr">二进制样式的字符串</param>
    /// <returns></returns>
    static public byte[] BinaryStr2ByteArray(String binaryStr)
    {
        return binaryStr.getBytes();
    }

    static public void downloadFile3(final String url, final String folderName){
        //下载路径，如果路径无效了，可换成你的下载路径
        //final String url = "http://c.qijingonline.com/test.mkv";
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath() + folderName;
                    File dest = new File(mSDCardPath, url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD","download success");
                    Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });
    }

}
