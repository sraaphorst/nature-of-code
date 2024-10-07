package vorpal.chapter01.movers

import javafx.application.Application
import vorpal.processing.*

/**
 * Random acceleration.
 */
class Algorithm2: AbstractAlgorithm() {
    override val velocity: Vector2D = Vector2D.ZERO
    override fun acceleration(): Vector2D = Vector2D.random().setMagnitude(0.5)
}

fun main() {
    Application.launch(Algorithm2::class.java)
}