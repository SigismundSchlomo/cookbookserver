package com.sigismund.data

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

class RecipeRepositoryImpl : RecipeRepository {
    override suspend fun addRecipe(recipe: Recipe): EntityID<Int>? {
        var id: EntityID<Int>? = null
        dbQuery {
            id = Recipes.insertAndGetId {
                it[header] = recipe.header
                it[body] = recipe.body
            }
        }
        return id
    }

    override suspend fun deleteRecipe(id: Int) {
        dbQuery { Recipes.deleteWhere { Recipes.id eq id } }
    }

    override suspend fun getRecipes(userId: Int): List<Recipe> {
        return dbQuery {
            Recipes.select {
                Recipes.userId.eq(userId)
            }.mapNotNull { rowToRecipe(it) }
        }
    }

    override suspend fun findRecipe(id: Int): Recipe {
        return dbQuery {
            Recipes.select { Recipes.id eq id }.mapNotNull { rowToRecipe(it) }.first()
        }
    }

    override suspend fun updateRecipe(recipe: Recipe, id: Int) {
        dbQuery { Recipes.update ({ Recipes.id eq id }) {
            it[header] = recipe.header
            it[body] = recipe.body
        } }
    }

    private fun rowToRecipe(row: ResultRow?): Recipe? {
        if (row == null) return null
        return Recipe(
            id = row[Recipes.id].value,
            header = row[Recipes.header],
            body = row[Recipes.body]
        )
    }

}