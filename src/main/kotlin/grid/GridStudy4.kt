package grid

import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.extra.noise.Random
import org.openrndr.extra.shapes.grid
import org.openrndr.shape.Circle

enum class CircleDirection {
    INNER, OUTER;

    companion object {
        fun random(): CircleDirection {
            return Random.pick(values().toList())
        }
    }
}

enum class CircleAxis {
    HORIZONTAL, VERTICAL;

    companion object {
        fun random(): CircleAxis {
            return Random.pick(values().toList())
        }
    }
}

fun main() = application {
    configure {
        width = 640
        height = 640
        multisample = WindowMultisample.SampleCount(8)
    }

    program {
        val margin = 100.0
        val grid = drawer.bounds.grid(10, 10, margin, margin).flatten()

        mouse.buttonUp.listen {
            Random.randomizeSeed()
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            Random.isolated {
                grid.forEach {
                    val bgColor = if (bool()) ColorRGBa.BLACK else ColorRGBa.WHITE
                    val circleColor1 = if (bool()) ColorRGBa.BLACK else ColorRGBa.WHITE
                    val circleColor2 = if (bool()) ColorRGBa.BLACK else ColorRGBa.WHITE

                    val circleAxis = CircleAxis.random()
                    val circleDirection = CircleDirection.random()

                    drawer.stroke = null
                    drawer.fill = bgColor
                    drawer.rectangle(it)

                    drawer.isolated {
                        drawer.translate(it.center)

                        val circle = when (circleAxis) {
                            CircleAxis.HORIZONTAL -> {
                                when (circleDirection) {
                                    CircleDirection.INNER -> Circle(0.0, 0.0, it.width / 2.0)
                                    CircleDirection.OUTER -> Circle(-it.width / 2.0, 0.0, it.width / 2.0)
                                }
                            }
                            CircleAxis.VERTICAL -> {
                                when (circleDirection) {
                                    CircleDirection.INNER -> Circle(0.0, 0.0, it.width / 2.0)
                                    CircleDirection.OUTER -> Circle(0.0, -it.height / 2.0, it.width / 2.0)
                                }
                            }
                        }

                        val leftRect = it.scaledBy(xScale = 0.5, yScale = 1.0, uAnchor = 0.0)
                        val rightRect = it.scaledBy(xScale = 0.5, yScale = 1.0, uAnchor = 1.0)
                        val upperRect = it.scaledBy(xScale = 1.0, yScale = 0.5, vAnchor = 0.0)
                        val lowerRect = it.scaledBy(xScale = 1.0, yScale = 0.5, vAnchor = 1.0)

                        // display circle 1
                        drawer.drawStyle.clip = when (circleAxis) {
                            CircleAxis.HORIZONTAL -> leftRect
                            CircleAxis.VERTICAL -> upperRect
                        }
                        drawer.fill = circleColor1
                        drawer.circle(circle)

                        // display circle 2
                        drawer.drawStyle.clip = when (circleAxis) {
                            CircleAxis.HORIZONTAL -> rightRect
                            CircleAxis.VERTICAL -> lowerRect
                        }
                        drawer.fill = circleColor2
                        drawer.circle(circle.timesCenter(-1.0))
                    }
                }
            }

            drawer.isolated {
                val gridWidth = drawer.bounds.width - margin * 2
                val gridHeight = drawer.bounds.height - margin * 2
                drawer.translate(drawer.bounds.width / 2.0, drawer.bounds.height / 2.0)
                drawer.fill = null
                drawer.stroke = ColorRGBa.BLACK
                drawer.rectangle(-gridWidth / 2.0, -gridHeight / 2.0, gridWidth, gridHeight)
            }
        }
    }
}

fun Circle.timesCenter(scale: Double) = Circle(center * scale, radius)
