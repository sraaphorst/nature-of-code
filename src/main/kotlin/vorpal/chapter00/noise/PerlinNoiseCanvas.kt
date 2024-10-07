package vorpal.chapter00.noise

import javafx.application.Application
import vorpal.processing.*

class PerlinNoiseCanvas: NoiseCanvas(PerlinNoise(), "Perlin")

fun main() {
    Application.launch(PerlinNoiseCanvas::class.java)
}