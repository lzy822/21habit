package android.lzy.a21habit;

import org.litepal.crud.LitePalSupport;

import java.sql.Time;

public class impulselist extends LitePalSupport {
    private long num;
    private String ic;
    private int lastdays;
    private String time;
    private int status;
    private String description;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public int getLastdays() {
        return lastdays;
    }

    public void setLastdays(int lastdays) {
        this.lastdays = lastdays;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
