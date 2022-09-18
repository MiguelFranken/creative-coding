package animation

import helper.flatMapGrid
import org.openrndr.WindowMultisample
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.animatable.easing.QuartInOut
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2

fun main() = application {
    configure {
        width = 640
        height = 640
        multisample = WindowMultisample.SampleCount(8)
    }

    program {
        class GridCircle(val position: Vector2, delay: Long) {
            val duration = 3000
            val srcSize = 1.0
            val targetSize = 5.0

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
                    ::size.animate(srcSize, (duration / 2.0).toLong(), Easing.QuartInOut)
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

        val circles = drawer.bounds.offsetEdges(-20.0).grid(25, 25).let {
            val gridCenter = drawer.bounds.center
            val firstCell = it.first().first()
            val maxDistance = firstCell.center.distanceTo(gridCenter)

            it.flatMapGrid { _, cell ->
                val distanceRelative = cell.center.distanceTo(gridCenter) / maxDistance
                val delay = (distanceRelative * 5000.0).toLong()
                GridCircle(cell.center, delay)
            }
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            circles.forEach {
                it.display()
            }
        }
    }
}

