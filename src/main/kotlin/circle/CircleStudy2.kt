package circle

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random

fun main() = application {
    program {
        val area = drawer.bounds.offsetEdges(-100.0)
        val numCircles = 5000

        mouse.buttonUp.listen {
            Random.randomizeSeed()
        }

        extend(Screenshots())

        extend {
            drawer.clear(ColorRGBa.WHITE)

            Random.isolated {
                val positions = List(numCircles) { point(area) }
                val radii = List(numCircles) { double(10.0, 30.0) }
                drawer.circles(positions, radii)
            }
        }
    }
}
