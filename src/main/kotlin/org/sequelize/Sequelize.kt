package org.sequelize

import org.sequelize.util.extractQueryMap
import org.sequelize.util.resultSetTransformer
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

class Sequelize(dataSource: DataSource, queriesFilePath: String) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private var queryMap: Map<String, String> = mapOf()

    init {
        queryMap = extractQueryMap(filePath = queriesFilePath)
    }

    fun getQuery(queryName: String): String? {
        return queryMap[queryName]
    }

    fun getQueryList(vararg queryNames: String): List<String?> {
        return queryNames.map { queryMap[it] }.filter { it != null }
    }


    fun getQueryResults(queryName: String, arguments: Map<String, Any>?): ArrayList<Map<String, Any>>? {
        val queryStmt = queryMap[queryName]
        return if (queryStmt != null && arguments != null) {
            namedParameterJdbcTemplate.execute(queryStmt, arguments) { ps ->
                val resultSet = ps.executeQuery()
                resultSetTransformer(resultSet, resultSet.metaData)
            }
        } else if (queryStmt != null && arguments == null) {
            namedParameterJdbcTemplate.execute(queryStmt) { ps ->
                val resultSet = ps.executeQuery()
                resultSetTransformer(resultSet, resultSet.metaData)
            }
        } else
            throw Exception("$queryName query Not Found")
    }
}