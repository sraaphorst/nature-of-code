package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

class SimplexNoiseCanvas: NoiseCanvas(ProcessingNoise.SimplexNoise, "Simplex")

fun main() {
    Application.launch(SimplexNoiseCanvas::class.java)
}