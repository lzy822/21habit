package android.lzy.a21habit;

import android.os.Environment;

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



    public static final String rootPath = Environment.getExternalStorageDirectory().toString();

    public static final String serverRootPath = "http://120.79.77.39:822/";

    public static final String serverApksPath = "Apks/";

    public static final String serverPhotosPath = "Photos/";

    public static final String appPhotoRootPath = rootPath + "/21Days/Photos";

    public static final String appApkRootPath = rootPath + "/21Days/Apk/";


    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE = 42042;

}
