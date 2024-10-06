package vorpal.processing

import vorpal.processing.ProcessingRandom as pr

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class ProcessingGraphics(private val gc: GraphicsContext?) {
    private inline fun withGC(action: GraphicsContext.() -> Unit) =
        gc?.apply(action)

    fun fill(gray: Int, alpha: Int = 255) = withGC {
        fill = Color.grayRgb(gray, alpha / 255.0)
    }

    fun stroke(color: Color) = withGC {
        stroke = color
    }

    fun stroke(gray: Int, alpha: Int = 255) = withGC {
        stroke = Color.grayRgb(gray, alpha / 255.0)
    }

    fun strokeWeight(weight: Double)  = withGC {
        lineWidth = weight
    }

    fun ellipse(x: Double, y: Double, width: Double, height: Double) = withGC {
        fillOval(x, y, width, height)
    }

    fun circle(x: Double, y: Double, diameter: Double) =
        ellipse(x, y, diameter, diameter)

    fun point(x: Double, y: Double) = withGC {
        strokeLine(x, y, x, y)
    }

    fun background(gray: Int) = withGC {
        fill = Color.grayRgb(gray)
        fillRect(0.0, 0.0, canvas.width, canvas.height)
    }

    fun clear()  = withGC {
        clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

    companion object {
        fun randomColor(): Color =
            Color.rgb(pr.randomInt(0xFF), pr.randomInt(0xFF), pr.randomInt(0xFF))
    }
}
