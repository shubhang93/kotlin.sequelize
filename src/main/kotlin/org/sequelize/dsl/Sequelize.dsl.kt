package org.sequelize.dsl


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









