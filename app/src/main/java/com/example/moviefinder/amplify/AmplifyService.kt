package com.example.moviefinder.amplify

import android.app.Activity
import android.content.Context
import android.util.Log
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.example.moviefinder.data.MoviesRepository

interface AmplifyService {
    fun login(callingActivity: Activity)

    fun logout()

    fun configureAmplify()

    fun checkAuthStatus()
}

class AmplifyHandler(
    private val context: Context,
    private val repository: MoviesRepository
) : AmplifyService {

    override fun login(callingActivity: Activity) {
        Amplify.Auth.signInWithWebUI(callingActivity,
            {
                Log.i("KILO", "Signin OK = $it")
                repository.setLoggedin(it.isSignInComplete)
            },
            { Log.e("KILO", "Signin failed", it) }
        )
    }

    override fun logout() {
        Amplify.Auth.signOut(
            {
                Log.i("KILO", "Sign out successful")
                repository.setLoggedin(false)
            },
            { Log.e("KILO", "Sign out error", it) }
        )
    }

    override fun checkAuthStatus() {
        Amplify.Auth.fetchAuthSession(
            {
                Log.i("KILO", "Auth session ${it.isSignedIn}")
                repository.setLoggedin(it.isSignedIn)
            },
            { Log.e("KILO", "Failed to get auth session", it) }
        )
    }

    override fun configureAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(context)
            Log.i("KILO", "Configured amplify")
        } catch (e: Exception) {
            Log.e("KILO", "Amplify configuration failed", e)
        }
    }

}