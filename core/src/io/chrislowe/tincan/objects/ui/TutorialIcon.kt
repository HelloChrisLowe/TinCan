package io.chrislowe.tincan.objects.ui

import io.chrislowe.tincan.TinCanGame
import io.chrislowe.tincan.objects.GameObject

class TutorialIcon(private val parent: GameObject) : GameObject() {

    init {
        if (!TinCanGame.storedData.showTutorialIcon()) deleteSelf()
        else setTexture("taphere.png")
    }

    override fun update(delta: Float) {
        super.update(delta)

        jumpToObject(parent)
        sprite.x += 70
        sprite.y -= 24
    }
}