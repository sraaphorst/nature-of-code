package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

/**
 * This class draws a vector to the mouse point, and then a thicker vector of magnitude 50 in the
 * same direction.
 */
class Vector2: ProcessingApp() {
    override fun setup() {
        createCanvas(1000, 1000)
        setTitle("Vector2")
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        val mouse = Vector2D.mouse() - Vector2D.halfCanvas()

        gc.translate(width / 2, height / 2)
        gc.stroke(200)
        gc.line(0, 0, mouse.x, mouse.y)

        val nm = mouse.normalized * 50
        gc.stroke(0)
        gc.strokeWeight(8)
        gc.line(0, 0, nm.x, nm.y)
    }
}

fun main() {
    Application.launch(Vector2::class.java)
}