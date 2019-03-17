package org.sequelize.util

import java.io.File

const val SQL_FILE_EXTENSION = ".sql"

private fun retrieveFileList(filePath: String): Array<File> {
    val dir = File(filePath)
    return dir.listFiles { _, name -> name.endsWith(SQL_FILE_EXTENSION) } ?: return arrayOf()
}

private fun retrieveFileContents(fileList: Array<File>): String {
    var fileContents = ""
    fileList.forEach {
        val fileContent = File(it.absolutePath).inputStream().readBytes().toString(Charsets.UTF_8)
        fileContents = fileContent + "\n" + fileContents
    }
    return fileContents
}


fun extractQueryMap(filePath: String): Map<String, String> {
    val fileList = retrieveFileList(filePath)
    val fileContents = retrieveFileContents(fileList)
    val queryNameStmt = fileContents.replace("""\s+""".toRegex(), " ").split("""-- name:\s+""".toRegex())
        .filter { it != "" }
        .map { it.trim().split(" ") }
    val queryNameStmtPairs = queryNameStmt.map {
        val queryName = it.first()
        val queryStatement = it.subList(1, it.size).joinToString(" ")
        Pair(queryName, queryStatement)
    }

    return mapOf<String, String>(*queryNameStmtPairs.toTypedArray())

}
