package day12

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.absoluteValue

data class Command(var name: Char, var value: Int)
data class MutablePoint(var x: Int, var y: Int) {
    fun manhattanDistance(): Int {
        return x.absoluteValue + y.absoluteValue
    }

    // given x=-1,y=-2 rotate counter-clockwise 90  => x=y, y=-x
    fun rotateLeftRelativeToOrigin() {
        x = y.also { y = -x } // swap variables
    }

    // given x,y and rotate clockwise 90  => x=-y, y=x
    fun rotateRightRelativeToOrigin() {
        x = -y.also { y = x } // swap variables
    }
}

data class ShipOne(
    val coordinates: MutablePoint = MutablePoint(0, 0), var direction: Int = 0
) {

    fun execute(command: Command) {
        val (com, value) = command
        when (com) {
            'N' -> coordinates.y -= value
            'S' -> coordinates.y += value
            'E' -> coordinates.x += value
            'W' -> coordinates.x -= value
            'L' -> direction = ((direction + 4) - (value / 90)) % 4
            'R' -> direction = ((direction + 4) + (value / 90)) % 4
            'F' -> {
                when (direction) {
                    0 -> coordinates.x += value
                    1 -> coordinates.y += value
                    2 -> coordinates.x -= value
                    3 -> coordinates.y -= value
                }
            }
        }
    }
}


data class ShipTwo(
    val coordinates: MutablePoint = MutablePoint(0, 0),
    val wayPoint: MutablePoint = MutablePoint(10, -1)
) {

    fun execute(command: Command) {
        val (com, value) = command
        when (com) {
            'N' -> wayPoint.y -= value
            'S' -> wayPoint.y += value
            'W' -> wayPoint.x -= value
            'E' -> wayPoint.x += value
            'L' -> repeat(value / 90) { wayPoint.rotateLeftRelativeToOrigin() }
            'R' -> repeat(value / 90) { wayPoint.rotateRightRelativeToOrigin() }
            'F' -> {
                coordinates.x += wayPoint.x * value
                coordinates.y += wayPoint.y * value
            }
        }
    }
}

// ================================
class DayTwelve(strings: List<String>) {
    /**
     * mappin of girection:
     * 0 - E, 1 - S, 2 - W, 3 - N
     */

    private val commands: List<Command> = strings.map { Command(it[0], it.substring(1).toInt()) }

    fun partOne(): Int {
        val ship = ShipOne()
        commands.forEach { ship.execute(it) }
        return ship.coordinates.manhattanDistance()
    }

    fun partTwo(): Int {
        val ship = ShipTwo()
        commands.forEach { ship.execute(it) }
        return ship.coordinates.manhattanDistance()
    }

}

class TestSake {
    private val input1 = """F10
N3
F7
R90
F11""".split("\n")

    @Test
    fun testOnProvidedSamplesBase() {
        assertEquals(25, DayTwelve(input1).partOne())
        assertEquals(286, DayTwelve(input1).partTwo())
    }

}

class ResultSake {
    @Test
    fun result() {
        val executor = DayTwelve(File("src/main/kotlin/day12/input.txt").readLines())
        assertEquals(1441, executor.partOne())
        assertEquals(61616, executor.partTwo())
    }
}