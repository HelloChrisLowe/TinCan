package io.chrislowe.tincan

abstract class StoredData {
    companion object {
        const val HIGH_SCORE_KEY = "highScore"
        const val TUTORIAL_ICON_KEY = "showTutorialIcon"
        const val MUSIC_VOLUME_KEY = "musicVolume"
        const val SFX_VOLUME_KEY = "sfxVolume"
    }

    abstract fun getLeaderboardKey(): String

    abstract fun showTutorialIcon(): Boolean
    abstract fun currentHighScore(): Int
    abstract fun submitHighScore(newScore: Int)

    abstract fun getMusicVolume(): Int
    abstract fun setMusicVolume(newVolume: Int)

    abstract fun getSfxVolume(): Int
    abstract fun setSfxVolume(newVolume: Int)
}