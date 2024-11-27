package sterngerlach.physics

import kotlin.math.sqrt

enum class Axis {
    Z,
    X,
    Y,
}

fun Axis.basisStates(): Pair<State, State> =
    when (this) {
        Axis.Z ->
            Pair(
                State(a = Complex(1.0), b = Complex(0.0)), // |+> z
                State(a = Complex(0.0), b = Complex(1.0)), // |-> z
            )

        Axis.X ->
            Pair(
                State(a = Complex(1 / sqrt(2.0)), b = Complex(1 / sqrt(2.0))), // |+> x
                State(a = Complex(1 / sqrt(2.0)), b = Complex(-1 / sqrt(2.0))), // |-> x
            )

        Axis.Y ->
            Pair(
                State(a = Complex(1 / sqrt(2.0)), b = Complex(0.0, 1 / sqrt(2.0))), // |+> y
                State(a = Complex(1 / sqrt(2.0)), b = Complex(0.0, -1 / sqrt(2.0))), // |-> y
            )
    }
