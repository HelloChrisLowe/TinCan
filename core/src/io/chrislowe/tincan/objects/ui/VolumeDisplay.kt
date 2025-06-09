package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import java.util.Locale

class VolumeDisplay(initialPercentage: Int, yPosition: Float, xPosition: Float = TinCanGame.GAME_WIDTH / 2f, width: Float = 150f) : GameObject() {

    private val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
    private val label: Label
    private var currentText = ""

    init {
        label = Label("", labelStyle) // Initial text set by updateText
        label.setSize(width, label.height)
        label.setAlignment(Align.center)
        // Adjust xPosition for center alignment
        val actualX = xPosition - width / 2f
        label.setPosition(actualX, yPosition)
        updateText(initialPercentage) // Set initial text

        // Set GameObject's sprite properties
        sprite.x = label.x
        sprite.y = label.y
        sprite.setSize(label.width, label.height)
    }

    fun updateText(newPercentage: Int) {
        currentText = String.format(Locale.ENGLISH, "%d%%", newPercentage)
        label.setText(currentText)
    }

    override fun draw(batch: SpriteBatch) {
        label.draw(batch, 1f)
    }
}
