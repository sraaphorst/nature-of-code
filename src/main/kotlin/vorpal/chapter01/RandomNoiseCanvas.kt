package vorpal.chapter01

import javafx.application.Application

class RandomNoiseCanvas: NoiseCanvas(null, "Random")

fun main() {
    Application.launch(RandomNoiseCanvas::class.java)
}