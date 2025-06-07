package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.Director
import io.chrislowe.tincan.GameBackground
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.plusOrMinus

class GameOver : GameObject() {
    private val shakeScale = 16

    private var selfShakeTimer = 12

    init {
        GameBackground.shakeTimer = selfShakeTimer

        setTexture("gameover.png")
        sprite.setScale((TinCanGame.GAME_WIDTH / sprite.width) * 0.85f)

        selfCenter()
    }

    override fun update() {
        if (selfShakeTimer > 0) {
            selfShakeTimer--
            selfCenter()

            if (selfShakeTimer != 0) {
                sprite.x += 0.plusOrMinus(shakeScale)
                sprite.y += 0.plusOrMinus(shakeScale)
            }
        }
    }

    override fun isTouched(touchX: Float, touchY: Float) = (selfShakeTimer == 0)

    override fun touch(touchX: Float, touchY: Float) =
            Director.changeGameState(Director.GameState.MENU)

    private fun selfCenter() {
        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = TinCanGame.GAME_HEIGHT * (2 / 3f)
    }
}