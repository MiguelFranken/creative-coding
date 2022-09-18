package animation

import org.openrndr.animatable.Animatable
import org.openrndr.application
import org.openrndr.color.ColorRGBa

fun main() = application {
    program {
        val animation = object : Animatable() {
            var color = ColorRGBa.RED
        }

        animation.apply {
            ::color.animate(ColorRGBa.BLUE, 5000)
        }

        extend {
            animation.updateAnimation()
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = animation.color
            drawer.circle(drawer.bounds.center, 100.0)
        }
    }
}
