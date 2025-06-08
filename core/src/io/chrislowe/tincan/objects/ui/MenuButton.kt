package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.Audio
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.ui.SettingsManager

enum class ButtonActionType {
    SFX_VOL_UP, SFX_VOL_DOWN,
    MUSIC_VOL_UP, MUSIC_VOL_DOWN,
    NAV_BACK
}

class MenuButton(
    val actionType: ButtonActionType,
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

        sprite.setPosition(xPosition - buttonWidth / 2f, yPosition - buttonHeight / 2f)
        sprite.setSize(buttonWidth, buttonHeight)
    }

    override fun draw(batch: SpriteBatch) {
        val oldColor = batch.color.cpy()

        batch.color = Color.WHITE
        batch.draw(whitePixelTexture, sprite.x, sprite.y, sprite.width, sprite.height)

        batch.color = oldColor

        label.draw(batch, 1f)
    }

    override fun touch(touchX: Float, touchY: Float) {
        var currentVolume: Int
        var newVolume: Int

        when (actionType) {
            ButtonActionType.NAV_BACK -> {
                SettingsManager.show(false)
                Audio.playSound(Audio.SoundTag.HIT) // Play sound for back button
            }
            ButtonActionType.SFX_VOL_UP -> {
                currentVolume = TinCanGame.storedData.getSfxVolume()
                newVolume = (currentVolume + 10).coerceAtMost(100)
                TinCanGame.storedData.setSfxVolume(newVolume)
                SettingsManager.updateVolumeDisplay(SettingsManager.VolumeTarget.SFX, newVolume)
                Audio.playSound(Audio.SoundTag.HIT)
            }
            ButtonActionType.SFX_VOL_DOWN -> {
                currentVolume = TinCanGame.storedData.getSfxVolume()
                newVolume = (currentVolume - 10).coerceAtLeast(0)
                TinCanGame.storedData.setSfxVolume(newVolume)
                SettingsManager.updateVolumeDisplay(SettingsManager.VolumeTarget.SFX, newVolume)
                Audio.playSound(Audio.SoundTag.HIT)
            }
            ButtonActionType.MUSIC_VOL_UP -> {
                currentVolume = TinCanGame.storedData.getMusicVolume()
                newVolume = (currentVolume + 10).coerceAtMost(100)
                TinCanGame.storedData.setMusicVolume(newVolume)
                SettingsManager.updateVolumeDisplay(SettingsManager.VolumeTarget.MUSIC, newVolume)
                Audio.updateMusicVolume() // Apply music volume change immediately
                Audio.playSound(Audio.SoundTag.HIT)
            }
            ButtonActionType.MUSIC_VOL_DOWN -> {
                currentVolume = TinCanGame.storedData.getMusicVolume()
                newVolume = (currentVolume - 10).coerceAtLeast(0)
                TinCanGame.storedData.setMusicVolume(newVolume)
                SettingsManager.updateVolumeDisplay(SettingsManager.VolumeTarget.MUSIC, newVolume)
                Audio.updateMusicVolume() // Apply music volume change immediately
                Audio.playSound(Audio.SoundTag.HIT)
            }
        }
    }
}
