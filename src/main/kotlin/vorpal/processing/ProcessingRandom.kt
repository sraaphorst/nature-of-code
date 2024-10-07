package vorpal.processing

import kotlin.math.*
import kotlin.random.Random

typealias Distribution<T> = ProcessingRandom.Distribution<T>
typealias SizedDistribution<T> = ProcessingRandom.SizedDistribution<T>
typealias UniformDoubleDistribution = ProcessingRandom.UniformDoubleDistribution
typealias UniformIntDistribution = ProcessingRandom.UniformIntDistribution
typealias GaussianDistribution = ProcessingRandom.GaussianDistribution
typealias NonUniformDistribution<T> = ProcessingRandom.NonUniformDistribution<T>
typealias CollectionWithReplacementDistribution<T> = ProcessingRandom.CollectionWithReplacementDistribution<T>
typealias MutableCollectionDistribution<T> = ProcessingRandom.MutableCollectionDistribution<T>
typealias ExponentialDistribution = ProcessingRandom.ExponentialDistribution
typealias PoissonDistribution = ProcessingRandom.PoissonDistribution
typealias BernoulliIntDistribution = ProcessingRandom.BernoulliIntDistribution
typealias BinomialDistribution = ProcessingRandom.BinomialDistribution
typealias BetaDistribution = ProcessingRandom.BetaDistribution
typealias GammaDistribution = ProcessingRandom.GammaDistribution

// Random functions that do not require a distribution.
object ProcessingRandom {
    /**
     * Superclass for all distributions.
     */
    sealed interface Distribution<T> {
        fun sample(): T
    }

    /**
     * A distribution with a finite number of elements.
     */
    sealed interface SizedDistribution<T>: Distribution<T> {
        /**
         * The number of elements available in the distribution. If the distribution's contents
         * are changed by sampling or elements can be added, then size must reflect this.
         */
        fun size(): Int
    }

    // Note that the bounds are [min, max), i.e. the upper bound is exclusive.
    class UniformDoubleDistribution(val min: Double, val max: Double): Distribution<Double> {
        constructor(min: Number, max: Number) : this(min.toDouble(), max.toDouble())
        constructor(max: Number) : this(0.0, max.toDouble())

        override fun sample(): Double = Random.nextDouble(min, max)
    }

    /**
     * A uniform discrete distribution that returns a value in the interval [min, max)
     */
    class UniformIntDistribution(val min: Int, val max: Int): SizedDistribution<Int> {
        constructor(max: Int) : this(0, max)

        override fun sample(): Int = Random.nextInt(min, max)
        override fun size(): Int = max - min
    }

    /**
     * A Gaussian / normal distribution with the specified mean and standard deviation.
     */
    class GaussianDistribution(val mean: Double = 0.0, val stdev: Double = 1.0): Distribution<Double> {
        override fun sample(): Double = Random.nextGaussian() * stdev + mean
    }

    /**
     * Given a map of objects and their relative probability of being selected (which does not
     * need to equal 1), determine the sum of the probabilities and considered them scaled to [0,1).
     */
    class NonUniformDistribution<T>(values: Map<T, Double>): SizedDistribution<T> {
        private val totalSum = values.values.sum()
        private val collection = values.toList()
        override fun sample(): T {
            tailrec fun aux(curr: Double = randomDouble(totalSum), idx: Int = 0): T {
                if (curr < collection[idx].second) return collection[idx].first
                else return aux(curr - collection[idx].second, idx + 1)
            }
            return aux()
        }

        override fun size(): Int = collection.size
    }

    /**
     * Given a collection, sample from it with replacement.
     */
    class CollectionWithReplacementDistribution<T>(collection: Collection<T>): SizedDistribution<T> {
        private val elements = collection.toList()
        override fun sample(): T =
            elements.random()

        override fun size(): Int = elements.size
    }

    /**
     * Given a collection, sample from it without replacement. Elements can also be added to this distribution.
     */
    class MutableCollectionDistribution<T>(val collection: Collection<T>): SizedDistribution<T> {
        private val elements = collection.toMutableList()

        init {
            elements.shuffle()
        }

        /**
         * Remove the first element and return it.
         */
        override fun sample(): T =
            elements.removeFirst()

        /**
         * Add an element to the underlying collection.
         */
        fun add(element: T) {
            elements.add(element)
        }

        /**
         * Shuffle the remaining elements of the collection.
         */
        fun shuffle() {
            elements.shuffle()
        }

        override fun size(): Int = elements.size
    }

    /**
     * Used for modelling time between events in a Poisson process, such as waiting times
     * in queueing systems or decay processes.
     */
    class ExponentialDistribution(val rate: Double): Distribution<Double> {
        override fun sample(): Double = -ln(Random.nextDouble()) / rate
    }

    /**
     * A discrete distribution used to model the number of events in a fixed interval of time or space.
     */
    class PoissonDistribution(val lambda: Double = 1.0): Distribution<Int> {
        override fun sample(): Int {
            val l: Double = exp(-lambda)

            tailrec fun aux(k: Int = 0, p: Double = 1.0): Int =
                when {
                    p > l -> k - 1
                    else -> aux(k + 1, p * Random.nextDouble())
                }

            return aux()
        }
    }

    /**
     * Used in binary events, where either 1 or 0 are returned with a specific probability.
     * Equal to a coin toss if p = 0.5.
     * 1 if probability < p, and 0 otherwise.
     */
    class BernoulliIntDistribution(val p: Double): SizedDistribution<Int> {
        override fun sample(): Int = if (Random.nextDouble() < p) 1 else 0
        override fun size(): Int = 2
    }

    /**
     * A Bernouilli distribution that returns true or false instead of 1 or 0.
     */
    class BernoulliBooleanDistribution(val p: Double): SizedDistribution<Boolean> {
        override fun sample(): Boolean = Random.nextDouble() < p
        override fun size(): Int = 2
    }

    /**
     * Models the number of successes in a fixed number of independent trials.
     * Example: we roll a four sided die 10 times to see the number of times
     * a given number is rolled: we then initialize with n = 10, p = 0.25.
     */
    class BinomialDistribution(val n: Int, val p: Double): SizedDistribution<Int> {
        override fun sample(): Int = (1..n).count { Random.nextDouble() < p }
        override fun size(): Int = n + 1
    }

    /**
     * Useful in Bayesian statistics and for modeling probabilities.
     */
    class BetaDistribution(val alpha: Double, val beta: Double): Distribution<Double> {
        private val gamma1 = GammaDistribution(alpha, 1.0)
        private val gamma2 = GammaDistribution(beta, 1.0)

        override fun sample(): Double {
            val x = gamma1.sample()
            val y = gamma2.sample()
            return x / (x + y)
        }
    }

    /**
     * The Gamma distribution is useful for modeling waiting times, reliability analysis, and in Bayesian contexts.
     * Example: in queueing theory, models the sum of multiple exponentially distributed random variables
     * (i.e. waiting times between independent events that occur at a constant average rate).
     */
    class GammaDistribution(val alpha: Double, val beta: Double): Distribution<Double> {
        override fun sample(): Double =
            when {
                alpha > 1 -> marsagliaTsang()
                else -> sampleGamma(alpha + 1, beta) * Random.nextDouble().pow(1.0 / alpha)
            }

        // Recursive for gamma(alpha, beta) where alpha < 1
        private fun sampleGamma(alpha: Double, beta: Double): Double {
            return GammaDistribution(alpha, beta).sample()
        }

        private fun marsagliaTsang(): Double {
            val d = alpha - 1.0 / 3.0
            val c = 1.0 / sqrt(9.0 * d)

            while (true) {
                val x = Random.nextGaussian()
                val v = (1.0 + c * x).pow(3.0)
                if (v > 0) {
                    val u = Random.nextDouble()
                    val x2 = x * x
                    if (u < 1 - 0.0331 * x2 * x2 || ln(u) < 0.5 * x2 + d * (1 - v + ln(v)))
                        return d * v / beta
                }
            }
        }
    }
}

// Simplifications to avoid having to use actual distributions.
fun randomInt(min: Int, max: Int): Int = Random.nextInt(min, max)
fun randomInt(max: Int): Int = Random.nextInt(0, max)
fun randomInt(): Int = randomInt(Int.MIN_VALUE, Int.MAX_VALUE)

fun randomDouble(min: Double, max: Double): Double = Random.nextDouble(min, max)
fun randomDouble(max: Double): Double = randomDouble(0.0, max)
fun randomDouble(): Double = randomDouble(Double.MIN_VALUE, Double.MAX_VALUE)

fun <T> randomElement(elements: Collection<T>): T =
    elements.random()

inline fun <reified T : Enum<T>> randomEnum(): T =
    enumValues<T>().random()

/**
 * We don't want to have to instantiate a Gaussian distribution for every value chosen
 * from a normal distribution, so while this is a bit redundant with the GaussianDistribution,
 * we add the extension into Random for convenience.
 */
fun Random.nextGaussian(mean: Double = 0.0, stdev: Double = 1.0): Double {
    // Use Box-Muller transform to generate Gaussian sample.
    val u1 = Random.nextDouble()
    val u2 = Random.nextDouble()
    val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    return z0 * stdev + mean
}

fun randomGaussian(mean: Double = 0.0, stdev: Double = 1.0): Double = Random.nextGaussian(mean, stdev)

/**
 * The accept-reject algorithm.
 *
 * We want an algorithm that picks a value, and the value itself determines the
 * probability of it being picked. Large values are more likely to be picked most
 * of the time, and small values are less likely. We pick a value and then either
 * accept it or reject it based on what the value is, which can be picked from
 * any probabilistic distribution. It is a Monte Carlo method.
 *
 * proposal: The distribution of the proposal (T can be any type)
 * acceptanceCriterion: Function to compute acceptance probability (must return Double)
 *
 * By default, we use a uniform double distribution [0.0, 1.0) and accept each value according to
 * the probability associated with it, so:
 * 0.1 has a 10% chance of acceptance
 * * 0.9 has a 90% chance of acceptance
 */
fun <T> acceptReject(
    proposal: Distribution<T> = UniformDoubleDistribution(1, 0) as Distribution<T>,
    acceptanceCriterion: (T) -> Double = { r1 -> (r1 as Double) }
): T {
    while (true) {
        // Get sample from proposal distribution.
        val r1 = proposal.sample()

        // Compute the acceptance probability as a Double.
        val p = acceptanceCriterion(r1)

        // Calculate a uniform random number to determine acceptance.
        val r2 = Random.nextDouble(1.0)

        // If we accept it, then return it. Otherwise, continue.
        if (r2 < p) return r1
    }
}
