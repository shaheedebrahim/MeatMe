package com.bandaids.meatme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Peachcobbler on 2/22/2017.
 */

public class SavedAuthInfo {
    static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUName(Context context, String uid) {
        SharedPreferences.Editor e = getPrefs(context).edit();
        e.putString("user_key", uid);
        e.commit();
    }

    public static String getUName(Context context) {
        return getPrefs(context).getString("user_key", "");
    }
}
