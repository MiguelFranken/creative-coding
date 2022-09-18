package animation

import org.openrndr.animatable.Animatable
import org.openrndr.application
import org.openrndr.color.ColorRGBa

fun main() = application {
    program {
        val animation = object : Animatable() {
            var x = 0.0
        }

        animation.apply {
            ::x.animate(width.toDouble(), 5000)
        }

        extend {
            animation.updateAnimation()
            drawer.fill = ColorRGBa.PINK
            drawer.stroke = null
            drawer.circle(animation.x, height / 2.0, 100.0)
        }
    }
}
