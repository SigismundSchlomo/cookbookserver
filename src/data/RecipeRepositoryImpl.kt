package com.sigismund.data

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.data.models.Recipe
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

class RecipeRepositoryImpl() : RecipeRepository {
    override suspend fun addRecipe(header: String, body: String): EntityID<Int> {
        var id: EntityID<Int>? = null
        dbQuery {
            id = Recipes.insertAndGetId {
                it[Recipes.header] = header
                it[Recipes.body] = body
            }
        }
        return id!!
    }

    override suspend fun deleteRecipe(id: Int) {
        dbQuery { Recipes.deleteWhere { Recipes.id eq id } }
    }

    override suspend fun getRecipes(): List<Recipe> {
        return dbQuery {
            Recipes.selectAll().mapNotNull { rowToRecipe(it) }
        }
    }

    override suspend fun findRecipe(id: Int): Recipe {
        return dbQuery {
            Recipes.select { Recipes.id eq id }.mapNotNull { rowToRecipe(it) }.first()
        }
    }

    override suspend fun updateRecipe(id: Int, header: String, body: String) {
        dbQuery { Recipes.update ({ Recipes.id eq id }) {
            it[Recipes.header] = header
            it[Recipes.body] = body
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