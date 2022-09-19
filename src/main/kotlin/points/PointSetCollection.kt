package points

import org.openrndr.color.rgb
import org.openrndr.draw.Drawer
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.OptionParameter
import org.openrndr.extra.parameters.Vector2Parameter
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeProvider

interface IPointSetShape : ShapeProvider {
    var center: Vector2

    fun display(drawer: Drawer)
}

@Description("Rectangle Shape")
data class PointSetRect(
    @Vector2Parameter("Center Coordinates", 0.0, 200.0)
    override var center: Vector2 = Vector2.ZERO,

    @DoubleParameter("Width", 0.1, 200.0)
    var width: Double = 100.0,

    @DoubleParameter("Height", 0.1, 200.0)
    var height: Double = 100.0
) : IPointSetShape {
    private val rect get() = Rectangle(center - Vector2(width / 2.0, height / 2.0), width, height)

    override fun display(drawer: Drawer) {
        drawer.fill = null
        drawer.stroke = rgb(0.8)
        drawer.rectangle(rect)
    }

    override val shape: Shape get() = rect.shape
}

enum class PointSetShape(val shapeProvider: IPointSetShape) {
    RECT(PointSetRect());

    fun display(drawer: Drawer) {
        shapeProvider.display(drawer)
    }

    companion object {
        fun addTo(gui: GUI) {
            values().map(PointSetShape::shapeProvider).forEach {
                it.addTo(gui)
            }
        }
    }
}

data class PointSetConfigurationDescription(val shape: IPointSetShape, val distributionConfig: PointSetConfiguration)

@Description("Point Set")
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
