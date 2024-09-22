package org.vorpal.vorpal.chapter01

import javafx.application.Application
import javafx.scene.paint.Color
import javafx.stage.Stage

import vorpal.processing.ProcessingGraphics as pg
import vorpal.processing.ProcessingRandom as pr
import vorpal.processing.ProcessingVector as pv
import vorpal.processing.*

data class Walker(val color: Color = pg.randomColor(),
                  var pos: pv.Vector2D = pv.Vector2D.random(0, width.toInt(), 0, height.toInt())) {
    fun show(gc: pg) {
        gc.stroke(color)
        gc.strokeWeight(weight)
        gc.point(pos.x, pos.y)
    }

    // Make the walker take a step.
    fun step() {
        pos += pr.randomEnum<Steps>().delta

        // Wrap around.
        if (pos.x >= width) pos = pv.Vector2D(0, pos.y)
        else if (pos.x < 0) pos = pv.Vector2D(width - 1, pos.y)
        if (pos.y >= height) pos = pv.Vector2D(pos.x, 0)
        else if (pos.y < 0) pos = pv.Vector2D(pos.x, height - 1)
    }

    companion object {
        enum class Steps(val delta: pv.Vector2D) {
            RIGHT(weight * pv.Vector2D.X),
            LEFT(weight * -pv.Vector2D.X),
            DOWN(weight * pv.Vector2D.Y),
            UP(weight * -pv.Vector2D.Y),
        }

        const val weight = 5.0
    }
}

class RandomWalkers(val numWalkers: Int = 6): ProcessingApp() {
    private var walkers: List<Walker> = emptyList()

    override fun start(stage: Stage) {
        // For now, we need to create the Canvas and call the superclass start to initialize the stage.
        createCanvas(1048.0, 512.0)
        super.start(stage)
        setTitle("Random Walkers")
        walkers = (1..numWalkers).map { Walker() }
    }

    override fun draw(gc: pg) {
        walkers.forEach { w ->
            w.step()
            w.show(gc)
        }
    }
}

fun main() {
    Application.launch(RandomWalkers::class.java)
}
