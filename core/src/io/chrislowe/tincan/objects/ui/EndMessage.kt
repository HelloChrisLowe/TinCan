package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.*
import io.chrislowe.tincan.objects.GameObject
import kotlin.math.sin

class EndMessage(hasHighScore: Boolean) : GameObject() {
    private val bobAmount = 64

    private var timeAlive = 0f

    init {
        val imageName = if (hasHighScore) "congrats.png" else "tryagain.png"
        setTexture(imageName)

        sprite.setScale((TinCanGame.GAME_WIDTH / sprite.width) * 0.85f)
        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = 2f * (TinCanGame.GAME_HEIGHT - sprite.height) / 3f
    }

    override fun update(delta: Float) {
        super.update(delta)

        timeAlive += delta

        val screenMiddle = TinCanGame.GAME_HEIGHT / 3f
        val sinePosition = sin(Math.PI * timeAlive.toDouble()).toFloat()

        sprite.y = screenMiddle + sinePosition * bobAmount
    }
}
