package smart_lock_library.preference;

import android.content.Context;

import core.preference.SharedPreferencesHelper;
import smart_lock_library.utils.SmartLockConstants;

public class SmartLockLibraryPreference extends SharedPreferencesHelper {

    public static void setUserRefusedSmartLock(Context context) {
        SharedPreferencesHelper.setBooleanPreference(context, SmartLockConstants.PREFERENCE_KEY_USE_SMART_LOCK, false);
    }

    public static boolean shouldUseSmartLock(Context context) {
        return SharedPreferencesHelper.getBooleanPreference(context, SmartLockConstants.PREFERENCE_KEY_USE_SMART_LOCK);
    }

    public static void resetUseSmartLock(Context context) {
        SharedPreferencesHelper.resetSinglePreference(context, SmartLockConstants.PREFERENCE_KEY_USE_SMART_LOCK);
    }
}
