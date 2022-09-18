package circle

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.Random
import org.openrndr.shape.Circle

fun main() = application {
    program {
        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 1.0

            Random.isolated {
                val circles = List(50000) {
                    Circle(double(0.0) * width, double(0.0) * height, double(0.0) * 10.0 + 10)
                }
                drawer.circles(circles)
            }
        }
    }
}
