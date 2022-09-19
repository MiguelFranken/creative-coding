package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.*


fun main() = application {
    program {
        val gui = GUI()

        val settings = @Description("Circles") object {
            @DoubleParameter("Circle Radius", 2.0, 4.0)
            var radius = 1.0

            @BooleanParameter("Filled Circle")
            var filled = true
        }

        val pointSetCollection = PointSetCollection().addTo(gui)

        extend(gui) {
            add(settings)
            add(pointSetCollection)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(width / 2.0, height / 2.0)

            pointSetCollection.displayShape(drawer)

            drawer.fill = ColorRGBa.BLACK
            drawer.fill = if (settings.filled) ColorRGBa.BLACK else null
            drawer.stroke = if (settings.filled) null else ColorRGBa.BLACK
            drawer.circles(pointSetCollection.points, settings.radius)
        }
    }
}