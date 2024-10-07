package vorpal.chapter00.noise

import javafx.application.Application
import vorpal.processing.*

class RandomNoiseCanvas: NoiseCanvas(UniformRandomNoise(), "Random")

fun main() {
    Application.launch(RandomNoiseCanvas::class.java)
}