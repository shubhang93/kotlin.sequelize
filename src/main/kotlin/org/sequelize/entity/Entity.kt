package org.sequelize.entity

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException
import javax.sql.DataSource


class Entity(dataSource: DataSource, private val entityRelationMapping: Map<String, Table>) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)


    private fun generateNamedArguments(keys: List<String>): String {
        return keys.joinToString(",") { ":$it" }
    }

    private fun getColumnNames(firstRow: Map<String, Any>): List<String> {
        return firstRow.keys.toList()
    }

    fun saveOrUpdateOne(entityName: String, data: Map<String, Any>) {
        val columnNames = getColumnNames(data)
        val namedArguments = generateNamedArguments(columnNames)
        val pk = entityRelationMapping[entityName]?.primaryKey
        val compoundKeys = entityRelationMapping[entityName]?.compoundPrimaryKeys
        val compoundWhereConstruct =
            if (compoundKeys != null) generateWhereConstructForCompoundKeys(compoundKeys) else " WHERE $pk = :$pk"
        val selectStmt = "SELECT * FROM $entityName$compoundWhereConstruct"

        println("Select Stmt --> $selectStmt")


    }

    private fun generateWhereConstructForCompoundKeys(compoundKeys: List<String>): String {
        return " WHERE " + compoundKeys.joinToString(" AND ") { "$it=:$it" }
    }


    fun saveBatch(entityName: String, data: List<Map<String, Any>>): IntArray {
        val columnNames = getColumnNames(data.first())
        val namedArguments = generateNamedArguments(columnNames)
        val queryStatement = "INSERT INTO $entityName (${columnNames.joinToString(",")}) VALUES ($namedArguments)"
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