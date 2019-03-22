package org.sequelize

import org.sequelize.dsl.QueryParamBuilder
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

    operator fun get(queryName: String): String? {
        return queryMap[queryName]
    }


    private fun getQueryResults(queryName: String, arguments: Map<String, Any>?): ArrayList<Map<String, Any>>? {
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

    fun fetch(block: QueryParamBuilder.() -> Unit): ArrayList<Map<String, Any>> {
        val queryParam = QueryParamBuilder().apply(block).build()
        return getQueryResults(queryParam.queryName, queryParam.params) ?: arrayListOf()
    }

    fun fetchOne(block: QueryParamBuilder.() -> Unit): Map<String, Any>? {
        val queryParam = QueryParamBuilder().apply(block).build()
        return getQueryResults(queryParam.queryName, queryParam.params)?.first()
    }

    fun fetch(queryName: String, params: Map<String, Any>? = null): ArrayList<Map<String, Any>> {
        return getQueryResults(queryName, params) ?: arrayListOf()
    }

    fun fetchOne(queryName: String, params: Map<String, Any>? = null): Map<String, Any>? {
        return getQueryResults(queryName, params)?.first()
    }


}