package android.lzy.a21habit;

import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        long difference =  (one.getTime()-two.getTime())/86400000;
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


}
