package io.chrislowe.tincan

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

object TextureCache {
    private val cache = mutableMapOf<String, Texture>()

    fun get(filename: String): Texture {
        return cache.getOrPut(filename) {
            Texture(Gdx.files.internal(filename))
        }
    }

    fun dispose() {
        for (texture in cache.values) {
            texture.dispose()
        }
        cache.clear()
    }
}
