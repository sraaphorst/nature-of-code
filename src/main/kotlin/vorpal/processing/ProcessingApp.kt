package vorpal.processing

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.stage.Stage

// Global access to width and height like in Processing.
var width: Double = 0.0
var height: Double = 0.0

abstract class ProcessingApp: Application() {
    private lateinit var canvas: Canvas
    private lateinit var gc: ProcessingGraphics
    private lateinit var stage: Stage

    fun createCanvas(w: Double, h: Double) {
        width = w
        height = h
        canvas = Canvas(width, height)
        gc = ProcessingGraphics(canvas.graphicsContext2D)
    }

    fun setTitle(title: String) {
        stage.title = title
    }

    final override fun start(stage: Stage) {
        this.stage = stage
        stage.title = "My Processing App"

        // Perform any setup that needs to be done, including creating the canvas.
        // TODO: Check what to do if we don't want a canvas or don't create one.
        setup()

        val pane = Pane()
        pane.children.add(canvas)
        val scene = Scene(pane)

        stage.scene = scene
        stage.show()

        // Create the AnimationTimer for continuous drawing
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                // Call the draw method in each frame
                draw(gc)
            }
        }

        // Start the timer
        timer.start()
    }

    // TODO: Probably want defaults that do nothing for these two functions, but have to handle canvas somehow.
    abstract fun setup()
    abstract fun draw(gc: ProcessingGraphics)
}
