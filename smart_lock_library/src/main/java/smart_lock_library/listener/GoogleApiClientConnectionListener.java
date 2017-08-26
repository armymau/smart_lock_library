package smart_lock_library.listener;

import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import armymau.it.smart_lock_library.R;
import smart_lock_library.activity.SmartLockActivity;
import smart_lock_library.utils.SmartLockConstants;

public class GoogleApiClientConnectionListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;

    public GoogleApiClientConnectionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(SmartLockConstants.TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(SmartLockConstants.TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(SmartLockConstants.TAG, "onConnectionFailed: " + connectionResult);

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult((SmartLockActivity) context, SmartLockConstants.SMART_LOCK_CONNECT_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.e(SmartLockConstants.TAG, "Unable to resolve connection issue with Smart Lock Google Api Client", e);
                Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.connection_error, Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.connection_error_response + connectionResult.getErrorCode(), Snackbar.LENGTH_LONG).show();
        }
    }
}
