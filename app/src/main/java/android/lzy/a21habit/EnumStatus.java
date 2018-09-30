package android.lzy.a21habit;

public class EnumStatus {
    //习惯完成状态标识符，对应summarylist中的status字段
    public static final int SYSTEM_STATUS = -2;
    public static final int BROKEN_STATUS = -1;
    public static final int INPROGRESS_STATUS = 0;
    public static final int FINISH_STATUS = 1;


    //冲动记录标识符，对应impulselist中的status字段
    public static final int IMPULSE_INPROGRESS_STATUS = 0;
    public static final int IMPULSE_BROKEN_STATUS = 1;

    //开始时间状态记录标识符
    public static final boolean CONTAINTODAY = false;
    public static final boolean STARTOMMOROW = true;

}
