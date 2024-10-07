package vorpal.chapter01.movers

import javafx.application.Application
import vorpal.processing.*

class Algorithm3: AbstractAlgorithm() {
    override val velocity = Vector2D.ZERO
    override fun acceleration() = (Vector2D.mouse() - mover.position).setMagnitude(0.2)
}

fun main() {
    Application.launch(Algorithm3::class.java)
}