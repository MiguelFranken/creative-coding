package grid

import org.openrndr.draw.Drawer
import org.openrndr.extra.easing.easeQuintInOut
import org.openrndr.extra.noise.Random
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extra.shapes.grid
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.math.abs

interface GridSettings {
    var cellCount : Int
    var maxOff : Double
}

class Grid(private val drawer : Drawer) {
    val settings = @Description("Grid Settings") object : GridSettings {
        @IntParameter("Cell Count", 10, 20)
        override var cellCount : Int = 15

        @DoubleParameter("Maximal Offset", 0.0, 50.0)
        override var maxOff : Double = 50.0
    }

    lateinit var rects : List<List<Rectangle>>
    lateinit var strengths : List<List<Double>>
    lateinit var rotations : List<List<Double>>
    lateinit var rectOffsets : List<List<Vector2>>

    private val firstGridCell get() = rects.first().first()
    private val maxDist get() = drawer.bounds.center.distanceTo(firstGridCell.center)

    companion object {
        const val maxOffNoise = 3.0
        const val maxRotationNoise = 6.0
        const val offThreshold = 4.0
    }

    init {
        update()
    }

    fun update() {
        rects = drawer.bounds.grid(settings.cellCount, settings.cellCount, 100.0, 100.0, -2.0, -2.0)

        strengths = rects.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, it ->
                var rawStrength = 0.0

                if (rowIndex != 0 && rowIndex != rects.size - 1 && colIndex != 0 && colIndex != rects.size - 1) {
                    val dist = (it.center.distanceTo(drawer.bounds.center)).coerceAtLeast(0.0)
                    val distNormalized = dist / maxDist
                    rawStrength = 1 - distNormalized
                }

                easeQuintInOut(rawStrength)
            }
        }

        rotations = strengths.map { row ->
            row.map { easedStrength ->
                val theta = easedStrength * Random.double(-60.0, 60.0)
                if (abs(theta) < 5) 0.0 else theta
            }
        }

        rectOffsets = rects.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, it ->
                val easedStrength = strengths[rowIndex][colIndex]
                val offX = Random.perlin(it.center * 0.33) * settings.maxOff * easedStrength
                val offY = Random.perlin(it.center.yx * 0.33) * settings.maxOff * easedStrength
                Vector2(if (abs(offX) < offThreshold) 0.0 else offX, if (abs(offY) < offThreshold) 0.0 else offX)
            }
        }
    }
}
