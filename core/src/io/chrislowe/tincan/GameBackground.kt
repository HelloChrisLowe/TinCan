package io.chrislowe.tincan

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

object GameBackground {
    private const val ZERO_OFFSET = 0f

    private val whiteBackground = Texture(Gdx.files.internal("white.png"))
    private val starBackground = Texture(Gdx.files.internal("stars.png"))

    var blindTimer = 0
    var shakeTimer = 0

    fun drawBackground(batch: SpriteBatch) {
        when {
            blindTimer > 0 -> {drawWhiteBackground(batch); blindTimer--}
            shakeTimer > 0 -> {drawShakingBackground(batch); shakeTimer--}
            else -> drawNormalBackground(batch)
        }
    }

    private fun drawWhiteBackground(batch: SpriteBatch) {
        batch.draw(whiteBackground, ZERO_OFFSET, ZERO_OFFSET)
    }

    private fun drawShakingBackground(batch: SpriteBatch) {
        val randomOffset = GameRandom.nextFloat(-8f, -1f)
        batch.draw(starBackground, randomOffset, randomOffset)
    }

    private fun drawNormalBackground(batch: SpriteBatch) {
        batch.draw(starBackground, ZERO_OFFSET, ZERO_OFFSET)
    }
}