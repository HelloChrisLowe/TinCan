package io.chrislowe.tincan.objects.game

import io.chrislowe.tincan.GameRandom
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.plusOrMinus

class Spark : GameObject() {
    init {
        setTexture("spark.png")
        sprite.rotation = GameRandom.nextInt(360).toFloat()
        xVel = 0f.plusOrMinus(2000f)
        yVel = 0f.plusOrMinus(2000f)

        secondsUntilDestruction = 4f / TinCanGame.FPS
    }
}