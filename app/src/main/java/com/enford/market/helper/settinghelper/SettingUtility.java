package com.enford.market.helper.settinghelper;

import android.content.Context;
import android.text.TextUtils;

import com.enford.market.model.EnfordSystemUser;
import com.enford.market.util.GlobalContext;

/**
 * User: qii
 * Date: 12-11-28
 */
public class SettingUtility {

    private static final String FIRSTSTART = "firststart";

    private SettingUtility() {

    }

    public static void setDefaultUser(String user, String pwd) {
        SettingHelper.setEditor(getContext(), "user", user);
        SettingHelper.setEditor(getContext(), "pwd", pwd);
    }

    public static EnfordSystemUser getDefaultUser() {
        String user = SettingHelper.getSharedPreferences(getContext(), "user", "");
        String pwd = SettingHelper.getSharedPreferences(getContext(), "pwd", "");
        EnfordSystemUser enfordUser = null;
        if (!TextUtils.isEmpty(user) &&
                !TextUtils.isEmpty(pwd)) {
            enfordUser = new EnfordSystemUser();
            enfordUser.setUsername(user);
            enfordUser.setPassword(pwd);
        }
        return enfordUser;
    }

    public static void clearDefaultUser() {
        SettingHelper.clearEditor(getContext(), "user");
        SettingHelper.clearEditor(getContext(), "pwd");
    }

    private static Context getContext() {
        return GlobalContext.getInstance();
    }

    public static boolean firstStart() {
        boolean value = SettingHelper.getSharedPreferences(getContext(), FIRSTSTART, true);
        if (value) {
            SettingHelper.setEditor(getContext(), FIRSTSTART, false);
        }
        return value;
    }

}
