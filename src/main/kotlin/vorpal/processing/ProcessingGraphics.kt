package vorpal.processing

import vorpal.processing.ProcessingMath as pm

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class ProcessingGraphics(private val gc: GraphicsContext) {
    fun fill(gray: Int, alpha: Int = 255) {
        gc.fill = Color.grayRgb(gray, alpha / 255.0)
    }

    fun stroke(gray: Int, alpha: Int = 255) {
        gc.stroke = Color.grayRgb(gray, alpha / 255.0)
    }

    fun ellipse(x: Double, y: Double, width: Double, height: Double) {
        gc.fillOval(x, y, width, height)
    }

    fun circle(x: Double, y: Double, diameter: Double) {
        ellipse(x, y, diameter, diameter)
    }

    fun point(x: Double, y: Double) {
        gc.strokeLine(x, y, x, y)
    }

    fun background(gray: Int) {
        gc.fill = Color.grayRgb(gray)
        gc.fillRect(0.0, 0.0, gc.canvas.width, gc.canvas.height)
    }

    companion object {
        fun randomColor(): Color =
            Color.rgb(pm.randomInt(0xFF), pm.randomInt(0xFF), pm.randomInt(0xFF))
    }
}
