package vorpal.chapter01.movers

import vorpal.processing.*

/**
 * The mover class introduced in Chapter 1.
 * THe four algorithms differ in terms of acceleration, and thus we pass the acceleration function in.
 * 0. No acceleration.
 * 1. Constant acceleration (to a maximum velocity).
 * 2, Random acceleration.
 * 3. Acceleration chasing the mouse cursor.
 */
class Mover(var position: Vector2D = Vector2D.halfCanvas(),
            var velocity: Vector2D = Vector2D.ZERO,
            val accelerationFn: () -> Vector2D = { Vector2D.ZERO },
            val topSpeed: Double = 3.0,
            val diameter: Double = 48.0) {

    fun update() {
        val acceleration = accelerationFn()
        velocity = (velocity + acceleration).limitMagnitude(topSpeed)
        position += velocity
    }

    fun show(gc: Graphics) {
        gc.stroke(0)
        gc.strokeWeight(2)
        gc.fill(127)
        gc.circle(position.x, position.y, diameter)
    }

    fun checkEdges() {
        if (this.position.x > width) this.position = this.position.withX(0)
        else if (this.position.x < 0) this.position = this.position.withX(width)
        if (this.position.y > height) this.position = this.position.withY(0)
        else if (this.position.y < 0) this.position = this.position.withY(height)
    }
}