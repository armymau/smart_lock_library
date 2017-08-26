package smart_lock_library.callback;

import android.content.Context;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import armymau.it.smart_lock_library.R;
import smart_lock_library.activity.SmartLockActivity;
import smart_lock_library.utils.SmartLockConstants;

public class StoreCredentialsResultCallback implements ResultCallback<Status> {

    private final Context context;

    public StoreCredentialsResultCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResult(@NonNull Status status) {

        if (status.isSuccess()) {
            ((SmartLockActivity) context).onCredentialsStored();

        } else if (status.getStatusCode() == CommonStatusCodes.CANCELED) {
            ((SmartLockActivity) context).onSmartLockCanceled();

        } else if (status.hasResolution()) {
            try {
                status.startResolutionForResult((SmartLockActivity) context, SmartLockConstants.SMART_LOCK_STORE_CREDENTIALS_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.e(SmartLockConstants.TAG, "STATUS: Failed to send resolution.", e);
                Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.error_store_failure_log, Snackbar.LENGTH_SHORT).show();
            }

        } else {
            // The user must create an account or sign in manually.
            Log.e(SmartLockConstants.TAG, "STATUS: Unsuccessful credential request had no resolution.");
            String message = context.getString(R.string.error_store_failure, status.getStatusMessage());
            Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
