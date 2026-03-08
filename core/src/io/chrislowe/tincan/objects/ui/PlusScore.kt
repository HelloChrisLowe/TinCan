package io.chrislowe.tincan.objects.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.GameRandom
import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class PlusScore(bonus: Int) : GameObject() {
    private val text = "+$bonus"

    private val label: Label

    init {
        val xOffset = TinCanGame.GAME_WIDTH - GameRandom.nextInt(80, 140)
        val yOffset = TinCanGame.GAME_HEIGHT - GameRandom.nextInt(100, 140)

        val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
        label = Label(text, labelStyle)
        label.setSize(TinCanGame.GAME_WIDTH, label.height)
        label.setPosition(xOffset, yOffset)

        secondsUntilDestruction = 30f / TinCanGame.FPS

        Director.increaseScore(bonus)
    }

    override fun update(delta: Float) {
        super.update(delta)

        label.y += 50f * delta
    }

    override fun draw(batch: SpriteBatch) {
        label.draw(batch, 1f)
    }
}
