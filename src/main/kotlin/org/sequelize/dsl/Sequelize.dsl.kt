package org.sequelize.dsl

import org.sequelize.Sequelize

@DslMarker
annotation class SequelizeDSL


data class QueryParam(var name: String, val params: Map<String, Any>?)


@SequelizeDSL
class QueryParamBuilder {
    var query = ""
    var params: Map<String, Any>? = null


    fun build(): QueryParam {
        return QueryParam(query, params)

    }

}


fun Sequelize.fetchResults(block: QueryParamBuilder.() -> Unit): ArrayList<Map<String, Any>> {
    val queryParam = QueryParamBuilder().apply(block).build()
    return getQueryResults(queryParam.name, queryParam.params) ?: arrayListOf()
}




