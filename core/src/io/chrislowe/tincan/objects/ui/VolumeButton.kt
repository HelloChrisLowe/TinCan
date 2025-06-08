package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.Audio // Required for Audio.updateMusicVolume()
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class VolumeButton(
    val target: VolumeTarget,
    val direction: VolumeDirection,
    yPosition: Float,
    xPosition: Float,
    val buttonText: String // Made buttonText a property to check for "Back"
) : GameObject() {

    private val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
    private val label: Label
    private var buttonWidth = 80f
    private var buttonHeight = 80f

    init {
        // Adjust width for "Back" button text
        if (buttonText == "Back") {
            buttonWidth = 150f
        }

        label = Label(buttonText, labelStyle)
        label.setSize(buttonWidth, buttonHeight)
        label.setAlignment(Align.center)
        label.setPosition(xPosition - buttonWidth / 2f, yPosition - buttonHeight / 2f)

        sprite.x = xPosition - buttonWidth / 2f
        sprite.y = yPosition - buttonHeight / 2f
        sprite.width = buttonWidth
        sprite.height = buttonHeight
    }

    override fun draw(batch: SpriteBatch) {
        label.draw(batch, 1f)
    }

    override fun touch(touchX: Float, touchY: Float) {
        if (buttonText == "Back") {
            Director.showSettingsUI(false)
            return
        }

        var currentVolume: Int
        var newVolume: Int

        if (target == VolumeTarget.SFX) {
            currentVolume = TinCanGame.storedData.getSfxVolume()
            newVolume = if (direction == VolumeDirection.UP) {
                (currentVolume + 10).coerceAtMost(100)
            } else {
                (currentVolume - 10).coerceAtLeast(0)
            }
            TinCanGame.storedData.setSfxVolume(newVolume)
            Director.updateVolumeDisplay(VolumeTarget.SFX, newVolume)
            // Optionally play a sound effect for button press
            // Audio.playSound(Audio.SoundTag.HIT, 0.5f) // Example
        } else { // MUSIC
            currentVolume = TinCanGame.storedData.getMusicVolume()
            newVolume = if (direction == VolumeDirection.UP) {
                (currentVolume + 10).coerceAtMost(100)
            } else {
                (currentVolume - 10).coerceAtLeast(0)
            }
            TinCanGame.storedData.setMusicVolume(newVolume)
            Director.updateVolumeDisplay(VolumeTarget.MUSIC, newVolume)
            Audio.updateMusicVolume() // Apply music volume change immediately
        }
        println("Volume changed: ${target} to ${newVolume}%") // For debugging
    }
}
