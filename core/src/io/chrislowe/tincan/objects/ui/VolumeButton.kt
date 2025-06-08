package io.chrislowe.tincan.objects.ui

enum class VolumeTarget {
    SFX,
    MUSIC
}

enum class VolumeDirection {
    UP,
    DOWN
}

import com.badlogic.gdx.Gdx // For Gdx.files.internal
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture // For whitePixelTexture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.Audio
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class VolumeButton(
    val target: VolumeTarget,
    val direction: VolumeDirection,
    yPosition: Float,
    xPosition: Float,
    val buttonText: String
) : GameObject() {

    private val whitePixelTexture: Texture
    private val blackTextStyle = Label.LabelStyle(TinCanGame.textFont, Color.BLACK) // New style for black text
    private val label: Label
    private var buttonWidth = 80f
    private var buttonHeight = 80f

    init {
        whitePixelTexture = Texture(Gdx.files.internal("white.png"))

        if (buttonText == "Back" || buttonText == "⬅ Back") {
            buttonWidth = 200f
        }

        label = Label(buttonText, blackTextStyle)
        label.setSize(buttonWidth, buttonHeight)
        label.setAlignment(Align.center)
        label.setPosition(xPosition - buttonWidth / 2f, yPosition - buttonHeight / 2f)

        sprite.x = xPosition - buttonWidth / 2f
        sprite.y = yPosition - buttonHeight / 2f
        sprite.width = buttonWidth
        sprite.height = buttonHeight
    }

    override fun draw(batch: SpriteBatch) {
        val oldColor = batch.color.cpy()

        batch.color = Color.WHITE
        batch.draw(whitePixelTexture, sprite.x, sprite.y, sprite.width, sprite.height)

        batch.color = oldColor

        label.draw(batch, 1f)
    }

    override fun touch(touchX: Float, touchY: Float) {
        if (buttonText == "Back" || buttonText == "⬅ Back") {
            Director.showSettingsUI(false)
            Audio.playSound(Audio.SoundTag.HIT)
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
            Audio.playSound(Audio.SoundTag.HIT)
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
            Audio.playSound(Audio.SoundTag.HIT)
        }
    }
}
