package org.sequelize.dsl

import org.sequelize.Sequelize

@DslMarker
annotation class SequelizeDSL

data class QueryParams(var name: String = "", var params: Params? = null)

class Params() {
    private val argumentsMap = mutableMapOf<String, Any>()
    val paramMap
        get() = mapOf<String, Any>(*argumentsMap.entries.map { Pair(it.key, it.value) }.toTypedArray())

    infix fun String.to(value: Any) {
        argumentsMap[this] = value
    }
}


@SequelizeDSL
fun Sequelize.query(block: QueryParams.() -> Unit): ArrayList<Map<String, Any>> {
    val query = QueryParams()
    val queryParams = query.apply(block)
    return getQueryResults(queryParams.name, queryParams.params?.paramMap) ?: arrayListOf()
}

@SequelizeDSL
fun QueryParams.params(block: Params.() -> Unit) {
    params = Params().apply(block)
}

