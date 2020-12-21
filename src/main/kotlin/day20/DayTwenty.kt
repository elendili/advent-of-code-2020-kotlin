package day20

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.sqrt

/*
Solved thanks to https://todd.ginsberg.com/post/advent-of-code/2020/day20/
 */
val sample = """Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###..."""

//data class Point2D(val x:Int,val y:Int)
fun BooleanArray.toInt(): Int {
    var out = 0
    for (i in this.indices) {
        if (this[i]) {
            val shift = this.size - i - 1
            out = out.or(1.shl(shift))
        }
    }
    return out
}

data class Point2D(val x: Int, val y: Int) {
    operator fun plus(other: Point2D): Point2D =
        Point2D(x + other.x, y + other.y)

    operator fun times(by: Int): Point2D =
        Point2D(x * by, y * by)

    infix fun distanceTo(other: Point2D): Int =
        (x - other.x) + (y - other.y)

    fun rotateLeft(): Point2D =
        Point2D(x = y * -1, y = x)

    fun rotateRight(): Point2D =
        Point2D(x = y, y = x * -1)

    companion object {
        val ORIGIN = Point2D(0, 0)
    }
}

enum class Orientation {
    North, East, South, West
}

class Tile {
    val id: Int
    var body: Array<BooleanArray>
    val sides: Set<Int>
    val flippedSides: Set<Int>

    constructor (id: Int, string: String) : this(id,
        string.split('\n')
            .filter { it.isNotEmpty() }
            .map { line ->
                line.toCharArray().map { it == '#' }.toBooleanArray()
            }
            .toTypedArray())

    constructor(tiles2D: List<List<Tile>>) : this(
        -1, fun(): Array<BooleanArray> {
            var out: Array<BooleanArray> = arrayOf()
            tiles2D.forEach { rowTiles ->
                val updatedRowTiles = rowTiles.map { tile ->
                    tile.body.clone().drop(1).dropLast(1)
                        .map { booleanArray ->
                            booleanArray.drop(1).dropLast(1).toBooleanArray()
                        }.toTypedArray()
                }
                var mergedRow: Array<BooleanArray> = arrayOf()
                for (rowInTileIndex in updatedRowTiles[0].indices) {
                    var bigline = booleanArrayOf()
                    for (columnInTileIndex in updatedRowTiles.indices) {
                        val z = updatedRowTiles[columnInTileIndex][rowInTileIndex]
                        bigline += z
                    }
                    mergedRow += bigline
                }
                out += mergedRow

            }
            return out
        }()
    )

    constructor(id: Int = -1, originalBody: Array<BooleanArray>) {
        this.id = id
        this.body = originalBody

        val top = body.first().clone().toInt()
        val topFlipped = body.first().reversedArray().toInt()

        val bottom = body.last().clone().toInt()
        val bottomFlipped = body.last().reversedArray().toInt()

        val left = body.map { it.first() }.toBooleanArray().toInt()
        val leftFlipped = body.map { it.first() }.toBooleanArray().reversedArray().toInt()

        val right = body.map { it.last() }.toBooleanArray().toInt()
        val rightFlipped = body.map { it.last() }.toBooleanArray().reversedArray().toInt()

        this.sides = setOf(top, bottom, left, right)
        this.flippedSides = setOf(topFlipped, bottomFlipped, leftFlipped, rightFlipped)
    }


    private fun hasSide(side: Int): Boolean = side in sides || side in flippedSides

    fun flip(): Tile {
        body = body.map { it.reversedArray() }.toTypedArray()
        return this
    }

    fun rotateClockwise(): Tile {
        body = body.mapIndexed { x, row ->
            row.mapIndexed { y, _ ->
                body[y][x]
            }.reversed().toBooleanArray()
        }.toTypedArray()
        return this;
    }

    fun orientations(): Sequence<Tile> = sequence {
        repeat(2) {
            repeat(4) {
                yield(this@Tile.rotateClockwise())
            }
            this@Tile.flip()
        }
    }

    private fun orientToSide(side: Int, direction: Orientation) =
        orientations().first { it.sideFacing(direction) == side }

    private fun sideFacing(dir: Orientation): Int =
        when (dir) {
            Orientation.North -> body.first().toInt()
            Orientation.South -> body.last().toInt()
            Orientation.West -> body.map { row -> row.first() }.toBooleanArray().toInt()
            Orientation.East -> body.map { row -> row.last() }.toBooleanArray().toInt()
        }

    fun sharedSideCount(tiles: Collection<Tile>): Int =
        sides.sumOf { side ->
            tiles.filterNot { it == this }
                .count { tile -> tile.hasSide(side) }
        }

    fun isSideShared(dir: Orientation, tiles: Collection<Tile>): Boolean =
        tiles
            .filterNot { it == this }
            .any { tile -> tile.hasSide(sideFacing(dir)) }

    fun findAndOrientNeighbor(mySide: Orientation, theirSide: Orientation, tiles: Collection<Tile>): Tile {
        val mySideValue = sideFacing(mySide)
        return tiles
            .filterNot { it == this }
            .first { it.hasSide(mySideValue) }
            .also { it.orientToSide(mySideValue, theirSide) }
    }

    fun convertBodyToPicture(): Array<Array<Char>> {
        var out: Array<Array<Char>> = arrayOf()
        body.forEach { row ->
            var newRow: Array<Char> = arrayOf()
            row.forEach { column -> newRow += if (column) '#' else '.' }
            out += newRow
        }
        return out
    }

    fun maskIfFound(mask: List<Point2D>): Pair<Boolean, Array<Array<Char>>> {
        var found = false
        val maxWidth = mask.maxByOrNull { it.y }!!.y
        val maxHeight = mask.maxByOrNull { it.x }!!.x
        val convertedBody = convertBodyToPicture()
        (0..(body.size - maxHeight)).forEach { x ->
            (0..(body.size - maxWidth)).forEach { y ->
                val lookingAt = Point2D(x, y)
                val actualSpots = mask.map { it + lookingAt }
                if (actualSpots.all { convertedBody[it.x][it.y] == '#' }) {
                    found = true
                    actualSpots.forEach { convertedBody[it.x][it.y] = '0' }
                }
            }
        }
        return found to convertedBody
    }
}

open class DayTwenty(val string: String) {
    val tiles: Map<Int, Tile> = string
        .split("\n\n")
        .map {
            it.split(":\n")
        }
        .map {
            val k = it[0].replace("Tile ", "").toInt()
            val v = it[1]
            k to Tile(k, v)
        }
        .toMap()


    private fun findCorners(): List<Tile> =
        tiles.values
            .filter { tile -> tile.sharedSideCount(tiles.values) == 2 }

    private fun findTopLeftCorner(): Tile =
        tiles.values
            .first { tile -> tile.sharedSideCount(tiles.values) == 2 }
            .orientations()
            .first {
                it.isSideShared(Orientation.South, tiles.values) && it.isSideShared(Orientation.East, tiles.values)
            }

    fun partOne(): Long {
        return findCorners().map { it.id.toLong() }.reduce { acc, i -> acc * i }
    }


    private fun createImage(): List<List<Tile>> {
        val width = sqrt(tiles.count().toFloat()).toInt()
        var mostRecentTile: Tile = findTopLeftCorner()
        var mostRecentRowHeader: Tile = mostRecentTile
        return (0 until width).map { row ->
            (0 until width).map { col ->
                when {
                    row == 0 && col == 0 ->
                        mostRecentTile
                    col == 0 -> {
                        mostRecentRowHeader =
                            mostRecentRowHeader.findAndOrientNeighbor(
                                Orientation.South,
                                Orientation.North,
                                tiles.values
                            )
                        mostRecentTile = mostRecentRowHeader
                        mostRecentRowHeader
                    }
                    else -> {
                        mostRecentTile =
                            mostRecentTile.findAndOrientNeighbor(Orientation.East, Orientation.West, tiles.values)
                        mostRecentTile
                    }
                }
            }
        }
    }

    fun partTwo(): Long {

        val seaMonsterOffsets = listOf(
            Point2D(0, 18), Point2D(1, 0), Point2D(1, 5), Point2D(1, 6), Point2D(1, 11), Point2D(1, 12),
            Point2D(1, 17), Point2D(1, 18), Point2D(1, 19), Point2D(2, 1), Point2D(2, 4), Point2D(2, 7),
            Point2D(2, 10), Point2D(2, 13), Point2D(2, 16)
        )

        val image = createImage()

        val maskedTile = Tile(image)
            .orientations()
            .map { it.maskIfFound(seaMonsterOffsets) }
            .first { it.first }.second

        return maskedTile
            .sumBy { row ->
                row.count { it == '#' }
            }.toLong()

    }

}

class TestSake {

    @Test
    fun booleanArray() {
        assertEquals(5, arrayOf(true, false, true).toBooleanArray().toInt())
        assertEquals(1, arrayOf(false, false, true).toBooleanArray().toInt())
        assertEquals(7, arrayOf(true, true, true).toBooleanArray().toInt())
    }

    @Test
    fun testOnSample() {
        assertEquals(20899048083289, DayTwenty(sample).partOne())
    }

    @Test
    fun testOnSample2() {
        assertEquals(273, DayTwenty(sample).partTwo())
    }
}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwenty(File("src/main/kotlin/day20/input.txt").readText())
        assertEquals(108603771107737, executor.partOne())
    }

    @Test
    fun result2() {
        val executor = DayTwenty(File("src/main/kotlin/day20/input.txt").readText())
        assertEquals(2129, executor.partTwo())
    }

}