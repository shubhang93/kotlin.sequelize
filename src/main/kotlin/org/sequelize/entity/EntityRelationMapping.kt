package org.sequelize.entity

data class Table(
    val tableName: String,
    val primaryKey: String,
    val childEntity: String?,
    val foreignKey: String?,
    val compoundPrimaryKeys: List<String>,
    val nullableColumns: List<String>
)


@DslMarker
annotation class EntityRelationMappingDSL


fun Map<String, Table>.toImmutableMap(): Map<String, Table> {
    val entries = this.entries.toList()
    val pairs = entries.map { Pair(it.key, it.value) }
    return mapOf(*pairs.toTypedArray())
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


fun entityRelationMapping(block: EntityRelationMapping.() -> Unit): Map<String, Table> {
    return EntityRelationMapping().apply(block).build()
}


@EntityRelationMappingDSL
class TableBuilder {
    var tableName = ""
    var primaryKey = ""
    var foreignKey: String? = null
    var childEntity: String? = null
    var compoundPrimaryKeys = listOf<String>()
    var nullableColumns: List<String> = listOf()

    fun build(): Table {
        return Table(tableName, primaryKey, foreignKey, childEntity, compoundPrimaryKeys, nullableColumns)
    }
}



