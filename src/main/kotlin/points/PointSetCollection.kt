package points

import org.openrndr.draw.Drawer
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.OptionParameter
import org.openrndr.math.Vector2



data class PointSetConfigurationDescription(val shape: IPointSetShape, val distributionConfig: PointSetConfiguration)

@Description("Distribution & Shape")
class PointSetCollection() {
    private val sets = mutableMapOf<Int, List<Vector2>>()

    @OptionParameter("Shape")
    var pointSetShape = PointSetShape.RECT

    fun addTo(gui: GUI): PointSetCollection {
        Distribution.addTo(gui)
        PointSetShape.addTo(gui)
        gui.add(this)
        return this
    }

//    @Suppress("unused")
    fun displayShape(drawer: Drawer) {
        pointSetShape.display(drawer)
    }

    @OptionParameter("Distribution")
    var activeDistribution = Distribution.POISSON

    private val activeDistributionConfiguration get() = activeDistribution.configuration

    private val activeConfigurationDescriptionHash get() = PointSetConfigurationDescription(pointSetShape.shapeProvider, activeDistributionConfiguration).hashCode()
    private val activeConfigurationDescription get() = PointSetConfigurationDescription(pointSetShape.shapeProvider, activeDistributionConfiguration)

    val points: List<Vector2>
        get() = sets.getOrPut(activeConfigurationDescriptionHash) {
            println("Calculate $activeConfigurationDescription")
            with(activeDistributionConfiguration) {
                pointSetShape.shapeProvider.generatePoints()
            }
        }
}
