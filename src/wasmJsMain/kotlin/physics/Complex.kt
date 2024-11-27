package sterngerlach.physics

data class Complex(
    val re: Double,
    val im: Double = 0.0,
) {
    operator fun plus(z: Complex) = Complex(re + z.re, im + z.im)

    operator fun times(z: Complex) = Complex(re * z.re - im * z.im, re * z.im + im * z.re)

    fun conjugate() = Complex(re, -im)

    fun dot(z: Complex) = Complex(re * z.re + im * z.im)

    fun norm() = dot(this)
}
