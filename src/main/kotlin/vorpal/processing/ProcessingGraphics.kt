package vorpal.processing

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

    fun strokeWeight(weight: Number)  = withGC {
        lineWidth = weight.toDouble()
    }

    fun ellipse(x: Number, y: Number, width: Number, height: Number) = withGC {
        fillOval(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    fun circle(x: Number, y: Number, diameter: Number) =
        ellipse(x.toDouble(), y.toDouble(), diameter.toDouble(), diameter.toDouble())

    fun point(x: Number, y: Number) = withGC {
        strokeLine(x.toDouble(), y.toDouble(), x.toDouble(), y.toDouble())
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
            Color.rgb(randomInt(0xFF), randomInt(0xFF), randomInt(0xFF))
    }
}
