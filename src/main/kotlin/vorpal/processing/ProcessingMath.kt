package vorpal.processing

import kotlin.math.PI

object ProcessingMath {
    // Given a value d in the range [a, b], convert it to d' in the range [u, v].
    // This must obviously be a Double in result because the ratios are unlikely to be Ints. )
    fun <T: Number, W: Number> remap(d: T, a: T, b: T, u: W, v: W): Double {
        val dD = d.toDouble()
        val aD = a.toDouble()
        val bD = b.toDouble()
        val uD = u.toDouble()
        val vD = v.toDouble()
        return (dD - aD) * (vD - uD) / (bD - aD) * (vD - aD) + uD
    }
}

data class Angle(val value: Double, val unit: AngleUnit) {
    fun toDegrees(): Angle = when (unit) {
        AngleUnit.DEGREE -> this
        AngleUnit.RADIAN -> Angle(value * PI / 180.0, AngleUnit.RADIAN)
    }

    fun toRadians(): Angle = when (unit) {
        AngleUnit.DEGREE -> Angle(value * 180.0 / PI, AngleUnit.DEGREE)
        AngleUnit.RADIAN -> this
    }

    fun normalize(): Angle  = when {
        unit == AngleUnit.DEGREE && (value < 0 || value >= 360) ->
            Angle(((value % 360.0) + 360.0) % 360.0, AngleUnit.DEGREE)
        unit == AngleUnit.RADIAN && (value < 0 || value > TWO_PI) ->
            Angle(((value % TWO_PI) + TWO_PI) % TWO_PI, AngleUnit.RADIAN)
        else -> this
    }

    // Trig functions.
    fun sin(): Double = kotlin.math.sin(toRadians().value)
    fun cos(): Double = kotlin.math.cos(toRadians().value)
    fun tan(): Double = kotlin.math.tan(toRadians().value)

    companion object {
        enum class AngleUnit {
            DEGREE,
            RADIAN,
        }

        const val TWO_PI = 2 * PI

        fun asin(scalar: Scalar): Angle = Angle(kotlin.math.asin(scalar), AngleUnit.RADIAN)
        fun acos(scalar: Scalar): Angle = Angle(kotlin.math.acos(scalar), AngleUnit.RADIAN)
        fun atan(scalar: Scalar): Angle = Angle(kotlin.math.atan(scalar), AngleUnit.RADIAN)
    }
}
