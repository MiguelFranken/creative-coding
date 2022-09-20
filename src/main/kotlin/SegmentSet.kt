import org.jgrapht.Graph
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense
import org.jgrapht.graph.SimpleWeightedGraph
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.triangulation.Delaunay
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Rectangle
import org.openrndr.shape.ShapeProvider
import org.openrndr.shape.Triangle

private data class Edge(val start: Vector2, val end: Vector2) {
    val length get() = start.distanceTo(end)

    /**
     * Direction-agnostic hash.
     */
    override fun hashCode(): Int {
        return ((end.y + start.y).toBits() xor (end.x + start.x - 1).toBits()).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Edge) {
            val e: Edge = other
            return e.start == start && e.end == end || e.start == end && e.end == start
        }
        return false
    }
}

private fun List<Edge>.toSegments(): List<LineSegment> {
    return this.map { LineSegment(it.start, it.end) }
}

private fun List<Triangle>.addToGraph(graph: Graph<Vector2, Edge>) {
    fun Graph<Vector2, Edge>.add(triangle: Triangle) {
        operator fun <V> Graph<V, *>.plusAssign(vertex: V) { addVertex(vertex) }

        operator fun Graph<Vector2, Edge>.plusAssign(edge: Edge) {
            addEdge(edge.start, edge.end, edge)
            setEdgeWeight(edge, edge.length)
        }

        // add vertices
        this += triangle.x1
        this += triangle.x2
        this += triangle.x3

        // add edges
        this += Edge(triangle.x1, triangle.x2)
        this += Edge(triangle.x1, triangle.x3)
    }

    this.forEach(graph::add)
}

private val defaultPointGenerator: ShapeProvider.() -> List<Vector2> = { scatter(5.0) }

@Suppress("unused", "private")
fun graphMatchedSegments(width: Double, height: Double, generatePoints: ShapeProvider.() -> List<Vector2> = defaultPointGenerator): List<LineSegment> {
    val rect = Rectangle(Vector2.ZERO, width, height)

    // Perfect matching only possible with an even number of vertices
    val points = rect.generatePoints().even()

    return graphMatchedSegments(points)
}

@Suppress("unused")
fun graphMatchedSegments(dimensions: Vector2, generatePoints: ShapeProvider.() -> List<Vector2> = defaultPointGenerator) = graphMatchedSegments(dimensions.x, dimensions.y, generatePoints)

fun graphMatchedSegments(points: List<Vector2>): List<LineSegment> {
    val triangles: List<Triangle> = Delaunay.from(points).triangles()

    val graph = SimpleWeightedGraph<Vector2, Edge>(Edge::class.java)
    triangles.addToGraph(graph)

    return KolmogorovWeightedPerfectMatching(
        graph,
        KolmogorovWeightedPerfectMatching.DEFAULT_OPTIONS,
        ObjectiveSense.MAXIMIZE
    ).matching.edges.toList().toSegments()
}
