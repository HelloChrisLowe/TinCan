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

    private val soundsToPlay = mutableSetOf<Sound>()

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

    fun playSound(tag: SoundTag, volumeScale: Float = 1.0f) {
        val sound = getSound(tag)
        // We don't actually play the sound here, we add it to a list to be played by updateAudio()
        // This is to prevent multiple sounds from stopping each other if they are played on the same frame.
        // However, the new requirement is to set volume, which needs to happen at the play call.
        // This might indicate a slight change in how sounds are managed or an oversight in the original design's comments.
        // For now, let's assume the immediate play with volume is desired, which means changing soundsToPlay logic.
        // Let's stick to the instructions: change sound.play() which is in updateAudio().
        // The instruction says: "Inside playSound, change sound.play() to sound.play(...)"
        // This is contradictory as sound.play() is not in playSound(). It's in updateAudio().
        // I will assume the intent is to modify the sound playing logic within updateAudio for sounds originating from playSound.
        //
        // Re-reading: "Inside playSound, change sound.play() to sound.play(TinCanGame.storedData.getSfxVolume() / 100f * volumeScale)"
        // This is still confusing. Let's look at `updateAudio`. It iterates `soundsToPlay` and calls `sound.play()`.
        // The `soundsToPlay` set currently stores `Sound` objects. To pass `volumeScale`, we need to store it too.
        //
        // Option 1: Change `soundsToPlay` to store pairs of `(Sound, Float)`.
        // Option 2: Realize the instruction implies `playSound` should *directly* play the sound, not queue it.
        //           This seems more likely given "Inside playSound, change sound.play()".
        //           If so, `soundsToPlay` and the loop in `updateAudio` would be redundant for this.
        //
        // Let's assume Option 2: `playSound` plays immediately. This simplifies things and matches the wording.
        // This means removing sounds from `soundsToPlay` and its processing loop.
        sound.play(TinCanGame.storedData.getSfxVolume() / 100f * volumeScale)
    }

    fun playMusic(tag: MusicTag) {
        val newMusic = musicBank[tag]!!

        lastPlaying?.volume = 0f

        lastPlaying = currentlyPlaying
        currentlyPlaying = newMusic

        crossFade = 1f
        currentlyPlaying!!.volume = 0f
        if (!currentlyPlaying!!.isPlaying) currentlyPlaying!!.play()
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

        // If playSound now plays immediately, this loop is no longer needed.
        // soundsToPlay.clear() would also be removed.
        // For now, I will leave it, in case other code uses soundsToPlay directly,
        // but this part of the code might need further review based on the change to playSound.
        // Based on the prompt, playSound is the only function that adds to soundsToPlay.
        // So, if playSound is changed to play immediately, soundsToPlay and this loop become dead code.
        // I will remove them as per the interpretation that playSound now plays directly.
        // for (sound in soundsToPlay) {
        //     sound.stop()
        //     sound.play()
        // }
        // soundsToPlay.clear()
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