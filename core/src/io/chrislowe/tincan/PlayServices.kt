package io.chrislowe.tincan

interface PlayServices {
    fun signIn()
    fun signOut()
    fun isSignedIn(): Boolean
    fun submitScore(score: Int)
    fun showLeaderboard()
}
