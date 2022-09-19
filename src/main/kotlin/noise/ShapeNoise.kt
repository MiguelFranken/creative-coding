package noise

import org.openrndr.math.Vector2
import org.openrndr.shape.ShapeProvider
import org.openrndr.shape.contains
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun ShapeProvider.phyllotaxis(n: Int): List<Vector2> {
    val goldenAngle = (PI * (3 - sqrt(5.0))).toFloat()
    val theta = 2 * PI - goldenAngle
    val radius = shape.bounds.corner.distanceTo(shape.bounds.center)
    val fillArea: Double = radius * radius * PI
    val circleSpace = fillArea / n // area per circle
    var cumArea = 0.0 // cumulative circle area

    return (1..n).map {
        val angle: Double = it * theta // rotation per circle
        cumArea += circleSpace // add sm_area to cum_area every loop
        val spiralR = sqrt(cumArea / PI) // expansion of spiral (distance of circle) per loop
        val pX = shape.bounds.center.x + cos(angle) * spiralR // spiral rotation of golden angle per loop on X
        val pY = shape.bounds.center.y + sin(angle) * spiralR // spiral rotation of golden angle per loop on Y
        Vector2(pX, pY)
    }.filter { shape.contains(it) }
}

fun ShapeProvider.plasticLDS(n: Int): List<Vector2> {
    // https://github.com/Atrix256/SampleZoo/blob/master/src/families/_2d/samples/irrational_numbers/irrational_numbers.cpp
    val corner = shape.bounds.corner
    val oppositeCorner = shape.bounds.corner + shape.bounds.dimensions
    val xMin = corner.x
    val yMin = corner.y
    val xMax = oppositeCorner.x
    val yMax = oppositeCorner.y

    val w: Double = xMax - xMin
    val h: Double = yMax - yMin
    val p = 1.324717957244746 // plastic constant

    val a1 = 1.0 / p
    val a2 = 1.0 / (p * p)

    return (0 until n).map { i ->
        val x = ((0.5 + a1 * i) % 1 * w + xMin)
        val y = ((0.5 + a2 * i) % 1 * h + yMin)
        Vector2(x, y)
    }
}
