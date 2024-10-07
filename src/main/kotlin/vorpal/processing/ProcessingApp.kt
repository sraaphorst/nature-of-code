package vorpal.processing

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.stage.Stage

// Global access to width and height like in Processing.
var width: Double = 0.0
var height: Double = 0.0

// Global access to the mouse coordinates like in Processing.
var mouseX: Double = 0.0
var mouseY: Double = 0.0

abstract class ProcessingApp: Application() {
    /**
     * By default, we have a 100x100 Canvas unless:
     * 1. A Canvas is explicitly created with createCanvas; or
     * 2. There is a deliberate specification of no Canvas with noCanvas.
     */
    private var canvas: Canvas? = null
    private var noCanvasCalled: Boolean = false
    private lateinit var gc: ProcessingGraphics
    private lateinit var stage: Stage

    final override fun init() {
        super.init()
    }

    fun <T: Number> createCanvas(w: T, h: T) {
        val wp = w.toDouble()
        val hp = h.toDouble()
        assert(wp > 0) { "createCanvas($w,$h) has illegal width $w" }
        assert(hp > 0) { "createCanvas($w,$h) has illegal height $h" }
        noCanvasCalled = false
        width = wp
        height = hp
        canvas = Canvas(width, height)
        gc = ProcessingGraphics(canvas!!.graphicsContext2D)
    }

    fun noCanvas() {
        noCanvasCalled = true
        width = 0.0
        height = 0.0
        canvas = null
        gc = ProcessingGraphics(null)
    }

    fun setTitle(title: String) {
        stage.title = title
    }

    final override fun start(stage: Stage) {
        this.stage = stage
        stage.title = "My Processing App"

        // Perform any setup that needs to be done, including creating the canvas.
        setup()

        val pane = Pane()

        // Unless we explicitly call for no canvas, a canvas of 100x100 is called.
        if (!noCanvasCalled && canvas == null)
            createCanvas(100.0, 100.0)
        canvas?.let { pane.children.add(it) }

        val scene = Scene(pane)
        scene.setOnMouseMoved { evt ->
            mouseX = evt.x
            mouseY = evt.y
        }

        stage.scene = scene
        stage.show()

        // Create the AnimationTimer for continuous drawing
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                gc.save()
                // Call the draw method in each frame
                draw(gc)
                gc.restore()
            }
        }

        // Start the timer
        timer.start()
    }

    final override fun stop() {
        super.stop()
        Platform.exit()
        System.exit(0)
    }

    /**
     * The default implementation of setup does nothing, which results in a 100x100 canvas.
     */
    open fun setup() {}

    /**
     * The default draw implementation does nothing. Note that if noCanvas is called, the
     * ProcessingGraphics inside here ignores all calls since there is no Canvas on which
     * to work.
     */
    open fun draw(gc: Graphics) {}
}
