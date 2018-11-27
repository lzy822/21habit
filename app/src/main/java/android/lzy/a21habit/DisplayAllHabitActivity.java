package android.lzy.a21habit;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayAllHabitActivity extends AppCompatActivity {
    private static final String TAG = "DisplayAllHabitActivity";
    List<summarylist> summarylist;

    boolean isOKForAddHabit;

    ScreenBootReceiver receiver;

    boolean timeStatus;

    SimpleDateFormat df;

    SimpleDateFormat df_time;

    NetWorkStateReceiver netWorkStateReceiver;
    //public static final String EnumStatus.appApkRootPath = EnumStatus.rootPath + "/Download/";
    DownloadManager downloadManager;
    DownloadFinishReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_habit);

        //sendRequestWithOkHttp4();
        Log.w(TAG, "onCreate: " + DataUtil.getVersionCode(this) + "; " + DataUtil.getVerName(this));
        //sendRequestWithOkHttp();

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //注册下载完成的广播
        mReceiver = new DownloadFinishReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        isLongClick = 1;
        longClickedPosition = -1;
        longClickedHabitIc = "";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pickFile();


        //注册亮屏广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        receiver = new ScreenBootReceiver();
        registerReceiver(receiver, filter);

    }

    private void sendRequestWithOkHttp4(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://120.79.77.39:822/Conn4.asp";
                    //String url = "http://120.79.77.39:822";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("path", "C:\\photo\\myFile")
                            .build();
                    Request request = new Request.Builder()
                            //.url(url+"/uploadImage")
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    /*byte[] bytes = response.body().bytes();
                    for (int i = 0; i < bytes.length; ++i){
                        Log.w(TAG, "sendRequestWithOkHttp: " + bytes[i]);
                    }
                    DataUtil.storeImgFileFromByte(DataUtil.BinaryStr2ByteArray(bytes.toString()), "2113132313.png");*/
                    Log.w(TAG, "sendRequestWithOkHttp: " + response.body().contentType());
                    parseUrlAndDownloadFile(response.body().string());
                    //JSONObject jsonObject = new JSONObject(response.body().string());
                }catch (Exception e){
                    Log.w(TAG, "sendRequestWithOkHttp: " + e.toString());
                }
            }
        }).start();
    }

    private void parseUrlAndDownloadFile(String mStr){
        String str = mStr.replace("C:\\photo\\", "http://120.79.77.39:822/");
        str = str.replace("\\", "/");
        Log.w(TAG, "sendRequestWithOkHttp: " + str);
        String[] mUrl = str.split(";");
        for (int i = 0; i < mUrl.length; ++i){
            DataUtil.downloadFile3(mUrl[i], "/TuZhi");
        }
    }

    private void sendRequestWithOkHttp2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imagePath = Environment.getExternalStorageDirectory().toString() + "/test.png";
                    String url = "http://120.79.77.39:822/Conn3.asp";
                    //String url = "http://120.79.77.39:822";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Log.d("imagePath", imagePath);
                    File file = new File(imagePath);
                    RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", imagePath, image)
                            .build();
                    /*RequestBody requestBody = new FormBody.Builder()
                            .add("file", DataUtil.getBitmapByte(DataUtil.getBitmapFromImg(imagePath), imagePath).toString())
                            .build();*/
                    Request request = new Request.Builder()
                            //.url(url+"/uploadImage")
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    /*byte[] bytes = response.body().bytes();
                    for (int i = 0; i < bytes.length; ++i){
                        Log.w(TAG, "sendRequestWithOkHttp: " + bytes[i]);
                    }
                    DataUtil.storeImgFileFromByte(DataUtil.BinaryStr2ByteArray(bytes.toString()), "2113132313.png");*/
                    Log.w(TAG, "sendRequestWithOkHttp: " + response.body().contentType());
                    Log.w(TAG, "sendRequestWithOkHttp: " + response.body().string());
                    //JSONObject jsonObject = new JSONObject(response.body().string());
                }catch (Exception e){
                    Log.w(TAG, "sendRequestWithOkHttp: " + e.toString());
                }
            }
        }).start();
    }

    private void sendRequestWithOkHttp1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", "lsj")
                            .build();
                    Request request = new Request.Builder()
                            .url("http://120.79.77.39:822/Conn.asp")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if (!responseData.contains("html"))
                        Log.w(TAG, "sendRequestWithOkHttp: " + responseData);
                    else
                        Log.w(TAG, "sendRequestWithOkHttp: " + "error");
                }catch (Exception e){
                    Log.w(TAG, "sendRequestWithOkHttp: " + e.toString());
                }
            }
        }).start();
    }

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(EnumStatus.serverRootPath + EnumStatus.serverApksPath + "VersionInfo.txt")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.w(TAG, "run: " + getVersionInfo(responseData));
                    Message msg = new Message();
                    msg.what = 1234;
                    Bundle bundle = new Bundle();
                    bundle.putString("version", getVersionInfo(responseData));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Log.w(TAG, e.toString());
                }
            }
        }).start();
    }

    private String getVersionInfo(String data){
        return data.substring(data.indexOf("<version>") + 9, data.indexOf("</version>"));
    }

    private boolean isImgOK;

    @Override
    protected void onResume() {
        super.onResume();

        initGlobalVariable();
        refreshRecycler();
        initFloatingButton();
        loadImage();
        invalidateOptionsMenu();
        refreshForeService();

        Log.w(TAG, "onCreate: " + DataUtil.daysBetween(df.format(new Date(System.currentTimeMillis())), df.format(new Date(System.currentTimeMillis()))));

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        isImgOK = false;
        netWorkStateReceiver.setBRInteractionListener(new NetWorkStateReceiver.BRInteraction() {
            @Override
            public void setText(final String uri) {
                Log.w(TAG, "setText: " + isImgOK);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setVisibility(View.VISIBLE);
                        if (!isImgOK)
                            showImg(uri, imageView);

                        isImgOK = true;
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
    }

    private void refreshIsOKForAddHabit(){
        isOKForAddHabit = isOKForAddHabit();
    }

    private void initGlobalVariable(){
        isLongClick = 1;
        longClickedPosition = -1;
        longClickedHabitIc = "";
        refreshIsOKForAddHabit();
        initDateFormat();
    }

    private void initDateFormat(){
        df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        df_time = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Time).toString());
    }

    private boolean isOKForAddHabit(){
        List<summarylist> summarylists = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
        if (summarylists.size() == 0) return true;
        else if (summarylists.size() == 1 && summarylists.get(0).getLastdays() >= 14) return true;
        else return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (isLongClick){
            case 1:
                toolbar.setBackgroundColor(Color.rgb(128, 128, 128));
                menu.findItem(R.id.delete_item).setVisible(false);
                menu.findItem(R.id.back).setVisible(false);
                //menu.findItem(R.id.calendar).setVisible(false);
                break;
            case 0:
                toolbar.setBackgroundColor(Color.rgb(233, 150, 122));
                menu.findItem(R.id.delete_item).setVisible(true);
                menu.findItem(R.id.back).setVisible(true);
                //menu.findItem(R.id.calendar).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.options, menu);
        getMenuInflater().inflate(R.menu.maintoolbar, menu);
        return true;
    }



    private void deleteHabit(String ic){
        LitePal.deleteAll(summarylist.class, "ic = ?", ic);
        LitePal.deleteAll(dailylist.class, "ic = ?", ic);
        LitePal.deleteAll(impulselist.class, "ic = ?", ic);
    }



    private void addImpulse(int status, String ic){
        long num = LitePal.where("ic = ?", ic).find(impulselist.class).size();
        impulselist impulselist = new impulselist();
        long time = System.currentTimeMillis();
        List<summarylist> summarylists = LitePal.where("ic = ?", ic).find(summarylist.class);
        if (summarylists.size() != 0) {
            impulselist.setNum(num);
            impulselist.setIc(ic);
            impulselist.setLastdays(summarylists.get(0).getLastdays());
            impulselist.setTime(df_time.format(time));
            impulselist.setStatus(status);
            impulselist.save();
        }
    }

    private void brokeHabit(String ic){
        addImpulse(EnumStatus.IMPULSE_BROKEN_STATUS, ic);
        summarylist summarylist = new summarylist();
        long time_long = System.currentTimeMillis();
        String date = df.format(time_long);
        List<summarylist> summarylists = LitePal.where("ic = ?", ic).find(summarylist.class);
        if (summarylists.size() != 0) {
            summarylist.setEnddate(date);
            summarylist.setStatus(EnumStatus.BROKEN_STATUS);
            summarylist.setLastdays(summarylists.get(0).getLastdays());
            summarylist.setBreakdate(date);
            summarylist.setBreaktime(df_time.format(time_long));
            summarylist.updateAll("ic = ?", ic);
        }
    }

    private void refreshForeService(){
        Intent startIntent = new Intent(DisplayAllHabitActivity.this, MyService.class);
        startService(startIntent);
    }

    private void refreshStandardService(){
        Intent startIntent = new Intent(DisplayAllHabitActivity.this, MyService.class);
        startService(startIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case  R.id.delete_item:
                if (isLongClick == 0) {
                    AlertDialog.Builder q = new AlertDialog.Builder(DisplayAllHabitActivity.this);
                    q.setPositiveButton(getResources().getText(R.string.DeleteHabit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteHabit(longClickedHabitIc);
                            //removeWidgetForNoInProgressActivity();
                            //resetInterface();
                            //DisplayAllHabitActivity.this.finish();
                            initGlobalVariable();
                            refreshRecycler();
                            initFloatingButton();
                            loadImage();
                            invalidateOptionsMenu();
                            cancelNotification();
                            refreshForeService();
                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.BrokeHabit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            brokeHabit(longClickedHabitIc);
                            //removeWidgetForNoInProgressActivity();
                            //resetInterface();
                            //DisplayAllHabitActivity.this.finish();
                            initGlobalVariable();
                            refreshRecycler();
                            initFloatingButton();
                            loadImage();
                            invalidateOptionsMenu();
                            cancelNotification();
                            refreshForeService();
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q2));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();
                }
                break;
            case  R.id.back:
                if (isLongClick == 0) {
                    isLongClick = 1;
                    longClickedHabitIc = "";
                    longClickedPosition = -1;
                    refreshRecycler();
                    invalidateOptionsMenu();
                }
                break;
            case  R.id.calendar:
                Intent intent = new Intent(this, CalendarActivity.class);
                /*if (isLongClick == 0) {
                    intent.putExtra("ic", longClickedHabitIc);
                }*/
                intent.putExtra("ic", longClickedHabitIc);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void cancelNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int num = manager.getActiveNotifications().length;
        for (int i = 0; i < num; ++i){
            manager.cancel(i + 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(mReceiver);
    }

    private void initFloatingButton(){
        final FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        Log.w(TAG, "initFloatingButton: " + isOKForAddHabit);
        if (!isOKForAddHabit) {
            add.setVisibility(View.GONE);
        }else add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindowForAddList();
            }
        });
    }

    private void newHabit(String name, boolean timeStatus){
        long num = LitePal.findAll(summarylist.class).size();
        summarylist summarylist = new summarylist();
        summarylist.setNum(num);
        summarylist.setName(name);
        long time_long = System.currentTimeMillis();
        String today = df.format(time_long);
        String time = df_time.format(time_long);
        if (timeStatus == EnumStatus.CONTAINTODAY){
            summarylist.setOridate(today);
            summarylist.setToDefault("lastdays");
        }else {
            summarylist.setOridate(DataUtil.datePlus(today, 1));
            summarylist.setLastdays(-1);
        }
        summarylist.setStatus(EnumStatus.INPROGRESS_STATUS);
        summarylist.setListedday(today);
        summarylist.setListedtime(time);
        summarylist.setIc(name + Long.toString(time_long));
        summarylist.save();
    }

    void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                EnumStatus.READ_EXTERNAL_STORAGE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                EnumStatus.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            EnumStatus.READ_EXTERNAL_STORAGE, EnumStatus.WRITE_EXTERNAL_STORAGE},
                    EnumStatus.PERMISSION_CODE
            );

            return;
        }else {

            newPhotoFolder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case EnumStatus.PERMISSION_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, DisplayAllHabitActivity.this.getResources().getText(R.string.PermissionError), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }else {

                            newPhotoFolder();
                        }
                    }
                }
                break;
            default:
        }
    }


    private void newPhotoFolder(){
        File appPhotoRootPathFile = new File(EnumStatus.appPhotoRootPath);
        if (!appPhotoRootPathFile.exists() || !appPhotoRootPathFile.isDirectory()) appPhotoRootPathFile.mkdirs();
    }

    private void showPopueWindowForAddList() {
        final View popView = View.inflate(this, R.layout.popupwindow_addlist, null);

        final EditText addName = (EditText) popView.findViewById(R.id.add_name);
        RadioGroup radioGroup = (RadioGroup) popView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.containtoday:
                        timeStatus = EnumStatus.CONTAINTODAY;
                        break;
                    case R.id.startomorrow:
                        timeStatus = EnumStatus.STARTOMMOROW;
                        break;
                }
            }
        });
        //获取屏幕宽高
        final int weight = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels - 60;

        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow OnTouchListener
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);

        Button confirm = (Button) popView.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newHabit(addName.getText().toString(), timeStatus);
                //initInProgressActivity();
                refreshRecycler();
                refreshIsOKForAddHabit();
                initFloatingButton();
                cancelNotification();
                refreshForeService();
                popupWindow.dismiss();
            }
        });
        Button cancel = (Button) popView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
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
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
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
            Log.w(TAG, "showPopueWindowForPhoto: " + degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        Glide.with(this)
                .load(bitmap)
                .apply(options)
                .into(imageView);
    }

    public void loadImage() {
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setVisibility(View.VISIBLE);
        //final String url = "http://7xr4g8.com1.z0.glb.clouddn.com/" + Long.toString(photoNum);
        //final String url = "https://source.unsplash.com/random";
        //final String url1 = "http://120.79.77.39:822/1.jpg";
        String date = DataUtil.getYMDString();
        Log.w(TAG, "loadImage: " + date);
        final String url = EnumStatus.serverRootPath + EnumStatus.serverPhotosPath + date + ".jpg";
        final String uri = EnumStatus.appPhotoRootPath + "/" + date + ".jpg";
        File file = new File(uri);
        if (!file.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadPicture(url, uri);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImg(uri, imageView);
                        }
                    });
                }
            }).start();
        }else {
            showImg(uri, imageView);
        }
    }

    String apkName;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1221) {
                Log.w(TAG, "handleMessage: " + "1221");
                refreshRecycler();
                refreshIsOKForAddHabit();
                initFloatingButton();
            } else if (msg.what == 2112) {
                Log.w(TAG, "handleMessage: " + "2112");
            }else if (msg.what == 1234) {
                Bundle bundle = msg.getData();
                final String version = bundle.getString("version");
                Log.w(TAG, "handleMessage: " + version + "; " + DataUtil.getVerName(DisplayAllHabitActivity.this));
                if (!version.equals(DataUtil.getVerName(DisplayAllHabitActivity.this))){
                    AlertDialog.Builder q = new AlertDialog.Builder(DisplayAllHabitActivity.this);
                    q.setPositiveButton(getResources().getText(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.Confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            apkName = "21habitV" + version + ".apk";
                            String apkUrl = EnumStatus.serverRootPath + EnumStatus.serverApksPath + apkName;
                            //使用DownLoadManager来下载
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
                            //将文件下载到自己的Download文件夹下,必须是External的
                            //这是DownloadManager的限制
                            //File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName);
                            apkStoredPath = EnumStatus.appApkRootPath + apkName;
                            File file = new File(apkStoredPath);
                            request.setDestinationUri(Uri.fromFile(file));
                            //添加请求 开始下载
                            long downloadId = downloadManager.enqueue(request);
                            //installNormal(DisplayAllHabitActivity.this, apkUrl);
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q3));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();

                }
            }

        }
    };
    private String apkStoredPath;

    //下载完成的广播
    private class DownloadFinishReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //下载完成的广播接收者
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            File file = new File(apkStoredPath);
            //Uri apkUri = downloadManager.getUriForDownloadedFile(completeDownloadId);
            //File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName);
            Log.w(TAG, "onReceive: " + Uri.fromFile(file).toString() + "; " + Uri.fromFile(file).getPath());
            installNormal(DisplayAllHabitActivity.this, file.getPath());
        }
    }

    //普通安装
    private void installNormal(Context context, String apkUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(getPackageManager().canRequestPackageInstalls()){

                //已经同意权限在这里执行安装应用的代码
                //版本在7.0以上是不能直接通过uri访问的
                File file = (new File(apkUrl));
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                //Uri apkUri = Uri.parse("file://" + apkUrl);
                //Log.w(TAG, "installNormal: " + apkUri.getPath());
                //
                /*if (!apkUri.isAbsolute())
                DataUtil.getRealPathFromUriForAudio(context, apkUri);*/
                //Log.w(TAG, "installNormal: " + DataUtil.getRealPathFromUriForAudio(context, Uri.parse(apkUrl)));
                Uri apkUri = FileProvider.getUriForFile(
                        DisplayAllHabitActivity.this
                        , "android.lzy.a21habit.fileprovider"
                        , file);
                Log.w(TAG, "installNormal: " + apkUri.getPath());
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                context.startActivity(intent);


            }else{



                //没有允许  需要去申请权限，由于这个权限不是运行时权限，所有需要用户手

                //动去开启权限，可以给用户一个弹窗 提示用户去权限列表开启权限     开启设

                //置的代码  8.0新的API

                Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);

                startActivityForResult(intent1, 1);

            }

        }else {
            //版本在7.0以上是不能直接通过uri访问的
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = (new File(apkUrl));
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                //Uri apkUri = Uri.parse("file://" + apkUrl);
                //Log.w(TAG, "installNormal: " + apkUri.getPath());
                //
                /*if (!apkUri.isAbsolute())
                DataUtil.getRealPathFromUriForAudio(context, apkUri);*/
                //Log.w(TAG, "installNormal: " + DataUtil.getRealPathFromUriForAudio(context, Uri.parse(apkUrl)));
                Uri apkUri = FileProvider.getUriForFile(
                        DisplayAllHabitActivity.this
                        , "android.lzy.a21habit.fileprovider"
                        , file);
                Log.w(TAG, "installNormal: " + apkUri.getPath());
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(apkUrl)),
                        "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        }


    }

    private int isLongClick;
    private String longClickedHabitIc;
    private int longClickedPosition;

    //重新刷新Recycler
    public void refreshRecycler(){
        summarylist = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(android.lzy.a21habit.summarylist.class);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_habit);
        GridLayoutManager layoutManager = null;
        if (summarylist.size() == 2){
            layoutManager = new GridLayoutManager(this,2);
        }else {
            layoutManager = new GridLayoutManager(this,1);
        }
        recyclerView.setLayoutManager(layoutManager);
        if (summarylist.size() == 0){
            summarylist summarylist1 = new summarylist();
            summarylist1.setName(this.getResources().getString(R.string.NoPlan));
            summarylist.add(summarylist1);
        }
        final HabitAdapter adapter = new HabitAdapter(summarylist, handler);
        adapter.setOnItemLongClickListener(new HabitAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String ic, int position) {
                isLongClick = 0;
                longClickedHabitIc = ic;
                longClickedPosition = position;
                invalidateOptionsMenu();
            }
        });
        adapter.setOnItemClickListener(new HabitAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String ic, int position) {
                HabitAdapter.ViewHolder holder = new HabitAdapter.ViewHolder(view);
                summarylist habit = summarylist.get(position);
                /*if (!habit.getName().equals(DisplayAllHabitActivity.this.getResources().getString(R.string.NoPlan))){
                    Intent intent = new Intent(DisplayAllHabitActivity.this, MainActivity.class);
                    intent.putExtra("ic", ic);
                    startActivity(intent);
                }*/
                if (isLongClick == 0 && !longClickedHabitIc.equals(ic)){
                    adapter.notifyItemChanged(longClickedPosition);
                    holder.cardView.setCardBackgroundColor(Color.RED);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
