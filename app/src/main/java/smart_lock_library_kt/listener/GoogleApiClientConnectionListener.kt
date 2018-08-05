package smart_lock_library_kt.listener

import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import armymau.it.smart_lock_library.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import smart_lock_library_kt.activity.SmartLockActivity
import smart_lock_library_kt.utils.SMART_LOCK_CONNECT_REQUEST_CODE
import smart_lock_library_kt.utils.TAG

class GoogleApiClientConnectionListener : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    var context: Context

    constructor(context: Context) {
        this.context = context
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected")
    }

    override fun onConnectionSuspended(cause: Int) {
        Log.d(TAG, "onConnectionSuspended: $cause")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed: $connectionResult")

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(context as SmartLockActivity, SMART_LOCK_CONNECT_REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "Unable to resolve connection issue with Smart Lock Google Api Client", e)
                Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_LONG).show()
            }

        } else {
            Snackbar.make((context as SmartLockActivity).findViewById(android.R.id.content), R.string.connection_error_response + connectionResult.errorCode, Snackbar.LENGTH_LONG).show()
        }
    }
}