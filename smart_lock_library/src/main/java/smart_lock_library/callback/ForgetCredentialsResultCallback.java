package smart_lock_library.callback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import armymau.it.smart_lock_library.R;
import smart_lock_library.activity.SmartLockActivity;
import smart_lock_library.utils.SmartLockConstants;

public class ForgetCredentialsResultCallback implements ResultCallback<Status> {

    private final Context context;

    public ForgetCredentialsResultCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResult(@NonNull Status status) {

        ((SmartLockActivity) context).hideProgress();

        if (status.isSuccess()) {
            onCredentialsForgotten();
        } else {
            onCredentialsForgotten();
            Log.e(SmartLockConstants.TAG, context.getResources().getString(R.string.error_forget_failure));

            //Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.error_forget_failure, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onCredentialsForgotten() {
        //Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), R.string.credentials_forgotten, Snackbar.LENGTH_SHORT).show();
        ((SmartLockActivity) context).onCredentialsForgottenListener();
    }
}
