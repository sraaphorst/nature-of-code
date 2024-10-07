package vorpal.chapter00.walkers

import javafx.application.Application
import javafx.scene.paint.Color

import vorpal.processing.*

data class Walker(val color: Color = Graphics.randomColor(),
                  var pos: Vector2D = Vector2D.random(0, width.toInt(), 0, height.toInt())) {
    fun show(gc: Graphics) {
        gc.stroke(color)
        gc.strokeWeight(WEIGHT)
        gc.point(pos.x, pos.y)
    }

    // Make the walker take a step.
    fun step() {
        pos += randomEnum<Steps>().delta

        // Wrap around.
        if (pos.x >= width) pos = Vector2D(0, pos.y)
        else if (pos.x < 0) pos = Vector2D(width - 1, pos.y)
        if (pos.y >= height) pos = Vector2D(pos.x, 0)
        else if (pos.y < 0) pos = Vector2D(pos.x, height - 1)
    }

    companion object {
        enum class Steps(val delta: Vector2D) {
            RIGHT(WEIGHT * Vector2D.X),
            LEFT(WEIGHT * -Vector2D.X),
            DOWN(WEIGHT * Vector2D.Y),
            UP(WEIGHT * -Vector2D.Y),
        }

        const val WEIGHT = 5.0
    }
}

class RandomWalkers(val numWalkers: Int = 6): ProcessingApp() {
    private var walkers: List<Walker> = emptyList()

    override fun setup() {
        createCanvas(1048.0, 512.0)
        setTitle("Random Walkers")
        walkers = (1..numWalkers).map { Walker() }
    }

    override fun draw(gc: Graphics) {
        walkers.forEach { w ->
            w.step()
            w.show(gc)
        }
    }
}

fun main() {
    Application.launch(RandomWalkers::class.java)
}
