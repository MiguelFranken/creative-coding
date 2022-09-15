package grid

import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.Random.randomizeSeed
import org.openrndr.extra.noise.fbmFunc3D
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex
import org.openrndr.math.Vector2

// Based on `Boxes I` by William Kolomyjec
// Based on https://github.com/hamoid/RemakeSession
fun main() = application {
    configure {
        width = 640
        height = 640
        title = "Grid Study 1"
        multisample = WindowMultisample.SampleCount(8)
    }

    program {
        val gui = GUI()

        val grid = Grid(drawer)
        gui.add(grid.settings)

        gui.onChange { _, _ -> grid.update() }

        val noise = fbmFunc3D(::simplex, octaves = 3)

        mouse.buttonDown.listen {
            randomizeSeed()
//            grid.update()
        }

        extend(Screenshots())
        extend(gui)
        extend {
            val t = seconds
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = null

            Random.isolated {
                grid.rects.forEachIndexed { rowIndex, row ->
                    row.forEachIndexed { colIndex, it ->
                        drawer.isolated {
                            val strength = grid.strengths[rowIndex][colIndex]
                            val g = gradient3D(noise, 100, it.x * 0.002, it.y * 0.002, t, 0.001).xy * strength

                            // rotate
                            val fixedTheta = grid.rotations[rowIndex][colIndex]
                            val animationTheta = g.x * Grid.maxRotationNoise
                            drawer.translate(it.center)
                            drawer.rotate(fixedTheta + animationTheta)

                            // offsetting
                            val fixedOffset = grid.rectOffsets[rowIndex][colIndex]
                            val animationOffset = g * Grid.maxOffNoise
                            translate(fixedOffset + animationOffset)

                            // draw
                            drawer.translate(-it.dimensions * 0.5) // draw rect from center
                            drawer.rectangle(Vector2.ZERO, it.width, it.height)
                        }
                    }
                }
            }
        }
    }
}
