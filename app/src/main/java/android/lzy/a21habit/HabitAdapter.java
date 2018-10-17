package android.lzy.a21habit;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by 54286 on 2018/3/6.
 */

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {
    private Context mContext;

    Handler handler;

    private List<summarylist> habitList;

    private OnRecyclerItemLongListener mOnItemLong;

    private OnRecyclerItemClickListener mOnItemClick;
    SimpleDateFormat df;
    SimpleDateFormat df_time;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        TextView lastDays;
        TextView habitName;
        LinearLayout linearLayout_Days;
        LinearLayout linearLayout_Confirm;
        ImageButton dontConfirmBT;
        ImageButton ConfirmBT;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            lastDays = (TextView) view.findViewById(R.id.day);
            habitName = (TextView) view.findViewById(R.id.name_txt);
            linearLayout_Days = (LinearLayout) view.findViewById(R.id.linear_days_cardview);
            linearLayout_Confirm = (LinearLayout) view.findViewById(R.id.linear_confirm);
            dontConfirmBT = (ImageButton) view.findViewById(R.id.dontFinishHabit);
            ConfirmBT = (ImageButton) view.findViewById(R.id.FinishHabit);


        }
    }
    public HabitAdapter(List<summarylist> habitList, Handler handler) {
        this.habitList = habitList;
        df = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Date).toString());
        df_time = new SimpleDateFormat(MyApplication.getContext().getResources().getText(R.string.Time).toString());
        this.handler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.habit_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                summarylist habit = habitList.get(position);
                mOnItemClick.onItemClick(v, habit.getIc(), position);
                /*
                Intent intent = new Intent(mContext, MainInterface.class);
                intent.putExtra("num", map.getM_num());
                mContext.startActivity(intent);*/
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    summarylist habit = habitList.get(position);
                    mOnItemLong.onItemLongClick(v, habit.getIc(), position);
                    holder.cardView.setCardBackgroundColor(Color.RED);
               }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        //notifyItemChanged(position);
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        }else {
            notifyItemChanged(position);
            //Toast.makeText(mContext, Integer.toString(position), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "find here" + payloads.toString() );
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final summarylist habit = habitList.get(position);
        holder.cardView.setCardBackgroundColor(Color.WHITE);

        String name = habit.getName();
        if (!name.equals(mContext.getResources().getString(R.string.NoPlan))){


            final long days_listed = habit.getLastdays();

            final String date = df.format(System.currentTimeMillis());
            long days_reality = DataUtil.daysBetween(date, habit.getOridate());
            Log.w(TAG, "onBindViewHolder: " + days_reality + ";" + days_listed + ";" + habit.getOridate());


            if (days_listed != days_reality){
                holder.linearLayout_Confirm.setVisibility(View.VISIBLE);
                holder.dontConfirmBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        brokeHabit(habit.getIc(), days_listed);
                        holder.linearLayout_Confirm.setVisibility(View.GONE);

                        habitList = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
                        if (habitList.size() == 0){
                            summarylist summarylist1 = new summarylist();
                            summarylist1.setName(mContext.getResources().getString(R.string.NoPlan));
                            habitList.add(summarylist1);
                        }
                        //notifyItemChanged(position);

                        Message msg = new Message();
                        msg.what = 1221;
                        handler.sendMessage(msg);

                    }
                });
                holder.ConfirmBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.lastDays.setText(Integer.toString(habit.getLastdays() + 1));
                        long days = DataUtil.daysBetween(date, habit.getOridate());
                        dateTransformation(days, habit.getIc());
                        refreshLastDays(holder, days_listed + 1, habit);
                        //notifyItemChanged(position);
                    }
                });
            }else {
                refreshLastDays(holder, days_listed, habit);
            }


            if (days_listed >= 21){
                finishHabit(date, habit.getIc());
                holder.linearLayout_Confirm.setVisibility(View.GONE);

                habitList = LitePal.where("status = ?", Integer.toString(EnumStatus.INPROGRESS_STATUS)).find(summarylist.class);
                if (habitList.size() == 0){
                    summarylist summarylist1 = new summarylist();
                    summarylist1.setName(mContext.getResources().getString(R.string.NoPlan));
                    habitList.add(summarylist1);
                }
                notifyItemChanged(position);
            }


        }else {
            holder.linearLayout_Days.setVisibility(View.GONE);
            holder.linearLayout_Confirm.setVisibility(View.GONE);
        }


        holder.habitName.setText(habit.getName());
    }

    private void refreshLastDays(final ViewHolder holder, final long days_listed, final summarylist habit){
        holder.linearLayout_Confirm.setVisibility(View.GONE);
        holder.linearLayout_Days.setVisibility(View.VISIBLE);
        long days_show = days_listed + 1;
        if (days_show > 0 && days_show <= 7){
            holder.lastDays.setTextColor(Color.RED);
            holder.habitName.setTextColor(Color.RED);
        }else if (days_show > 7 && days_show <= 14){
            holder.lastDays.setTextColor(Color.rgb(233, 150, 122));
            holder.habitName.setTextColor(Color.rgb(233, 150, 122));
        }else if (days_show > 14 && days_show <= 22){
            holder.lastDays.setTextColor(Color.GREEN);
            holder.habitName.setTextColor(Color.GREEN);
        }
        holder.lastDays.setText(Long.toString(days_show));
        holder.habitName.setText(habit.getName());
    }

    private void dateTransformation(long days, String ic){
        summarylist summarylist = new summarylist();
        summarylist.setLastdays((int)days);
        summarylist.updateAll("ic = ?", ic);
    }

    private void finishHabit(String date, String ic){
        summarylist summarylist1 = new summarylist();
        summarylist1.setEnddate(date);
        summarylist1.setStatus(EnumStatus.FINISH_STATUS);
        summarylist1.updateAll("ic = ?", ic);
    }

    private void addImpulse(int status, String ic, long lastdays){
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

    private void brokeHabit(String ic, long lastdays){
        addImpulse(EnumStatus.IMPULSE_BROKEN_STATUS, ic, lastdays);
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

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String ic, int position);
    }

    public void setOnItemLongClickListener(OnRecyclerItemLongListener listener){
        this.mOnItemLong =  listener;
    }

    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String ic, int position);
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}
