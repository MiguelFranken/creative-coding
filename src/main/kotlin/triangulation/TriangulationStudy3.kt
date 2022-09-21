package triangulation

import graphMatchedSegments
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.scatter
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment

fun main() = application {
    program {
        val rect = drawer.bounds.scaledBy(0.33, 0.5, 0.5, 0.5)

        val offset = 12.0
        val scaledRect = rect.offsetEdges(offset)

        fun generateSegments(): List<LineSegment> {
            val maxTries = 40
            var segments: List<LineSegment> = listOf()

            for (i in 0 until maxTries) {
                try {
                    segments = graphMatchedSegments(rect.dimensions) {
                        val rectSegments = shape.bounds.contour.segments

                        val boundingPoints = rectSegments.map { segment ->
                            segment.equidistantPositions(15)
                        }.flatten()

                        scatter(5.0, distanceToEdge = 5.0) + boundingPoints
                    }
                    break
                } catch (_: Exception) {
                    continue
                }
            }

            return segments
        }

        val segmentsLeft = generateSegments()
        val segmentsRight = generateSegments()

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.translate(drawer.bounds.center)

            // draw segments
            drawer.stroke = ColorRGBa.BLACK
            drawer.fill = null

            // draw segments left rect
            Random.isolated {
                drawer.isolated {
                    drawer.translate(-rect.width/2.0, -rect.height/2.0)
                    drawer.translate(-drawer.bounds.width/4.0, 0.0)

                    segmentsLeft.forEach {
                        drawer.strokeWeight = double(0.01, 5.0)
                        drawer.lineSegment(it)
                    }
                }
            }


            // draw border left rect
            drawer.isolated {
                drawer.strokeWeight = 3.0
                drawer.translate(-scaledRect.width/2.0, -scaledRect.height/2.0)
                drawer.translate(-drawer.bounds.width/4.0, 0.0)
                drawer.rectangle(Vector2.ZERO, scaledRect.width, scaledRect.height)
            }

            Random.isolated {
                // draw segments right rect
                drawer.isolated {
                    drawer.translate(-rect.width/2.0, -rect.height/2.0)
                    drawer.translate(drawer.bounds.width/4.0, 0.0)

                    segmentsRight.forEach {
                        drawer.strokeWeight = double(0.01, 5.0)
                        drawer.lineSegment(it)
                    }
                }
            }

            // draw border right rect
            drawer.isolated {
                drawer.strokeWeight = 3.0
                drawer.translate(-scaledRect.width/2.0, -scaledRect.height/2.0)
                drawer.translate(drawer.bounds.width/4.0, 0.0)
                drawer.rectangle(Vector2.ZERO, scaledRect.width, scaledRect.height)
            }
        }
    }
}
