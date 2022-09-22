package grid

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.grid

// Based on `Detail from Tapestry` by Jean Claude Marquette
// See http://recodeproject.com/artwork/v1n2detail-from-tapestry
fun main() = application {
    configure {
        width = 320
        height = 640
        title = "Grid Study 3"
    }

    oliveProgram {
        val grid = drawer.bounds.grid(10, 20).flatten()

        mouse.buttonUp.listen {
            Random.randomizeSeed()
        }

        extend(Screenshots())

        extend {
            drawer.clear(ColorRGBa.WHITE)

            Random.isolated {
                grid.forEach { cell ->
                    drawer.stroke = null
                    if (bool()) {
                        drawer.fill = rgb(0.0)
                    } else {
                        drawer.fill = null
                    }
                    drawer.rectangle(cell)
                }
            }
        }
    }
}
