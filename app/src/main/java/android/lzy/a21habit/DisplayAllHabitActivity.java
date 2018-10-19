package android.lzy.a21habit;

import android.app.DownloadManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayAllHabitActivity extends AppCompatActivity {
    private static final String TAG = "DisplayAllHabitActivity";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;
    List<summarylist> summarylist;

    boolean isOKForAddHabit;

    ScreenBootReceiver receiver;

    boolean timeStatus;

    SimpleDateFormat df;

    SimpleDateFormat df_time;

    Toolbar toolbar;

    public static final String rootPath = Environment.getExternalStorageDirectory().toString();

    public static final String serverRootPath = "http://120.79.77.39:822/";

    public static final String appPhotoRootPath = rootPath + "/21Days/Photos";

    public static final String appApkRootPath = rootPath + "/21Days/Apk/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_habit);
        Log.w(TAG, "onCreate: " + DataUtil.getVersionCode(this) + "; " + DataUtil.getVerName(this));
        sendRequestWithOkHttp();

        isLongClick = 1;
        longClickedPosition = -1;
        longClickedHabitIc = "";
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://120.79.77.39:822/VersionInfo.txt")
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

    @Override
    protected void onResume() {
        super.onResume();

        initGlobalVariable();
        refreshRecycler();
        initFloatingButton();
        loadImage();
        invalidateOptionsMenu();
        Intent startIntent = new Intent(this, MyService.class);
        startService(startIntent);

        Log.w(TAG, "onCreate: " + DataUtil.daysBetween(df.format(new Date(System.currentTimeMillis())), df.format(new Date(System.currentTimeMillis()))));
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
        switch (isLongClick){
            case 1:
                toolbar.setBackgroundColor(Color.rgb(128, 128, 128));
                menu.findItem(R.id.delete_item).setVisible(false);
                menu.findItem(R.id.back).setVisible(false);
                menu.findItem(R.id.calendar).setVisible(false);
                break;
            case 0:
                toolbar.setBackgroundColor(Color.rgb(233, 150, 122));
                menu.findItem(R.id.delete_item).setVisible(true);
                menu.findItem(R.id.back).setVisible(true);
                menu.findItem(R.id.calendar).setVisible(true);
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
                            Intent startIntent = new Intent(DisplayAllHabitActivity.this, MyService.class);
                            startService(startIntent);
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
                            Intent startIntent = new Intent(DisplayAllHabitActivity.this, MyService.class);
                            startService(startIntent);
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
                if (isLongClick == 0) {
                    Intent intent = new Intent(this, CalendarActivity.class);
                    intent.putExtra("ic", longClickedHabitIc);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
                READ_EXTERNAL_STORAGE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE
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
            case PERMISSION_CODE:
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
        File appPhotoRootPathFile = new File(appPhotoRootPath);
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
        final String url = "http://120.79.77.39:822/" + date + ".jpg";
        final String uri = appPhotoRootPath + "/" + date + ".jpg";
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
                            String apkName = "21habitV" + version + ".apk";
                            String apkUrl = serverRootPath + apkName;
                            installNormal(DisplayAllHabitActivity.this, apkUrl);
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q3));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();

                }
            }

        }
    };

    //普通安装
    private void installNormal(Context context, String apkUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            //File file = (new File(apkUrl));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = Uri.parse(apkUrl);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkUrl)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
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
