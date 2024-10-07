package vorpal.chapter00.noise

import javafx.application.Application
import vorpal.processing.*

class WorleyNoiseCanvas: NoiseCanvas(WorleyNoise(), "Worley")

fun main() {
    Application.launch(WorleyNoiseCanvas::class.java)
}