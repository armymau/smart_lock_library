package smart_lock_library.it.armymau.smartlocklibrarykt.common_interface

interface SmartLockInterface {

    fun onCredentialsResult(username: String, password: String)

    fun signInRequiredResult()

    fun onSmartLockCanceledResult()

    fun onCredentialForgottenResult()

    fun onNoneOfCredentialOptionsResult()
}