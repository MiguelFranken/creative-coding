package animation

import org.openrndr.WindowMultisample
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Shape

fun main() = application {
    configure {
        width = 640
        height = 640
        multisample = WindowMultisample.SampleCount(8)
    }

    program {
        class ScatterAnimation(val shape: Shape, val margin: Double) {
            val bounds = shape.bounds
            val radius = (bounds.center.y - margin - bounds.corner.y).coerceAtMost(bounds.center.x - margin - bounds.corner.x)
            val circle = Circle(bounds.center, radius)

            val positions: List<Vector2> = shape.scatter(5.0).filter { circle.contains(it) }

            // animation settings
            val duration = 3000
            val srcSize = 0.0
            val targetSize = 1.5

            inner class ScatterCircle(val position: Vector2, delay: Long) {
                val animation = object : Animatable() {
                    var size = srcSize
                    var initalized = false

                    fun animate() {
                        if (!initalized) {
                            delay(delay)
                        }

                        ::size.animate(targetSize, (duration / 3.0).toLong(), Easing.QuartIn).completed.listen {
                            initalized = true
                        }
                        ::size.complete()
                        ::size.animate(0.0, (duration / 3.0).toLong(), Easing.QuartOut)
                        ::size.complete()
                        ::size.animate(0.0, (duration / 3.0).toLong())
                        ::size.complete()
                    }

                    fun update() {
                        updateAnimation()

                        // loop animation
                        if (!hasAnimations()) {
                            animate()
                        }
                    }
                }

                fun display() {
                    animation.update()
                    drawer.fill = ColorRGBa.BLACK
                    drawer.stroke = null
                    val size = if (animation.size < 0.1) 0.0 else animation.size
                    if (size <= 0.5) {
                        drawer.fill = rgb(0.0, (size - 0.1) / 0.4)
                    }
                    drawer.circle(position, size)
                }
            }

            fun getCircles(): List<ScatterCircle> {
                val gridCenter = bounds.center
                val maxDistance = bounds.corner.distanceTo(gridCenter)

                return positions.map { position ->
                    val distanceRelative = position.distanceTo(gridCenter) / maxDistance
                    val delay = (distanceRelative * 5000.0).toLong()
                    ScatterCircle(position, delay)
                }
            }
        }

        val margin = 10.0

        val scatterAnimations = drawer.bounds.offsetEdges(-margin).grid(1, 1).flatten().map { bounds ->
            val radius = (bounds.center.y - bounds.corner.y).coerceAtMost(bounds.center.x - bounds.corner.x) - margin
            val circle = Circle(bounds.center, radius).shape
            ScatterAnimation(circle, margin)
        }

        val circles = scatterAnimations.map(ScatterAnimation::getCircles).flatten()

        extend {
            drawer.clear(ColorRGBa.WHITE)
            circles.forEach(ScatterAnimation.ScatterCircle::display)
        }
    }
}

