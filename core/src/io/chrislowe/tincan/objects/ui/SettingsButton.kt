package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.ui.SettingsManager

class SettingsButton : GameObject() {
    init {
        setTexture("settings.png")

        // Using the existing positioning logic for SettingsButton
        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = sprite.height * 2f
    }

    override fun touch(touchX: Float, touchY: Float) {
        SettingsManager.show(true) // Call SettingsManager to show the settings UI
    }
}