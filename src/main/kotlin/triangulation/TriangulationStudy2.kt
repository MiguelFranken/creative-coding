package triangulation

import even
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Shape
import org.openrndr.shape.Triangle
import org.openrndr.shape.shape


// To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are collinear
// 1 --> Clockwise
// 2 --> Counterclockwise
fun orientation(p: Vector2, q: Vector2, r: Vector2): Int {
    val `val`: Double = (q.y - p.y) * (r.x - q.x) -
            (q.x - p.x) * (r.y - q.y)
    if (`val` < 0.001) return 0 // collinear
    return if (`val` > 0) 1 else 2 // clock or counterclock wise
}

// Prints convex hull of a set of n points.
fun convexHull(points: List<Vector2>): MutableList<Vector2> {
    // There must be at least 3 points
//    if (n < 3) return
    val n = points.size

    // Initialize Result
    val hull: MutableList<Vector2> = mutableListOf()

    // Find the leftmost point
    var l = 0
    for (i in 1 until n) if (points[i].x < points[l].x) l = i

    // Start from leftmost point, keep moving
    // counterclockwise until reach the start point
    // again. This loop runs O(h) times where h is
    // number of points in result or output.
    var p = l
    var q: Int
    do {
        // Add current point to result
        hull.add(points[p])

        // Search for a point 'q' such that
        // orientation(p, q, x) is counterclockwise
        // for all points 'x'. The idea is to keep
        // track of last visited most counterclock-
        // wise point in q. If any point 'i' is more
        // counterclock-wise than q, then update q.
        q = (p + 1) % n
        for (i in 0 until n) {
            // If i is more counterclockwise than
            // current q, then update q
            if (orientation(points[p], points[i], points[q])
                == 2
            ) q = i
        }

        // Now q is the most counterclockwise with
        // respect to p. Set p as q for next iteration,
        // so that q is added to result 'hull'
        p = q
    } while (p != l) // While we don't come to first
    // point


    return hull
    // Print Result
//    for (temp: Vector2 in hull) System.out.println(
//        (("(" + temp.x).toString() + ", " +
//                temp.y).toString() + ")"
//    )
}


fun main() = application {
    program {
        val rect = Rectangle(Vector2.ZERO, 300.0, 300.0)

        // Perfect matching only possible with an even number of vertices
        val points = rect.scatter(20.0).even()
        val delaunay = Delaunay.from(points)
        val triangles = delaunay.triangles()

        val hull = delaunay.hull()

        extend {
            drawer.translate(drawer.bounds.center - rect.dimensions/2.0)
            drawer.clear(ColorRGBa.WHITE)

            drawer.fill = null
            drawer.stroke = ColorRGBa.BLACK
            drawer.shapes(triangles.map(Triangle::shape))

//            drawer.stroke = ColorRGBa.GREEN.opacify(0.5)
//            drawer.contour(hull)

            drawer.fill = ColorRGBa.RED
            drawer.stroke = null
            drawer.circles(points, 2.0)
        }
    }
}
