package helper

fun <T, R> Iterable<Iterable<T>>.flatMapGrid(
    transform: (rowIndex: Int, colIndex: Int, cell: T) -> R
): List<R> = this.flatMapIndexed { rowIndex, cells -> cells.mapIndexed { colIndex, it -> transform(rowIndex, colIndex, it) }}

fun <T, R> Iterable<Iterable<T>>.flatMapGrid(
    transform: (rowIndex: Int, T) -> R
): List<R> = this.flatMapIndexed { rowIndex, cells -> cells.map { transform(rowIndex, it) }}
