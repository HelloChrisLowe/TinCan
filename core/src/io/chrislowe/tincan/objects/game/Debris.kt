package io.chrislowe.tincan.objects.game

import io.chrislowe.tincan.GameRandom
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.plusOrMinus

class Debris : GameObject() {
    init {
        val textureName = when (GameRandom.nextInt(3)) {
            0 -> "gib0.png"
            1 -> "gib1.png"
            2 -> "gib2.png"
            else -> throw RuntimeException("Bad random value")
        }
        setTexture(textureName)

        val randomVel = 0f.plusOrMinus(400f)
        rotationVel = randomVel + 600
        xVel = randomVel / 4
        yVel = -(randomVel * 4 - 200)
        gravity = -4000f
    }

    override fun update(delta: Float) {
        super.update(delta)

        if (sprite.y - sprite.height < 0) deleteSelf()
    }
}