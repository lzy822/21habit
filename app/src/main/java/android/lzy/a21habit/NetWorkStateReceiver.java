package android.lzy.a21habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NetWorkStateReceiver extends BroadcastReceiver {
    private BRInteraction brInteraction;
    private static final String TAG = "NetWorkStateReceiver";


    public static final String rootPath = Environment.getExternalStorageDirectory().toString();

    public static final String serverRootPath = "http://120.79.77.39:822/";

    public static final String serverPhotosPath = "Photos/";

    public static final String appPhotoRootPath = rootPath + "/21Days/Photos";
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected()) {
                //Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
                String date = DataUtil.getYMDString();
                final String url = serverRootPath + serverPhotosPath + date + ".jpg";
                final String uri = appPhotoRootPath + "/" + date + ".jpg";
                File file = new File(uri);
                if (!file.exists()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloadPicture(url, uri);
                            brInteraction.setText(uri);
                        }
                    }).start();
                }
            }
//API大于23时使用下面的方式进行网络监听
        }else {

            System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            boolean isOK = false;
            for (int i = 0; i < networks.length; i++){
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                //sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                if (networkInfo.isConnected()){
                    isOK = true;
                    break;
                }

            }
            if (isOK) {
                String date = DataUtil.getYMDString();
                final String url = serverRootPath + serverPhotosPath + date + ".jpg";
                final String uri = appPhotoRootPath + "/" + date + ".jpg";
                File file = new File(uri);
                if (!file.exists()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w(TAG, "setText: " + "ok");
                            downloadPicture(url, uri);
                            brInteraction.setText(uri);
                        }
                    }).start();
                }
            }
            //Toast.makeText(context, sb.toString(),Toast.LENGTH_SHORT).show();
        }
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    //链接url下载图片
    private static void downloadPicture(String urlList,String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImg(String uri, ImageView imageView){
        /*WindowManager manager = MyApplication.getContext().get.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);*/
        DisplayMetrics outMetrics = MyApplication.getContext().getResources().getDisplayMetrics();
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(width,height / 3)
                .dontAnimate();
        Bitmap bitmap = DataUtil.getImageThumbnail(uri, width, height / 3);
        int degree = DataUtil.getPicRotate(uri);
        if (degree != 0) {
            Matrix m = new Matrix();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        Glide.with(MyApplication.getContext())
                .load(bitmap)
                .apply(options)
                .into(imageView);
    }
    public interface BRInteraction {
        public void setText(String uri);
    }

    public void setBRInteractionListener(BRInteraction brInteraction) {
        this.brInteraction = brInteraction;
    }
}
