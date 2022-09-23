package triangulation

import MDXSaver
import graphMatchedSegments
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Triangle

fun main() = application {
    program {
        val circle = Circle(Vector2.ZERO, 150.0)

        var debug = false

        var points: List<Vector2> = listOf()
        var triangles: List<Triangle> = listOf()
        val segments = tryUntil(100) {
//            points = circle.contour.equidistantPositions(60) + circle.scatter(3.0, distanceToEdge = 6.0)
//            points = circle.contour.equidistantPositions(80) + circle.scatter(1.5, distanceToEdge = 3.0)
            points = circle.contour.equidistantPositions(180) + circle.scatter(1.0, distanceToEdge = 1.5)
            triangles = Delaunay.from(points).triangles()
            graphMatchedSegments(points)
        } ?: listOf()

        mouse.buttonUp.listen {
            debug = !debug
        }

        extend(MDXSaver()) {
            metadata {
                title = "Segment Set Study 1"
                description = "This study explores how to generate non-intersecting line segments via a perfect matching graph algorithm applied to a random triangulated irregular network (TIN)."
            }

            screenshots {
                contentScale = 4.0
            }
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(drawer.bounds.center)

            if (debug) {
                drawer.fill = null
                drawer.stroke = ColorRGBa.BLACK
                drawer.circle(circle)

                drawer.fill = null
                drawer.stroke = rgb(0.8)
                drawer.shapes(triangles.map { it.shape })
            }

            drawer.isolated {
                drawer.stroke = ColorRGBa.BLACK
                drawer.strokeWeight = .4
                drawer.lineSegments(segments)
            }

            if (debug) {
                drawer.fill = ColorRGBa.RED
                drawer.stroke = null
                drawer.circles(points, 2.0)
            }
        }
    }
}
