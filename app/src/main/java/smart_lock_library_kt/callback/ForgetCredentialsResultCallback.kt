package smart_lock_library_kt.callback

import android.content.Context
import android.util.Log
import armymau.it.smart_lock_library.R
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import smart_lock_library_kt.activity.SmartLockActivity
import smart_lock_library_kt.utils.TAG

class ForgetCredentialsResultCallback : ResultCallback<Status> {

    var context: Context

    constructor(context: Context) {
        this.context = context
    }

    override fun onResult(status: Status) {

        (context as SmartLockActivity).hideProgress()

        if (status.isSuccess) {
            onCredentialsForgotten()
        } else {
            onCredentialsForgotten()
            Log.e(TAG, context.resources.getString(R.string.error_forget_failure))

            //Snackbar.make(((SmartLockActivity) context).findViewById(android.R.id.content), R.string.error_forget_failure, Snackbar.LENGTH_SHORT).show();
        }
    }

    private fun onCredentialsForgotten() {
        //Snackbar.make(((AppCompatActivity) context).findViewById(android.R.id.content), R.string.credentials_forgotten, Snackbar.LENGTH_SHORT).show();
        (context as SmartLockActivity).onCredentialsForgottenListener()
    }
}