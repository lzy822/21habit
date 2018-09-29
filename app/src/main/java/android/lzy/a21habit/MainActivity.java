package android.lzy.a21habit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
import com.github.clans.fab.FloatingActionButton;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    public static final String rootPath = Environment.getExternalStorageDirectory().toString();

    public static final String appPhotoRootPath = rootPath + "/21Days/Photos";

    SimpleDateFormat df;

    SimpleDateFormat df_time;

    boolean isInProgress;

    long lastdays;

    String ic;

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

        df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        df_time = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Time).toString());
        //LitePal.deleteAll(summarylist.class);
        //loadImage();
        pickFile();
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
            initRecord();
            isInProgress = isInProgressActivity();
            initFloatingButton(isInProgress);
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
                            newPhotoFolder();
                            initRecord();
                            isInProgress = isInProgressActivity();
                            initFloatingButton(isInProgress);
                        }
                    }
                }
                break;
            default:
        }
    }

    private void initFloatingButton(final boolean isInProgress){
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
                            removeWidget();
                            initFloatingButton(isInProgressActivity());
                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.BrokeHabit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            brokeHabit();
                            removeWidget();
                            initFloatingButton(isInProgressActivity());
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
                        }
                    });
                    q.setNegativeButton(getResources().getText(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addImpulse(EnumStatus.IMPULSE_INPROGRESS_STATUS);
                        }
                    });
                    q.setMessage(getResources().getText(R.string.Q1));
                    q.setTitle(getResources().getText(R.string.Warning));
                    q.show();
                }else{
                    showPopueWindowForAddList();
                }
            }
        });
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

    private void newPhotoFolder(){
        File appPhotoRootPathFile = new File(appPhotoRootPath);
        if (!appPhotoRootPathFile.exists() || !appPhotoRootPathFile.isDirectory()) appPhotoRootPathFile.mkdirs();
    }

    private void doSpecificOperation(){

    }

    private void showImg(String uri, ImageView imageView){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .override(width,height / 3)
                .dontAnimate()
                .into(imageView);
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

    private boolean isInProgressActivity(){
        List<summarylist> summarylists = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
        if (summarylists.size() == 0) return false;
        else {
            initInProgressActivity();
            return true;
        }
    }

    private void initInProgressActivity(){
        List<summarylist> summarylists = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
        ic = summarylists.get(0).getIc();
        String date = df.format(System.currentTimeMillis());
        long days = DataUtil.daysBetween(summarylists.get(0).getOridate(), date);
        summarylist summarylist = new summarylist();
        summarylist.setLastdays((int)days);
        summarylist.updateAll("ic = ?", ic);
        if (days > 21){
            summarylist summarylist1 = new summarylist();
            summarylist1.setEnddate(date);
            summarylist1.setStatus(EnumStatus.FINISH_STATUS);
            summarylist1.updateAll("ic = ?", ic);
            removeWidget();
            initFloatingButton(isInProgressActivity());
        }else{
            lastdays = days;
            initLinearDays(days);
            initDescription(days);
            initName(summarylists.get(0).getName());
            loadImage(summarylists.get(0).getOridate());
        }

    }

    private void removeWidget(){
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setVisibility(View.GONE);
        LinearLayout linear_days = (LinearLayout) findViewById(R.id.linear_days);
        linear_days.setVisibility(View.GONE);
        TextView description = (TextView) findViewById(R.id.description);
        description.setVisibility(View.GONE);
        TextView nameTextview = (TextView) findViewById(R.id.name);
        nameTextview.setText(getResources().getText(R.string.NoPlan));
    }

    public void loadImage(String originDate) {
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setVisibility(View.VISIBLE);
        long photoNum = DataUtil.daysBetween(originDate, df.format(System.currentTimeMillis()));
        final String uri = appPhotoRootPath + "/" + Long.toString(photoNum);
        final String url = "http://7xr4g8.com1.z0.glb.clouddn.com/" + Long.toString(photoNum);
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

    boolean timeStatus;
    private void showPopueWindowForAddList() {
        final View popView = View.inflate(this, R.layout.popupwindow_addlist, null);

        final EditText addName = (EditText) popView.findViewById(R.id.add_name);
        RadioGroup radioGroup = (RadioGroup) popView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.containtoday:
                        timeStatus = false;
                        break;
                    case R.id.startomorrow:
                        timeStatus = true;
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
                initInProgressActivity();
                isInProgress = true;
                initFloatingButton(isInProgress);
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
        if (!timeStatus){
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

}
