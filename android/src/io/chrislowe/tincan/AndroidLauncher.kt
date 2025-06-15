package io.chrislowe.tincan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.chrislowe.tincan.BuildConfig // Added import for BuildConfig
import io.chrislowe.tincan.R
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.PlayGamesSdk
import io.chrislowe.tincan.PlayServices

class AndroidLauncher : AndroidApplication(), PlayServices {

    // Member variables for clients removed as per instruction to always get fresh clients.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PlayGamesSdk.initialize(this)

        // Clients are no longer stored in member variables here.
        // They will be fetched from PlayGames.* directly when needed.

        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false

        val preferences = getPreferences(Context.MODE_PRIVATE)
        val androidStoredData = AndroidStoredData(preferences)
        initialize(TinCanGame(androidStoredData, this), config)
    }

    override fun signIn() {
        PlayGames.getGamesSignInClient(this).isAuthenticated.addOnCompleteListener { isAuthenticatedTask ->
            val isAuthenticated = isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated
            if (isAuthenticated) {
                Log.d("PlayServices", "Already signed in or auto-signed in")
                // Check if silent sign-in was successful, otherwise prompt interactive sign-in
                 PlayGames.getGamesSignInClient(this).signIn().addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Log.d("PlayServices", "Silent sign-in successful.")
                    } else {
                        Log.d("PlayServices", "Silent sign-in failed, attempting interactive sign-in.")
                        interactiveSignIn()
                    }
                }
            } else {
                Log.d("PlayServices", "Not authenticated, attempting interactive sign-in.")
                interactiveSignIn()
            }
        }
    }

    override fun signOut() {
        // TODO
    }

    private fun interactiveSignIn() {
        PlayGames.getGamesSignInClient(this).signIn().addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                Log.d("PlayServices", "Interactive sign-in successful")
            } else {
                Log.e("PlayServices", "Interactive sign-in failed: ", signInTask.exception)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)
                // Signed in successfully via GoogleSignIn, now check Play Games layer
                PlayGames.getGamesSignInClient(this).isAuthenticated.addOnCompleteListener { isAuthenticatedTask ->
                    if (isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated) {
                        Log.d("PlayServices", "Google Sign-In successful and Play Games authenticated.")
                    } else {
                        Log.w("PlayServices", "Google Sign-In successful BUT Play Games NOT authenticated.")
                    }
                }
            } catch (apiException: ApiException) {
                // Sign in failed
                Log.e("PlayServices", "Google Sign-In failed after activity result: ", apiException)
            }
        }
    }

    override fun isSignedIn(): Boolean {
        // This is a synchronous check which might not be perfectly accurate immediately after an async call.
        // For v2, it's better to rely on the async results of isAuthenticated or signIn.
        // However, to satisfy the interface, we'll do our best.
        val authTask = PlayGames.getGamesSignInClient(this).isAuthenticated
        return authTask.isComplete && authTask.isSuccessful && authTask.result.isAuthenticated
    }

    override fun submitScore(score: Int) {
        if (isSignedIn()) { // It's good practice to check, though operations will often fail gracefully if not signed in.
            PlayGames.getLeaderboardsClient(this).submitScoreImmediate(BuildConfig.leaderboardKey, score.toLong())
                .addOnSuccessListener {
                    Log.d("PlayServices", "Score submitted successfully: $score")
                }
                .addOnFailureListener { e ->
                    Log.e("PlayServices", "Failed to submit score: $score", e)
                }
        } else {
            Log.w("PlayServices", "Cannot submit score, user not signed in.")
        }
    }

    override fun showLeaderboard() {
        if (isSignedIn()) { // Good practice to check.
            PlayGames.getLeaderboardsClient(this).getLeaderboardIntent(BuildConfig.leaderboardKey)
                .addOnSuccessListener { intent ->
                    startActivityForResult(intent, RC_LEADERBOARD_UI)
                }
                .addOnFailureListener { e ->
                    Log.e("PlayServices", "Failed to get leaderboard intent", e)
                    // Potentially show a toast or message to the user
                }
        } else {
            Log.w("PlayServices", "Cannot show leaderboard, user not signed in.")
            // Potentially show a toast or message to the user, or trigger signIn()
            // For now, let's try to sign them in.
            signIn()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_LEADERBOARD_UI = 9004
    }
}
