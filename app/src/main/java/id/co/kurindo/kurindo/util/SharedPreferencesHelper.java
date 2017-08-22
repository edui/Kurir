package id.co.kurindo.kurindo.util;

import android.content.Context;

public class SharedPreferencesHelper extends AbsSharedPre {

    private final static String PREFS_FILE_NAME = "id.kurindo.android";

    public static final String CRASH_EXCEPTION = "CRASH_EXCEPTION";

    /** 用户ID */
    public static final String USER_ID = "USER_ID";
    /** 未上传成功的数据 */
    public static final String OFFLINE_DATA = "OfflineData";

    @Override
    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }
}
