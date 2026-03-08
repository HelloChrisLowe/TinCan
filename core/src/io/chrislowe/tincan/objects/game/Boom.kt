package io.chrislowe.tincan.objects.game

import io.chrislowe.tincan.GameBackground
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class Boom : GameObject() {
    init {
        setTexture("boom.png")

        GameBackground.blindTimer = 1f / TinCanGame.FPS
        secondsUntilDestruction = 3f / TinCanGame.FPS
    }
}
