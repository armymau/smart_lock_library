package smart_lock_library.callback;

import android.content.Context;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import armymau.it.smart_lock_library.R;
import smart_lock_library.activity.SmartLockActivity;
import smart_lock_library.utils.SmartLockConstants;

public class ReadCredentialsResultCallback implements ResultCallback<CredentialRequestResult> {

    private final Context context;
    private final boolean retryOnInternalError;

    public ReadCredentialsResultCallback(Context context, boolean retryOnInternalError) {
        this.context = context;
        this.retryOnInternalError = retryOnInternalError;
    }

    @Override
    public void onResult(@NonNull CredentialRequestResult result) {
        //4
        //((SmartLockActivity) context).hideProgress();

        Status status = result.getStatus();

        if (status.isSuccess()) {
            /* Credentials retrieved from Smart Lock */
            Log.e(SmartLockConstants.TAG, context.getResources().getString(R.string.credentials_retrieved));
            ((SmartLockActivity) context).onCredentialsRetrieved(result.getCredential());

        } else if (status.getStatusCode() == CommonStatusCodes.CANCELED) {
            ((SmartLockActivity) context).onSmartLockCanceled();

        } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
            ((SmartLockActivity) context).startSigninHintFlow();

        } else if (status.hasResolution()) {
            try {
                status.startResolutionForResult((AppCompatActivity) context, SmartLockConstants.SMART_LOCK_READ_CREDENTIALS_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.e(SmartLockConstants.TAG, "STATUS: Failed to send resolution.", e);
                Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.error_retrieve_failure_log, Snackbar.LENGTH_SHORT).show();
            }

        } else if (status.getStatusCode() == CommonStatusCodes.INTERNAL_ERROR && retryOnInternalError) {
            ((SmartLockActivity) context).retrieveCredentialsWithoutRetrying();

        } else {
            // Request has no resolution
            String message = context.getString(R.string.error_retrieve_failure, status.getStatusMessage());
            Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            ((SmartLockActivity) context).onSmartLockCanceled();
        }
    }
}
