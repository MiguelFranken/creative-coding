package points

import noise.phyllotaxis
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.OptionParameter
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle

enum class Distribution {
    POISSON_1,
    POISSON_2,
    POISSON_WITH_OBSTACLE,
    PHYLLOTAXIS_1,
    PHYLLOTAXIS_2,
}

fun main() = application {
    program {
        val rectWidth = 300.0
        val rectHeight = 300.0
        val rect = Rectangle(-rectWidth / 2.0, -rectHeight / 2.0, rectWidth, rectHeight)

        val obstacles = listOf(
            Pair(20.0, listOf(Vector2(-40.0, -40.0), Vector2(40.0, -40.0))),
            Pair(40.0, listOf(Vector2(-60.0, 80.0), Vector2(60.0, 80.0)))
        )

        fun getPointSet(distribution: Distribution): List<Vector2> {
            return when(distribution) {
                Distribution.POISSON_1 -> rect.scatter(5.0)
                Distribution.POISSON_2 -> rect.scatter(10.0)
                Distribution.POISSON_WITH_OBSTACLE -> rect.scatter(3.0, obstacles = obstacles)
                Distribution.PHYLLOTAXIS_1 -> rect.phyllotaxis(1000)
                Distribution.PHYLLOTAXIS_2 -> rect.phyllotaxis(2000)
            }
        }

        var points: List<Vector2> = getPointSet(Distribution.values().first())

        val settings = @Description("General") object {
            @DoubleParameter("Circle Radius", 0.1, 4.0)
            var radius = 1.0

            @OptionParameter("Distribution")
            @Suppress("unused")
            var option = Distribution.PHYLLOTAXIS_1
                set(value) {
                    field = value
                    points = getPointSet(value)
                }
        }

        extend(GUI()) {
            add(settings)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.fill = null
            drawer.stroke = rgb(0.8)
            drawer.translate(width / 2.0, height / 2.0)
            drawer.rectangle(rect)

            drawer.fill = ColorRGBa.BLACK
            drawer.stroke = null
            drawer.circles(points, settings.radius)
        }
    }
}
