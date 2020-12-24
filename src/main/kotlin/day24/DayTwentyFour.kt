package day24

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.ConcurrentHashMap

val sample = """sesenwnenenewseeswwswswwnenewsewsw
neeenesenwnwwswnenewnwwsewnenwseswesw
seswneswswsenwwnwse
nwnwneseeswswnenewneswwnewseswneseene
swweswneswnenwsewnwneneseenw
eesenwseswswnenwswnwnwsewwnwsene
sewnenenenesenwsewnenwwwse
wenwwweseeeweswwwnwwe
wsweesenenewnwwnwsenewsenwwsesesenwne
neeswseenwwswnwswswnw
nenwswwsewswnenenewsenwsenwnesesenew
enewnwewneswsewnwswenweswnenwsenwsw
sweneswneswneneenwnewenewwneswswnese
swwesenesewenwneswnwwneseswwne
enesenwswwswneneswsenwnewswseenwsese
wnwnesenesenenwwnenwsewesewsesesew
nenewswnwewswnenesenwnesewesw
eneswnwswnwsenenwnwnwwseeswneewsenese
neswnwewnwnwseenwseesewsenwsweewe
wseweeenwnesenwwwswnew"""

/*
coordinate system is based on axial coordinates from https://www.redblobgames.com/grids/hexagons/
 */

class Floor : ConcurrentHashMap<HexagonalCoordinates, Int>() {
    fun countBlack(): Int {
        return this.values.count { isBlack(it) }
    }

    fun getBlackNeighboursOf(cell: HexagonalCoordinates): Int {
        return cell.neighbours().map { this.getOrDefault(it, 0) }.count { isBlack(it) }
    }

    fun expandWhiteFloorInPlace(): Floor {
        this.filter { (k, v) -> isBlack(v) }
            .forEach { (k, v) ->
                k.neighbours().forEach { neighbour -> this.putIfAbsent(neighbour, 0) }
            }
        return this
    }

    fun isBlack(value: Int): Boolean {
        return value.rem(2) == 1
    }

    fun dayPassInPlace(): Floor {
        this.expandWhiteFloorInPlace()
        val updateMap: MutableMap<HexagonalCoordinates, Int> = mutableMapOf()
        this.forEach { (k, v) ->
            val blackNeighbours = this.getBlackNeighboursOf(k)
            val isBlack = isBlack(v)
            if (isBlack) {
                if (blackNeighbours == 0 || blackNeighbours > 2) {
                    updateMap.put(k, 0)
                }
            } else {
                if (blackNeighbours == 2) {
                    updateMap.put(k, 1)
                }
            }
        }
        this += updateMap
        return this
    }
}


data class HexagonalCoordinates(val q: Int = 0, val r: Int = 0) {
    fun executeCommandsList(string: String): HexagonalCoordinates {
        var current = this
        val startingChars = setOf('n', 's')
        var i = 0
        while (i in string.indices) {
            val c = string[i]
            val command = if (c in startingChars) c.toString() + string[++i] else c.toString()
            current = current.jump(command)
            i++
        }
        return current
    }

    fun neighbours(): Set<HexagonalCoordinates> {
        return setOf(
            HexagonalCoordinates(this.q + 1, this.r),
            HexagonalCoordinates(this.q + 1, this.r - 1),
            HexagonalCoordinates(this.q, this.r - 1),
            HexagonalCoordinates(this.q - 1, this.r),
            HexagonalCoordinates(this.q - 1, this.r + 1),
            HexagonalCoordinates(this.q, this.r + 1)
        )
    }

    fun jump(string: String): HexagonalCoordinates {
        return when (string) {
            "e" -> HexagonalCoordinates(this.q + 1, this.r)
            "ne" -> HexagonalCoordinates(this.q + 1, this.r - 1)
            "nw" -> HexagonalCoordinates(this.q, this.r - 1)
            "w" -> HexagonalCoordinates(this.q - 1, this.r)
            "sw" -> HexagonalCoordinates(this.q - 1, this.r + 1)
            "se" -> HexagonalCoordinates(this.q, this.r + 1)
            else -> throw IllegalArgumentException("unknown")
        }
    }
}

class DayTwentyFour(val string: String) {
    val lines = string.split('\n')

    /* use Axial coordinates

        var axial_directions = [
        Hex(+1, 0),  - east
        Hex(+1, -1), - north-east
        Hex(0, -1),  - north-west
        Hex(-1, 0),  - west
        Hex(-1, +1), - south-west
        Hex(0, +1),  - south-east
    ]

    function hex_direction(direction):
        return axial_directions[direction]

    function hex_neighbor(hex, direction):
        var dir = hex_direction(direction)
        return Hex(hex.q + dir.q, hex.r + dir.r)


         read
         convert to moves and save converted tiles to map

    */

    fun createFloor(): Floor {
        val floor = Floor()
        lines.map { HexagonalCoordinates(0, 0).executeCommandsList(it) }
            .forEach { floor.compute(it) { _, v -> if (v == null) 1 else v + 1 } }
        return floor
    }

    fun partOne(): Int {
        return createFloor().countBlack()
    }

    fun partTwo(daysCount: Int): Int {
        val floor = createFloor()
        repeat(daysCount) { floor.dayPassInPlace() }
        return floor.countBlack()
    }

}

class TestSake1 {

    @Test
    fun whiteBox() {
        assertEquals(
            HexagonalCoordinates(0, 1),
            HexagonalCoordinates().executeCommandsList("esew")
        )
        assertEquals(
            HexagonalCoordinates(),
            HexagonalCoordinates().executeCommandsList("nwwswee")
        )
    }

    @Test
    fun test1OnSample() {
        assertEquals(10, DayTwentyFour(sample).partOne())
    }

    @Test
    fun test2OnSample() {
        assertEquals(15, DayTwentyFour(sample).partTwo(1))
        assertEquals(12, DayTwentyFour(sample).partTwo(2))
        assertEquals(25, DayTwentyFour(sample).partTwo(3))
        assertEquals(37, DayTwentyFour(sample).partTwo(10))
        assertEquals(2208, DayTwentyFour(sample).partTwo(100))
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyFour(File("src/main/kotlin/day24/input.txt").readText())
        assertEquals(388, executor.partOne())
        assertEquals(4002, executor.partTwo(100))
    }

}
