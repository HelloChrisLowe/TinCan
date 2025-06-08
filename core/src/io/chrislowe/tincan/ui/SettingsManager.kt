package io.chrislowe.tincan.ui

import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.ui.MenuButton // Changed import
import io.chrislowe.tincan.objects.ui.VolumeDisplay
import io.chrislowe.tincan.objects.ui.VolumeLabel
import io.chrislowe.tincan.objects.ui.ButtonActionType // Import new Enum

object SettingsManager {
    public enum class VolumeTarget { SFX, MUSIC }

    private var settingsUIVisible = false

    private lateinit var settingsTitle: VolumeLabel
    private lateinit var sfxVolumeLabel: VolumeLabel
    private lateinit var sfxVolumeDisplay: VolumeDisplay
    private lateinit var sfxVolumeUpButton: MenuButton // Changed type
    private lateinit var sfxVolumeDownButton: MenuButton // Changed type

    private lateinit var musicVolumeLabel: VolumeLabel
    private lateinit var musicVolumeDisplay: VolumeDisplay
    private lateinit var musicVolumeUpButton: MenuButton // Changed type
    private lateinit var musicVolumeDownButton: MenuButton // Changed type

    private lateinit var backButton: MenuButton // Changed type

    fun initialize() {
        val screenWidth = TinCanGame.GAME_WIDTH
        val screenHeight = TinCanGame.GAME_HEIGHT
        val centerX = screenWidth / 2f

        settingsTitle = VolumeLabel("Settings", screenHeight * 0.85f, xPosition = centerX, alignment = Align.center)
        settingsTitle.setFontScaleAndRecenter(1.5f, centerX, screenHeight * 0.85f)

        sfxVolumeLabel = VolumeLabel("SFX Volume", screenHeight * 0.75f, xPosition = centerX, alignment = Align.center)
        sfxVolumeDisplay = VolumeDisplay(100, screenHeight * 0.70f, xPosition = centerX)
        sfxVolumeDownButton = MenuButton(ButtonActionType.SFX_VOL_DOWN, screenHeight * 0.70f, centerX - 120f, "-")
        sfxVolumeUpButton = MenuButton(ButtonActionType.SFX_VOL_UP, screenHeight * 0.70f, centerX + 120f, "+")

        musicVolumeLabel = VolumeLabel("Music Volume", screenHeight * 0.60f, xPosition = centerX, alignment = Align.center)
        musicVolumeDisplay = VolumeDisplay(100, screenHeight * 0.55f, xPosition = centerX)
        musicVolumeDownButton = MenuButton(ButtonActionType.MUSIC_VOL_DOWN, screenHeight * 0.55f, centerX - 120f, "-")
        musicVolumeUpButton = MenuButton(ButtonActionType.MUSIC_VOL_UP, screenHeight * 0.55f, centerX + 120f, "+")

        backButton = MenuButton(ButtonActionType.NAV_BACK, screenHeight * 0.25f, centerX, "⬅ Back")
    }

    fun show(visible: Boolean) {
        settingsUIVisible = visible
        Director.gameObjects.clear()

        if (visible) {
            sfxVolumeDisplay.updateText(TinCanGame.storedData.getSfxVolume())
            musicVolumeDisplay.updateText(TinCanGame.storedData.getMusicVolume())

            Director.gameObjects.addAll(listOf(
                settingsTitle,
                sfxVolumeLabel, sfxVolumeDisplay, sfxVolumeDownButton, sfxVolumeUpButton,
                musicVolumeLabel, musicVolumeDisplay, musicVolumeDownButton, musicVolumeUpButton,
                backButton
            ))
        } else {
            Director.setupMenu() // setupMenu is part of Director to restore its state
        }
    }

    fun updateVolumeDisplay(target: SettingsManager.VolumeTarget, value: Int) {
        when (target) {
            SettingsManager.VolumeTarget.SFX -> sfxVolumeDisplay.updateText(value)
            SettingsManager.VolumeTarget.MUSIC -> musicVolumeDisplay.updateText(value)
        }
    }

    fun isSettingsUIVisible(): Boolean {
        return settingsUIVisible
    }
}
