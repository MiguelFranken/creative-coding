package animation

import helper.flatMapGrid
import org.openrndr.WindowMultisample
import org.openrndr.animatable.Animatable
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
        class GridCircle(val position: Vector2, delay: Double = 0.0) {
            val animation = object : Animatable() {
                var size = 1.0
                var initalized = false

                fun animate() {
                    if (!initalized) {
                        delay(delay.toLong())
                    }

                    ::size.animate(10.0, 4000).completed.listen {
                        initalized = true
                    }
                    ::size.complete()
                    ::size.animate(1.0, 4000)
                }

                fun update() {
                    updateAnimation()

                    if (!hasAnimations()) {
                        animate()
                    }
                }
            }
        }

        val circles = drawer.bounds.offsetEdges(-20.0).grid(20, 20).flatMapGrid { rowIndex, cell ->
            GridCircle(cell.center, rowIndex * 200.0)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            circles.forEach {
                it.animation.update()
                drawer.fill = ColorRGBa.BLACK
                drawer.circle(it.position, it.animation.size)
            }
        }
    }
}
