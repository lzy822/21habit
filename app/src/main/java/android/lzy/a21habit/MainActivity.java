package android.lzy.a21habit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
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
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;

    private static final String TAG = "MainActivity";

    SimpleDateFormat df;

    SimpleDateFormat df_time;

    boolean isInProgress;

    long lastdays;

    String ic;

    String name;

    boolean timeStatus;

    //注册亮屏广播
    IntentFilter filter;

    ScreenBootReceiver receiver;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.back).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*SimpleDateFormat df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        try {
            Log.w(TAG, "onCreate: " + DataUtil.daysBetween(df.parse("2018年9月28日"), df.parse("2018年9月30日")));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        initGlobalVariable();
        pickFile();
        doSpecificOperation();
    }

    private void initGlobalVariable(){
        initDateFormat();
    }

    private void initDateFormat(){
        df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        df_time = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Time).toString());
    }

    private void doSpecificOperation(){
        /*
        增加一条记录
         */
        /*LitePal.deleteAll(summarylist.class);
        LitePal.deleteAll(dailylist.class);
        LitePal.deleteAll(impulselist.class);
        String name = "不看论坛";
        long time = System.currentTimeMillis();
        int time_before = -7;
        summarylist summarylist = new summarylist();
        summarylist.setListedday(DataUtil.datePlus(df.format(time), time_before));
        summarylist.setListedtime(df_time.format(time));
        summarylist.setNum(0);
        summarylist.setName(name);
        summarylist.setIc(name + time);
        summarylist.setOridate(DataUtil.datePlus(df.format(time), time_before));
        summarylist.setLastdays(7);
        summarylist.setStatus(EnumStatus.INPROGRESS_STATUS);
        summarylist.save();*/
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
            //newPhotoFolder();
            initRecord();
            resetInterface();
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
                            Toast.makeText(this, MainActivity.this.getResources().getText(R.string.PermissionError), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }else {
                            //newPhotoFolder();
                            initRecord();
                            resetInterface();
                        }
                    }
                }
                break;
            default:
        }
    }

    private void initFloatingButton(){
        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete);
        if (isInProgress){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder q = new AlertDialog.Builder(MainActivity.this);
                    q.setPositiveButton(getResources().getText(R.string.DeleteHabit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteHabit();
                            //removeWidgetForNoInProgressActivity();
                            //resetInterface();
                            MainActivity.this.finish();
                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.BrokeHabit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            brokeHabit();
                            //removeWidgetForNoInProgressActivity();
                            //resetInterface();
                            MainActivity.this.finish();
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q2));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();
                }
            });
        }else{
            delete.setVisibility(View.GONE);
        }
        /*
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInProgress){
                    AlertDialog.Builder q = new AlertDialog.Builder(MainActivity.this);
                    q.setPositiveButton(getResources().getText(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addImpulse(EnumStatus.IMPULSE_BROKEN_STATUS);
                            resetInterface();
                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            brokeHabit();
                            //removeWidgetForNoInProgressActivity();
                            resetInterface();
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q1));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();
                }else{
                    showPopueWindowForAddList();
                }
            }
        });*/
    }

    private void addImpulse(int status){
        long num = LitePal.findAll(impulselist.class).size();
        impulselist impulselist = new impulselist();
        long time = System.currentTimeMillis();
        impulselist.setNum(num);
        impulselist.setIc(ic);
        impulselist.setLastdays((int)lastdays);
        impulselist.setTime(df_time.format(time));
        impulselist.setStatus(status);
        impulselist.save();
    }

    private void deleteHabit(){
        LitePal.deleteAll(summarylist.class, "ic = ?", ic);
        LitePal.deleteAll(dailylist.class, "ic = ?", ic);
        LitePal.deleteAll(impulselist.class, "ic = ?", ic);
    }

    private void brokeHabit(){
        addImpulse(EnumStatus.IMPULSE_BROKEN_STATUS);
        summarylist summarylist = new summarylist();
        long time_long = System.currentTimeMillis();
        String date = df.format(time_long);
        summarylist.setEnddate(date);
        summarylist.setStatus(EnumStatus.BROKEN_STATUS);
        summarylist.setLastdays((int)lastdays);
        summarylist.setBreakdate(date);
        summarylist.setBreaktime(df_time.format(time_long));
        summarylist.updateAll("ic = ?", ic);
    }

    private void initRecord(){
        LitePal.getDatabase();
        List<summarylist> summarylists = LitePal.findAll(summarylist.class);
        if (summarylists.size() == 0){
            summarylist summarylist = new summarylist();
            String name = "开始";
            long time = System.currentTimeMillis();
            summarylist.setNum(0);
            summarylist.setName(name);
            summarylist.setOridate(df.format(time));
            summarylist.setIc(name + Long.toString(time));
            summarylist.setStatus(EnumStatus.SYSTEM_STATUS);
            summarylist.save();
        }
    }

    private boolean isInProgressActivity(){
        List<summarylist> summarylists = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
        if (summarylists.size() == 0) return false;
        else return true;
    }

    private void initInProgressActivity(){
        Intent intent = getIntent();
        Log.w(TAG, "initInProgressActivity: " + intent.getStringExtra("ic"));
        String mic = intent.getStringExtra("ic");
        List<summarylist> summarylists = null;
        if (mic != null) {
            summarylists = LitePal.where("ic = ?", intent.getStringExtra("ic")).find(summarylist.class);
        }else {
            summarylists = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
        }
        ic = summarylists.get(0).getIc();
        name = summarylists.get(0).getName();
        String date = df.format(System.currentTimeMillis());
        long days = DataUtil.daysBetween(date, summarylists.get(0).getOridate());
        dateTransformation(days);
        if (days >= 21){
            finishHabit(date);
            //removeWidgetForNoInProgressActivity();
            resetInterface();
        }else{
            lastdays = days;
            initLinearDays(days);
            initDescription(days + 1);
            initName(summarylists.get(0).getName());
            //loadImage(summarylists.get(0).getOridate());
        }

    }

    private void dateTransformation(long days){
        summarylist summarylist = new summarylist();
        summarylist.setLastdays((int)days);
        summarylist.updateAll("ic = ?", ic);
    }

    private void finishHabit(String date){
        summarylist summarylist1 = new summarylist();
        summarylist1.setEnddate(date);
        summarylist1.setStatus(EnumStatus.FINISH_STATUS);
        summarylist1.updateAll("ic = ?", ic);
    }

    private void removeWidgetForNoInProgressActivity(){
        /*ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setVisibility(View.GONE);*/
        LinearLayout linear_days = (LinearLayout) findViewById(R.id.linear_days);
        linear_days.setVisibility(View.GONE);
        TextView description = (TextView) findViewById(R.id.description);
        description.setVisibility(View.GONE);
        TextView nameTextview = (TextView) findViewById(R.id.name);
        nameTextview.setText(getResources().getText(R.string.NoPlan));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case  R.id.back:
                this.finish();
                break;
            case  R.id.calendar:
                Intent intent = new Intent(this, CalendarActivity.class);
                intent.putExtra("ic", ic);
                startActivity(intent);
                break;
            default:
                break;

        }
        return true;
    }

    private void initLinearDays(long days){
        LinearLayout linear_days = (LinearLayout) findViewById(R.id.linear_days);
        linear_days.setVisibility(View.VISIBLE);
        TextView lastdays = (TextView)findViewById(R.id.lastdays);
        lastdays.setText(Long.toString(days + 1));
    }

    private void initDescription(long days){
        TextView description = (TextView) findViewById(R.id.description);
        if (days <= 7){
            description.setText(R.string.LevelOne);
        }else if (days > 7 && days <= 14){
            description.setText(R.string.LevelTwo);
        }else if (days > 14 && days <= 21){
            description.setText(R.string.LevelThree);
        }
        description.setVisibility(View.VISIBLE);
    }

    private void initName(String name){
        TextView nameTextview = (TextView) findViewById(R.id.name);
        nameTextview.setText(name);
        nameTextview.setVisibility(View.VISIBLE);
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
                resetInterface();
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
        }else {
            summarylist.setOridate(DataUtil.datePlus(today, 1));
        }
        summarylist.setStatus(EnumStatus.INPROGRESS_STATUS);
        summarylist.setListedday(today);
        summarylist.setListedtime(time);
        summarylist.setIc(name + Long.toString(time_long));
        summarylist.save();
    }



    private void resetInterface(){
        isInProgress = isInProgressActivity();
        if (isInProgress) {
            initInProgressActivity();
            //castNotification();

        }
        else
            removeWidgetForNoInProgressActivity();
        initFloatingButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void castNotification(){
        CharSequence name1 = getResources().getText(R.string.YouHavePersist).toString() + (lastdays + 1) + getResources().getText(R.string.Day);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            //CharSequence name = "渠道名称1";
            String description = "渠道描述1";
            String channelId="channelId1";//渠道id
            int importance = NotificationManager.IMPORTANCE_LOW;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(description);//渠道描述
            mChannel.enableLights(true);//是否显示通知指示灯
            mChannel.enableVibration(true);//是否振动
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);//创建通知渠道
            //第二个参数与channelId对应
            Notification.Builder builder = new Notification.Builder(this,channelId);
//icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setNumber((int)lastdays + 1); //久按桌面图标时允许的此条通知的数量

            /*Intent intent=new Intent(this, MainActivity.class);
            PendingIntent ClickPending = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentIntent(ClickPending);*/

            notificationManager.notify(1,builder.build());
        }else {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .build();
            manager.notify(1, notification);
        }
    }

}
