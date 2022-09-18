package animation

import org.openrndr.WindowMultisample
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

fun main() = application {
    configure {
        width = 640
        height = 640
        multisample = WindowMultisample.SampleCount(8)
    }

    program {
        class GridCircle(val position: Vector2, delay: Long) {
            val duration = 3000
            val srcSize = 0.0
            val targetSize = 1.5

            val animation = object : Animatable() {
                var size = srcSize
                var initalized = false

                fun animate() {
                    if (!initalized) {
                        delay(delay)
                    }

                    ::size.animate(targetSize, (duration / 2.0).toLong(), Easing.QuartIn).completed.listen {
                        initalized = true
                    }
                    ::size.complete()
                    ::size.animate(srcSize, (duration / 2.0).toLong(), Easing.QuadInOut)
                }

                fun update() {
                    updateAnimation()

                    // loop animation
                    if (!hasAnimations()) {
                        animate()
                    }
                }
            }

            fun display() {
                animation.update()
                drawer.fill = ColorRGBa.BLACK
                drawer.circle(position, animation.size)
            }
        }

        val margin = 10.0

        val circleGroups = drawer.bounds.offsetEdges(-margin).grid(2, 1).flatten().map { bounds ->
            val radius = (bounds.center.y - margin - bounds.corner.y).coerceAtMost(bounds.center.x - margin - bounds.corner.x)
            val circle = Circle(bounds.center, radius)

            bounds.offsetEdges(-margin).grid(50, 50).let {
                val gridCenter = bounds.center
                val firstCell = it.first().first()
                val maxDistance = firstCell.center.distanceTo(gridCenter)

                it.flatten().filter { cell ->
                    circle.contains(cell.center)
                }.map { cell ->
                    val distanceRelative = cell.center.distanceTo(gridCenter) / maxDistance
                    val delay = (distanceRelative * 5000.0).toLong()
                    GridCircle(cell.center, delay)
                }
            }
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            circleGroups.forEach { group ->
                group.forEach { circle ->
                    circle.display()
                }
            }
        }
    }
}

