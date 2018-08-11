package smart_lock_library_kt.callback

import android.content.Context
import android.content.IntentSender
import android.support.design.widget.Snackbar
import android.util.Log
import armymau.it.smart_lock_library.R
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import smart_lock_library_kt.activity.SmartLockActivity
import smart_lock_library_kt.utils.SMART_LOCK_STORE_CREDENTIALS_REQUEST_CODE
import smart_lock_library_kt.utils.TAG

class StoreCredentialsResultCallback(var context: Context) : ResultCallback<Status> {

    override fun onResult(status: Status) {

        if (status.isSuccess) {
            (context as SmartLockActivity).onCredentialsStored()

        } else if (status.statusCode == CommonStatusCodes.CANCELED) {
            (context as SmartLockActivity).onSmartLockCanceled()

        } else if (status.hasResolution()) {
            try {
                status.startResolutionForResult(context as SmartLockActivity, SMART_LOCK_STORE_CREDENTIALS_REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "STATUS: Failed to send resolution.", e)
                Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), R.string.error_store_failure_log, Snackbar.LENGTH_SHORT).show()
            }

        } else {
            // The user must create an account or sign in manually.
            Log.e(TAG, "STATUS: Unsuccessful credential request had no resolution.")
            val message = context.getString(R.string.error_store_failure, status.statusMessage)
            Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }
    }
}