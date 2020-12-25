package day25

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

fun getLoopSize(publicKey: Int): Int {
    val subjectNumber = 7
    var result = 1
    var counter = 0
    while (result != publicKey) {
        val temp = result * subjectNumber
        result = temp.rem(20201227)
        counter += 1
    }
    return counter
}

fun getEncryptionKeyWithLoopSize(publicKey: Int, loopSize: Int): Int {
    var result = 1L
    var counter = 0
    while (counter < loopSize) {
        val temp = result * publicKey
        result = temp.rem(20201227)
        counter += 1
    }
    return result.toInt()
}

fun getEncryptionKeyFromPublicKeys(pk1: Int, pk2: Int): Int {
    val loopSize = getLoopSize(pk1)
    return getEncryptionKeyWithLoopSize(pk2, loopSize)
}

class DayTwentyFive(val string: String) {
    val publicKeys = string.split('\n').map { it.toInt() }

    fun partOne(): Int {
        return getEncryptionKeyFromPublicKeys(publicKeys.first(), publicKeys.last())
    }

}

class TestSake1 {

    @Test
    fun getLoopSizeTest() {
        assertEquals(8, getLoopSize(5764801));
        assertEquals(11, getLoopSize(17807724));
    }

    @Test
    fun getEncryptionKeyTest() {
        assertEquals(14897079, getEncryptionKeyWithLoopSize(17807724, 8));
        assertEquals(14897079, getEncryptionKeyWithLoopSize(5764801, 11));
        assertEquals(14897079, getEncryptionKeyFromPublicKeys(5764801, 17807724));
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyFive(File("src/main/kotlin/day25/input.txt").readText())
        assertEquals(16933668, executor.partOne())
    }

}
