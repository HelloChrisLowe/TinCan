package io.chrislowe.tincan

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class TinCanGame(platformStoredData: StoredData) : ApplicationAdapter() {
    companion object {
        const val FPS = 60
        const val GAME_WIDTH = 720f
        const val GAME_HEIGHT = 1280f
        lateinit var storedData: StoredData
        lateinit var textFont: BitmapFont
    }

    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch

    init {
        storedData = platformStoredData
    }

    override fun create() {
        camera = OrthographicCamera()
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT)
        camera.update()

        batch = SpriteBatch()
        batch.projectionMatrix = camera.combined

        textFont = BitmapFont(Gdx.files.internal("scorefont.fnt"))

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val scaleX = GAME_WIDTH / Gdx.app.graphics.width
                val scaleY = GAME_HEIGHT / Gdx.app.graphics.height

                val touchX = screenX * scaleX
                val touchY = GAME_HEIGHT - (screenY * scaleY)

                Director.handleTouchEvent(touchX, touchY)
                return true
            }
        }

        GameBackground.init()
        Audio.init()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        Director.updateGameObjects(delta)
        Audio.updateAudio(delta)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        GameBackground.drawBackground(batch, delta)
        Director.drawGameObjects(batch)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        textFont.dispose()
        Audio.dispose()
        TextureCache.dispose()
    }
}
