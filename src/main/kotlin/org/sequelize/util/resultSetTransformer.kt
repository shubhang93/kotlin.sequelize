package org.sequelize.util

import java.sql.ResultSet
import java.sql.ResultSetMetaData

fun resultSetTransformer(
    resultSet: ResultSet,
    resultSetMetaData: ResultSetMetaData
): ArrayList<Map<String, Any>> {
    val results = arrayListOf<Map<String, Any>>()
    val columnCount = resultSetMetaData.columnCount
    while (resultSet.next()) {
        val rowPairs = mutableListOf<Pair<String, Any>>()
        for (i in 1..columnCount) {
            rowPairs.add(Pair(resultSetMetaData.getColumnName(i), resultSet.getObject(i)))
        }
        results.add(mapOf(*rowPairs.toTypedArray()))
    }
    return results
}

