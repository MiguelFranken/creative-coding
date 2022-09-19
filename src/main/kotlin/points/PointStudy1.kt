package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.noise.scatter
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle

enum class Distribution {
    RANDOM_1,
    RANDOM_2,
    RANDOM_WITH_OBSTACLE,
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
                Distribution.RANDOM_1 -> rect.scatter(5.0)
                Distribution.RANDOM_2 -> rect.scatter(10.0)
                Distribution.RANDOM_WITH_OBSTACLE -> rect.scatter(3.0, obstacles = obstacles)
            }
        }

        var i = 0
        var points: List<Vector2> = getPointSet(Distribution.values().first())
        mouse.buttonUp.listen {
            val distribution = Distribution.values()[++i % Distribution.values().size]
             points = getPointSet(distribution)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.fill = null
            drawer.stroke = rgb(0.8)
            drawer.translate(width / 2.0, height / 2.0)
            drawer.rectangle(rect)

            points.forEach {
                drawer.fill = ColorRGBa.BLACK
                drawer.stroke = null
                drawer.circle(it, 1.0)
            }
        }
    }
}
