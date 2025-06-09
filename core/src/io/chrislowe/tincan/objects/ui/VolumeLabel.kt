package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class VolumeLabel(text: String, yPosition: Float, xPosition: Float = TinCanGame.GAME_WIDTH / 2f, width: Float = 300f, alignment: Int = Align.center) : GameObject() {

    private val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
    private val label: Label

    init {
        label = Label(text, labelStyle)
        label.setSize(width, label.height)
        label.setAlignment(alignment)

        val actualX = if (alignment == Align.center) xPosition - width / 2f else xPosition
        label.setPosition(actualX, yPosition)

        sprite.x = label.x
        sprite.y = label.y
        sprite.setSize(label.width, label.height)
    }

    override fun draw(batch: SpriteBatch) {
        label.draw(batch, 1f)
    }

    fun setFontScale(scale: Float) {
        label.setFontScale(scale)
    }

    // Method to apply scaling and then re-center based on new preferred width
    fun setFontScaleAndRecenter(scale: Float, originalXCenter: Float, originalYPos: Float) {
        label.setFontScale(scale)
        // Get new preferred dimensions after scaling
        val newPrefWidth = label.prefWidth
        val newPrefHeight = label.prefHeight

        label.setSize(newPrefWidth, newPrefHeight) // Update label size

        // Recalculate X for centering
        val actualX = if (label.labelAlign == Align.center) originalXCenter - newPrefWidth / 2f else originalXCenter
        label.setPosition(actualX, originalYPos)

        // Update sprite bounds to match new label dimensions
        sprite.setPosition(label.x, label.y)
        sprite.setSize(newPrefWidth, newPrefHeight)
    }
}
