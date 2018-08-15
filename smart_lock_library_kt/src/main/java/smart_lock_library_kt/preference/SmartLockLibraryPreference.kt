package smart_lock_library_kt.preference

import android.content.Context
import core_kt.preference.getBooleanPreference
import core_kt.preference.resetSinglePreference
import core_kt.preference.setBooleanPreference
import smart_lock_library_kt.utils.PREFERENCE_KEY_USE_SMART_LOCK

class SmartLockLibraryPreference {

    fun setUserRefusedSmartLock(context: Context) {
        setBooleanPreference(context, PREFERENCE_KEY_USE_SMART_LOCK, false)
    }

    fun shouldUseSmartLock(context: Context): Boolean {
        return getBooleanPreference(context, PREFERENCE_KEY_USE_SMART_LOCK)
    }

    fun resetUseSmartLock(context: Context) {
        resetSinglePreference(context, PREFERENCE_KEY_USE_SMART_LOCK)
    }
}