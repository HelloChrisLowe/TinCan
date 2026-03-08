package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.Audio
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import kotlin.math.sin

class StartCan : GameObject() {
    private val bobAmount = 64

    private var timeAlive = 0f

    init {
        setTexture("can0.png")

        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = TinCanGame.GAME_HEIGHT / 2f
    }

    override fun update(delta: Float) {
        super.update(delta)

        timeAlive += delta

        val screenMiddle = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        val sinePosition = sin(Math.PI * timeAlive.toDouble()).toFloat()

        sprite.x = screenMiddle + sinePosition * bobAmount
    }

    override fun touch(touchX: Float, touchY: Float) {
        Audio.playSound(Audio.SoundTag.HIT)
        Director.changeGameState(Director.GameState.PLAYING)
    }
}
