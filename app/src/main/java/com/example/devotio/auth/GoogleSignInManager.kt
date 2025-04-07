package com.example.devotio.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.devotio.R

class GoogleSignInManager(private val context: Context) {
    private val TAG = "GoogleSignInManager"
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        Log.d(TAG, "Getting sign in intent")
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        Log.d(TAG, "Handling sign in result")
        if (data == null) {
            Log.e(TAG, "Sign in result data is null")
            return null
        }

        // Check network connectivity
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No network connection available")
            return null
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Sign in successful: ${account?.email}")
            account
        } catch (e: ApiException) {
            val statusCode = e.statusCode
            val errorMessage = when (statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in was cancelled"
                GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Sign in is already in progress"
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign in failed"
                GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error occurred"
                GoogleSignInStatusCodes.INVALID_ACCOUNT -> "Invalid account"
                GoogleSignInStatusCodes.TIMEOUT -> "Sign in timed out"
                else -> "Unknown error occurred"
            }
            Log.e(TAG, "Sign in failed with status code: $statusCode, message: $errorMessage", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sign in", e)
            null
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasNetwork = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        
        Log.d(TAG, "Network status - Has Internet: $hasInternet, Has Network: $hasNetwork")
        return hasInternet && hasNetwork
    }

    fun signOut() {
        googleSignInClient.signOut()
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                Log.w(TAG, "Google Play Services is not available: ${googleApiAvailability.getErrorString(resultCode)}")
            } else {
                Log.e(TAG, "This device is not supported for Google Play Services")
            }
            return false
        }
        return true
    }
} 