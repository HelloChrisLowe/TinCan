package io.chrislowe.tincan

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.objects.game.Can
import io.chrislowe.tincan.objects.game.Spawner
import io.chrislowe.tincan.objects.ui.*
import io.chrislowe.tincan.TinCanGame.playServices
import io.chrislowe.tincan.ui.SettingsManager
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

    init {
        SettingsManager.initialize()
        playServices.signIn()
        changeGameState(GameState.MENU)
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

    internal fun setupMenu() {
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
            playServices.submitScore(gameScore)
        }

        gameObjects.addAll(listOf(GameOver(), EndMessage(hasHighScore)))
    }
}