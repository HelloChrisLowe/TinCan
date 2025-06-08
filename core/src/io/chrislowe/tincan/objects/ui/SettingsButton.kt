package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.Director // Ensure this import is present
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class SettingsButton : GameObject() {
    init {
        setTexture("settings.png")

        // Using the existing positioning logic for SettingsButton
        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = sprite.height * 2f
    }

    override fun touch(touchX: Float, touchY: Float) {
        Director.showSettingsUI(true) // Call Director to show the settings UI
    }
}