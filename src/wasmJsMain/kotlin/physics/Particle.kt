package sterngerlach.physics

import kotlin.random.Random.Default.nextDouble

// Nothing else currently needed to represent a particle other than its state
typealias Particle = State

// Coefficients in |psi> = a|+> + b|->
data class State(
    val a: Complex,
    val b: Complex,
)

// Returns the spin and a new particle after measuring the spin along the given axis
fun Particle.measure(axis: Axis): Pair<Spin, Particle> {
    val (upState, downState) = axis.basisStates()

    // p(|+>) = |<upState|psi>|^2
    val inner = upState.a.conjugate() * a + upState.b.conjugate() * b
    val probabilityUp = inner.norm().re

    val spin = if (probabilityUp > nextDouble()) Spin.Up else Spin.Down
    val particle =
        when (spin) {
            Spin.Up -> upState
            Spin.Down -> downState
        }
    return Pair(spin, particle)
}

fun randomState(): State =
    Axis.entries
        .random()
        .basisStates()
        .toList()
        .random()

fun randomParticle(): Particle {
    val (a, b) = randomState()
    return Particle(a, b)
}
