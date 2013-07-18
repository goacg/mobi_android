package mobi.hubtech.goacg.global;

import mobi.hubtech.goacg.bean.UserInfo;
import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoUtils {
    
    private static UserInfo sUserInfo;
    
    public static UserInfo getUserInfo(Context context) {
        if (sUserInfo == null) {
            SharedPreferences sp = context.getSharedPreferences(C.SHARED_PRE_NAME_USER_INFO, Context.MODE_PRIVATE);
            String userMAC = sp.getString(C.SHARED_PRE_KEY_USER_MAC, null);
            long userID = sp.getLong(C.SHARED_PRE_KEY_USER_ID, -1);
            UserInfo ui = new UserInfo();
            ui.setUserMAC(userMAC);
            ui.setUserID(userID);
            sUserInfo = ui;
            return ui;
        } else {
            return sUserInfo;
        }
    }
    
    public static void setUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences sp = context.getSharedPreferences(C.SHARED_PRE_NAME_USER_INFO, Context.MODE_PRIVATE);
        sp.edit()
            .putString(C.SHARED_PRE_KEY_USER_MAC, userInfo.getUserMAC())
            .putLong(C.SHARED_PRE_KEY_USER_ID, userInfo.getUserID())
        .apply();
        sUserInfo = userInfo;
    }
}
