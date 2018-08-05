package smart_lock_library_kt.preference

import android.content.Context
import core.preference.SharedPreferencesHelper
import smart_lock_library_kt.utils.PREFERENCE_KEY_USE_SMART_LOCK

class SmartLockLibraryPreference : SharedPreferencesHelper() {

    fun setUserRefusedSmartLock(context: Context) {
        SharedPreferencesHelper.setBooleanPreference(context, PREFERENCE_KEY_USE_SMART_LOCK, false)
    }

    fun shouldUseSmartLock(context: Context): Boolean {
        return SharedPreferencesHelper.getBooleanPreference(context, PREFERENCE_KEY_USE_SMART_LOCK)
    }

    fun resetUseSmartLock(context: Context) {
        SharedPreferencesHelper.resetSinglePreference(context, PREFERENCE_KEY_USE_SMART_LOCK)
    }
}