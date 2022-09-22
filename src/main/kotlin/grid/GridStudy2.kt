package grid

import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.Random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.grid
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Segment

// Based on `Untitled 3` by Reiner Schneeberger
// Based on https://github.com/hamoid/RemakeSession
fun main() = application {
    configure {
        width = 640
        height = 640
        title = "Grid Study 2"
        multisample = WindowMultisample.SampleCount(8)
    }

    oliveProgram {
        val grid = drawer.bounds.grid(10, 10, 50.0, 50.0).flatten()

        fun getSegments(grid: List<Rectangle>): List<Segment> {
            return grid.map {
                Random.pick(it.contour.segments, count = Random.int0(2))
            }.flatten()
        }

        var lines = getSegments(grid)

        mouse.buttonDown.listen {
            Random.randomizeSeed()
            lines = getSegments(grid)
        }

        extend(Screenshots())

        extend {
            drawer.clear(ColorRGBa.WHITE)
            lines.forEach {
                drawer.segment(it)
            }
        }
    }
}

