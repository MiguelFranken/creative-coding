package points

import noise.phyllotaxis
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.parameters.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.ShapeProvider

interface PointSetConfiguration {
    fun ShapeProvider.generatePoints(): List<Vector2>
}

interface PointSetConfigurationWithObstacle {
    var useObstacles: Boolean

    val obstacles: List<Pair<Double, List<Vector2>>>
        get() = if (useObstacles) listOf(
            Pair(20.0, listOf(Vector2(-40.0, -40.0), Vector2(40.0, -40.0))),
            Pair(40.0, listOf(Vector2(-60.0, 80.0), Vector2(60.0, 80.0)))
        ) else emptyList()
}

@Description("Poisson Point Set")
data class PoissonPointSetConfiguration(
    @DoubleParameter("Placement Radius", 2.0, 10.0)
    var placementRadius: Double = 5.0,

    @BooleanParameter("Use Obstacles")
    override var useObstacles: Boolean = true
): PointSetConfiguration, PointSetConfigurationWithObstacle {
    override fun ShapeProvider.generatePoints() = scatter(placementRadius, obstacles = obstacles)
}

@Description("Phyllotaxis Point Set")
data class PhyllotaxisPointSetConfiguration(
    @IntParameter("Number Points", 100, 4000)
    var n: Int = 1000
): PointSetConfiguration {
    override fun ShapeProvider.generatePoints() = phyllotaxis(n)
}

enum class Distribution(val configuration: PointSetConfiguration) {
    POISSON(PoissonPointSetConfiguration()),
    PHYLLOTAXIS(PhyllotaxisPointSetConfiguration()),
}

fun main() = application {
    program {
        val gui = GUI()

        val settings = @Description("General") object {
            @DoubleParameter("Circle Radius", 2.0, 4.0)
            var radius = 1.0
        }

        @Description("Point Set")
        class PointSetCollection(val shapeProvider: ShapeProvider) {
            private val sets = mutableMapOf<Int, List<Vector2>>()

            init {
                Distribution.values().forEach { it.configuration.addTo(gui) }
            }

            @OptionParameter("Distribution")
            var activeDistribution = Distribution.POISSON

            val activeConfiguration get() = activeDistribution.configuration

            val points: List<Vector2>
                get() = sets.getOrPut(activeConfiguration.hashCode()) {
                    with(activeConfiguration) {
                        shapeProvider.generatePoints()
                    }
                }
        }

        val rectWidth = 300.0
        val rectHeight = 300.0
        val rect = Rectangle(-rectWidth / 2.0, -rectHeight / 2.0, rectWidth, rectHeight)
        val pointSetCollection = PointSetCollection(rect)

        extend(gui) {
            add(settings)
            add(pointSetCollection)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.fill = null
            drawer.stroke = rgb(0.8)
            drawer.translate(width / 2.0, height / 2.0)
            drawer.rectangle(rect)

            drawer.fill = ColorRGBa.BLACK
            drawer.stroke = null
            drawer.circles(pointSetCollection.points, settings.radius)
        }
    }
}
