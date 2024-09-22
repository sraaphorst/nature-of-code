package vorpal.processing

import vorpal.processing.ProcessingRandom as pr

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class ProcessingGraphics(private val gc: GraphicsContext) {
    fun fill(gray: Int, alpha: Int = 255) {
        gc.fill = Color.grayRgb(gray, alpha / 255.0)
    }

    fun stroke(color: Color) {
        gc.stroke = color
    }

    fun stroke(gray: Int, alpha: Int = 255) {
        gc.stroke = Color.grayRgb(gray, alpha / 255.0)
    }

    fun strokeWeight(weight: Double) {
        gc.lineWidth = weight
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
            Color.rgb(pr.randomInt(0xFF), pr.randomInt(0xFF), pr.randomInt(0xFF))
    }
}
