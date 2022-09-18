package animation

import org.openrndr.animatable.Animatable
import org.openrndr.application
import org.openrndr.color.ColorRGBa

fun main() = application {
    program {
        val radius = 100.0

        val position = object : Animatable() {
            var x = radius

            fun animate() {
                ::x.animate(width.toDouble() - radius, 2000)
                ::x.complete()
                ::x.animate(radius, 2000)
            }

            fun update() {
                updateAnimation()

                if (!hasAnimations()) {
                    animate()
                }
            }
        }

        extend {
            position.update()
            drawer.clear(ColorRGBa.WHITE)
            drawer.circle(position.x, height / 2.0, radius)
        }
    }
}
