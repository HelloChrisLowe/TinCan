package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class SettingsButton : GameObject() {
    init {
        setTexture("settings.png")

        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = sprite.height * 2f
    }

    override fun touch(touchX: Float, touchY: Float) {

    }
}