package org.sequelize.entity


fun entityRelationMapping(block: EntityRelationMapping.() -> Unit): Map<String, Table> {
    return EntityRelationMapping().apply(block).build()
}






