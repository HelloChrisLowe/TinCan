package io.chrislowe.tincan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.chrislowe.tincan.R
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.leaderboard.LeaderboardsClient
import io.chrislowe.tincan.PlayServices

class AndroidLauncher : AndroidApplication(), PlayServices {

    private lateinit var gamesSignInClient: GamesSignInClient
    private lateinit var leaderboardsClient: LeaderboardsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PlayGames.initialize(this)

        gamesSignInClient = PlayGames.getGamesSignInClient(this)
        leaderboardsClient = PlayGames.getLeaderboardsClient(this)

        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false

        val preferences = getPreferences(Context.MODE_PRIVATE)
        val androidStoredData = AndroidStoredData(preferences)
        initialize(TinCanGame(androidStoredData, this), config)
    }

    override fun signIn() {
        gamesSignInClient.isAuthenticated.addOnCompleteListener { isAuthenticatedTask ->
            val isAuthenticated = isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isSignedIn
            if (isAuthenticated) {
                Log.d("PlayServices", "Already signed in")
            } else {
                gamesSignInClient.signIn().addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Log.d("PlayServices", "Sign-in successful")
                    } else {
                        Log.e("PlayServices", "Sign-in failed: ", signInTask.exception)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Signed in successfully
                Log.d("PlayServices", "Signed in successfully after activity result")
                gamesSignInClient = PlayGames.getGamesSignInClient(this) // Re-initialize after sign-in
                leaderboardsClient = PlayGames.getLeaderboardsClient(this) // Re-initialize after sign-in
            } catch (apiException: ApiException) {
                // Sign in failed
                Log.e("PlayServices", "Sign in failed after activity result: ", apiException)
            }
        }
    }

    override fun signOut() {
        gamesSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("PlayServices", "Sign-out successful")
            } else {
                Log.e("PlayServices", "Sign-out failed: ", task.exception)
            }
        }
    }

    override fun isSignedIn(): Boolean {
        val isAuthenticatedTask = gamesSignInClient.isAuthenticated
        return isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isSignedIn
    }

    override fun submitScore(score: Int) {
        if (isSignedIn()) {
            leaderboardsClient.submitScoreImmediate(getString(R.string.leaderboard_key), score.toLong())
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
        if (isSignedIn()) {
            leaderboardsClient.getLeaderboardIntent(getString(R.string.leaderboard_key))
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
    }
}
