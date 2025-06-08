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
        label.setSize(width, label.height) // Width can be adjusted
        label.setAlignment(alignment)
        // GameObject's sprite position is usually its bottom-left. Label's position is also bottom-left.
        // If xPosition is center, we need to adjust.
        val actualX = if (alignment == Align.center) xPosition - width / 2f else xPosition
        label.setPosition(actualX, yPosition)

        // Set GameObject's sprite properties if needed for touch detection or dimensions, though this is a display-only object.
        // For simplicity, we'll make its bounds match the label.
        sprite.x = label.x
        sprite.y = label.y
        sprite.setSize(label.width, label.height)
    }

    override fun draw(batch: SpriteBatch) {
        label.draw(batch, 1f)
    }
}
