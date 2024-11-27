package sterngerlach.graphics

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.asList
import org.w3c.dom.events.MouseEvent
import sterngerlach.physics.Spin
import kotlin.js.JsArray
import kotlin.math.atan2
import kotlin.math.sqrt

fun HTMLElement.enableDragging() {
    style.apply {
        cursor = "grabbing"
    }
    var offsetX = 0
    var offsetY = 0
    var isDragging = false

    // Stop propagation of mousedown event for buttons to prevent dragging when clicking buttons
    this.querySelectorAll("button").asList().forEach { button ->
        button.addEventListener("mousedown") { event ->
            event.stopPropagation()
        }
    }

    addEventListener("mousedown") { event ->
        event as MouseEvent
        offsetX = event.clientX - offsetLeft
        offsetY = event.clientY - offsetTop
        isDragging = true
    }

    document.addEventListener("mousemove") { event ->
        if (isDragging) {
            event as MouseEvent
            this.style.left = "${event.clientX - offsetX}px"
            this.style.top = "${event.clientY - offsetY}px"
        }
    }

    document.addEventListener("mouseup") {
        isDragging = false
    }
}

fun alert(message: String) {
    document.getElementById("alert")?.textContent = message
}

fun div(text: String? = null): HTMLElement {
    val div = document.createElement("div") as HTMLElement
    text?.let {
        div.textContent = text
    }
    return div
}

fun button(text: String): HTMLElement {
    val button = document.createElement("button") as HTMLElement
    button.style.apply {
        cursor = "pointer"
    }
    button.textContent = text
    return button
}

fun findById(id: String) = document.getElementById(id) as HTMLElement

fun findByClassName(className: String): List<HTMLElement> =
    document.querySelectorAll(".$className").asList().map { it as HTMLElement }

fun findAllByClassName(vararg classNames: String): List<HTMLElement> =
    classNames.flatMap { className ->
        document.querySelectorAll(".$className").asList().map { it as HTMLElement }
    }

fun HTMLElement.findById(id: String) = this.querySelector("#$id") as HTMLElement

fun HTMLElement.enable() {
    removeAttribute("disabled")
}

fun HTMLElement.disable() {
    setAttribute("disabled", "true")
}

fun HTMLElement.show() {
    hidden = false
}

fun HTMLElement.hide() {
    hidden = true
}

fun drawLine(
    a: HTMLElement,
    b: HTMLElement,
    spin: Spin? = null,
) {
    val line = document.createElement("div") as HTMLElement
    line.style.apply {
        position = "absolute"
        backgroundColor = "black"
        height = "1px"
        zIndex = "-1"
    }
    document.body?.appendChild(line)

    val delete = deleteLineButton(a, spin)
    line.append(delete)

    fun update() {
        val aRect = a.getBoundingClientRect()
        val bRect = b.getBoundingClientRect()
        val startX = aRect.right
        val startY = aRect.top + (aRect.height / 2) - offset(spin)
        val endX = bRect.left
        val endY = bRect.top + bRect.height / 2
        val dx = endX - startX
        val dy = endY - startY
        val length = sqrt(dx * dx + dy * dy)
        line.style.top = "${startY}px"
        line.style.left = "${startX}px"
        line.style.width = "${length}px"
        line.style.transformOrigin = "0 0"
        line.style.transform = "rotate(${atan2(dy, dx)}rad)"

        delete.style.top = "-8px"
        delete.style.left = "${-(startX - endX) / 2}px"
    }

    update()
    val observer = MutationObserver { _, _ -> update() }

    observer.observe(
        a,
        MutationObserverInit().apply {
            attributes = true
            attributeFilter = arrayOf("style", "class").toJsArray()
        },
    )

    observer.observe(
        b,
        MutationObserverInit().apply {
            attributes = true
            attributeFilter = arrayOf("style", "class").toJsArray()
        },
    )
}

private fun offset(spin: Spin?): Int =
    when (spin) {
        Spin.Up -> 25
        Spin.Down -> -25
        null -> 0
    }

private fun deleteLineButton(
    start: HTMLElement,
    spin: Spin?,
): HTMLElement {
    val button = button("X")
    button.style.apply {
        position = "absolute"
        top = "-7px"
        backgroundColor = "white"
        border = "1px solid black"
        borderRadius = "50%"
        width = "20px"
    }

    button.addEventListener("click") {
        when (val component = start.findComponent()!!) {
            is Analyser -> component.disconnect(spin)
            is Furnace -> component.disconnect()
            else -> {}
        }
        button.parentElement?.remove()
    }

    return button
}

private fun Array<String>.toJsArray() =
    JsArray<JsString>().apply {
        forEachIndexed { index, element ->
            this[index] = element.toJsString()
        }
    }

fun selectButton(): HTMLElement {
    val button = button("Select")
    button.classList.add("select")
    button.style.apply {
        position = "absolute"
        left = "-60px"
        top = "50%"
        transform = "translateY(-50%)"
    }

    button.addEventListener("mousedown") { event ->
        event.stopPropagation()
    }
    return button
}
