package smart_lock_library.it.armymau.smartlocklibrarykt.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import armymau.it.smart_lock_library.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import core.fragment.ProgressDialogFragment
import smart_lock_library.it.armymau.smartlocklibrarykt.callback.ForgetCredentialsResultCallback
import smart_lock_library.it.armymau.smartlocklibrarykt.callback.ReadCredentialsResultCallback
import smart_lock_library.it.armymau.smartlocklibrarykt.callback.StoreCredentialsResultCallback
import smart_lock_library.it.armymau.smartlocklibrarykt.common_interface.SmartLockInterface
import smart_lock_library.it.armymau.smartlocklibrarykt.listener.GoogleApiClientConnectionListener
import smart_lock_library.it.armymau.smartlocklibrarykt.preference.SmartLockLibraryPreference
import smart_lock_library.it.armymau.smartlocklibrarykt.utils.*

class SmartLockActivity : AppCompatActivity() {

    lateinit var dialog: ProgressDialogFragment
    lateinit var credentialsRequest: CredentialRequest
    lateinit var credentials: Credential
    lateinit var listener: SmartLockInterface
    var isAutoManageEnabled: Boolean = false
    lateinit var credentialsClient: GoogleApiClient
    val smartLockLibraryPreference = SmartLockLibraryPreference()

    override fun onDestroy() {
        super.onDestroy()
        if (credentialsClient.isConnected) {
            if (isAutoManage())
                credentialsClient.stopAutoManage(this)
            credentialsClient.disconnect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isAutoManage())
            credentialsClient.stopAutoManage(this)
    }

    override fun onStart() {
        super.onStart()
        if (!credentialsClient.isConnected)
            credentialsClient.connect()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGoogleApi()
    }

    private fun initGoogleApi() {
        val googleApiClientConnectionListener = GoogleApiClientConnectionListener(this)
        credentialsClient = createCredentialsClient(this, googleApiClientConnectionListener)
        credentialsRequest = createCredentialsRequest()
    }

    @Synchronized
    private fun createCredentialsClient(context: Context, listener: GoogleApiClientConnectionListener): GoogleApiClient {
        return if (isAutoManage()) {
            GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(listener)
                    .enableAutoManage(context as AppCompatActivity, listener)
                    .addApi(Auth.CREDENTIALS_API)
                    .build()
        } else {
            GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(listener)
                    .addApi(Auth.CREDENTIALS_API)
                    .build()
        }
    }

    private fun createCredentialsRequest(): CredentialRequest {
        return CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build()
    }


    //>>>>> READ CREDENTIALS
    fun smartLockReadCredentials() {
        val callback = ReadCredentialsResultCallback(this, true)
        requestReadCredentials(credentialsClient, credentialsRequest, callback)
    }

    private fun requestReadCredentials(apiClient: GoogleApiClient, credentialRequest: CredentialRequest, callback: ReadCredentialsResultCallback) {
        Auth.CredentialsApi.request(apiClient, credentialRequest).setResultCallback(callback)
    }

    fun onCredentialsRetrieved(credentials: Credential) {
        this.credentials = credentials
        val username = credentials.id
        val password = credentials.password

        /* Signing you in with stored credentials */
        Log.e(TAG, this.resources.getString(R.string.signing_in_with_stored_credentials))
        listener.onCredentialsResult(username, password!!)
    }
    //**************************************************


    //>>>>> STORE CREDENTIALS
    fun smartLockStoreCredentials(username: String, password: String) {

        credentials = prepareCredentials(username, password)
        if(credentials != null) {
            val callback = StoreCredentialsResultCallback(this)
            requestSaveCredentials(credentialsClient, credentials!!, callback)
        }
    }

    private fun requestSaveCredentials(apiClient: GoogleApiClient?, credentials: Credential, callback: StoreCredentialsResultCallback) {
        Auth.CredentialsApi.save(apiClient, credentials).setResultCallback(callback)
    }

    private fun prepareCredentials(username: String, password: String): Credential {
        return Credential.Builder(username).setPassword(password).build()
    }

    fun onCredentialsStored() {
        /* credentials_saved */
        Log.e(TAG, this.resources.getString(R.string.credentials_saved))
        listener.onCredentialsResult(credentials.id, credentials.password!!)
    }
    //**************************************************


    //>>>>> FORGET CREDENTIALS
    fun smartLockForgetCredentials(username: String, password: String) {
        credentials = prepareCredentials(username, password)
        if (credentials == null) {
            Snackbar.make(this.findViewById<View>(android.R.id.content), R.string.error_forget_no_credentials, Snackbar.LENGTH_SHORT).show()
            return
        }
        showProgress()
        Auth.CredentialsApi.delete(credentialsClient, credentials).setResultCallback(ForgetCredentialsResultCallback(this))
    }

    fun onCredentialsForgottenListener() {
        /* credentials_forgotten */
        Log.e(TAG, this.resources.getString(R.string.credentials_forgotten))
        listener.onCredentialForgottenResult()
    }
    //**************************************************


    /* status.getStatusCode() == CommonStatusCodes.CANCELED */
    fun onSmartLockCanceled() {
        smartLockLibraryPreference.setUserRefusedSmartLock(this)
        listener.onSmartLockCanceledResult()
    }


    /* status == CommonStatusCodes.SIGN_IN_REQUIRED */
    fun startSigninHintFlow() {
        listener.signInRequiredResult()
    }

    fun createHintRequest(): HintRequest {
        val pickerConfig = CredentialPickerConfig.Builder()
                .setShowAddAccountButton(true)
                .setShowCancelButton(true)
                .build()
        return HintRequest.Builder()
                .setEmailAddressIdentifierSupported(true)
                .setHintPickerConfig(pickerConfig)
                .build()
    }
    //**************************************************


    /* status == CommonStatusCodes.INTERNAL_ERROR && retryOnInternalError */
    fun retrieveCredentialsWithoutRetrying() {
        val callback = ReadCredentialsResultCallback(this, false)
        requestReadCredentials(credentialsClient, credentialsRequest, callback)
    }


    private fun showSmartLockDisabledMessage() {
        Snackbar.make(this.findViewById<View>(android.R.id.content), R.string.error_smart_lock_disabled, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.reset) { smartLockLibraryPreference.resetUseSmartLock(this@SmartLockActivity) }.show()
    }

    fun showProgress() {
        dialog = ProgressDialogFragment()
        dialog.show(supportFragmentManager, R.string.core_waiting)
    }

    fun hideProgress() {
        if (dialog.isAdded) {
            dialog.dismissAllowingStateLoss()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult:$requestCode:$resultCode:$data")

        if (requestCode == SMART_LOCK_READ_CREDENTIALS_REQUEST_CODE) {
            handleCredentialsReadResult(resultCode, data)
        } else if (requestCode == SMART_LOCK_STORE_CREDENTIALS_REQUEST_CODE) {
            handleCredentialsStoreResult(resultCode)
        } else if (requestCode == SMART_LOCK_CONNECT_REQUEST_CODE) {
            handleGmsConnectionResult(resultCode)
        }
    }


    /* handle */
    private fun handleCredentialsReadResult(resultCode: Int, data: Intent?) {
        when (resultCode) {

            Activity.RESULT_OK -> {
                val credentials = data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                onCredentialsRetrieved(credentials)

            }

            NONE_OF_PREVIOUS_OPTIONS_RESULT_CODE -> listener.onNoneOfCredentialOptionsResult()

            else -> {
                Log.e(TAG, "Credentials read failed")
                listener.onSmartLockCanceledResult()
            }
        }
    }

    private fun handleCredentialsStoreResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            onCredentialsStored()
        } else {
            Log.e(TAG, "SAVE: Canceled by user")
            onSmartLockCanceled()
        }
    }

    private fun handleGmsConnectionResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            credentialsClient.connect()
        }
    }
    //**************************************************


    fun getCredClient(): GoogleApiClient {
        return credentialsClient
    }

    fun isAutoManage(): Boolean {
        return isAutoManageEnabled
    }

    fun setAutoManage(autoManageEnabled: Boolean) {
        isAutoManageEnabled = autoManageEnabled
    }
}
