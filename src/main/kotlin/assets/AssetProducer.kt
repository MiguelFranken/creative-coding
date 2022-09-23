import mu.KotlinLogging
import org.openrndr.*
import org.openrndr.events.Event
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.gitarchiver.GitArchiver
import org.openrndr.extra.gitarchiver.GitProvider
import java.io.File

data class MDXMetadata(var title: String = "SomeTitle", var description: String = "SomeDescription")

class MDXSaver(var frontmatter: MDXMetadata = MDXMetadata()) : Extension {
    override var enabled: Boolean = true

    private val assetsFolder = "assets"

    private val archiver = GitArchiver()
    private val screenshots = Screenshots()

    private val afterMdx = Event<Unit>("after-mdx")

    private val git: GitProvider = GitProvider.create()

    fun metadata(configure: MDXMetadata.() -> Unit) = frontmatter.configure()

    fun screenshots(configure: Screenshots.() -> Unit) = screenshots.configure()

    override fun setup(program: Program) {
        program.extend(archiver) {
            commitOnRequestAssets = false
        }

        screenshots.folder = "$assetsFolder/images"
        program.extend(screenshots) {
            async = false
            listenToKeyDownEvent = false
        }

        setupSaver(program)
    }

    private fun setupSaver(program: Program) {
        val oldMetadataFunction = program.assetMetadata

        program.assetMetadata = {
            val oldMetadata = oldMetadataFunction()
            val qualifier = oldMetadata.assetBaseName.split("-").first()
            program.assetProperties["qualifier"] = qualifier
            program.assetProperties["title"] = frontmatter.title
            program.assetProperties["description"] = frontmatter.description
            program.assetProperties["image"] = "$qualifier.png"
            program.assetProperties["hash"] = program.assetProperties["git-commit-hash"]!!
            program.assetProperties.remove("git-commit-hash")
            AssetMetadata(oldMetadata.programName, qualifier, program.assetProperties)
        }

        program.produceAssets.listen { event ->
            val valueText = buildString {
                appendLine("---")
                event.assetMetadata.assetProperties.map { appendLine("${it.key}: '${it.value}'") }.joinToString("\n")
                appendLine("---")
            }

            val output = "${event.assetMetadata.assetBaseName}.mdx"
            val parent = "$assetsFolder/mdx"
            val f = File(parent, output)
            logger.info { "mdx saved to: ${f.path}" }

            if (!f.parentFile.exists())
                f.parentFile.mkdirs()
            if (!f.exists())
                f.createNewFile()

            f.writeText(valueText)

            afterMdx.trigger(Unit)
        }

        program.keyboard.keyDown.listen {
            if (!it.propagationCancelled) {
                git.commitChanges("auto commit from ${program.name}")

                listenAllOnce(afterMdx, screenshots.afterScreenshot, fun() {
                    git.commitChanges("saved assets for program ${program.name}")
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
