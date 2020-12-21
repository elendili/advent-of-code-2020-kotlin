package day21

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class Food(val ingredients: Set<String>, val allergens: Set<String>)

open class DayTwentyOne(val string: String) {
    private val allergens: MutableSet<String> = mutableSetOf()
    private val ingredients: MutableSet<String> = mutableSetOf()
    private val foods: Set<Food> = string.split('\n').map { line ->
        val (ingredientsInFood, allergensInFood) = line.dropLast(1)
            .split("(contains ", limit = 2)
            .map { piece ->
                piece.split(Regex(" |,")).filterNot { it.isNullOrEmpty() }.toSet()
            }
        allergens += allergensInFood
        ingredients += ingredientsInFood
        Food(ingredientsInFood, allergensInFood)
    }.toSet()

    private fun findGoodIngredients(): Set<String> {
        val allergenToIngredientsWhichAppearInAllFoodsWithAllergen = allergens.map { allergen ->
            val ingredients_exist_in_all_food_with_given_allergen = foods
                .filter { it.allergens.contains(allergen) }
                .map { it.ingredients }.reduce { a, b -> a.intersect(b) }
            allergen to ingredients_exist_in_all_food_with_given_allergen
        }.toMap()

        val allergenAssociatedIngredients = allergenToIngredientsWhichAppearInAllFoodsWithAllergen
            .values.reduce { a, b -> a union b }
        val unassociatedIngredientsSet = ingredients - allergenAssociatedIngredients
        return unassociatedIngredientsSet
    }

    fun partOne(): Int {
        val goodIngredients = findGoodIngredients()
        val unassociatedCount = foods.map { food ->
            food.ingredients.count { it in goodIngredients }
        }.sum()
        return unassociatedCount
    }

    fun partTwo(): String {
        val goodIngredients = findGoodIngredients()

        var allergenToPossibleIngredientsCleaned = allergens.map { allergen ->
            val ingredients = foods
                .filter { it.allergens.contains(allergen) }
                .map { it.ingredients - goodIngredients }
                .reduce { a, b -> a intersect b }
            allergen to ingredients
        }.filter {
            it.second.isNotEmpty()
        }.toMap()

        // out map
        val allergenToIngredientMatched: MutableMap<String, String> = mutableMapOf()
        while (allergenToPossibleIngredientsCleaned.isNotEmpty()) {
            // find allergen with only 1 ingredient
            val association = allergenToPossibleIngredientsCleaned
                .filterValues { it.size == 1 }
                .map { (k, s) -> k to s.first() }.first()
            // add to result
            allergenToIngredientMatched += association
            // clean source from found association
            allergenToPossibleIngredientsCleaned =
                allergenToPossibleIngredientsCleaned.mapValues { (_, ingredients) ->
                    ingredients - association.second
                }.filter { (_, ingredients) -> ingredients.isNotEmpty() }
        }

        val outString = allergenToIngredientMatched
            .map { (k, v) -> k to v }
            .sortedBy { it.first }
            .map { it.second }
            .joinToString(",")
        return outString
    }
}

class TestSakePartTwo {
    val sample = """mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)"""

    @Test
    fun test1() {
        assertEquals(5, DayTwentyOne(sample).partOne())
        assertEquals("mxmxvkd,sqjhc,fvjkl", DayTwentyOne(sample).partTwo())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyOne(File("src/main/kotlin/day21/input.txt").readText())
        assertEquals(1913, executor.partOne())
        assertEquals("gpgrb,tjlz,gtjmd,spbxz,pfdkkzp,xcfpc,txzv,znqbr", executor.partTwo())
    }

}