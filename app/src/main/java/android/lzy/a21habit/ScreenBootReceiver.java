package android.lzy.a21habit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ScreenBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            List<summarylist> summarylist = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(android.lzy.a21habit.summarylist.class);
            for (int i = 0; i < summarylist.size(); i++){
                int lastdays = summarylist.get(i).getLastdays() + 1;
                CharSequence name = summarylist.get(i).getName();
                CharSequence name1 = context.getResources().getText(R.string.YouHavePersist).toString() + lastdays + context.getResources().getText(R.string.Day);
                //Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //创建通知渠道
                    String description = "渠道描述1";
                    String channelId="channelId1";//渠道id
                    int importance = NotificationManager.IMPORTANCE_LOW;//重要性级别
                    NotificationChannel mChannel = new NotificationChannel(channelId, context.getResources().getText(R.string.app_name), importance);
                    mChannel.setDescription(description);//渠道描述
                    mChannel.enableLights(true);//是否显示通知指示灯
                    mChannel.enableVibration(true);//是否振动

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                            NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(mChannel);//创建通知渠道
                    //第二个参数与channelId对应
                    Notification.Builder builder = new Notification.Builder(context,channelId);
//icon title text必须包含，不然影响桌面图标小红点的展示
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(name)
                            .setContentText(name1)
                            .setNumber(lastdays); //久按桌面图标时允许的此条通知的数量

                    Intent intent1=new Intent(context, MainActivity.class);
                    PendingIntent ClickPending = PendingIntent.getActivity(context, 0, intent1, 0);
                    builder.setContentIntent(ClickPending);

                    notificationManager.notify(1,builder.build());
                }else {
                    Intent intent1 = new Intent(context, MainActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, 0);
                    NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(context)
                            .setContentTitle(name)
                            .setContentText(name1)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pi)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                            .build();
                    manager.notify(1, notification);
                }
            }
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
