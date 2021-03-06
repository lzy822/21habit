package android.lzy.a21habit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.litepal.LitePal;

import java.util.List;

public class MyService extends Service {
    private static final String TAG = "MyService";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initForeServiceForNull(){
        //int lastdays = summarylist.get(i).getLastdays() + 1;
        //CharSequence name = summarylist.get(i).getName();
        CharSequence name = this.getResources().getText(R.string.Notification);
        //CharSequence name1 = this.getResources().getText(R.string.YouHavePersist).toString() + lastdays + this.getResources().getText(R.string.Day);
        CharSequence name1 = this.getResources().getText(R.string.NoPlan);
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            String description = "渠道描述1";
            String channelId = "channelId1";//渠道id
            int importance = NotificationManager.IMPORTANCE_LOW;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(channelId, this.getResources().getText(R.string.app_name), importance);
            mChannel.setDescription(description);//渠道描述
            mChannel.enableLights(true);//是否显示通知指示灯
            mChannel.enableVibration(true);//是否振动

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);//创建通知渠道
            //第二个参数与channelId对应
            Notification.Builder builder = new Notification.Builder(this, channelId);
//icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setNumber(0); //久按桌面图标时允许的此条通知的数量
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent ClickPending = PendingIntent.getActivity(this, 0, intent1, 0);
            builder.setContentIntent(ClickPending);
            //notificationManager.notify(i + 1, builder.build());
            startForeground(1, builder.build());
        } else {
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .build();
            //manager.notify(1, notification);
            startForeground(1, notification);
        }
    }

    private void initForeService(summarylist summarylist){
        int lastdays = summarylist.getLastdays() + 1;
        CharSequence name = summarylist.getName();
        CharSequence name1 = this.getResources().getText(R.string.YouHavePersist).toString() + lastdays + this.getResources().getText(R.string.Day);
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            String description = "渠道描述1";
            String channelId = "channelId1";//渠道id
            int importance = NotificationManager.IMPORTANCE_LOW;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(channelId, this.getResources().getText(R.string.app_name), importance);
            mChannel.setDescription(description);//渠道描述
            mChannel.enableLights(true);//是否显示通知指示灯
            mChannel.enableVibration(true);//是否振动

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);//创建通知渠道
            //第二个参数与channelId对应
            Notification.Builder builder = new Notification.Builder(this, channelId);
//icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setNumber(lastdays); //久按桌面图标时允许的此条通知的数量
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent ClickPending = PendingIntent.getActivity(this, 0, intent1, 0);
            builder.setContentIntent(ClickPending);
            //notificationManager.notify(i + 1, builder.build());
            startForeground(1, builder.build());
        } else {
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .build();
            //manager.notify(1, notification);
            startForeground(1, notification);
        }
    }

    private void initNotification(summarylist summarylist){
        int lastdays = summarylist.getLastdays() + 1;
        CharSequence name = summarylist.getName();
        CharSequence name1 = this.getResources().getText(R.string.YouHavePersist).toString() + lastdays + this.getResources().getText(R.string.Day);
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            String description = "渠道描述1";
            String channelId = "channelId1";//渠道id
            int importance = NotificationManager.IMPORTANCE_LOW;//重要性级别
            NotificationChannel mChannel = new NotificationChannel(channelId, this.getResources().getText(R.string.app_name), importance);
            mChannel.setDescription(description);//渠道描述
            mChannel.enableLights(true);//是否显示通知指示灯
            mChannel.enableVibration(true);//是否振动

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);//创建通知渠道
            //第二个参数与channelId对应
            Notification.Builder builder = new Notification.Builder(this, channelId);
//icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setNumber(lastdays); //久按桌面图标时允许的此条通知的数量
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent ClickPending = PendingIntent.getActivity(this, 0, intent1, 0);
            builder.setContentIntent(ClickPending);
            notificationManager.notify(2, builder.build());
            //startForeground(1, builder.build());
        } else {
            Intent intent1 = new Intent(this, DisplayAllHabitActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(name)
                    .setContentText(name1)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .build();
            manager.notify(2, notification);
            //startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        List<summarylist> summarylist = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(android.lzy.a21habit.summarylist.class);
        if (summarylist.size() > 0) {
            initForeService(summarylist.get(0));
            if (summarylist.size() == 2) {
                initNotification(summarylist.get(1));
            }
        }else {
            initForeServiceForNull();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //stopForeground(true);
    }
}
