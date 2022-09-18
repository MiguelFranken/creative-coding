package animation

import org.openrndr.animatable.Animatable
import org.openrndr.application
import org.openrndr.color.ColorRGBa

fun main() = application {
    program {
        val position = object : Animatable() {
            var x = 0.0
            var y = 0.0
        }

        position.apply {
            ::x.animate(width / 2.0, 2000)
            ::x.complete()
            ::y.animate(height / 2.0, 2000)
            ::y.complete()
            ::x.animate(width.toDouble(), 2000)
            ::y.animate(height.toDouble(), 2000)
        }

        extend {
            position.updateAnimation()
            drawer.clear(ColorRGBa.WHITE)
            drawer.circle(position.x, position.y, 100.0)
        }
    }
}
