package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

class PerlinNoiseCanvas: NoiseCanvas(ProcessingNoise.PerlinNoise(), "Perlin")

fun main() {
    Application.launch(PerlinNoiseCanvas::class.java)
}