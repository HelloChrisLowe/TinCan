package io.chrislowe.tincan.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import io.chrislowe.tincan.TinCanGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "Tin Can"
        config.width = TinCanGame.GAME_WIDTH.toInt()
        config.height = TinCanGame.GAME_HEIGHT.toInt()

        LwjglApplication(TinCanGame(DesktopStoredData()), config)
    }
}
