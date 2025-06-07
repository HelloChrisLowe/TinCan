package io.chrislowe.tincan.objects.game

import io.chrislowe.tincan.*
import io.chrislowe.tincan.objects.GameObject

class Spawner : GameObject() {
    enum class SpawnPhase(val startsAt: Int) {
        SINGLE(0),
        DOUBLE(5),
        TRIPLE(12)
    }

    private val verticalRange = TinCanGame.GAME_HEIGHT / 6
    private val horizontalRange = TinCanGame.GAME_WIDTH / 6

    private val upperBoundTicks = 180
    private val lowerBoundTicks = 60

    private val upperBoundRange = 60
    private val lowerBoundRange = 30

    private var minSpawnTicks = upperBoundTicks
    private var spawnRange = upperBoundRange
    private var spawnPhase = SpawnPhase.SINGLE

    private var spawnTicks = minSpawnTicks + spawnRange
    private var spawnCount = 0

    override fun update() {
        spawnTicks--

        if (spawnTicks == 0) {
            spawnCans()

            if (minSpawnTicks > lowerBoundTicks) minSpawnTicks -= 5
            if (spawnRange > lowerBoundRange) spawnRange -= 3

            if (spawnCount == SpawnPhase.DOUBLE.startsAt) changePhase(SpawnPhase.DOUBLE)
            if (spawnCount == SpawnPhase.TRIPLE.startsAt) changePhase(SpawnPhase.TRIPLE)

            spawnTicks = minSpawnTicks + GameRandom.nextInt(spawnRange)
        }
    }

    private fun spawnCans() {
        spawnCount++

        when (spawnPhase) {
            SpawnPhase.SINGLE -> {spawnRightCan()}
            SpawnPhase.DOUBLE -> {spawnRightCan(); spawnLeftCan()}
            SpawnPhase.TRIPLE -> {spawnRightCan(); spawnLeftCan(); spawnUpperCan()}
        }
    }

    private fun changePhase(newPhase: SpawnPhase) {
        spawnPhase = newPhase

        minSpawnTicks = upperBoundTicks
        spawnRange = upperBoundRange
    }

    private fun spawnRightCan() {
        val can = Can()
        can.sprite.x = TinCanGame.GAME_WIDTH
        can.sprite.y = (TinCanGame.GAME_HEIGHT / 2).plusOrMinus(verticalRange)
        can.xVel = (-800f).plusOrMinus(150f)
        can.yVel = 600f
        Director.gameObjects.add(can)
    }

    private fun spawnLeftCan() {
        val can = Can()
        can.sprite.x = -can.sprite.width
        can.sprite.y = (TinCanGame.GAME_HEIGHT / 2).plusOrMinus(verticalRange)
        can.xVel = 800f.plusOrMinus(150f)
        can.yVel = 600f
        Director.gameObjects.add(can)
    }

    private fun spawnUpperCan() {
        val can = Can()
        can.sprite.x = (TinCanGame.GAME_WIDTH / 2).plusOrMinus(horizontalRange)
        can.sprite.y = TinCanGame.GAME_HEIGHT + can.sprite.height
        can.xVel = 0f
        can.yVel = 0f
        Director.gameObjects.add(can)
    }
}