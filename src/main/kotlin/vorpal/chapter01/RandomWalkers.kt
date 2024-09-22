package org.vorpal.vorpal.chapter01

import javafx.application.Application
import javafx.scene.paint.Color
import vorpal.processing.ProcessingApp
import vorpal.processing.ProcessingMath
import vorpal.processing.ProcessingGraphics as pg
import vorpal.processing.height
import vorpal.processing.width
import vorpal.processing.ProcessingRandom as pr
import vorpal.processing.ProcessingVector as pv

data class Walker(val color: Color = pg.randomColor(),
                  var pos: pv.Vector2D = pv.Vector2D(width / 2.0, height / 2.0)) {
    fun show(gc: pg) {
        gc.stroke(0)
        gc.point(pos.x, pos.y)
    }

    // Make the walker take a step.
    fun step() {
        pos += pr.randomEnum<Steps>().delta
    }

    companion object {
        enum class Steps(val delta: pv.Vector2D) {
            RIGHT(pv.Vector2D.X),
            LEFT(-pv.Vector2D.X),
            DOWN(pv.Vector2D.Y),
            UP(-pv.Vector2D.Y),
        }
    }
}

class RandomWalkers(numWalkers: Int = 6): ProcessingApp() {
    init {
        createCanvas(640.0, 240.0)
        setTitle("Random Walkers")
    }

    private val walkers = (1..numWalkers).map { Walker() }

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
