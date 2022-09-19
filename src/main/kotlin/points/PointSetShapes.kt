package points

import org.openrndr.color.rgb
import org.openrndr.draw.Drawer
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.Vector2Parameter
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.shape.*

interface IPointSetShape : ShapeProvider {
    var center: Vector2

    fun display(drawer: Drawer)
}

@Description("Rectangle Shape")
data class PointSetRect(
    @Vector2Parameter("Center Coordinates", 0.0, 200.0)
    override var center: Vector2 = Vector2.ZERO,

    @DoubleParameter("Width", 0.1, 400.0)
    var width: Double = 200.0,

    @DoubleParameter("Height", 0.1, 400.0)
    var height: Double = 200.0
) : IPointSetShape {
    private val rect get() = Rectangle(center - Vector2(width / 2.0, height / 2.0), width, height)

    override fun display(drawer: Drawer) {
        drawer.fill = null
        drawer.stroke = rgb(0.8)
        drawer.rectangle(rect)
    }

    override val shape: Shape get() = rect.shape
}

@Description("Rectangle With Hole Shape")
data class PointSetRectWithHole(
    @Vector2Parameter("Center Coordinates", 0.0, 200.0)
    override var center: Vector2 = Vector2.ZERO,

    @DoubleParameter("Width", 0.1, 400.0)
    var width: Double = 200.0,

    @DoubleParameter("Height", 0.1, 400.0)
    var height: Double = 200.0,

    @DoubleParameter("Offset", 1.0, 200.0)
    var offset: Double = 20.0
) : IPointSetShape {
    private val dimensions: Vector2 get() = Vector2(width, height)
    private val corner: Vector2 get() = center - dimensions/2.0

    private fun computeRect(start: Vector2) = shape {
        contour {
            moveTo(start)
            lineTo(start.x + width, start.y)
            lineTo(start.x + width, start.y + height)
            lineTo(start.x, start.y + height)
            close()
        }
    }

    private val outer: Shape get() = computeRect(corner)

    private val transformationMatrix: Matrix44 get() {
        val scale = Vector2(
            ((width-offset*2.0)/width).coerceIn(0.0, 1.0),
            ((height-offset*2.0)/height).coerceIn(0.0, 1.0)
        )
        return Matrix44(
            c0r0 = scale.x,
            c1r1 = scale.y,
            c2r2 = 1.0,
            c3r3 = 1.0,
            c3r0 = corner.x + (width - width*scale.x) / 2.0,
            c3r1 = corner.y + (height - height*scale.y) / 2.0,
        )
    }

    private val invalidTransformation: Boolean get() = transformationMatrix.c0r0 < 0.000001 || transformationMatrix.c1r1 < 0.000001

    private val inner: Shape get() = computeRect(Vector2.ZERO).transform(transformationMatrix)

    override val shape get() = if (invalidTransformation) outer else outer.difference(inner)

    override fun display(drawer: Drawer) {
        drawer.fill = null
        drawer.stroke = rgb(0.8)
        drawer.shape(shape)
    }
}

@Description("Circle Shape")
data class PointSetCircle(
    @Vector2Parameter("Center Coordinates", 0.0, 200.0)
    override var center: Vector2 = Vector2.ZERO,

    @DoubleParameter("Width", 0.1, 400.0)
    var radius: Double = 100.0,
) : IPointSetShape {
    private val circle get() = Circle(center, radius)

    override fun display(drawer: Drawer) {
        drawer.fill = null
        drawer.stroke = rgb(0.8)
        drawer.circle(circle)
    }

    override val shape: Shape get() = circle.shape
}

enum class PointSetShape(val shapeProvider: IPointSetShape) {
    RECT(PointSetRect()),
    RECT_WITH_HOLE(PointSetRectWithHole()),
    CIRCLE(PointSetCircle());

    fun display(drawer: Drawer) {
        shapeProvider.display(drawer)
    }

    companion object {
        fun addTo(gui: GUI) {
            values().map(PointSetShape::shapeProvider).forEach {
                it.addTo(gui)
            }
        }
    }
}
