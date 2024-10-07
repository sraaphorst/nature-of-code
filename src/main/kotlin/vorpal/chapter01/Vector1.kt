package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

class Vector1(diameter: Number = 48): ProcessingApp() {
    private val d: Double = diameter.toDouble()
    private val r: Double = d / 2.0

    private lateinit var position: Vector2D
    private lateinit var velocity: Vector2D

    override fun setup() {
        createCanvas(640, 240)
        setTitle("Vector1")
        position = Vector2D.random(d, width - d, d, height - d)
        velocity = Vector2D(3, 2)
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        position += velocity

        // Correct bounce logic for the left, right, top, and bottom walls
        if (position.x <= 0 || position.x + d >= width) {
            velocity = velocity.bounceX() // Reverse the X direction
        }
        if (position.y <= 0 || position.y + d >= height) {
            velocity = velocity.bounceY() // Reverse the Y direction
        }

        gc.stroke(0)
        gc.fill(127)
        gc.circle(position.x, position.y, d)
    }
}

fun main() {
    Application.launch(Vector1::class.java)
}