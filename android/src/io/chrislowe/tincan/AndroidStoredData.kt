package io.chrislowe.tincan

import android.content.SharedPreferences

class AndroidStoredData(private val preferences: SharedPreferences) : StoredData() {

    override fun showTutorialIcon(): Boolean {
        val showIconCount = preferences.getInt(TUTORIAL_ICON_KEY, 2)

        return if (showIconCount == 0) false
        else {
            setInt(TUTORIAL_ICON_KEY, showIconCount - 1)
            true
        }
    }

    override fun getLeaderboardKey(): String = BuildConfig.leaderboardKey

    override fun currentHighScore() = preferences.getInt(HIGH_SCORE_KEY, 0)
    override fun submitHighScore(newScore: Int) = setInt(HIGH_SCORE_KEY, newScore)

    override fun getMusicVolume(): Int = preferences.getInt(MUSIC_VOLUME_KEY, 5)
    override fun setMusicVolume(newVolume: Int) = setInt(MUSIC_VOLUME_KEY, newVolume)

    override fun getSfxVolume(): Int = preferences.getInt(MUSIC_VOLUME_KEY, 5)
    override fun setSfxVolume(newVolume: Int) = setInt(SFX_VOLUME_KEY, newVolume)

    private fun setInt(keyName: String, value: Int) = with(preferences.edit()) {
        putInt(keyName, value)
        apply()
    }
}