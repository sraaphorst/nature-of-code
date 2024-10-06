package vorpal.processing

import kotlin.math.abs
import kotlin.math.sqrt

class ProcessingNoise {
    sealed interface Noise {
        fun noise(x: Double): Double
        fun noise(x: Double, y: Double): Double
        fun noise(x: Double, y: Double, z: Double): Double
        fun noise(x: Double, y: Double, z: Double, w: Double): Double
    }

    // Use repeats instead of repeat because repeat is a Kotlin keyword.
    class PerlinNoise(private val repeats: Int = -1) : Noise {
        // Perlin 1D noise
        override fun noise(x: Double): Double {
            // Step 1: Calculate grid cell coordinates
            val xi = fastFloor(x) and 0xff

            // Step 2: Calculate relative position within the cell
            val xf = x - fastFloor(x)

            // Step 3: Calculate fade values for interpolation
            val u = fade(xf)

            // Step 4: Hash coordinates of the four corners of the grid cell
            val aa = p[xi]
            val bb = p[inc(xi)]

            // Step 5: Calculate gradients for the four corners using 2D grad
            val gradAA = grad(aa, xf)
            val gradBB = grad(bb, xf - 1)

            // Step 6: Interpolate between the gradients
            return lerp(gradAA, gradBB, u)
        }

        // Perlin 2D noise
        override fun noise(x: Double, y: Double): Double {
            val xi = fastFloor(x) and 0xff
            val yi = fastFloor(y) and 0xff

            val xf = x - fastFloor(x)
            val yf = y - fastFloor(y)

            val u = fade(xf)
            val v = fade(yf)

            val aa = p[p[xi] + yi] // Top-left
            val ab = p[p[xi] + inc(yi)] // Bottom-left
            val ba = p[p[inc(xi)] + yi] // Top-right
            val bb = p[p[inc(xi)] + inc(yi)] // Bottom-right

            val gradAA = grad(aa, xf, yf)
            val gradBA = grad(ba, xf - 1, yf)
            val gradAB = grad(ab, xf, yf - 1)
            val gradBB = grad(bb, xf - 1, yf - 1)

            val x1 = lerp(gradAA, gradBA, u)
            val x2 = lerp(gradAB, gradBB, u)
            return lerp(x1, x2, v)
        }

        // Perlin 3D noise
        override fun noise(x: Double, y: Double, z: Double): Double {
            val xi = fastFloor(x) and 0xff
            val yi = fastFloor(y) and 0xff
            val zi = fastFloor(z) and 0xff

            val xf = x - fastFloor(x)
            val yf = y - fastFloor(y)
            val zf = z - fastFloor(z)

            val u = fade(xf)
            val v = fade(yf)
            val w = fade(zf)

            val aaa = p[p[p[xi] + yi] + zi]
            val aba = p[p[p[xi] + inc(yi)] + zi]
            val aab = p[p[p[xi] + yi] + inc(zi)]
            val abb = p[p[p[xi] + inc(yi)] + inc(zi)]
            val baa = p[p[p[inc(xi)] + yi] + zi]
            val bba = p[p[p[inc(xi)] + inc(yi)] + zi]
            val bab = p[p[p[inc(xi)] + yi] + inc(zi)]
            val bbb = p[p[p[inc(xi)] + inc(yi)] + inc(zi)]

            val gradAAA = grad(aaa, xf, yf, zf)
            val gradBAA = grad(baa, xf - 1, yf, zf)
            val gradABA = grad(aba, xf, yf - 1, zf)
            val gradBBA = grad(bba, xf - 1, yf - 1, zf)
            val gradAAB = grad(aab, xf, yf, zf - 1)
            val gradBAB = grad(bab, xf - 1, yf, zf - 1)
            val gradABB = grad(abb, xf, yf - 1, zf - 1)
            val gradBBB = grad(bbb, xf - 1, yf - 1, zf - 1)

            val x1 = lerp(gradAAA, gradBAA, u)
            val x2 = lerp(gradABA, gradBBA, u)
            val y1 = lerp(x1, x2, v)

            val x3 = lerp(gradAAB, gradBAB, u)
            val x4 = lerp(gradABB, gradBBB, u)
            val y2 = lerp(x3, x4, v)

            return lerp(y1, y2, w)
        }

        // Perlin 4D noise
        override fun noise(x: Double, y: Double, z: Double, w: Double): Double {
            val xp = fastFloor(x) and 0xff
            val yp = fastFloor(y) and 0xff
            val zp = fastFloor(z) and 0xff
            val wp = fastFloor(w) and 0xff

            val xf = x - fastFloor(x)
            val yf = y - fastFloor(y)
            val zf = z - fastFloor(z)
            val wf = w - fastFloor(w)

            val u = fade(xf)
            val v = fade(yf)
            val t = fade(zf)
            val s = fade(wf)

            // Hash coordinates of the 16 corners of the hypercube
            val aaa = p[p[p[p[xp] + yp] + zp] + wp]
            val aba = p[p[p[p[xp] + inc(yp)] + zp] + wp]
            val aab = p[p[p[p[xp] + yp] + inc(zp)] + wp]
            val abb = p[p[p[p[xp] + inc(yp)] + inc(zp)] + wp]
            val baa = p[p[p[p[inc(xp)] + yp] + zp] + wp]
            val bba = p[p[p[p[inc(xp)] + inc(yp)] + zp] + wp]
            val bab = p[p[p[p[inc(xp)] + yp] + inc(zp)] + wp]
            val bbb = p[p[p[p[inc(xp)] + inc(yp)] + inc(zp)] + wp]

            val x1 = lerp(
                grad(aaa, xf, yf, zf, wf),
                grad(baa, xf - 1, yf, zf, wf),
                u
            )
            val x2 = lerp(
                grad(aba, xf, yf - 1, zf, wf),
                grad(bba, xf - 1, yf - 1, zf, wf),
                u
            )
            val y1 = lerp(x1, x2, v)

            val x3 = lerp(
                grad(aab, xf, yf, zf - 1, wf),
                grad(bab, xf - 1, yf, zf - 1, wf),
                u
            )
            val x4 = lerp(
                grad(abb, xf, yf - 1, zf - 1, wf),
                grad(bbb, xf - 1, yf - 1, zf - 1, wf),
                u
            )
            val y2 = lerp(x3, x4, v)
            val z1 = lerp(y1, y2, t)
            val z2 = lerp(x3, x4, t)  // Add this to properly blend the fourth interpolated value
            return lerp(z1, z2, s)
        }

        private fun inc(num: Int): Int =
            if (repeats > 0) (num + 1) % repeats else num + 1

        companion object {
            // Gradient for 1D
            private fun grad(hash: Int, x: Double): Double =
                if (hash and 1 == 0) x else -x

            // Gradient for 2D
            private fun grad(hash: Int, x: Double, y: Double): Double {
                val h = hash and 3
                val u = if (h and 1 == 0) x else -x
                val v = if (h and 2 == 0) y else -y
                return u + v
            }

            // Gradient for 3D
            private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
                val h = hash and 15
                val u = if (h < 8) x else y
                val v = if (h < 4) y else if (h == 12 || h == 14) x else z
                return (if (h and 1 == 0) u else -u) + (if (h and 2 == 0) v else -v)
            }

            // Gradient for 4D
            private fun grad(hash: Int, x: Double, y: Double, z: Double, w: Double): Double {
                val h = hash and 31
                val u = if (h < 24) x else y
                val v = if (h < 16) y else z
                val s = if (h < 8) z else w
                return (if (h and 1 == 0) u else -u) +
                        (if (h and 2 == 0) v else -v) +
                        (if (h and 4 == 0) s else -s)
            }

            private fun fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

            private fun lerp(a: Double, b: Double, t: Double): Double = a + t * (b - a)
        }
    }

    object SimplexNoise: Noise {
        // Simplex noise in 2D, 3D and 4D
        private val grad3 = arrayOf(
            Grad(1.0, 1.0, 0.0), Grad(-1.0, 1.0, 0.0), Grad(1.0, -1.0, 0.0), Grad(-1.0, -1.0, 0.0),
            Grad(1.0, 0.0, 1.0), Grad(-1.0, 0.0, 1.0), Grad(1.0, 0.0, -1.0), Grad(-1.0, 0.0, -1.0),
            Grad(0.0, 1.0, 1.0), Grad(0.0, -1.0, 1.0), Grad(0.0, 1.0, -1.0), Grad(0.0, -1.0, -1.0)
        )

        private val grad4 = arrayOf(
            Grad(0.0, 1.0, 1.0, 1.0),
            Grad(0.0, 1.0, 1.0, -1.0),
            Grad(0.0, 1.0, -1.0, 1.0),
            Grad(0.0, 1.0, -1.0, -1.0),
            Grad(0.0, -1.0, 1.0, 1.0),
            Grad(0.0, -1.0, 1.0, -1.0),
            Grad(0.0, -1.0, -1.0, 1.0),
            Grad(0.0, -1.0, -1.0, -1.0),
            Grad(1.0, 0.0, 1.0, 1.0),
            Grad(1.0, 0.0, 1.0, -1.0),
            Grad(1.0, 0.0, -1.0, 1.0),
            Grad(1.0, 0.0, -1.0, -1.0),
            Grad(-1.0, 0.0, 1.0, 1.0),
            Grad(-1.0, 0.0, 1.0, -1.0),
            Grad(-1.0, 0.0, -1.0, 1.0),
            Grad(-1.0, 0.0, -1.0, -1.0),
            Grad(1.0, 1.0, 0.0, 1.0),
            Grad(1.0, 1.0, 0.0, -1.0),
            Grad(1.0, -1.0, 0.0, 1.0),
            Grad(1.0, -1.0, 0.0, -1.0),
            Grad(-1.0, 1.0, 0.0, 1.0),
            Grad(-1.0, 1.0, 0.0, -1.0),
            Grad(-1.0, -1.0, 0.0, 1.0),
            Grad(-1.0, -1.0, 0.0, -1.0),
            Grad(1.0, 1.0, 1.0, 0.0),
            Grad(1.0, 1.0, -1.0, 0.0),
            Grad(1.0, -1.0, 1.0, 0.0),
            Grad(1.0, -1.0, -1.0, 0.0),
            Grad(-1.0, 1.0, 1.0, 0.0),
            Grad(-1.0, 1.0, -1.0, 0.0),
            Grad(-1.0, -1.0, 1.0, 0.0),
            Grad(-1.0, -1.0, -1.0, 0.0)
        )

        private val permMod12 = p.map { it % 12 }.toIntArray()

        // Skewing and unskewing factors for 2, 3, and 4 dimensions.
        private val F2 = 0.5 * (sqrt(3.0) - 1.0)
        private val G2 = (3.0 - sqrt(3.0)) / 6.0
        private const val F3 = 1.0 / 3.0
        private const val G3 = 1.0 / 6.0
        private val F4 = (sqrt(5.0) - 1.0) / 4.0
        private val G4 = (5.0 - sqrt(5.0)) / 20.0

        private fun dot(g: Grad, x: Double, y: Double): Double =
            g.x * x + g.y * y

        private fun dot(g: Grad, x: Double, y: Double, z: Double): Double =
            g.x * x + g.y * y + g.z * z

        private fun dot(g: Grad, x: Double, y: Double, z: Double, w: Double): Double =
            g.x * x + g.y * y + g.z * z + g.w * w

        override fun noise(x: Double): Double =
            noise(x, 0.0)
        override fun noise(x: Double, y: Double): Double {
            val n0: Double
            val n1: Double
            val n2: Double // Noise contributions from the three corners
            // Skew the input space to determine which simplex cell we're in
            val s = (x + y) * F2 // Hairy factor for 2D
            val i = fastFloor(x + s)
            val j = fastFloor(y + s)
            val t = (i + j) * G2
            val X0 = i - t // Unskew the cell origin back to (x,y) space
            val Y0 = j - t
            val x0 = x - X0 // The x,y distances from the cell origin
            val y0 = y - Y0
            // For the 2D case, the simplex shape is an equilateral triangle.
            // Determine which simplex we are in.
            val i1: Int
            val j1: Int // Offsets for second (middle) corner of simplex in (i,j) coords
            if (x0 > y0) {
                i1 = 1
                j1 = 0
            } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            else {
                i1 = 0
                j1 = 1
            } // upper triangle, YX order: (0,0)->(0,1)->(1,1)

            // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
            // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
            // c = (3-sqrt(3))/6
            val x1 = x0 - i1 + G2 // Offsets for middle corner in (x,y) unskewed coords
            val y1 = y0 - j1 + G2
            val x2 = x0 - 1.0 + 2.0 * G2 // Offsets for last corner in (x,y) unskewed coords
            val y2 = y0 - 1.0 + 2.0 * G2
            // Work out the hashed gradient indices of the three simplex corners
            val ii = i and 255
            val jj = j and 255
            val gi0 = permMod12[ii + p[jj]].toInt()
            val gi1 = permMod12[ii + i1 + p[jj + j1]].toInt()
            val gi2 = permMod12[ii + 1 + p[jj + 1]].toInt()
            // Calculate the contribution from the three corners
            var t0 = 0.5 - x0 * x0 - y0 * y0
            if (t0 < 0) n0 = 0.0
            else {
                t0 *= t0
                n0 = t0 * t0 * dot(grad3[gi0], x0, y0) // (x,y) of grad3 used for 2D gradient
            }
            var t1 = 0.5 - x1 * x1 - y1 * y1
            if (t1 < 0) n1 = 0.0
            else {
                t1 *= t1
                n1 = t1 * t1 * dot(grad3[gi1], x1, y1)
            }
            var t2 = 0.5 - x2 * x2 - y2 * y2
            if (t2 < 0) n2 = 0.0
            else {
                t2 *= t2
                n2 = t2 * t2 * dot(grad3[gi2], x2, y2)
            }
            // Add contributions from each corner to get the final noise value.
            // The result is scaled to return values in the interval [-1,1].
            return 70.0 * (n0 + n1 + n2)
        }

        override fun noise(x: Double, y: Double, z: Double): Double {
            val n0: Double
            val n1: Double
            val n2: Double
            val n3: Double // Noise contributions from the four corners
            // Skew the input space to determine which simplex cell we're in
            val s = (x + y + z) * F3 // Very nice and simple skew factor for 3D
            val i = fastFloor(x + s)
            val j = fastFloor(y + s)
            val k = fastFloor(z + s)
            val t = (i + j + k) * G3
            val X0 = i - t // Unskew the cell origin back to (x,y,z) space
            val Y0 = j - t
            val Z0 = k - t
            val x0 = x - X0 // The x,y,z distances from the cell origin
            val y0 = y - Y0
            val z0 = z - Z0
            // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
            // Determine which simplex we are in.
            val i1: Int
            val j1: Int
            val k1: Int // Offsets for second corner of simplex in (i,j,k) coords
            val i2: Int
            val j2: Int
            val k2: Int // Offsets for third corner of simplex in (i,j,k) coords
            if (x0 >= y0) {
                if (y0 >= z0) {
                    i1 = 1
                    j1 = 0
                    k1 = 0
                    i2 = 1
                    j2 = 1
                    k2 = 0
                } // X Y Z order
                else if (x0 >= z0) {
                    i1 = 1
                    j1 = 0
                    k1 = 0
                    i2 = 1
                    j2 = 0
                    k2 = 1
                } // X Z Y order
                else {
                    i1 = 0
                    j1 = 0
                    k1 = 1
                    i2 = 1
                    j2 = 0
                    k2 = 1
                } // Z X Y order
            } else { // x0<y0
                if (y0 < z0) {
                    i1 = 0
                    j1 = 0
                    k1 = 1
                    i2 = 0
                    j2 = 1
                    k2 = 1
                } // Z Y X order
                else if (x0 < z0) {
                    i1 = 0
                    j1 = 1
                    k1 = 0
                    i2 = 0
                    j2 = 1
                    k2 = 1
                } // Y Z X order
                else {
                    i1 = 0
                    j1 = 1
                    k1 = 0
                    i2 = 1
                    j2 = 1
                    k2 = 0
                } // Y X Z order
            }
            // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
            // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
            // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
            // c = 1/6.
            val x1 = x0 - i1 + G3 // Offsets for second corner in (x,y,z) coords
            val y1 = y0 - j1 + G3
            val z1 = z0 - k1 + G3
            val x2 = x0 - i2 + 2.0 * G3 // Offsets for third corner in (x,y,z) coords
            val y2 = y0 - j2 + 2.0 * G3
            val z2 = z0 - k2 + 2.0 * G3
            val x3 = x0 - 1.0 + 3.0 * G3 // Offsets for last corner in (x,y,z) coords
            val y3 = y0 - 1.0 + 3.0 * G3
            val z3 = z0 - 1.0 + 3.0 * G3
            // Work out the hashed gradient indices of the four simplex corners
            val ii = i and 255
            val jj = j and 255
            val kk = k and 255
            val gi0 = permMod12[ii + p[jj + p[kk]]].toInt()
            val gi1 = permMod12[ii + i1 + p[jj + j1 + p[kk + k1]]].toInt()
            val gi2 = permMod12[ii + i2 + p[jj + j2 + p[kk + k2]]].toInt()
            val gi3 = permMod12[ii + 1 + p[jj + 1 + p[kk + 1]]].toInt()
            // Calculate the contribution from the four corners
            var t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0
            if (t0 < 0) n0 = 0.0
            else {
                t0 *= t0
                n0 = t0 * t0 * dot(grad3[gi0], x0, y0, z0)
            }
            var t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1
            if (t1 < 0) n1 = 0.0
            else {
                t1 *= t1
                n1 = t1 * t1 * dot(grad3[gi1], x1, y1, z1)
            }
            var t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2
            if (t2 < 0) n2 = 0.0
            else {
                t2 *= t2
                n2 = t2 * t2 * dot(grad3[gi2], x2, y2, z2)
            }
            var t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3
            if (t3 < 0) n3 = 0.0
            else {
                t3 *= t3
                n3 = t3 * t3 * dot(grad3[gi3], x3, y3, z3)
            }
            // Add contributions from each corner to get the final noise value.
            // The result is scaled to stay just inside [-1,1]
            return 32.0 * (n0 + n1 + n2 + n3)
        }

        // 4D simplex noise, better simplex rank ordering method 2012-03-09
        override fun noise(x: Double, y: Double, z: Double, w: Double): Double {
            val n0: Double
            val n1: Double
            val n2: Double
            val n3: Double
            val n4: Double // Noise contributions from the five corners
            // Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
            val s = (x + y + z + w) * F4 // Factor for 4D skewing
            val i = fastFloor(x + s)
            val j = fastFloor(y + s)
            val k = fastFloor(z + s)
            val l = fastFloor(w + s)
            val t = (i + j + k + l) * G4 // Factor for 4D unskewing
            val X0 = i - t // Unskew the cell origin back to (x,y,z,w) space
            val Y0 = j - t
            val Z0 = k - t
            val W0 = l - t
            val x0 = x - X0 // The x,y,z,w distances from the cell origin
            val y0 = y - Y0
            val z0 = z - Z0
            val w0 = w - W0
            // For the 4D case, the simplex is a 4D shape I won't even try to describe.
            // To find out which of the 24 possible simplices we're in, we need to
            // determine the magnitude ordering of x0, y0, z0 and w0.
            // Six pair-wise comparisons are performed between each possible pair
            // of the four coordinates, and the results are used to rank the numbers.
            var rankx = 0
            var ranky = 0
            var rankz = 0
            var rankw = 0
            if (x0 > y0) rankx++ else ranky++
            if (x0 > z0) rankx++ else rankz++
            if (x0 > w0) rankx++ else rankw++
            if (y0 > z0) ranky++ else rankz++
            if (y0 > w0) ranky++ else rankw++
            if (z0 > w0) rankz++ else rankw++
            // simplex[c] is a 4-vector with the numbers 0, 1, 2 and 3 in some order.
            // Many values of c will never occur, since e.g. x>y>z>w makes x<z, y<w and x<w
            // impossible. Only the 24 indices which have non-zero entries make any sense.
            // We use a thresholding to set the coordinates in turn from the largest magnitude.
            // Rank 3 denotes the largest coordinate.
            val i1 = if (rankx >= 3) 1 else 0
            val j1 = if (ranky >= 3) 1 else 0
            val k1 = if (rankz >= 3) 1 else 0
            val l1 = if (rankw >= 3) 1 else 0 // The integer offsets for the second simplex corner
            // Rank 2 denotes the second largest coordinate.
            val i2 = if (rankx >= 2) 1 else 0
            val j2 = if (ranky >= 2) 1 else 0
            val k2 = if (rankz >= 2) 1 else 0
            val l2 = if (rankw >= 2) 1 else 0 // The integer offsets for the third simplex corner
            // Rank 1 denotes the second smallest coordinate.
            val i3 = if (rankx >= 1) 1 else 0
            val j3 = if (ranky >= 1) 1 else 0
            val k3 = if (rankz >= 1) 1 else 0
            val l3 = if (rankw >= 1) 1 else 0 // The integer offsets for the fourth simplex corner
            // The fifth corner has all coordinate offsets = 1, so no need to compute that.
            val x1 = x0 - i1 + G4 // Offsets for second corner in (x,y,z,w) coords
            val y1 = y0 - j1 + G4
            val z1 = z0 - k1 + G4
            val w1 = w0 - l1 + G4
            val x2 = x0 - i2 + 2.0 * G4 // Offsets for third corner in (x,y,z,w) coords
            val y2 = y0 - j2 + 2.0 * G4
            val z2 = z0 - k2 + 2.0 * G4
            val w2 = w0 - l2 + 2.0 * G4
            val x3 = x0 - i3 + 3.0 * G4 // Offsets for fourth corner in (x,y,z,w) coords
            val y3 = y0 - j3 + 3.0 * G4
            val z3 = z0 - k3 + 3.0 * G4
            val w3 = w0 - l3 + 3.0 * G4
            val x4 = x0 - 1.0 + 4.0 * G4 // Offsets for last corner in (x,y,z,w) coords
            val y4 = y0 - 1.0 + 4.0 * G4
            val z4 = z0 - 1.0 + 4.0 * G4
            val w4 = w0 - 1.0 + 4.0 * G4
            // Work out the hashed gradient indices of the five simplex corners
            val ii = i and 255
            val jj = j and 255
            val kk = k and 255
            val ll = l and 255
            val gi0 = p[ii + p[jj + p[kk + p[ll]]]] % 32
            val gi1 = p[ii + i1 + p[jj + j1 + p[kk + k1 + p[ll + l1]]]] % 32
            val gi2 = p[ii + i2 + p[jj + j2 + p[kk + k2 + p[ll + l2]]]] % 32
            val gi3 = p[ii + i3 + p[jj + j3 + p[kk + k3 + p[ll + l3]]]] % 32
            val gi4 = p[ii + 1 + p[jj + 1 + p[kk + 1 + p[ll + 1]]]] % 32
            // Calculate the contribution from the five corners
            var t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0
            if (t0 < 0) n0 = 0.0
            else {
                t0 *= t0
                n0 = t0 * t0 * dot(grad4[gi0], x0, y0, z0, w0)
            }
            var t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1
            if (t1 < 0) n1 = 0.0
            else {
                t1 *= t1
                n1 = t1 * t1 * dot(grad4[gi1], x1, y1, z1, w1)
            }
            var t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2
            if (t2 < 0) n2 = 0.0
            else {
                t2 *= t2
                n2 = t2 * t2 * dot(grad4[gi2], x2, y2, z2, w2)
            }
            var t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3
            if (t3 < 0) n3 = 0.0
            else {
                t3 *= t3
                n3 = t3 * t3 * dot(grad4[gi3], x3, y3, z3, w3)
            }
            var t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4
            if (t4 < 0) n4 = 0.0
            else {
                t4 *= t4
                n4 = t4 * t4 * dot(grad4[gi4], x4, y4, z4, w4)
            }
            // Sum up and scale the result to cover the range [-1,1]
            return 27.0 * (n0 + n1 + n2 + n3 + n4)
        }

        // Inner class to speed upp gradient computations
        // (array access is a lot slower than member access)
        private class Grad {
            var x: Double
            var y: Double
            var z: Double
            var w: Double = 0.0

            constructor(x: Double, y: Double, z: Double) {
                this.x = x
                this.y = y
                this.z = z
            }

            constructor(x: Double, y: Double, z: Double, w: Double) {
                this.x = x
                this.y = y
                this.z = z
                this.w = w
            }
        }

        class WorleyNoise(private val numFeaturePoints: Int = 1, private val seed: Int = 0) : Noise {
            // Hashing function to generate pseudo-random values
            private fun random(seed: Int, x: Int): Double {
                val n = x * 374761393 + seed * 668265263
                return ((n xor (n shl 13)) * 1274126177 and 0x7fffffff).toDouble() / Int.MAX_VALUE.toDouble()
            }

            private fun random(seed: Int, x: Int, y: Int): Double {
                val n = x * 374761393 + y * 668265263 + seed * 1376312589
                return ((n xor (n shl 13)) * 1274126177 and 0x7fffffff).toDouble() / Int.MAX_VALUE.toDouble()
            }

            private fun random(seed: Int, x: Int, y: Int, z: Int): Double {
                val n = x * 374761393 + y * 668265263 + z * 1376312589 + seed * 123456789
                return ((n xor (n shl 13)) * 1274126177 and 0x7fffffff).toDouble() / Int.MAX_VALUE.toDouble()
            }

            private fun random(seed: Int, x: Int, y: Int, z: Int, w: Int): Double {
                val n = x * 374761393 + y * 668265263 + z * 1376312589 + w * 1577334179 + seed * 918273645
                return ((n xor (n shl 13)) * 1274126177 and 0x7fffffff).toDouble() / Int.MAX_VALUE.toDouble()
            }

            // Worley noise for 1D
            override fun noise(x: Double): Double {
                val xi = x.toInt()
                var minDist = Double.MAX_VALUE

                for (dx in -1..1) {
                    val gridX = xi + dx
                    val featurePoint = gridX + random(seed, gridX)

                    val dist = abs(featurePoint - x)
                    minDist = kotlin.math.min(minDist, dist)
                }

                return minDist
            }

            // Worley noise for 2D
            override fun noise(x: Double, y: Double): Double {
                val xi = x.toInt()
                val yi = y.toInt()
                var minDist = Double.MAX_VALUE

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        val gridX = xi + dx
                        val gridY = yi + dy

                        // Random feature point in this grid cell
                        val featureX = gridX + random(seed, gridX, gridY)
                        val featureY = gridY + random(seed, gridX, gridY + 1)

                        // Calculate distance to this feature point
                        val dist = sqrt((featureX - x) * (featureX - x) + (featureY - y) * (featureY - y))
                        minDist = kotlin.math.min(minDist, dist)
                    }
                }

                return minDist
            }

            // Worley noise for 3D
            override fun noise(x: Double, y: Double, z: Double): Double {
                val xi = x.toInt()
                val yi = y.toInt()
                val zi = z.toInt()
                var minDist = Double.MAX_VALUE

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        for (dz in -1..1) {
                            val gridX = xi + dx
                            val gridY = yi + dy
                            val gridZ = zi + dz

                            // Random feature point in this grid cell
                            val featureX = gridX + random(seed, gridX, gridY, gridZ)
                            val featureY = gridY + random(seed, gridX, gridY, gridZ + 1)
                            val featureZ = gridZ + random(seed, gridX, gridY + 1, gridZ)

                            // Calculate distance to this feature point
                            val dist = sqrt((featureX - x) * (featureX - x) + (featureY - y) * (featureY - y) + (featureZ - z) * (featureZ - z))
                            minDist = kotlin.math.min(minDist, dist)
                        }
                    }
                }

                return minDist
            }

            // Worley noise for 4D
            override fun noise(x: Double, y: Double, z: Double, w: Double): Double {
                val xi = x.toInt()
                val yi = y.toInt()
                val zi = z.toInt()
                val wi = w.toInt()
                var minDist = Double.MAX_VALUE

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        for (dz in -1..1) {
                            for (dw in -1..1) {
                                val gridX = xi + dx
                                val gridY = yi + dy
                                val gridZ = zi + dz
                                val gridW = wi + dw

                                // Random feature point in this grid cell
                                val featureX = gridX + random(seed, gridX, gridY, gridZ, gridW)
                                val featureY = gridY + random(seed, gridX, gridY + 1, gridZ, gridW)
                                val featureZ = gridZ + random(seed, gridX, gridY, gridZ + 1, gridW)
                                val featureW = gridW + random(seed, gridX, gridY, gridZ, gridW + 1)

                                // Calculate distance to this feature point
                                val dist = sqrt(
                                    (featureX - x) * (featureX - x) +
                                            (featureY - y) * (featureY - y) +
                                            (featureZ - z) * (featureZ - z) +
                                            (featureW - w) * (featureW - w)
                                )
                                minDist = kotlin.math.min(minDist, dist)
                            }
                        }
                    }
                }

                return minDist
            }
        }
    }
    companion object {
        private val permutation: IntArray = intArrayOf(
            151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        )

        // p is just two appended copies of permutation.
        private val p: IntArray = IntArray(512).apply {
            // Copy permutation twice into p.
            permutation.copyInto(this, 0)
            permutation.copyInto(this, 256)
        }

        // This method is a *lot* faster than using floor.
        private fun fastFloor(x: Double): Int {
            val xi = x.toInt()
            return if (x < xi) xi - 1 else xi
        }
    }
}

private val perlinNoise = ProcessingNoise.PerlinNoise()
public fun noise(x: Double): Double = perlinNoise.noise(x)
public fun noise(x: Double, y: Double): Double = perlinNoise.noise(x, y)
public fun noise(x: Double, y: Double, z: Double) = perlinNoise.noise(x, y, z)
