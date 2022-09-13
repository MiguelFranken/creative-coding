package grid

import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.easing.*
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.Random.randomizeSeed
import org.openrndr.extra.noise.fbmFunc3D
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import kotlin.math.abs

// Based on `Boxes I` by William Kolomyjec
// Based on https://github.com/hamoid/RemakeSession
fun main() = application {
    configure {
        width = 640
        height = 640
        title = "Grid Study 1"
        multisample = WindowMultisample.SampleCount(8)
    }

    oliveProgram {
        val cellCount = 15
        val grid = drawer.bounds.grid(cellCount, cellCount, 100.0, 100.0, -2.0, -2.0)
        val firstGridCell = grid.first().first()
        val maxDist = drawer.bounds.center.distanceTo(firstGridCell.center)
        val maxOff = 50.0
        val maxOffNoise = 3.0
        val maxRotationNoise = 6.0
        val offThreshold = 4.0

        val noise = fbmFunc3D(::simplex, octaves = 3)

        mouse.buttonDown.listen {
            randomizeSeed()
        }

        val strengths = grid.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, it ->
                var rawStrength = 0.0

                if (rowIndex != 0 && rowIndex != grid.size - 1 && colIndex != 0 && colIndex != grid.size - 1) {
                    val dist = (it.center.distanceTo(drawer.bounds.center)).coerceAtLeast(0.0)
                    val distNormalized = dist / maxDist
                    rawStrength = 1 - distNormalized
                }

               easeQuintInOut(rawStrength)
            }
        }

        val rotations = strengths.map { row ->
            row.map { easedStrength ->
                val theta = easedStrength * Random.double(-60.0, 60.0)
                if (abs(theta) < 5) 0.0 else theta
            }
        }

        val rectOffsets = grid.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, it ->
                val easedStrength = strengths[rowIndex][colIndex]
                val offX = Random.perlin(it.center * 0.33) * maxOff * easedStrength
                val offY = Random.perlin(it.center.yx * 0.33) * maxOff * easedStrength
                Vector2(if (abs(offX) < offThreshold) 0.0 else offX, if (abs(offY) < offThreshold) 0.0 else offX)
            }
        }

        extend(Screenshots())

        extend {
            val t = seconds
            drawer.clear(ColorRGBa.WHITE)
            drawer.fill = null

            Random.isolated {
                grid.forEachIndexed { rowIndex, row ->
                    row.forEachIndexed { colIndex, it ->
                        drawer.isolated {
                            val strength = strengths[rowIndex][colIndex]
                            val g = gradient3D(noise, 100, it.x * 0.002, it.y * 0.002, t, 0.001).xy * strength

                            // rotate
                            val fixedTheta = rotations[rowIndex][colIndex]
                            val animationTheta = g.x * maxRotationNoise
                            drawer.translate(it.center)
                            drawer.rotate(fixedTheta + animationTheta)

                            // offsetting
                            val fixedOffset = rectOffsets[rowIndex][colIndex]
                            val animationOffset = g * maxOffNoise
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
