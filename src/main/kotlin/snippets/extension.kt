package snippets

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import kotlin.math.PI
import kotlin.math.sin

fun main() = application {
    program {
        extend {
            drawer.clear(ColorRGBa.WHITE)
        }
        extend(userDraw = someExtension)
        extend(someOtherExtension)
    }
}

val someExtension: Program.() -> Unit = {
    drawer.isolated {
        drawer.translate(sin(seconds + PI) * 100.0, 0.0)
        drawer.circle(width/2.0,height/2.0, 100.0)
    }
}

val someOtherExtension = object : Extension {
    override var enabled: Boolean = true

    private val drawCircle: Program.() -> Unit = {
        drawer.isolated {
            drawer.translate(sin(seconds) * 100.0, 0.0)
            drawer.circle(width/2.0,height/2.0, 100.0)
        }
    }

    override fun beforeDraw(drawer: Drawer, program: Program) {
        program.drawCircle()
    }
}
