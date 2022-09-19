package points

import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.OptionParameter
import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeProvider

@Description("Point Set")
class PointSetCollection(private val shapeProvider: ShapeProvider) {
    private val sets = mutableMapOf<Int, List<Vector2>>()

    fun addTo(gui: GUI): PointSetCollection {
        Distribution.values().forEach { it.configuration.addTo(gui) }
        return this
    }

    @OptionParameter("Distribution")
    var activeDistribution = Distribution.POISSON

    private val activeConfiguration get() = activeDistribution.configuration

    val points: List<Vector2>
        get() = sets.getOrPut(activeConfiguration.hashCode()) {
            with(activeConfiguration) {
                shapeProvider.generatePoints()
            }
        }
}
