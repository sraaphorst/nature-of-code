package vorpal.chapter01.movers

import javafx.application.Application
import vorpal.processing.*

/**
 * Algorithm 1: Constant acceleration.
 */
class Algorithm1 : AbstractAlgorithm() {
    override val velocity = Vector2D.ZERO
    override fun acceleration() = Vector2D(-0.001, 0.01)
}

fun main() {
    Application.launch(Algorithm1::class.java)
}
