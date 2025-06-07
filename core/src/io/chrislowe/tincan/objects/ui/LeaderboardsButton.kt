package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class LeaderboardsButton : GameObject() {
    init {
        setTexture("leaderboards.png")

        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = 24f
    }

    override fun touch(touchX: Float, touchY: Float) {

    }
}