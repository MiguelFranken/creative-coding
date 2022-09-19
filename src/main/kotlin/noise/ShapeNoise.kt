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
