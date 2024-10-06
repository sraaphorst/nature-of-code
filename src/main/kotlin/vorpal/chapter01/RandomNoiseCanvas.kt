package org.vorpal.vorpal.chapter01

import javafx.application.Application
import javafx.scene.paint.Color
import vorpal.processing.*

class RandomNoiseCanvas: ProcessingApp() {
    override fun setup() {
        createCanvas(512, 512)
        setTitle("Random Noise")
    }

    override fun draw(gc: Graphics) {
        val u = ProcessingRandom.UniformDoubleDistribution(0.0, 1.0)
        (0 until width.toInt()).forEach { w ->
            (0 until height.toInt()).forEach { h ->
                // Generate a random value in [0, 1].
                val c = Color.gray(u.sample())
                gc.stroke(c)
                gc.point(w.toDouble(), h.toDouble())
            }
        }
    }
}

fun main() {
    Application.launch(RandomNoiseCanvas::class.java)
}