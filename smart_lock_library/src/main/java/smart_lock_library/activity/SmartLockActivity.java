package smart_lock_library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;

import armymau.it.smart_lock_library.R;
import core.fragment.ProgressDialogFragment;
import smart_lock_library.callback.ForgetCredentialsResultCallback;
import smart_lock_library.callback.ReadCredentialsResultCallback;
import smart_lock_library.callback.StoreCredentialsResultCallback;
import smart_lock_library.common_interface.SmartLockInterface;
import smart_lock_library.listener.GoogleApiClientConnectionListener;
import smart_lock_library.preference.SmartLockLibraryPreference;
import smart_lock_library.utils.SmartLockConstants;

public class SmartLockActivity extends AppCompatActivity {

    private ProgressDialogFragment dialog;
    private GoogleApiClient credentialsClient;
    private CredentialRequest credentialsRequest;
    private Credential credentials;
    public boolean isAutoManageEnabled;
    private SmartLockInterface listener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(credentialsClient != null && credentialsClient.isConnected()) {
            if(isAutoManageEnabled())
                credentialsClient.stopAutoManage(this);
            credentialsClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAutoManageEnabled())
            credentialsClient.stopAutoManage(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(credentialsClient != null && !credentialsClient.isConnected())
            credentialsClient.connect();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (SmartLockInterface) this;
        initGoogleApi();
    }

    private void initGoogleApi() {

        /*
        if (!SmartLockLibraryPreference.shouldUseSmartLock(this)) {
            showSmartLockDisabledMessage();
        }
        */

        GoogleApiClientConnectionListener googleApiClientConnectionListener = new GoogleApiClientConnectionListener(this);
        credentialsClient = createCredentialsClient(this, googleApiClientConnectionListener);
        credentialsRequest = createCredentialsRequest();
    }

    private synchronized GoogleApiClient createCredentialsClient(Context context, GoogleApiClientConnectionListener listener) {
        if(isAutoManageEnabled()) {
            return new Builder(context)
                    .addConnectionCallbacks(listener)
                    .enableAutoManage((AppCompatActivity) context, listener)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();
        } else {
            return new Builder(context)
                    .addConnectionCallbacks(listener)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();
        }
    }

    private CredentialRequest createCredentialsRequest() {
        return new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();
    }


    //>>>>> READ CREDENTIALS
    public void smartLockReadCredentials() {
        ReadCredentialsResultCallback callback = new ReadCredentialsResultCallback(this, true);
        requestReadCredentials(credentialsClient, credentialsRequest, callback);
    }

    private void requestReadCredentials(GoogleApiClient apiClient, CredentialRequest credentialRequest, ReadCredentialsResultCallback callback) {
        Auth.CredentialsApi.request(apiClient, credentialRequest).setResultCallback(callback);
    }

    public void onCredentialsRetrieved(Credential credentials) {
        this.credentials = credentials;
        String username = credentials.getId();
        String password = credentials.getPassword();

        /* Signing you in with stored credentials */
        Log.e(SmartLockConstants.TAG, this.getResources().getString(R.string.signing_in_with_stored_credentials));
        listener.onCredentialsResult(username, password);
    }
    //**************************************************


    //>>>>> STORE CREDENTIALS
    public void smartLockStoreCredentials(String username, String password) {

        /*
        if (!SmartLockLibraryPreference.shouldUseSmartLock(this)) {
            showSmartLockDisabledMessage();
        }
        */

        credentials = prepareCredentials(username, password);
        if (credentials == null) {
            Snackbar.make(this.findViewById(android.R.id.content), R.string.error_store_incomplete_credentials, Snackbar.LENGTH_SHORT).show();
        } else {
            StoreCredentialsResultCallback callback = new StoreCredentialsResultCallback(this);
            requestSaveCredentials(credentialsClient, credentials, callback);
        }
    }

    private void requestSaveCredentials(GoogleApiClient apiClient, Credential credentials, StoreCredentialsResultCallback callback) {
        Auth.CredentialsApi.save(apiClient, credentials).setResultCallback(callback);
    }

    private Credential prepareCredentials(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return null;
        }
        return new Credential.Builder(username).setPassword(password).build();
    }

    public void onCredentialsStored() {
        /* credentials_saved */
        Log.e(SmartLockConstants.TAG, this.getResources().getString(R.string.credentials_saved));
        listener.onCredentialsResult(credentials.getId(), credentials.getPassword());
    }
    //**************************************************


    //>>>>> FORGET CREDENTIALS
    public void smartLockForgetCredentials(String username, String password) {
        credentials = prepareCredentials(username, password);
        if (credentials == null) {
            Snackbar.make(this.findViewById(android.R.id.content), R.string.error_forget_no_credentials, Snackbar.LENGTH_SHORT).show();
            return;
        }
        showProgress();
        Auth.CredentialsApi.delete(credentialsClient, credentials).setResultCallback(new ForgetCredentialsResultCallback(this));
    }

    public void onCredentialsForgottenListener() {
        /* credentials_forgotten */
        Log.e(SmartLockConstants.TAG, this.getResources().getString(R.string.credentials_forgotten));
        listener.onCredentialForgottenResult();
    }
    //**************************************************


    /* status.getStatusCode() == CommonStatusCodes.CANCELED */
    public void onSmartLockCanceled() {
        SmartLockLibraryPreference.setUserRefusedSmartLock(this);
        listener.onSmartLockCanceledResult();
    }


    /* status == CommonStatusCodes.SIGN_IN_REQUIRED */
    public void startSigninHintFlow() {
        listener.signInRequiredResult();
    }

    public HintRequest createHintRequest() {
        CredentialPickerConfig pickerConfig = new CredentialPickerConfig.Builder()
                .setShowAddAccountButton(true)
                .setShowCancelButton(true)
                .build();
        return new HintRequest.Builder()
                .setEmailAddressIdentifierSupported(true)
                .setHintPickerConfig(pickerConfig)
                .build();
    }
    //**************************************************


    /* status == CommonStatusCodes.INTERNAL_ERROR && retryOnInternalError */
    public void retrieveCredentialsWithoutRetrying() {
        ReadCredentialsResultCallback callback = new ReadCredentialsResultCallback(this, false);
        requestReadCredentials(credentialsClient, credentialsRequest, callback);
    }


    private void showSmartLockDisabledMessage() {
        Snackbar.make(this.findViewById(android.R.id.content), R.string.error_smart_lock_disabled, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.reset, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmartLockLibraryPreference.resetUseSmartLock(SmartLockActivity.this);
                    }
                }).show();
    }

    public void showProgress() {
        dialog = new ProgressDialogFragment();
        dialog.show(getSupportFragmentManager(), R.string.core_waiting);
    }

    public void hideProgress() {
        if(dialog != null && dialog.isAdded()) {
            dialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(SmartLockConstants.TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == SmartLockConstants.SMART_LOCK_READ_CREDENTIALS_REQUEST_CODE) {
            handleCredentialsReadResult(resultCode, data);
        } else if (requestCode == SmartLockConstants.SMART_LOCK_STORE_CREDENTIALS_REQUEST_CODE) {
            handleCredentialsStoreResult(resultCode);
        } else if (requestCode == SmartLockConstants.SMART_LOCK_CONNECT_REQUEST_CODE) {
            handleGmsConnectionResult(resultCode);
        }
    }


    /* handle */
    private void handleCredentialsReadResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            onCredentialsRetrieved(credentials);
        } else if (resultCode == SmartLockConstants.NONE_OF_PREVIOUS_OPTIONS_RESULT_CODE) {
            listener.onNoneOfCredentialOptionsResult();
        } else {
            Log.e(SmartLockConstants.TAG, "Credentials read failed");
            //Snackbar.make(this.findViewById(android.R.id.content), R.string.error_retrieve_fatal, Snackbar.LENGTH_SHORT).show();
            listener.onSmartLockCanceledResult();
        }
    }

    private void handleCredentialsStoreResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            onCredentialsStored();
        } else {
            Log.e(SmartLockConstants.TAG, "SAVE: Canceled by user");
            onSmartLockCanceled();
        }
    }

    private void handleGmsConnectionResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            credentialsClient.connect();
        }
    }
    //**************************************************


    public GoogleApiClient getCredentialsClient() {
        return credentialsClient;
    }

    public boolean isAutoManageEnabled() {
        return isAutoManageEnabled;
    }

    public void setAutoManageEnabled(boolean autoManageEnabled) {
        isAutoManageEnabled = autoManageEnabled;
    }
}
