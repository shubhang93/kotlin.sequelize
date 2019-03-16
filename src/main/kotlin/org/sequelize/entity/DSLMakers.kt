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
    val compoundPrimaryKeys: List<String>,
    val nullableColumns: List<String>
)

@EntityRelationMappingDSL
class TableBuilder {
    private var tname = ""
    private var pk = ""
    private var fk: String? = null
    private var childTable: String? = null
    private var compoundKeys = listOf<String>()
    private var nullColumns: List<String> = listOf()


    infix fun tableName(name: String) {
        tname = name
    }

    infix fun foriegnKey(foriegnKey: String) {
        fk = foriegnKey
    }

    infix fun primaryKey(primaryKey: String) {
        pk = primaryKey
    }

    infix fun childEntity(childEntity: String?) {
        childTable = childEntity
    }

    infix fun compoundPrimaryKeys(keys: List<String>) {
        compoundKeys = keys
    }

    infix fun nullableColumns(columns: List<String>) {
        nullColumns = columns
    }


    fun build(): Table {
        return Table(tname, pk, fk, childTable, compoundKeys, nullColumns)
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