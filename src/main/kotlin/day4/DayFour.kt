package day4

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

abstract class DayFour {
    val requiredFields: Set<String> = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid") // "cid" is optional
    val validEyeColors: Set<String> = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

    //    Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?
    fun process(separatePassports: List<String>): Int {
        return separatePassports.filter { isValid(it) }.size
    }

    fun isValid(passport: String): Boolean {
        val keyValues = passport.split(Regex("[ \\n]"))
            .map { keyValue -> keyValue.split(":") }
            .map { it[0] to it[1] }.toMap()
        return isValid(keyValues)
    }

    abstract fun isValid(passport: Map<String, String>): Boolean

    fun process(string: String): Int {
        return process(string.split("\n\n"))
    }

}

class DayFourPartOne : DayFour() {
    override fun isValid(passport: Map<String, String>): Boolean {
        requiredFields.forEach {
            if (!passport.containsKey(it)) {
                return false
            }
        }
        return true
    }
}

class DayFourPartTwo : DayFour() {
    /*
byr (Birth Year) - four digits; at least 1920 and at most 2002.
iyr (Issue Year) - four digits; at least 2010 and at most 2020.
eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
hgt (Height) - a number followed by either cm or in:
If cm, the number must be at least 150 and at most 193.
If in, the number must be at least 59 and at most 76.
hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
pid (Passport ID) - a nine-digit number, including leading zeroes.
cid (Country ID) - ignored, missing or not.
     */
    override fun isValid(passport: Map<String, String>): Boolean {
        requiredFields.forEach {
            if (!passport.containsKey(it)) {
                return false
            }
        }
        val out = passport.getOrDefault("byr", "1").toInt() in 1920..2002
                &&
                passport.getOrDefault("iyr", "1").toInt() in 2010..2020
                &&
                passport.getOrDefault("eyr", "1").toInt() in 2020..2030
                &&
                isValid_hgt(passport)
                &&
                isValid_hcl(passport)
                &&
                isValid_ecl(passport)
                &&
                isValid_pid(passport)
        return out
    }

    private fun isValid_hcl(passport: Map<String, String>): Boolean {
        return passport.getOrDefault("hcl", "1").matches(Regex("#[0-9a-f]{6}"))
    }

    private fun isValid_ecl(passport: Map<String, String>): Boolean {
        return passport.getOrDefault("ecl", "1") in validEyeColors
    }
    private fun isValid_pid(passport: Map<String, String>): Boolean {
        return passport.getOrDefault("pid", "1")
            .matches(Regex("[0-9]{9}"))
    }

    private fun isValid_hgt(passport: Map<String, String>): Boolean {
        val hgt = passport.getOrDefault("hgt", "1")
        if (hgt.endsWith("cm")) {
            if (hgt.replace("cm", "").toInt() in 150..193) {
                return true
            }
        } else if (hgt.endsWith("in")) {
            if (hgt.replace("in", "").toInt() in 59..76) {
                return true
            }
        }
        return false
    }
}

class TestSake1 {
    @Test
    fun testOnProvidedSamples() {
        var input =
            """ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

ecl:gry pid:060033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in"""
        assertEquals(3, DayFourPartOne().process(input))

        // debug part two

        assertEquals(3, DayFourPartTwo().process(input))
        input = """eyr:1972 cid:100
hcl:#18171d ecl:amb hgt:170 pid:186cm iyr:2018 byr:1926

iyr:2019
hcl:#602927 eyr:1967 hgt:170cm
ecl:grn pid:012533040 byr:1946

hcl:dab227 iyr:2012
ecl:brn hgt:182cm pid:021572410 eyr:2020 byr:1992 cid:277

hgt:59cm ecl:zzz
eyr:2038 hcl:74454a iyr:2023
pid:3556412378 byr:2007"""
        assertEquals(0, DayFourPartTwo().process(input))
    }
    @Test
    fun testOnProvidedSamples2() {
        var input = """pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
hcl:#623a2f

eyr:2029 ecl:blu cid:129 byr:1989
iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm

hcl:#888785
hgt:164cm byr:2001 iyr:2015 cid:88
pid:545766238 ecl:hzl
eyr:2022

iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719"""
        assertEquals(4, DayFourPartTwo().process(input))
    }
}

class ResultSake {
    @Test
    fun result() {
        val input = File("src/main/kotlin/day4/input.txt").readText()
        assertEquals(235, DayFourPartOne().process(input))
        assertEquals(194, DayFourPartTwo().process(input))
    }
}
