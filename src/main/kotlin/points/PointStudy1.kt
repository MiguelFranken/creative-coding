package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.*


fun main() = application {
    program {
        val gui = GUI()

        val settings = @Description("General") object {
            @DoubleParameter("Circle Radius", 2.0, 4.0)
            var radius = 1.0
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
            drawer.stroke = null
            drawer.circles(pointSetCollection.points, settings.radius)
        }
    }
}
