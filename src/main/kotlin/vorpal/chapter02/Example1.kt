package vorpal.chapter02

import javafx.application.Application
import vorpal.processing.*
import kotlin.math.absoluteValue
import kotlin.math.sign

class Example1: ProcessingApp() {
    private val movers = listOf(
        Mover(Vector2D(100, 30), 10),
        Mover(Vector2D(400, 30), 2))

    // Constants for gravity and wind.
    private val gravity = Vector2D(0, 0.1)
    private val wind = Vector2D(0.1, 0)

    // Ww would like to add a force at the sides of the canvas to keep it in the canvas.
    // Thus, when approaching 0 or width, we should have max force, and at width / 2, we should have 0.
    // This could probably be improved considerably.
    fun calculateHorizontalForce(mover: Mover): Vector2D {
        val distFromCenter = width / 2 - mover.position.x
        val scaling = sign(distFromCenter) * remap(distFromCenter.absoluteValue, 0, width / 2, 0, 0.5)

        // Small mass = small force, large mass = large force?
        return Vector2D(scaling, 0) / mover.mass
    }

    override fun setup() {
        createCanvas(1000, 1000)
        setTitle("Forces Acting on Two Objects")
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        movers.forEach {
            it.applyForce(gravity * it.mass)
            it.applyForce(calculateHorizontalForce(it))
        }
        if (mouseIsPressed) movers.forEach { it.applyForce(wind) }
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
