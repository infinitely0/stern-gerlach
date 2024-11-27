package sterngerlach.graphics

import org.w3c.dom.HTMLElement
import sterngerlach.physics.Particle

class Counter(
    override val element: HTMLElement,
    private var count: Int = 0,
) : Component() {
    init {
        element.style.apply {
            position = "absolute"
            display = "flex"
            alignItems = "center"
        }
        element.classList.add("counter")
        element.append(bar(), label())
        this.registerComponent()
    }

    override fun send(particle: Particle) {
        if (count < 100) {
            count++
        }
        update()
    }

    fun reset() {
        count = 0
        update()
    }

    private fun update() {
        val label = element.findById("counterLabel")
        label.textContent = count.toString()

        val fill = element.findById("counterFill")
        fill.style.width = "${(count / 100.0) * 100}%"
    }

    private fun label(): HTMLElement {
        val label = div(count.toString())
        label.id = "counterLabel"
        label.style.apply {
            fontSize = "30px"
            paddingLeft = "10px"
        }
        return label
    }

    private fun bar(): HTMLElement {
        val bar = div()
        bar.style.apply {
            width = "150px"
            height = "20px"
            border = "1px solid black"
            position = "relative"
        }

        val fill = div()
        fill.id = "counterFill"
        fill.style.apply {
            height = "100%"
            backgroundColor = "darkgrey"
            position = "absolute"
            top = "0"
            left = "0"
        }
        bar.append(fill)
        return bar
    }
}
