package vorpal.processing

import vorpal.processing.ProcessingRandom as pr
import kotlin.math.atan2
import kotlin.math.sqrt

typealias Scalar = Double

sealed interface ProcessingVector<T: ProcessingVector<T>> {
    val magnitude: Double
    val normalized: T

    operator fun unaryMinus(): T
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(scalar: Scalar): T
    operator fun times(scalar: Number): T
    operator fun div(scalar: Scalar): T
    operator fun div(scalar: Number): T
    fun dot(other: T): Double

    /**
     * Project the vector a onto this.
     */
    fun project(a: T): T {
        val scalar = (a.dot(this as T)) / (this.dot(this as T))
        return this * scalar
    }

    /**
     * Reflect this vector across another vector or plane. Note that the vector n is normalized
     * here, so pre-normalization is not necessary.
     */
    fun reflect(n: T): T {
        val nNormalized = n.normalized
        return this - 2 * (this.dot(nNormalized)) * nNormalized
    }

    /**
     * Calculate the angle between two vectors.
     */
    fun angle(b: T): Angle {
        val dotProduct = this.dot(b) / (this.magnitude * b.magnitude)

        // Clamp to -1,1].
        val clampedDotProduct = dotProduct.coerceIn(-1.0, 1.0)
        return Angle.acos(clampedDotProduct)
    }

    /**
     * Distance between two vectors: just the magnitude of their difference.
     */
    fun distance(other: T): Double = (this - other).magnitude

    /**
     * Limit the magnitude of the vector so that the magnitude is at most the specified limit
     */
    fun limitMagnitude(maxMagnitude: Double): T = when {
        this.magnitude <= maxMagnitude -> this as T
        else -> normalized * maxMagnitude
    }

    fun clampMagnitude(minMagnitude: Double, maxMagnitude: Double): T = when {
        magnitude < minMagnitude -> normalized * minMagnitude
        magnitude > maxMagnitude -> normalized * maxMagnitude
        else -> this as T
    }

    /**
     * Hadamard product: multiplication of pairwise elements.
     */
    fun hadamardProduct(other: T): T

    data class Vector3D(val x: Double, val y: Double, val z: Double) : ProcessingVector<Vector3D> {
        constructor(x: Number, y: Number, z: Number)
                : this(x.toDouble(), y.toDouble(), z.toDouble())

        override val magnitude: Double by lazy { sqrt(x * x + y * y + z * z) }
        override val normalized by lazy {
            if (magnitude == 0.0) this@Vector3D else Vector3D(x / magnitude, y / magnitude, z / magnitude)
        }

        override operator fun unaryMinus(): Vector3D = Vector3D(-x, -y, -z)
        override operator fun plus(other: Vector3D): Vector3D = Vector3D(x + other.x, y + other.y, z + other.z)
        override operator fun minus(other: Vector3D): Vector3D = Vector3D(x - other.x, y - other.y, z - other.z)
        override operator fun times(scalar: Scalar): Vector3D = Vector3D(x * scalar, y * scalar, z * scalar)
        override operator fun times(scalar: Number): Vector3D =
            Vector3D(x * scalar.toDouble(), y * scalar.toDouble(), z * scalar.toDouble())

        override operator fun div(scalar: Double): Vector3D = Vector3D(x / scalar, y / scalar, z / scalar)
        override operator fun div(scalar: Number): Vector3D =
            Vector3D(x / scalar.toDouble(), y / scalar.toDouble(), z / scalar.toDouble())

        override fun dot(other: Vector3D): Double = x * other.x + y * other.y + z * other.z
        fun cross(other: Vector3D): Vector3D = Vector3D(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )

        override fun hadamardProduct(other: Vector3D): Vector3D =
            Vector3D(this.x * other.x, this.y * other.y, this.z * other.z)

        fun projectToYZ(): Vector2D = Vector2D(y, z)
        fun projectToXZ(): Vector2D = Vector2D(x, z)
        fun projectToXY(): Vector2D = Vector2D(x, y)

        companion object {
            val ZERO = Vector3D(0.0, 0.0, 0.0)
            val X = Vector3D(1.0, 0.0, 0.0)
            val Y = Vector3D(0.0, 1.0, 0.0)
            val Z = Vector3D(0.0, 0.0, 1.0)

            fun random(min: Int, max: Int): Vector3D =
                Vector3D(pr.randomInt(min, max), pr.randomInt(min, max), pr.randomInt(min, max))

            fun random(max: Int): Vector3D = random(0, max)
            fun random(min: Double, max: Double) =
                Vector3D(pr.randomDouble(min, max), pr.randomDouble(min, max), pr.randomDouble(min, max))

            fun random(max: Double): Vector3D = random(0.0, max)
            fun random(
                dx: pr.Distribution<Double>,
                dy: pr.Distribution<Double>,
                dz: pr.Distribution<Double>
            ) =
                Vector3D(dx.sample(), dy.sample(), dz.sample())

            fun random(d: pr.Distribution<Double>): Vector3D = random(d, d, d)
        }
    }

    data class Vector2D(val x: Double, val y: Double) : ProcessingVector<Vector2D> {
        constructor(x: Number, y: Number)
                : this(x.toDouble(), y.toDouble())

        override val magnitude: Double by lazy { sqrt(x * x + y * y) }
        override val normalized: Vector2D by lazy {
            if (magnitude == 0.0) this@Vector2D else Vector2D(x / magnitude, y / magnitude)
        }

        override operator fun unaryMinus(): Vector2D = Vector2D(-x, -y)
        override operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
        override operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
        override operator fun times(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)
        override operator fun times(scalar: Number): Vector2D = Vector2D(x * scalar.toDouble(), y * scalar.toDouble())
        override operator fun div(scalar: Double): Vector2D = Vector2D(x / scalar, y / scalar)
        override operator fun div(scalar: Number): Vector2D = Vector2D(x / scalar.toDouble(), y / scalar.toDouble())
        override fun dot(other: Vector2D): Double = x * other.x + y * other.y

        /**
         * In 2D space, we can find a perpendicular vector.
         * Note that the dot product of x * perpendicular(x) will always be 0.
         * A zero vector obviously does not have a perpendicular vector.
         */
        fun perpendicular(): Vector2D? =
            if (this == ZERO) null else Vector2D(-y, x)

        override fun hadamardProduct(other: Vector2D): Vector2D =
            Vector2D(this.x * other.x, this.y * other.y)

        fun rotate(angle: Angle): Vector2D {
            val c = angle.cos()
            val s = angle.sin()
            return Vector2D(x * c - y * s, x * s + y * c)
        }

        /**
         * Lift the vector up into the plane at z. By default z = 0.
         */
        fun lift(z: Double = 0.0): Vector3D = Vector3D(x, y, z)
        fun lift(z: Number): Vector3D = Vector3D(x, y, z.toDouble())

        /**
         * Calculate the arctan between the vector and the x--axis.
         */
        fun atan2(): Angle = Angle(atan2(y, x), Angle.Companion.AngleUnit.RADIAN)

        companion object {
            val ZERO = Vector2D(0.0, 0.0)
            val X = Vector2D(1.0, 0.0)
            val Y = Vector2D(0.0, 1.0)

            fun random(min: Int, max: Int): Vector2D = Vector2D(pr.randomInt(min, max), pr.randomInt(min, max))
            fun random(max: Int): Vector2D = random(0, max)
            fun random(min: Double, max: Double) = Vector2D(pr.randomDouble(min, max), pr.randomDouble(min, max))
            fun random(max: Double): Vector2D = random(0.0, max)
            fun random(dx: pr.Distribution<Double>, dy: pr.Distribution<Double>) =
                Vector2D(dx.sample(), dy.sample())
            fun random(d: pr.Distribution<Double>): Vector2D = random(d, d)
        }
    }
}

operator fun <T: ProcessingVector<T>> Number.times(vector: T) = vector * this

/**
 * Linear interpolation between two vectors by supplying a value [0, 1], where
 * 0 will give you a, and 1 will give you b. It is possible to use other values, which
 * will interpolate as if the vectors continue.
 */
fun <T: ProcessingVector<T>> lerp(u: T, v: T, t: Double): T =
    (1.0 - t) * u + t * v
