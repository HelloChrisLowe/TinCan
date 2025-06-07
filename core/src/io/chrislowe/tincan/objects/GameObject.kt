package io.chrislowe.tincan.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TinCanGame

abstract class GameObject {
    companion object {
        const val TOUCH_BUFFER = 32
    }

    var sprite: Sprite = Sprite()

    var xVel = 0f
    var yVel = 0f
    var rotationVel = 0f

    var gravity = 0f

    var ticksUntilDestruction = -1

    open fun update() {
        val fps = TinCanGame.FPS.toFloat()

        sprite.x += xVel / fps
        sprite.y += yVel / fps
        sprite.rotation += rotationVel / fps

        yVel += gravity / fps

        if (ticksUntilDestruction > 0) {
            ticksUntilDestruction--

            if (ticksUntilDestruction == 0) {
                deleteSelf()
            }
        }
    }

    open fun draw(batch: SpriteBatch) {
        if (sprite.texture != null) {
            sprite.draw(batch)
        }
    }

    open fun touch(touchX: Float, touchY: Float) {}

    open fun isTouched(touchX: Float, touchY: Float) =
                touchX > centerX() - sprite.width - TOUCH_BUFFER &&
                touchX < centerX() + sprite.width + TOUCH_BUFFER &&
                touchY > centerY() - sprite.height - TOUCH_BUFFER &&
                touchY < centerY() + sprite.height + TOUCH_BUFFER

    fun jumpToObject(other: GameObject) {
        sprite.x = other.sprite.x
        sprite.y = other.sprite.y
        sprite.rotation = other.sprite.rotation
    }

    fun deleteSelf() {
        Director.gameObjects.remove(this)
    }

    fun setTexture(filename: String) {
        setTexture(Texture(Gdx.files.internal(filename)))
    }

    private fun setTexture(texture: Texture) {
        val x = sprite.x
        val y = sprite.y
        val rotation = sprite.rotation

        sprite = Sprite(texture)
        sprite.x = x
        sprite.y = y
        sprite.rotation = rotation
        sprite.setOriginCenter()
    }

    private fun centerX() = sprite.x + sprite.originX
    private fun centerY() = sprite.y + sprite.originY
}