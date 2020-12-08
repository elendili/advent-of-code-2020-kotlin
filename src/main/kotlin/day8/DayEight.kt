package day8

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*


class DayEight(strings: List<String>) {
    data class Command(val name: String, val arg: Int)
    data class ExecutionResult(val normalExit: Int = -1, val infiniteLoopExit: Int = -1) {
        fun isNormal(): Boolean {
            return normalExit > -1
        }
    }

    private val commands: List<Command> = strings.map {
        val (command, argS) = it.split(" ")
        Command(command, argS.toInt())
    }

    fun partOne(): Int {
        return executeCommands(commands).infiniteLoopExit
    }

    private fun executeCommands(commands: List<Command>): ExecutionResult {
        var acc = 0;
        var index = 0;
        val bitset = BitSet(commands.size)
        while (index in commands.indices) {
            // check second execution of the same command
            if (bitset.get(index)) return ExecutionResult(infiniteLoopExit = acc)
            else bitset.set(index)
            // execute command
            val cc = commands[index]
            when (cc.name) {
                "acc" -> {
                    acc += cc.arg
                    index += 1
                }
                "jmp" -> index += cc.arg
                "nop" -> index += 1
                else -> throw IllegalArgumentException("unknown command '${cc.name}'")
            }
        }
        return ExecutionResult(normalExit = acc)
    }

    fun partTwo(): Int {
        for (changeIndex in 0..commands.size) {
            val updatedCommands = commands.mapIndexed { i, com ->
                if (i == changeIndex) {
                    when (com.name) {
                        "jmp" -> {
                            Command("nop", com.arg)
                        }
                        "nop" -> {
                            Command("jmp", com.arg)
                        }
                        else -> com
                    }
                } else com
            }.toList()
            val result = executeCommands(updatedCommands)
            if (result.isNormal()) {
                return result.normalExit
            }
        }
        throw IllegalArgumentException("no way to fix commands")
    }


}

class TestSake {
    val input = """nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6""".split("\n")

    @Test
    fun testOnProvidedSamplesBase() {
        val executor = DayEight(input);
        assertEquals(5, executor.partOne())
        assertEquals(8, executor.partTwo())
    }

}


class ResultSake {
    @Test
    fun result() {
        val executor = DayEight(File("src/main/kotlin/day8/input.txt").readLines())
        assertEquals(1584, executor.partOne())
        assertEquals(920, executor.partTwo())
    }
}
