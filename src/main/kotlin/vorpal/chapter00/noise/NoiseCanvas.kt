package vorpal.chapter00.noise

import javafx.scene.paint.Color
import vorpal.processing.*

abstract class NoiseCanvas(val noiseType: ProcessingNoise.Noise?,
                           val noiseName: String,
                           val scale: Double = 0.025): ProcessingApp() {
    private var t = 0.0

    override fun setup() {
        createCanvas(512, 512)
        setTitle("$noiseName Noise")
    }

    override fun draw(gc: Graphics) {
        (0 until width.toInt()).forEach { w ->
            (0 until height.toInt()).forEach { h ->
                // Generates a value in [-1, 1].
                val z = noiseType
                    ?.noise(w * scale, h * scale, t)
                    ?.let { ((it + 1) / 2.0).coerceIn(0.0, 1.0) }
                    ?: randomDouble(1.0)
                val c = Color.gray(z)
                gc.stroke(c)
                gc.point(w.toDouble(), h.toDouble())
            }
        }
        t += scale
    }
}