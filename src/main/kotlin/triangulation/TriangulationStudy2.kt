package triangulation

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun main() {
    val graph: Graph<String, DefaultEdge> = SimpleGraph(DefaultEdge::class.java)
    val v1 = "v1"
    val v2 = "v2"
    val v3 = "v3"
    val v4 = "v4"

    // add the vertices
    graph.addVertex(v1)
    graph.addVertex(v2)
    graph.addVertex(v3)
    graph.addVertex(v4)

    // add edges to create a circuit
    graph.addEdge(v1, v2)
    graph.addEdge(v2, v3)
    graph.addEdge(v3, v4)
    graph.addEdge(v4, v1)

    println("Graph: $graph")
}
