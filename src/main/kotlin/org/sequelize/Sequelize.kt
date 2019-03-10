package org.sequelize

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import javax.sql.DataSource

class Sequelize(dataSource: DataSource, queriesFilePath: String) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private var queryMap: Map<String, String> = mapOf()

    init {
        queryMap = extractQueryMap(filePath = queriesFilePath)
    }


    private fun resultSetTransformer(
        resultSet: ResultSet,
        resultSetMetaData: ResultSetMetaData
    ): ArrayList<Map<String, Any>> {
        val results = arrayListOf<Map<String, Any>>()
        val columnCount = resultSetMetaData.columnCount
        while (resultSet.next()) {
            val rowMap = mutableMapOf<String, Any>()
            for (i in 1..columnCount) {
                rowMap[resultSetMetaData.getColumnName(i)] = resultSet.getObject(i)
            }
            results.add(rowMap)
        }
        return results
    }

    fun getQueryResults(queryName: String, arguments: Map<String, Any>?): ArrayList<Map<String, Any>>? {
        val queryStmt = queryMap[queryName]
        if (queryStmt != null && arguments != null) {
            return namedParameterJdbcTemplate.execute(queryStmt, arguments) { ps ->
                val resultSet = ps.executeQuery()
                resultSetTransformer(resultSet, resultSet.metaData)
            }
        } else if (queryStmt != null && arguments == null) {
            return namedParameterJdbcTemplate.execute(queryStmt) { ps ->
                val resultSet = ps.executeQuery()
                resultSetTransformer(resultSet, resultSet.metaData)
            }
        } else
            throw Exception("$queryName query Not Found")
    }
}