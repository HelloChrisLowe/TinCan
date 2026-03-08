package io.chrislowe.tincan.objects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.chrislowe.tincan.Director
import io.chrislowe.tincan.TextureCache

abstract class GameObject {
    companion object {
        const val TOUCH_BUFFER = 32
    }

    var sprite: Sprite = Sprite()

    var xVel = 0f
    var yVel = 0f
    var rotationVel = 0f

    var gravity = 0f

    var secondsUntilDestruction = -1f

    open fun update(delta: Float) {
        sprite.x += xVel * delta
        sprite.y += yVel * delta
        sprite.rotation += rotationVel * delta

        yVel += gravity * delta

        if (secondsUntilDestruction > 0f) {
            secondsUntilDestruction -= delta

            if (secondsUntilDestruction <= 0f) {
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
        setTexture(TextureCache.get(filename))
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