package com.sigismund.data.cookingstep

import com.sigismund.data.DatabaseFactory.dbQuery
import com.sigismund.domain.data.sources.CookingStepDataSource
import com.sigismund.models.CookingStep
import org.jetbrains.exposed.sql.*

class CookingStepsDataSourceImpl : CookingStepDataSource {

    override suspend fun getCookingSteps(recipeId: Int): List<CookingStep> {
        return dbQuery {
            CookingSteps.select {
                CookingSteps.recipeId.eq(recipeId)
            }.mapNotNull { row ->
                rowToCookingStep(row)
            }
        }
    }

    override suspend fun deleteCookingSteps(recipeId: Int) {
        dbQuery { CookingSteps.deleteWhere { CookingSteps.recipeId eq recipeId} }
    }

    override suspend fun insertCookingSteps(cookingSteps: List<CookingStep>) {
        if (cookingSteps.isNullOrEmpty()) return
        cookingSteps.forEach { cookingStep ->
            dbQuery {
                CookingSteps.insert { cookingSteps ->
                    cookingSteps[recipeId] = cookingStep.recipeId
                    cookingSteps[description] = cookingStep.description
                }
            }
        }
    }

    override suspend fun updateCookingSteps(cookingSteps: List<CookingStep>, recipeId: Int) {
        if (cookingSteps.isNullOrEmpty()) return
        cookingSteps.forEach { cookingStep ->
            dbQuery { CookingSteps.update ({ CookingSteps.recipeId eq recipeId }) {
                it[description] = cookingStep.description
            } }
        }
    }

    private fun rowToCookingStep(row: ResultRow?): CookingStep? {
        if (row == null) return null
        return CookingStep(
                recipeId = row[CookingSteps.recipeId],
                description = row[CookingSteps.description]
        )
    }

}