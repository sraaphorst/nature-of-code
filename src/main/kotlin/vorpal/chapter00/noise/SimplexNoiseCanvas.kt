package vorpal.chapter00.noise

import javafx.application.Application
import vorpal.processing.*

class SimplexNoiseCanvas: NoiseCanvas(SimplexNoise(), "Simplex")

fun main() {
    Application.launch(SimplexNoiseCanvas::class.java)
}