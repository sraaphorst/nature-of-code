package vorpal.processing

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class ProcessingGraphics(private val gc: GraphicsContext?) {
    private inline fun withGC(action: GraphicsContext.() -> Unit) =
        gc?.apply(action)

    fun fill(r: Number, g: Number, b: Number) = withGC {
        fill = Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

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

    fun line(startX: Number, startY: Number, endX: Number, endY: Number) = withGC {
        strokeLine(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble())
    }

    fun line(from: Vector2D, to: Vector2D) = withGC {
        strokeLine(from.x, from.y, to.x, to.y)
    }

    fun background(gray: Int) = withGC {
        fill = Color.grayRgb(gray)
        fillRect(0.0, 0.0, canvas.width, canvas.height)
    }

    fun clear()  = withGC {
        clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

    fun translate(x: Number, y: Number) = withGC {
        translate(x.toDouble(), y.toDouble())
    }

    fun translate(vector: Vector2D) = withGC {
        translate(vector.x, vector.y)
    }

    fun save() = withGC {
        save()
    }

    fun restore() = withGC {
        restore()
    }

    companion object {
        fun randomColor(): Color =
            Color.rgb(randomInt(0xFF), randomInt(0xFF), randomInt(0xFF))
    }
}
