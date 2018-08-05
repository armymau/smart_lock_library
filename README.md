#android #kotlin #android-library #smart_lock

# Google Smart Lock for Password on Android

By integrating Smart Lock for Passwords into your Android app.
Integrating Smart Lock for Passwords into your Android app in a simple and automatic way.
You can automatically sign users in to your app using the credentials they have saved. Users can save both username-password credentials and federated identity provider credentials.
Integrate Smart Lock for Passwords into your app by using the Credentials API to retrieve saved credentials on sign-in. Use successfully retrieved credentials to sign the user in, or use the Credentials API to rapidly on-board new users by partially completing your app's sign in or sign up form. Prompt users after sign-in or sign-up to store their credentials for future automatic authentication.


### Installing

**Step 1**. Add the JitPack repository to your build file.
Add it in your root build.gradle at the end of repositories:
 
```
  allprojects {
  	repositories {
		
    ...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2**. Add the dependency

```	
  dependencies {
	      compile 'com.github.armymau:smart_lock_library:v1.6'
	}
```

**Step 3**. You Extend your Login Activity with SmartLockActivity class and you can use SmartLockInterface

```	
    //READ CREDENTIALS
    /*
     * This method wrap Auth.CredentialsApi.request and retrieve user credentials if saved before
     */
    fun smartLockReadCredentials() {}
    

    //STORE CREDENTIALS
    /*
     * This method wrap Auth.CredentialsApi.save() and store user credentials if Smart Lock is enable on device
     */
    fun smartLockStoreCredentials(username: String, password: String) {}
    

    /*
     * This method wrap Auth.CredentialsApi.delete() and delete user credentials
     */
    fun smartLockStoreCredentials(username: String, password: String) {}
    
    
    /* SmartLockInterface */
    fun onCredentialsResult(username: String, password: String) //you can use auto login from credential retrieved

    fun signInRequiredResult() //Mandatory access to login

    fun onSmartLockCanceledResult() //Mandatory access due to no-retrieve of credentials from Smart Lock

    fun onCredentialForgottenResult() //Not save login credentials

    fun onNoneOfCredentialOptionsResult()
```
   
For more complete documentation 
https://developers.google.com/identity/smartlock-passwords/android/

## Authors
Armando Mennini  - [armymau](https://github.com/armymau)
