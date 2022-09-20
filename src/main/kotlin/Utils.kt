// Return an even sized sublist of the original list
fun <E> List<E>.even(): List<E> {
    if (size and 1 == 1) {
        return subList(0, size - 1)
    }
    return this
}
