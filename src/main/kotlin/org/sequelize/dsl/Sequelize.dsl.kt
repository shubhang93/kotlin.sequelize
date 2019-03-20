package org.sequelize.dsl

import org.sequelize.Sequelize


@DslMarker
annotation class SequelizeDSL


data class QueryParam(val queryName: String, val params: Map<String, Any>?)

@SequelizeDSL
class QueryParamBuilder {
    var queryName = ""
    var params: Map<String, Any>? = null


    fun build(): QueryParam {

        return QueryParam(queryName, params)

    }


}


fun Sequelize.fetchResults(block: QueryParamBuilder.() -> Unit): ArrayList<Map<String, Any>> {
    val queryParam = QueryParamBuilder().apply(block).build()
    return getQueryResults(queryParam.queryName, queryParam.params) ?: arrayListOf()
}








