package sterngerlach.graphics

import org.w3c.dom.HTMLElement

private val components = mutableMapOf<HTMLElement, Component>()

fun Component.registerComponent() {
    element.enableDragging()
    components[this.element] = this
}

fun HTMLElement.findComponent(): Component? = components[this]
