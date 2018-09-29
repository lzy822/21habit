package android.lzy.a21habit;

import org.litepal.crud.LitePalSupport;

import java.sql.Date;
import java.sql.Time;

public class summarylist extends LitePalSupport {
    private long num;
    private String name;
    private String oridate;
    private String enddate;
    private int status;
    private int lastdays;
    private String listedday;
    private String listedtime;
    private String ic;
    private String breakdate;
    private String breaktime;
    private String breakdate1;
    private String breaktime1;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOridate() {
        return oridate;
    }

    public void setOridate(String oridate) {
        this.oridate = oridate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLastdays() {
        return lastdays;
    }

    public void setLastdays(int lastdays) {
        this.lastdays = lastdays;
    }

    public String getListedday() {
        return listedday;
    }

    public void setListedday(String listedday) {
        this.listedday = listedday;
    }

    public String getListedtime() {
        return listedtime;
    }

    public void setListedtime(String listedtime) {
        this.listedtime = listedtime;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public String getBreakdate() {
        return breakdate;
    }

    public void setBreakdate(String breakdate) {
        this.breakdate = breakdate;
    }

    public String getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(String breaktime) {
        this.breaktime = breaktime;
    }

    public String getBreakdate1() {
        return breakdate1;
    }

    public void setBreakdate1(String breakdate1) {
        this.breakdate1 = breakdate1;
    }

    public String getBreaktime1() {
        return breaktime1;
    }

    public void setBreaktime1(String breaktime1) {
        this.breaktime1 = breaktime1;
    }
}
