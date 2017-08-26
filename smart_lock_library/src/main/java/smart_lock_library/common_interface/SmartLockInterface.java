package smart_lock_library.common_interface;

public interface SmartLockInterface {

    void onCredentialsResult(String username, String password);

    void signInRequiredResult();

    void onSmartLockCanceledResult();

    void onCredentialForgottenResult();
}
