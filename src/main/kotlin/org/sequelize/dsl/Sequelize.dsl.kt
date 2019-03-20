package org.sequelize.dsl

import org.sequelize.Sequelize


@DslMarker
annotation class SequelizeDSL


data class QueryParam(val queryName: String, val params: Map<String, Any>?, val one: Boolean?)

@SequelizeDSL
class QueryParamBuilder {
    var queryName = ""
    var params: Map<String, Any>? = null
    var one: Boolean? = false


    fun build(): QueryParam {
        return QueryParam(queryName, params, one)
    }


}


fun Sequelize.fetch(block: QueryParamBuilder.() -> Unit): ArrayList<Map<String, Any>> {
    val queryParam = QueryParamBuilder().apply(block).build()
    return getQueryResults(queryParam.queryName, queryParam.params) ?: arrayListOf()
}

fun Sequelize.fetchOne(block: QueryParamBuilder.() -> Unit): Map<String, Any>? {
    val queryParam = QueryParamBuilder().apply(block).build()
    return getQueryResults(queryParam.queryName, queryParam.params)?.first()
}


fun Sequelize.fetch(queryName: String, params: Map<String, Any>? = null): ArrayList<Map<String, Any>> {
    return getQueryResults(queryName, params) ?: arrayListOf()
}

fun Sequelize.fetchOne(queryName: String, params: Map<String, Any>? = null): Map<String, Any>? {
    return getQueryResults(queryName, params)?.first()
}






