package sterngerlach.graphics

import org.w3c.dom.HTMLElement
import sterngerlach.physics.Particle

sealed class Component {
    abstract val element: HTMLElement

    abstract fun send(particle: Particle)
}
