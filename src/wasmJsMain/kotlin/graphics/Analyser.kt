package sterngerlach.graphics

import org.w3c.dom.HTMLElement
import sterngerlach.physics.Axis
import sterngerlach.physics.Particle
import sterngerlach.physics.Spin
import sterngerlach.physics.measure

class Analyser(
    override val element: HTMLElement,
    private var axis: Axis,
    private var receiverUp: Component? = null,
    private var receiverDown: Component? = null,
) : Component() {
    init {
        element.style.apply {
            position = "absolute"
            display = "flex"
            flexDirection = "column"
            justifyContent = "space-around"
            alignItems = "center"
            paddingTop = "40px"
        }
        element.classList.add("analyser")
        element.append(analyser())
        element.append(rotator())
        this.registerComponent()
    }

    override fun send(particle: Particle) {
        val (spin, new) = particle.measure(axis)

        when (spin) {
            Spin.Up -> receiverUp?.send(new)
            Spin.Down -> receiverDown?.send(new)
        }
    }

    private fun rotate() {
        axis = Axis.entries[(axis.ordinal + 1) % Axis.entries.size]
    }

    private fun analyser(): HTMLElement {
        val container = div()
        container.style.apply {
            width = "120px"
            height = "120px"
            backgroundColor = "lightgrey"
            display = "flex"
            fontSize = "40px"
            border = "1px solid black"
            borderRadius = "10%"
            overflowX = "hidden"
            overflowY = "hidden"
        }
        container.append(axisLabel())
        container.append(spinArrows())
        return container
    }

    private fun axisLabel(): HTMLElement {
        val container = div()
        container.style.apply {
            width = "60%"
            display = "flex"
            flexDirection = "column"
            justifyContent = "center"
            alignItems = "center"
        }

        val label = div(axis.name)
        label.id = "axisLabel"
        label.style.apply {
            backgroundColor = "white"
            border = "1px solid black"
            width = "60%"
            paddingTop = "5px"
            paddingBottom = "5px"
            borderRadius = "50%"
            textAlign = "center"
        }
        container.append(label)

        return container
    }

    private fun spinArrows(): HTMLElement {
        val container = div()
        container.style.apply {
            width = "40%"
            display = "flex"
            flexDirection = "column"
            justifyContent = "center"
            backgroundColor = "white"
            borderLeft = "1px solid black"
        }

        val upArrow = div("↑")
        upArrow.id = "up"
        upArrow.style.apply {
            display = "flex"
            justifyContent = "center"
            paddingBottom = "10px"
            borderBottom = "1px solid black"
        }

        val upConnector = connectorButton(Spin.Up)
        upArrow.append(upConnector)
        container.append(upArrow)

        val downArrow = div("↓")
        downArrow.id = "down"
        downArrow.style.apply {
            width = "100%"
            display = "flex"
            justifyContent = "center"
        }
        val downConnector = connectorButton(Spin.Down)
        downArrow.append(downConnector)
        container.append(downArrow)

        return container
    }

    private fun connectorButton(spin: Spin): HTMLElement {
        val connector = button("Connect")
        connector.setAttribute("id", "${hashCode()}-$spin")
        connector.style.apply {
            position = "absolute"
            right = "-70px"
            top = if (spin == Spin.Up) "35%" else "65%"
            transform = "translateY(-50%)"
        }

        // Create selection for possible connections from this analyser
        connector.addEventListener("click") {
            findAllByClassName("counter", "analyser").forEach { receiver ->
                connector.disable()
                val button = selectButton()

                button.addEventListener("click") {
                    val selected = receiver.findComponent()
                    when (spin) {
                        Spin.Up -> this.receiverUp = selected
                        Spin.Down -> this.receiverDown = selected
                    }
                    // The connection line needs to be drawn from the parent element, not the arrow,
                    // since the analyser as a whole is dragged - if the line is drawn from the
                    // arrow it won't redraw when the analyser is moved. An offset (based on spin)
                    // is added so that the line starts from the arrow and not the parent element.
                    drawLine(this.element, receiver, spin)
                    findByClassName("select").forEach { it.remove() }
                    connector.hide()
                }
                receiver.append(button)
            }
        }
        return connector
    }

    private fun rotator(): HTMLElement {
        val rotateButton = button("↻")
        rotateButton.style.apply {
            fontSize = "16px"
            marginTop = "10px"
        }
        val label = element.findById("axisLabel")
        rotateButton.addEventListener("click") {
            rotate()
            label.textContent = this.axis.name
        }
        return rotateButton
    }

    fun disconnect(spin: Spin?) {
        when (spin) {
            Spin.Up -> receiverUp = null
            Spin.Down -> receiverDown = null
            else -> throw IllegalStateException()
        }
        val select = element.querySelector("[id='${hashCode()}-$spin']") as HTMLElement
        select.enable()
        select.show()
    }
}
