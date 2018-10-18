package android.lzy.a21habit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import org.litepal.LitePal;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Intent intent = getIntent();
        String ic = intent.getStringExtra("ic");
        List<summarylist> list = LitePal.where("ic = ?", ic).find(summarylist.class);
        if (isOKForShowMaterialCalendarView(list))
            showMaterialCalendarView(list);
        else
            showMaterialCalendarViewForNull();

    }

    private boolean isOKForShowMaterialCalendarView(List<summarylist> list){
        if (list.size() > 0) return true;
        else return false;
    }

    private void showMaterialCalendarViewForNull(){
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        materialCalendarView.setAllowClickDaysOutsideCurrentMonth(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    private void showMaterialCalendarView(List<summarylist> list){
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        materialCalendarView.setAllowClickDaysOutsideCurrentMonth(true);
        materialCalendarView.setSelectionColor(Color.rgb(153, 204, 51));
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        try {
            long size = DataUtil.daysBetween(dateFormat.format(new Date(System.currentTimeMillis())), list.get(0).getOridate());
            if (size >= 1) {
                for (int k = 0; k < size; k++) {
                    Date date = dateFormat.parse(DataUtil.datePlus(list.get(0).getOridate(), k));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                    CalendarDay calendarDay = CalendarDay.from(localDate);
                    materialCalendarView.setDateSelected(calendarDay, true);
                }
            }
        }catch (ParseException e){
            Log.w(TAG, e.toString());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.calendar).setVisible(false);
        menu.findItem(R.id.delete_item).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
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
            default:
                break;

        }
        return true;
    }
}
