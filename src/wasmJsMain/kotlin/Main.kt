package sterngerlach

import kotlinx.browser.document
import sterngerlach.graphics.Analyser
import sterngerlach.graphics.Counter
import sterngerlach.graphics.Furnace
import sterngerlach.graphics.div
import sterngerlach.graphics.findById
import sterngerlach.physics.Axis

fun main() {
    Counter(findById("counterUp"))
    Counter(findById("counterDown"))
    Analyser(findById("analyser"), Axis.Z)
    Furnace(findById("furnace"))

    findById("addAnalyser").addEventListener("click") {
        val container = div()
        document.body?.append(container)
        Analyser(container, Axis.Z)
    }

    findById("addCounter").addEventListener("click") {
        val container = div()
        document.body?.append(container)
        Counter(container)
    }
}
