package io.chrislowe.tincan

import java.util.*

class GameRandom {
    companion object : Random() {
        override fun nextInt(from: Int, to: Int): Int {
            val difference = to - from
            return from + nextInt(difference)
        }

        override fun nextFloat(from: Float, to: Float): Float {
            val difference = to - from
            return from + difference * nextFloat()
        }

        override fun nextDouble(from: Double, to: Double): Double {
            val difference = to - from
            return from + difference * nextDouble()
        }
    }
}

fun Int.plusOrMinus(amount: Int): Int = GameRandom.nextInt(this - amount, this + amount)

fun Float.plusOrMinus(amount: Float): Float = this + amount * GameRandom.nextFloat(-1f, 1f)
