package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

class Vector1(diameter: Number = 48): ProcessingApp() {
    private val d: Double = diameter.toDouble()
    private val r: Double = d / 2.0
    private var t: Double = 0.0

    private lateinit var position: Vector2D
    private lateinit var velocity: Vector2D

    override fun setup() {
        createCanvas(640, 320)
        setTitle("Vector1")
        position = Vector2D.random(d, width - d, d, height - d)
        velocity = Vector2D(1.5, 2)
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        position += velocity

        // Correct bounce logic for the left, right, top, and bottom walls
        if (position.x <= 0 || position.x + d >= width) velocity = velocity.bounceX()
        if (position.y <= 0 || position.y + d >= height) velocity = velocity.bounceY()

        // We are going to set the color of the ball based on Perlin noise.
        // This doesn't work as well as we would like.
        t += timeEpsilon
        val r = (noise(position.x * epsilon, position.y * epsilon, t) + 1) / 2.0 * 0xff
        val g = (noise(position.x * 2 * epsilon, position.y * 2 * epsilon, t + 100) + 1) / 2.0 * 0xff
        val b = (noise(position.x * 3 * epsilon, position.y * 3 * epsilon, t + 200) + 1) / 2.0 * 0xff

        gc.stroke(0)
        gc.fill(r, g, b)
        gc.circle(position.x, position.y, d)
    }

    companion object {
        const val timeEpsilon = 1e-4
        const val epsilon = 1e-3
    }
}

fun main() {
    Application.launch(Vector1::class.java)
}