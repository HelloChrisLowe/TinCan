package io.chrislowe.tincan

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.objects.game.Can
import io.chrislowe.tincan.objects.game.Spawner
import io.chrislowe.tincan.objects.ui.*
import io.chrislowe.tincan.objects.ui.VolumeButton
import io.chrislowe.tincan.objects.ui.VolumeDisplay
import io.chrislowe.tincan.objects.ui.VolumeLabel
import io.chrislowe.tincan.objects.ui.VolumeTarget
import io.chrislowe.tincan.objects.ui.VolumeDirection
import java.util.concurrent.CopyOnWriteArrayList

object Director {
    enum class GameState {
        MENU,
        PLAYING,
        FINISHED
    }

    var highScore = TinCanGame.storedData.currentHighScore()
    var gameScore = 0
    val gameObjects = CopyOnWriteArrayList<GameObject>()

    var hasHighScore = false

    private var settingsUIVisible = false

    private lateinit var settingsTitle: VolumeLabel
    private lateinit var sfxVolumeLabel: VolumeLabel
    private lateinit var sfxVolumeDisplay: VolumeDisplay
    private lateinit var sfxVolumeUpButton: VolumeButton
    private lateinit var sfxVolumeDownButton: VolumeButton

    private lateinit var musicVolumeLabel: VolumeLabel
    private lateinit var musicVolumeDisplay: VolumeDisplay
    private lateinit var musicVolumeUpButton: VolumeButton
    private lateinit var musicVolumeDownButton: VolumeButton

    private lateinit var backButton: VolumeButton

    init {
        initSettingsUI()
        changeGameState(GameState.MENU)
    }

    private fun initSettingsUI() {
        val screenWidth = TinCanGame.GAME_WIDTH
        val screenHeight = TinCanGame.GAME_HEIGHT
        val centerX = screenWidth / 2f

        settingsTitle = VolumeLabel("Settings", screenHeight * 0.85f, xPosition = centerX, alignment = Align.center)
        settingsTitle.setFontScaleAndRecenter(1.5f, centerX, screenHeight * 0.85f)

        sfxVolumeLabel = VolumeLabel("SFX Volume", screenHeight * 0.75f, xPosition = centerX, alignment = Align.center)
        sfxVolumeDisplay = VolumeDisplay(TinCanGame.storedData.getSfxVolume(), screenHeight * 0.70f, xPosition = centerX)
        sfxVolumeDownButton = VolumeButton(VolumeTarget.SFX, VolumeDirection.DOWN, screenHeight * 0.70f, centerX - 120f, "-")
        sfxVolumeUpButton = VolumeButton(VolumeTarget.SFX, VolumeDirection.UP, screenHeight * 0.70f, centerX + 120f, "+")

        musicVolumeLabel = VolumeLabel("Music Volume", screenHeight * 0.60f, xPosition = centerX, alignment = Align.center)
        musicVolumeDisplay = VolumeDisplay(TinCanGame.storedData.getMusicVolume(), screenHeight * 0.55f, xPosition = centerX)
        musicVolumeDownButton = VolumeButton(VolumeTarget.MUSIC, VolumeDirection.DOWN, screenHeight * 0.55f, centerX - 120f, "-")
        musicVolumeUpButton = VolumeButton(VolumeTarget.MUSIC, VolumeDirection.UP, screenHeight * 0.55f, centerX + 120f, "+")

        backButton = VolumeButton(VolumeTarget.SFX, VolumeDirection.DOWN, screenHeight * 0.25f, centerX, "⬅ Back")
    }

    fun showSettingsUI(visible: Boolean) {
        settingsUIVisible = visible
        gameObjects.clear() // Clear previous game objects before adding new ones

        if (visible) {
            // Refresh volume displays with current values
            sfxVolumeDisplay.updateText(TinCanGame.storedData.getSfxVolume())
            musicVolumeDisplay.updateText(TinCanGame.storedData.getMusicVolume())

            gameObjects.addAll(listOf(
                settingsTitle,
                sfxVolumeLabel, sfxVolumeDisplay, sfxVolumeDownButton, sfxVolumeUpButton,
                musicVolumeLabel, musicVolumeDisplay, musicVolumeDownButton, musicVolumeUpButton,
                backButton
            ))
        } else {
            setupMenu() // Revert to main menu UI
        }
    }

    fun updateVolumeDisplay(target: VolumeTarget, value: Int) {
        when (target) {
            VolumeTarget.SFX -> sfxVolumeDisplay.updateText(value)
            VolumeTarget.MUSIC -> musicVolumeDisplay.updateText(value)
        }
    }

    fun changeGameState(gameState: GameState) {
        when (gameState) {
            GameState.MENU -> setupMenu()
            GameState.PLAYING -> startGame()
            GameState.FINISHED -> endGame()
        }
    }

    fun handleTouchEvent(touchX: Float, touchY: Float) {
        for (gameObject in gameObjects) {
            if (gameObject.isTouched(touchX, touchY)) {
                gameObject.touch(touchX, touchY)
            }
        }
    }

    fun updateGameObjects() {
        for (gameObject in gameObjects) {
            gameObject.update()
        }
    }

    fun drawGameObjects(batch: SpriteBatch) {
        for (gameObject in gameObjects) {
            gameObject.draw(batch)
        }
    }

    fun increaseScore(amount: Int) {
        gameScore += amount

        if (gameScore > highScore) {
            highScore = gameScore
            hasHighScore = true
        }
    }

    private fun setupMenu() {
        Audio.playMusic(Audio.MusicTag.MENU)

        val startCan = StartCan()

        gameObjects.clear()
        gameObjects.addAll(listOf(Logo(), ScoreDisplay(), LeaderboardsButton(), SettingsButton(),
                startCan, TutorialIcon(startCan)))
    }

    private fun startGame() {
        Audio.playMusic(Audio.MusicTag.GAMEPLAY)

        gameScore = 0
        hasHighScore = false

        val startCan = gameObjects.find { it is StartCan }!!
        val can = Can()
        can.jumpToObject(startCan)

        gameObjects.clear()
        gameObjects.addAll(listOf(can, ScoreDisplay(), Spawner()))
    }

    private fun endGame() {
        Audio.pauseMusic()

        val soundTag = if (hasHighScore) Audio.SoundTag.HIGHSCORE else Audio.SoundTag.GAMEOVER
        Audio.playSound(soundTag)

        for (gameObject in gameObjects) {
            when (gameObject) {
                is Can -> gameObject.destroy()
                is Spawner -> gameObjects.remove(gameObject)
            }
        }

        if (hasHighScore) {
            TinCanGame.storedData.submitHighScore(gameScore)
        }

        gameObjects.addAll(listOf(GameOver(), EndMessage(hasHighScore)))
    }
}