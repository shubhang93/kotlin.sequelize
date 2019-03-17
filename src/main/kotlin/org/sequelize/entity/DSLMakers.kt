package org.sequelize.entity

@DslMarker
annotation class EntityRelationMappingDSL

private fun Map<String, Table>.toImmutableMap(): Map<String, Table> {
    val entries = this.entries.toList()
    val pairs = entries.map { Pair(it.key, it.value) }
    return mapOf(*pairs.toTypedArray())
}


data class Table(
    val tableName: String,
    val primaryKey: String,
    val childEntity: String?,
    val foreignKey: String?,
    val compoundPrimaryKeys: List<String>?,
    val nullableColumns: List<String>?
)

@EntityRelationMappingDSL
class TableBuilder {
    var name = ""
    var primaryKey = ""
    var foreignKey: String? = null
    var childTable: String? = null
    var compoundKeys: List<String>? = null
    var nullColumns: List<String>? = null


    fun build(): Table {
        return Table(name, primaryKey, childTable, foreignKey, compoundKeys, nullColumns)
    }
}

@EntityRelationMappingDSL
class EntityRelationMapping {
    private val tableMapping = mutableMapOf<String, Table>()
    fun table(block: TableBuilder.() -> Unit) {
        val table = TableBuilder().apply(block).build()
        tableMapping[table.tableName] = table
    }

    fun build(): Map<String, Table> {
        return tableMapping.toImmutableMap()
    }
}