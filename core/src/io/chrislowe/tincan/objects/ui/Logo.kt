package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class Logo : GameObject() {
    private val copyrightText = ""  // Disable for now.
    private val copyrightX = 0f
    private val copyrightY = TinCanGame.GAME_HEIGHT / 3f
    private val copyrightLabel: Label

    init {
        setTexture("logo.png")
        sprite.setScale((TinCanGame.GAME_WIDTH / sprite.width) * 0.85f)
        sprite.x = TinCanGame.GAME_WIDTH / 2f - sprite.width / 2f
        sprite.y = 2 * (TinCanGame.GAME_HEIGHT - sprite.height) / 3f

        val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
        copyrightLabel = Label(copyrightText, labelStyle)
        copyrightLabel.setSize(TinCanGame.GAME_WIDTH, copyrightLabel.height)
        copyrightLabel.setAlignment(Align.center)
        copyrightLabel.setPosition(copyrightX, copyrightY)
    }

    override fun draw(batch: SpriteBatch) {
        super.draw(batch)
        copyrightLabel.draw(batch, 1f)
    }
}