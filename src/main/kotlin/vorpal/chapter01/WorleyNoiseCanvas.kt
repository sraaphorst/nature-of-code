package vorpal.chapter01

import javafx.application.Application
import vorpal.processing.*

class WorleyNoiseCanvas: NoiseCanvas(ProcessingNoise.WorleyNoise(), "Worley")

fun main() {
    Application.launch(WorleyNoiseCanvas::class.java)
}