package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.compositor.blend
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.fx.blend.Normal
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.*
import points.set.PointSetCollection


fun main() = application {
    program {
        val gui = GUI()

        val circle = (@Description("Circles") object {
            @DoubleParameter("Circle Radius", 2.0, 4.0)
            var radius = 1.0

            @BooleanParameter("Filled Circle")
            var filled = true
        }).addTo(gui)

        val pointSetCollection = PointSetCollection().addTo(gui)

        extend(gui)

        val composite = compose {
            draw {
                drawer.clear(ColorRGBa.WHITE)
            }

            layer {
                // clipping mask
                layer {
                    draw {
                        drawer.fill = ColorRGBa.WHITE
                        drawer.stroke = null
                        drawer.shape(pointSetCollection.shape)
                    }
                }

                // circles
                layer {
                    blend(Normal()) {
                        clip = true
                    }
                    draw {
                        drawer.fill = if (circle.filled) ColorRGBa.BLACK else null
                        drawer.stroke = if (circle.filled) null else ColorRGBa.BLACK
                        drawer.circles(pointSetCollection.points, circle.radius)
                    }
                }

                // shape border
                layer {
                    draw {
                        pointSetCollection.displayShape(drawer)
                    }
                }
            }
        }

        extend {
            drawer.translate(width / 2.0, height / 2.0)
            composite.draw(drawer)
        }
    }
}
