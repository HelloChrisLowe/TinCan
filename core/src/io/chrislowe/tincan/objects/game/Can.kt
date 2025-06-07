package io.chrislowe.tincan.objects.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Pool
import io.chrislowe.tincan.*
import io.chrislowe.tincan.objects.GameObject
import io.chrislowe.tincan.objects.ui.PlusScore

class Can : GameObject(), Pool.Poolable {
    private val particleCount = 3

    private var damage = 0
    private val lethal = 4

    private lateinit var arrowLabel: Label

    init { reset() }

    override fun reset() {
        sprite.rotation = 0f
        rotationVel = 0f
        gravity = -900f

        damage = 0

        createLabel()
        updateImage()
        punchUp()
    }

    private fun createLabel() {
        val labelStyle = Label.LabelStyle(TinCanGame.textFont, Color.WHITE)
        arrowLabel = Label("^", labelStyle)
    }

    private fun updateImage() {
        val imageName = when (damage) {
            0   -> "can0.png"
            1   -> "can1.png"
            2   -> "can2.png"
            else-> "can3.png"
        }

        setTexture(imageName)
    }

    private fun punchUp() {
        xVel = 0f.plusOrMinus(860f)
        yVel = 680f

        borderCan()
    }

    private fun borderCan() {
        xVel *= -1
        rotationVel = ((-rotationVel * 20) + xVel) / 10
    }

    override fun update() {
        super.update()

        if (rotationVel > 200f)
            rotationVel = 200f

        if (rotationVel < -200f)
            rotationVel = -200f

        if (sprite.x > TinCanGame.GAME_WIDTH - sprite.width && xVel > 0)
            borderCan()

        if (sprite.x < 0 && xVel < 0)
            borderCan()

        if (sprite.y < 0)
            Director.changeGameState(Director.GameState.FINISHED)
    }

    override fun draw(batch: SpriteBatch) {
        super.draw(batch)

        if (sprite.y > TinCanGame.GAME_HEIGHT) {
            arrowLabel.setPosition(sprite.x, TinCanGame.GAME_HEIGHT - 48)
            arrowLabel.draw(batch, 1f)
        }
    }

    override fun touch(touchX: Float, touchY: Float) {
        GameBackground.shakeTimer = 6

        damage++

        if (damage == lethal) {
            destroy()
        } else {
            takeHit(touchX, touchY)
            updateImage()
        }
    }

    fun destroy() {
        Audio.playSound(Audio.SoundTag.KILL)
        GameBackground.blindTimer = 3

        val newObjects = ArrayList<GameObject>(particleCount + 2)
        (1..particleCount).forEach { _ ->
            val debris = Debris()
            debris.jumpToObject(this)
            debris.sprite.x += GameRandom.nextFloat(0f, sprite.width)
            debris.sprite.y += GameRandom.nextFloat(0f, sprite.height)
            newObjects.add(debris)
        }

        val boom = Boom()
        boom.jumpToObject(this)
        newObjects.add(boom)

        val plusScore = PlusScore(5)
        newObjects.add(plusScore)

        Director.gameObjects.addAll(newObjects)
        Director.gameObjects.remove(this)
    }

    private fun takeHit(hitX: Float, hitY: Float) {
        Audio.playSound(Audio.SoundTag.HIT)

        val newObjects = ArrayList<GameObject>(particleCount + 1)
        (1..particleCount).forEach { _ ->
            val spark = Spark()
            spark.sprite.x = hitX
            spark.sprite.y = hitY
            newObjects.add(spark)
        }

        val plusScore = PlusScore(1)
        newObjects.add(plusScore)

        Director.gameObjects.addAll(newObjects)

        punchUp()
    }
}