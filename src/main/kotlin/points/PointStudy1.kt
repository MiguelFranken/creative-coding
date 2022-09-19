package points

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.*
import org.openrndr.shape.Rectangle


fun main() = application {
    program {
        val gui = GUI()

        val settings = @Description("General") object {
            @DoubleParameter("Circle Radius", 2.0, 4.0)
            var radius = 1.0
        }

        val rectWidth = 300.0
        val rectHeight = 300.0
        val rect = Rectangle(-rectWidth / 2.0, -rectHeight / 2.0, rectWidth, rectHeight)
        val pointSetCollection = PointSetCollection(rect).addTo(gui)

        extend(gui) {
            add(settings)
            add(pointSetCollection)
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.fill = null
            drawer.stroke = rgb(0.8)
            drawer.translate(width / 2.0, height / 2.0)
            drawer.rectangle(rect)

            drawer.fill = ColorRGBa.BLACK
            drawer.stroke = null
            drawer.circles(pointSetCollection.points, settings.radius)
        }
    }
}
