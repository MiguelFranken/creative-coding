package triangulation

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Triangle

fun main() = application {
    program {
        val rect = Rectangle(Vector2.ZERO, 100.0, 100.0)
        val points = rect.scatter(20.0)
        val delaunay = Delaunay.from(points)

        val triangles: List<Triangle> = delaunay.triangles()
        delaunay.update()

        extend {
            drawer.translate(width / 2.0, height / 2.0)
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = null
            drawer.stroke = ColorRGBa.BLACK
            drawer.rectangle(rect)

            drawer.fill = null
            drawer.stroke= rgb(0.7)
            drawer.shapes(triangles.map { it.shape })

            drawer.stroke = null
            drawer.fill = ColorRGBa.BLACK
            drawer.circles(points, 2.0)
        }
    }
}
