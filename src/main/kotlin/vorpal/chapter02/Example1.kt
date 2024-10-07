package vorpal.chapter02

import javafx.application.Application
import vorpal.processing.*

class Example1: ProcessingApp() {
    private val movers = listOf(
        Mover(Vector2D(100, 30), 10),
        Mover(Vector2D(400, 30), 2))

    // Constants for gravity and wind.
    private val gravity = Vector2D(0, 0.1)
    private val wind = Vector2D(0.1, 0)

    override fun setup() {
        createCanvas(1000, 1000)
        setTitle("Forces Acting on Two Objects")
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        movers.forEach { it.applyForce(gravity) }
        if (mouseIsPressed) movers.forEach { it.applyForce(wind) }
        println(mouseIsPressed)
        movers.forEach {
            it.checkEdges()
            it.update()
            it.show(gc)
        }
    }
}

fun main() {
    Application.launch(Example1::class.java)
}
