package circle

import org.openrndr.application
import org.openrndr.color.rgb
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.ApproximateGaussianBlur
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.math.Vector2

fun main() = application {
    program {
        val gui = GUI()
        val center = drawer.bounds.center

        val composite = compose {
            layer {
                draw {
                    drawer.stroke = null
                    drawer.circle(center - Vector2.UNIT_X * 100.0, 100.0)
                }
                post(ApproximateGaussianBlur()) {
                    window = 25
                    sigma = 5.0
                }
            }.addTo(gui)
            layer {
                draw {
                    drawer.stroke = null
                    drawer.circle(center + Vector2.UNIT_X * 100.0, 100.0)
                }
                post(ApproximateGaussianBlur()) {
                    window = 25
                    sigma = 5.0
                }.addTo(gui, "Blur Layer 1")
            }
        }.addTo(gui, "Root Composition Layer")

        extend(gui)

        extend {
            drawer.clear(rgb(0.2))
            composite.draw(drawer)
        }
    }
}
