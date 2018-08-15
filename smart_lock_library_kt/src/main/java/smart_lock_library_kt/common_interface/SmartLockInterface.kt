package smart_lock_library_kt.common_interface

interface SmartLockInterface {

    fun onCredentialsResult(username: String, password: String)

    fun signInRequiredResult()

    fun onSmartLockCanceledResult()

    fun onCredentialForgottenResult()

    fun onNoneOfCredentialOptionsResult()
}