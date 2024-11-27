package sterngerlach.graphics

import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import sterngerlach.physics.Particle
import sterngerlach.physics.randomParticle

class Furnace(
    override val element: HTMLElement,
    private var receiver: Component? = null,
) : Component() {
    init {
        element.style.apply {
            width = "120px"
            height = "120px"
            position = "absolute"
            backgroundColor = "white"
        }
        element.append(trigger(), arrow(), connector())
        this.registerComponent()
    }

    override fun send(particle: Particle) {
        // Furnace has no inbound connections
    }

    // Sends 100 particles to the analyser
    private fun start() {
        receiver?.let {
            resetCounters()
            sendWithDelay(100)
            alert("")
        } ?: alert("Nothing connected to furnace")
    }

    private fun resetCounters() {
        findAllByClassName("counter").forEach {
            val counter = it.findComponent() as? Counter
            counter?.reset()
        }
    }

    // Animates firing of particles
    private fun sendWithDelay(n: Int) {
        fun scheduleSend(i: Int) {
            if (i < n) {
                window.setTimeout({
                    receiver?.send(randomParticle())
                    scheduleSend(i + 1)
                    null
                }, 10)
            }
        }
        scheduleSend(0)
    }

    private fun trigger(): HTMLElement {
        val container = div()
        container.style.apply {
            display = "flex"
            justifyContent = "space-around"
            alignItems = "center"
            height = "100%"
            border = "1px solid black"
        }

        val trigger = button("Start")
        trigger.addEventListener("click") {
            start()
        }
        container.append(trigger)
        return container
    }

    private fun arrow(): HTMLElement {
        val arrow = div("→")
        arrow.style.apply {
            width = "40px"
            height = "40px"
            position = "absolute"
            backgroundColor = "white"
            top = "37px"
            left = "119px"
            display = "flex"
            alignItems = "center"
            borderTop = "1px solid black"
            borderBottom = "1px solid black"
            fontSize = "40px"
        }
        return arrow
    }

    private fun connector(): HTMLElement {
        receiver?.let { return div() }

        val connector = button("Connect")
        connector.setAttribute("id", "${hashCode()}")
        connector.style.apply {
            position = "absolute"
            right = "-110px"
            top = "50%"
            transform = "translateY(-50%)"
        }

        // Create selection for possible connections from the furnace
        connector.addEventListener("click") {
            findAllByClassName("counter", "analyser").forEach { receiver ->
                connector.disable()
                val button = selectButton()

                button.addEventListener("click") {
                    val selected = receiver.findComponent()
                    this.receiver = selected
                    connector.hide()
                    findByClassName("select").forEach { it.remove() }
                    drawLine(element, receiver)
                }
                receiver.append(button)
            }
        }
        return connector
    }

    fun disconnect() {
        receiver = null
        val select = element.querySelector("[id='${hashCode()}']") as? HTMLElement
        select?.enable()
        select?.show()
    }
}
