package vorpal.chapter01.movers

import vorpal.processing.*

abstract class AbstractAlgorithm: ProcessingApp() {
    lateinit var mover: Mover
    open val velocity: Vector2D = Vector2D.ZERO
    abstract fun acceleration(): Vector2D

    override fun setup() {
        createCanvas(1000, 1000)
        val position = Vector2D(randomDouble(width), randomDouble(height))
        mover = Mover(position, velocity, ::acceleration)
    }

    override fun draw(gc: Graphics) {
        gc.background(255)
        mover.update()
        mover.checkEdges()
        mover.show(gc)
    }
}