package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.*
import org.openrndr.math.Vector2
import org.openrndr.shape.shape


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

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(width / 2.0, height / 2.0)

            pointSetCollection.displayShape(drawer)

            drawer.fill = if (circle.filled) ColorRGBa.BLACK else null
            drawer.stroke = if (circle.filled) null else ColorRGBa.BLACK
            drawer.circles(pointSetCollection.points, circle.radius)

            val shape = shape {
                contour {
                    moveTo(Vector2.ZERO)
                    lineTo(100.0, 0.0)
                    lineTo(100.0, 100.0)
                    lineTo(0.0, 100.0)
                    close()
                }
                hole {
                    moveTo(Vector2.ONE * 20.0)
                    lineTo(80.0, 20.0)
                    lineTo(80.0, 80.0)
                    lineTo(20.0, 80.0)
                    close()
                }
            }

//            drawer.fill = ColorRGBa.RED
//            drawer.shape(shape)
        }
    }
}
