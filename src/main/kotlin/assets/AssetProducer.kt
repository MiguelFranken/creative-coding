import mu.KotlinLogging
import org.openrndr.*
import org.openrndr.events.Event
import org.openrndr.extensions.ScreenshotEvent
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.gitarchiver.GitArchiver
import org.openrndr.extra.gitarchiver.GitProvider
import java.io.File

class MDXSaver(var title: String = "", var description: String = "") : Extension {
    override var enabled: Boolean = true

    private val assetsFolder = "assets"

    private val archiver = GitArchiver()
    private val screenshots = Screenshots()

    private val afterMdx = Event<Unit>("after-mdx")

    private val git: GitProvider = GitProvider.create()

    fun screenshots(configure: Screenshots.() -> Unit) = screenshots.configure()

    override fun setup(program: Program) {
        program.extend(archiver) {
            commitOnRequestAssets = false
        }

        screenshots.folder = assetsFolder
        program.extend(screenshots) {
            async = false
            listenToKeyDownEvent = false
        }

        setupSaver(program)
    }

    private fun setupSaver(program: Program) {
        val oldMetadataFunction = program.assetMetadata

        program.assetMetadata = {
            program.assetProperties["title"] = title
            program.assetProperties["description"] = description
            val oldMetadata = oldMetadataFunction()
            AssetMetadata(oldMetadata.programName, oldMetadata.assetBaseName, program.assetProperties)
        }

        program.produceAssets.listen {
            val valueText = buildString {
                appendLine("---")
                it.assetMetadata.assetProperties.map { appendLine("${it.key}: '${it.value}'") }.joinToString("\n")
                appendLine("---")
            }

            val output = "${assetsFolder}/${it.assetMetadata.assetBaseName}.mdx"
            logger.info { "mdx saved to: $output" }
            File(output).writeText(valueText)

            afterMdx.trigger(Unit)
        }

        program.keyboard.keyDown.listen {
            if (!it.propagationCancelled) {
                listenAllOnce(afterMdx, screenshots.afterScreenshot, fun() {
                    git.commitChanges("auto commit from ${program.name}")
                })

                program.requestAssets.trigger(RequestAssetsEvent(this, program))
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger { }
    }
}

@Suppress("unused")
@JvmName("listenAllOnceEvent1")
fun <R, S> listenAllOnce(event1: Event<R>, event2: Event<S>, listener: (e1: R) -> Unit) {
    listenAllOnce(event1, event2) { e1, _ -> listener(e1) }
}

@Suppress("unused")
@JvmName("listenAllOnceEvent2")
fun <R, S> listenAllOnce(event1: Event<R>, event2: Event<S>, listener: (e2: S) -> Unit) {
    listenAllOnce(event1, event2) { _, e2 -> listener(e2) }
}

@Suppress("unused")
fun <R, S> listenAllOnce(event1: Event<R>, event2: Event<S>, listener: () -> Unit) {
    listenAllOnce(event1, event2) { _, _ -> listener() }
}

fun <R, S> listenAllOnce(event1: Event<R>, event2: Event<S>, listener: (e1: R, e2: S) -> Unit) {
    var completed = 0

    var result1: R? = null
    var result2: S? = null

    val allEvent = Event<Unit>("all-event", postpone = false)

    fun <T> Event<T>.wait(listener: (T) -> Unit) {
        listenOnce {
            completed++
            listener(it)

            if (completed == 2) {
                allEvent.trigger(Unit)
            }
        }
    }

    event1.wait() {
        result1 = it
    }

    event2.wait() {
        result2 = it
    }

    allEvent.listenOnce { listener(result1!!, result2!!) }
}