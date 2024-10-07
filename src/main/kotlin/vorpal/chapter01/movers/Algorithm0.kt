package vorpal.chapter01.movers

import javafx.application.Application
import vorpal.processing.*

/**
 * No acceleration.
 */
class Algorithm0: AbstractAlgorithm() {
    override val velocity = Vector2D(randomDouble(-2, 2), randomDouble(-2, 2))
    override fun acceleration() = Vector2D.ZERO
}

fun main() {
    Application.launch(Algorithm0::class.java)
}