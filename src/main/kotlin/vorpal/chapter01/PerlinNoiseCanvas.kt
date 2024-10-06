package org.vorpal.vorpal.chapter01

import javafx.application.Application
import javafx.scene.paint.Color
import vorpal.processing.*

class PerlinNoiseCanvas(val scale: Double = 0.02): ProcessingApp() {
    private var t = 0.0

    override fun setup() {
        createCanvas(512, 512)
        setTitle("Perlin Noise")
    }

    override fun draw(gc: Graphics) {
        (0 until width.toInt()).forEach { w ->
            (0 until height.toInt()).forEach { h ->
                // Generates a value in [-1, 1].
                val z_orig = noise(w * scale, h * scale, t)
                val z = (z_orig + 1) / 2.0
                val c = Color.gray(z)
                gc.stroke(c)
                gc.point(w.toDouble(), h.toDouble())
            }
        }
        t += scale
    }
}

fun main() {
    Application.launch(PerlinNoiseCanvas::class.java)
}