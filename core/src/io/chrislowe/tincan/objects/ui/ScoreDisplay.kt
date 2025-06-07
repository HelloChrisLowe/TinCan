package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject
import java.util.Locale

class ScoreDisplay : GameObject() {
    private val currentScoreText = "CURR %04d   "
    private val highScoreText = "   HIGH %04d"

    private val whiteLabelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
    private val goldLabelStyle = Label.LabelStyle(TinCanGame.textFont, Color.GOLD)

    private val currentScoreLabel = Label(currentScoreText, whiteLabelStyle)
    private val highScoreLabel = Label(highScoreText, whiteLabelStyle)

    init {
        currentScoreLabel.setSize(TinCanGame.GAME_WIDTH, currentScoreLabel.height)
        currentScoreLabel.setAlignment(Align.right)
        currentScoreLabel.setPosition(0f, TinCanGame.GAME_HEIGHT - currentScoreLabel.height * 2f)
        highScoreLabel.setSize(TinCanGame.GAME_WIDTH, highScoreLabel.height)
        highScoreLabel.setAlignment(Align.left)
        highScoreLabel.setPosition(0f, TinCanGame.GAME_HEIGHT - highScoreLabel.height * 2f)
    }

    override fun draw(batch: SpriteBatch) {
        currentScoreLabel.setText(String.format(Locale.ENGLISH, currentScoreText, Director.gameScore))
        highScoreLabel.setText(String.format(Locale.ENGLISH, highScoreText, Director.highScore))

        if (Director.hasHighScore) highScoreLabel.style = goldLabelStyle

        currentScoreLabel.draw(batch, 1f)
        highScoreLabel.draw(batch, 1f)
    }
}