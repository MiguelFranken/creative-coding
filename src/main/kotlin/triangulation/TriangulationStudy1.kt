package triangulation

import graphMatchedSegments
import noise.plasticLDS
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2

fun main() = application {
    program {
        val dimensions = Vector2(300.0, 300.0)
        val segments = graphMatchedSegments(dimensions) {
            plasticLDS(2000)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(drawer.bounds.center - dimensions/2.0)

            // draw segments
            drawer.stroke = ColorRGBa.BLACK
            drawer.lineSegments(segments)
        }
    }
}
