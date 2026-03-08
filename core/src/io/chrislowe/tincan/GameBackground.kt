package io.chrislowe.tincan

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

object GameBackground {
    private const val ZERO_OFFSET = 0f

    private lateinit var whiteBackground: Texture
    private lateinit var starBackground: Texture

    fun init() {
        whiteBackground = TextureCache.get("white.png")
        starBackground = TextureCache.get("stars.png")
    }

    var blindTimer = 0f
    var shakeTimer = 0f

    fun drawBackground(batch: SpriteBatch, delta: Float) {
        when {
            blindTimer > 0f -> {drawWhiteBackground(batch); blindTimer -= delta}
            shakeTimer > 0f -> {drawShakingBackground(batch); shakeTimer -= delta}
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