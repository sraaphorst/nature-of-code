package vorpal.chapter02

import vorpal.processing.*

/**
 * Notes about units of measurement in this mover:
 * In this chapter, units of measurements indicating distance will be in PIXELS.
 * Mass has no set unit of measurement, and is tied to pixels as well: the larger the diameter of a circle,
 *   the larger the mass; hence, we consider mass a scalar here.
 */
class Mover(var position: Vector2D = Vector2D(width / 2 , 30),
            val mass: Number = 1.0) {

    private val diameter: Double = mass.toDouble() * 16
    private var velocity: Vector2D = Vector2D.ZERO
    private var acceleration: Vector2D = Vector2D.ZERO

    fun applyForce(force: Vector2D) {
        acceleration += (force / mass.toDouble())
    }

    fun update() {
        velocity += acceleration
        position += velocity
        acceleration = Vector2D.ZERO
    }

    fun show(gc: Graphics) {
        gc.stroke(0)
        gc.fill(175)
        gc.circle(position.x, position.y, diameter)
    }

    fun checkEdges() {
        if (position.x > width - diameter) {
            position = position.withX(width - diameter)
            velocity = velocity.bounceX()
        }
        else if (position.x < 0) {
            position = position.withX(0)
            velocity = velocity.bounceX()
        }

        if (position.y > height - diameter) {
            position = position.withY(height - diameter)
            velocity = velocity.bounceY()
        }
        else if (position.y < 0) {
            position = position.withY(0)
            velocity = velocity.bounceY()
        }
    }
}