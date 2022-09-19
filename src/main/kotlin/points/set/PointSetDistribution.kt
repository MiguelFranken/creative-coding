package points.set

import noise.phyllotaxis
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.parameters.BooleanParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.math.Vector2
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

    @DoubleParameter("Distance To Edge", 0.0, 50.0)
    var distanceToEdge: Double = 0.0,

    @BooleanParameter("Use Obstacles")
    override var useObstacles: Boolean = false
): PointSetConfiguration, PointSetConfigurationWithObstacle {
    override fun ShapeProvider.generatePoints() = scatter(placementRadius, distanceToEdge = distanceToEdge, obstacles = obstacles)
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
    PHYLLOTAXIS(PhyllotaxisPointSetConfiguration());

    companion object {
        fun addTo(gui: GUI) {
            values().map(Distribution::configuration).forEach {
                it.addTo(gui)
            }
        }
    }
}
