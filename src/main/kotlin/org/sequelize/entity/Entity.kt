package org.sequelize.entity

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException
import javax.sql.DataSource


class Entity(val dataSource: DataSource, val entityRelationMapping: EntityRelationMapping) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)


    private fun generateNamedArguments(keys: List<String>): String {
        return keys.joinToString(",") { ":$it" }
    }

    private fun getColumnNames(firstRow: Map<String, Any>): List<String> {
        return firstRow.keys.toList()
    }




    fun saveBatch(entityName: String, data: List<Map<String, Any>>): IntArray {
        val firstRow = data.first()
        val columnNames = getColumnNames(firstRow)
        val namesArguments = generateNamedArguments(columnNames)
        val queryStatement = "INSERT INTO $entityName (${columnNames.joinToString(",")}) VALUES ($namesArguments)"
        var results: IntArray = intArrayOf()
        val connection = namedParameterJdbcTemplate.jdbcTemplate.dataSource?.connection
        try {
            connection?.autoCommit = false
            results =
                namedParameterJdbcTemplate.batchUpdate(queryStatement, data.map { it.toMutableMap() }.toTypedArray())
            connection?.commit()

        } catch (exception: Exception) {
            connection?.rollback()
        } catch (exception: SQLException) {
            connection?.rollback()
        } finally {
            connection?.close()
        }

        return results
    }


}