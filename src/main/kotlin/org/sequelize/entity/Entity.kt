package org.sequelize.entity

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource


class Entity(val dataSource: DataSource) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private fun getColumnNamesAndValueCount(data: List<Map<String, Any>>): Map<String, Any> {
        val firstRow = data.first()
        return mapOf(
            "columns" to firstRow.keys.toList(),
            "valueCount" to firstRow.values.size
        )
    }

    fun save(entityName: String, data: List<Map<String, Any>>): IntArray {
        val columns = getColumnNamesAndValueCount(data)["columns"] as List<String>
        val valueCount = getColumnNamesAndValueCount(data)["valueCount"]
        val placeholders = "?".repeat(valueCount as Int).split("").filter { it != "" }.joinToString(",")
        val queryStatement = "INSERT INTO $entityName (${columns.joinToString(",")}) VALUES($placeholders)"
        println("QS -> $queryStatement")
        val values = data.map { it.values.toTypedArray() }
        var result: IntArray? = null
        namedParameterJdbcTemplate.jdbcTemplate.execute { con: Connection ->
            try {
                con.autoCommit = false
                result = namedParameterJdbcTemplate.jdbcTemplate.batchUpdate(queryStatement, values)
                con.commit()
            } catch (exception: SQLException) {
                con.rollback()
                println("Exception Occurred while Saving")
                println(exception.stackTrace)
            }
        }

        return result ?: intArrayOf()

    }
}