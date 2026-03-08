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

    private val upperBoundSeconds = 180f / TinCanGame.FPS
    private val lowerBoundSeconds = 60f / TinCanGame.FPS

    private val upperBoundRange = 60f / TinCanGame.FPS
    private val lowerBoundRange = 30f / TinCanGame.FPS

    private val decreasePerSpawnMin = 5f / TinCanGame.FPS
    private val decreasePerSpawnRange = 3f / TinCanGame.FPS

    private var minSpawnSeconds = upperBoundSeconds
    private var spawnRange = upperBoundRange
    private var spawnPhase = SpawnPhase.SINGLE

    private var spawnTimer = minSpawnSeconds + spawnRange
    private var spawnCount = 0

    override fun update(delta: Float) {
        spawnTimer -= delta

        if (spawnTimer <= 0f) {
            spawnCans()

            if (minSpawnSeconds > lowerBoundSeconds) minSpawnSeconds -= decreasePerSpawnMin
            if (spawnRange > lowerBoundRange) spawnRange -= decreasePerSpawnRange

            if (spawnCount == SpawnPhase.DOUBLE.startsAt) changePhase(SpawnPhase.DOUBLE)
            if (spawnCount == SpawnPhase.TRIPLE.startsAt) changePhase(SpawnPhase.TRIPLE)

            spawnTimer = minSpawnSeconds + GameRandom.nextFloat(0f, spawnRange)
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

        minSpawnSeconds = upperBoundSeconds
        spawnRange = upperBoundRange
    }

    private fun spawnRightCan() {
        val can = Can.pool.obtain()
        can.sprite.x = TinCanGame.GAME_WIDTH
        can.sprite.y = (TinCanGame.GAME_HEIGHT / 2).plusOrMinus(verticalRange)
        can.xVel = (-800f).plusOrMinus(150f)
        can.yVel = 600f
        Director.gameObjects.add(can)
    }

    private fun spawnLeftCan() {
        val can = Can.pool.obtain()
        can.sprite.x = -can.sprite.width
        can.sprite.y = (TinCanGame.GAME_HEIGHT / 2).plusOrMinus(verticalRange)
        can.xVel = 800f.plusOrMinus(150f)
        can.yVel = 600f
        Director.gameObjects.add(can)
    }

    private fun spawnUpperCan() {
        val can = Can.pool.obtain()
        can.sprite.x = (TinCanGame.GAME_WIDTH / 2).plusOrMinus(horizontalRange)
        can.sprite.y = TinCanGame.GAME_HEIGHT + can.sprite.height
        can.xVel = 0f
        can.yVel = 0f
        Director.gameObjects.add(can)
    }
}
