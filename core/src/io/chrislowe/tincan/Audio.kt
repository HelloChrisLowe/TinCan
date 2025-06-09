package io.chrislowe.tincan

import com.badlogic.gdx.Gdx
import io.chrislowe.tincan.TinCanGame
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

/* This object handles all game audio.
 *
 * Music track status changes cause lag on the test android phone, so we use volume manipulations
 * instead where possible.
 */
object Audio {
    enum class SoundTag {
        HIT,
        KILL,
        GAMEOVER,
        HIGHSCORE,
    }

    enum class MusicTag {
        MENU,
        GAMEPLAY
    }

    private val soundBank = mutableMapOf<SoundTag, MutableList<Sound>>()
    private val musicBank = mutableMapOf<MusicTag, Music>()

    private val soundsToPlayNextFrame = mutableListOf<SoundTag>()

    private var currentlyPlaying: Music? = null
    private var lastPlaying: Music? = null
    private const val CROSS_FADE_RATE = .05f
    private var crossFade = 1f

    fun init() {
        for (i in 0..2) {
            addSound(SoundTag.HIT, "hit$i.ogg")
            addSound(SoundTag.KILL, "kill$i.ogg")
        }

        addSound(SoundTag.GAMEOVER, "gameover.ogg")
        addSound(SoundTag.HIGHSCORE, "highscore.ogg")

        addMusic(MusicTag.MENU, "menu.ogg")
        addMusic(MusicTag.GAMEPLAY, "gameplay.ogg")
    }

    private fun getSound(tag: SoundTag) = soundBank[tag]!![GameRandom.nextInt(soundBank[tag]!!.size)]

    fun playSound(tag: SoundTag) {
        soundsToPlayNextFrame.add(tag)
    }

    fun playMusic(tag: MusicTag) {
        val newMusic = musicBank[tag]!!
        if (newMusic == currentlyPlaying) return

        lastPlaying?.volume = 0f
        lastPlaying = currentlyPlaying
        currentlyPlaying = newMusic

        crossFade = 1f
        newMusic.volume = 0f
        if (!newMusic.isPlaying) newMusic.play()
    }

    fun pauseMusic() {
        currentlyPlaying?.pause()
        lastPlaying?.pause()
    }

    fun updateAudio() {
        crossFade -= CROSS_FADE_RATE
        if (crossFade < 0f) crossFade = 0f

        val musicVol = TinCanGame.storedData.getMusicVolume() / 100f
        currentlyPlaying?.volume = (1f - crossFade) * musicVol
        lastPlaying?.volume = crossFade * musicVol

        // Play queued sounds with current SFX volume
        if (soundsToPlayNextFrame.isNotEmpty()) {
            val sfxVol = TinCanGame.storedData.getSfxVolume() / 100f
            for (tagInList in soundsToPlayNextFrame) { // Renamed to avoid conflict with outer scope 'tag' if any
                val sound = getSound(tagInList)
                sound.play(sfxVol)
            }
            soundsToPlayNextFrame.clear()
        }
    }

    fun updateMusicVolume() {
        val musicVol = TinCanGame.storedData.getMusicVolume() / 100f
        // This will apply the base volume. updateAudio will then modulate it based on crossFade.
        currentlyPlaying?.volume = musicVol
    }

    private fun addSound(tag: SoundTag, filename: String) {
        val file = Gdx.files.internal(filename)
        val sound = Gdx.audio.newSound(file)

        val list = soundBank[tag] ?: mutableListOf()
        list.add(sound)
        soundBank[tag] = list
    }

    private fun addMusic(tag: MusicTag, filename: String) {
        val file = Gdx.files.internal(filename)
        val music = Gdx.audio.newMusic(file)
        music.isLooping = true

        musicBank[tag] = music
    }
}