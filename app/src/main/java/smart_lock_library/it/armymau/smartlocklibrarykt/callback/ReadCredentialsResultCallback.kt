package smart_lock_library.it.armymau.smartlocklibrarykt.callback

import android.content.Context
import android.content.IntentSender
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import armymau.it.smart_lock_library.R
import com.google.android.gms.auth.api.credentials.CredentialRequestResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResultCallback
import smart_lock_library.it.armymau.smartlocklibrarykt.activity.SmartLockActivity
import smart_lock_library.it.armymau.smartlocklibrarykt.utils.SMART_LOCK_READ_CREDENTIALS_REQUEST_CODE
import smart_lock_library.it.armymau.smartlocklibrarykt.utils.TAG

class ReadCredentialsResultCallback : ResultCallback<CredentialRequestResult> {

    var context: Context
    var retryOnInternalError: Boolean = false

    constructor(context: Context, retryOnInternalError: Boolean) {
        this.context = context
        this.retryOnInternalError = retryOnInternalError
    }

    override fun onResult(result: CredentialRequestResult) {
        //4
        //((SmartLockActivity) context).hideProgress();

        val status = result.status

        if (status.isSuccess) {
            /* Credentials retrieved from Smart Lock */
            Log.e(TAG, context.resources.getString(R.string.credentials_retrieved))
            (context as SmartLockActivity).onCredentialsRetrieved(result.credential)

        } else if (status.statusCode == CommonStatusCodes.CANCELED) {
            (context as SmartLockActivity).onSmartLockCanceled()

        } else if (status.statusCode == CommonStatusCodes.SIGN_IN_REQUIRED) {
            (context as SmartLockActivity).startSigninHintFlow()

        } else if (status.hasResolution()) {
            try {
                status.startResolutionForResult(context as AppCompatActivity, SMART_LOCK_READ_CREDENTIALS_REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "STATUS: Failed to send resolution.", e)
                Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), R.string.error_retrieve_failure_log, Snackbar.LENGTH_SHORT).show()
            }

        } else if (status.statusCode == CommonStatusCodes.INTERNAL_ERROR && retryOnInternalError) {
            (context as SmartLockActivity).retrieveCredentialsWithoutRetrying()

        } else {
            // Request has no resolution
            val message = context.getString(R.string.error_retrieve_failure, status.statusMessage)
            Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
            (context as SmartLockActivity).onSmartLockCanceled()
        }
    }
}