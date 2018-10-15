package android.lzy.a21habit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
import java.util.List;

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

    public static final String rootPath = Environment.getExternalStorageDirectory().toString();

    public static final String appPhotoRootPath = rootPath + "/21Days/Photos";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_habit);
        pickFile();


        //注册亮屏广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        receiver = new ScreenBootReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGlobalVariable();
        refreshRecycler();
        initFloatingButton();
        loadImage();
        Intent startIntent = new Intent(this, MyService.class);
        startService(startIntent);
    }

    private void refreshIsOKForAddHabit(){
        isOKForAddHabit = isOKForAddHabit();
    }

    private void initGlobalVariable(){
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
        else if (summarylists.size() == 1 && summarylists.get(0).getLastdays() > 14) return true;
        else return false;
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
        }else {
            summarylist.setOridate(DataUtil.datePlus(today, 1));
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
        HabitAdapter adapter = new HabitAdapter(summarylist);
        adapter.setOnItemLongClickListener(new HabitAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String ic, int position) {

            }
        });
        adapter.setOnItemClickListener(new HabitAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String ic, int position) {
                summarylist habit = summarylist.get(position);
                if (!habit.getName().equals(DisplayAllHabitActivity.this.getResources().getString(R.string.NoPlan))){
                    Intent intent = new Intent(DisplayAllHabitActivity.this, MainActivity.class);
                    intent.putExtra("ic", ic);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
