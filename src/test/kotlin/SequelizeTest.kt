import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.sequelize.Sequelize
import org.sequelize.dsl.fetchResults
import org.sequelize.util.extractQueryMap
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

val PATH = System.getenv("QUERIES_PATH") ?: "/Users/shubhangb/kotlin.sequelize/src/test/resources/sql"

enum class QueryName(val queryName: String) {
    PRODUCT("product"),
    PRODUCT_WITH_ARG("productWithArg"),
    PRODUCT_IN_QUERY("productsInQuery")
}


class SequelizeTest {


    @Test
    fun testQueryMapGeneration() {
        val qm = extractQueryMap(PATH)
        Assert.assertTrue(qm.size > 1)
    }


    companion object {
        private lateinit var sequelize: Sequelize
        val keys = listOf<String>("product_code", "product_name")
        @BeforeClass
        @JvmStatic
        fun setUpDB() {
            val ds = JdbcDataSource()
            ds.password = ""
            ds.user = "sa"
            ds.url =
                "jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE"


            val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(ds)


            val products = arrayOf<Map<String, Any>>(
                mutableMapOf("product_name" to "SOAP", "product_code" to "P1234"),
                mutableMapOf("product_name" to "SHAMPOO", "product_code" to "P5678"),
                mutableMapOf("product_name" to "LIGHTER", "product_code" to "P7890"),
                mutableMapOf("product_name" to "KLEENX", "product_code" to "P9871"),
                mutableMapOf("product_name" to "CUP", "product_code" to "P9451"),
                mutableMapOf("product_name" to "BRUSH", "product_code" to "P1214"),
                mutableMapOf("product_name" to "TOOTH PASTE", "product_code" to "P8901")
            )

            val rows = products.map {
                it.values.toTypedArray()
            }.toMutableList()


            val placeholders = "?".repeat(rows.first().size).split("").filter { it != "" }.joinToString(",")
            val columnNames = products.first().keys.toList().joinToString(",")

            namedParameterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product(id int auto_increment,product_code varchar(255) primary key,product_name varchar(255));") { it.execute() }

            namedParameterJdbcTemplate.execute("TRUNCATE TABLE product;") { it.execute() }

            val insertStatement = "INSERT INTO product ($columnNames) VALUES($placeholders)".toString()

            namedParameterJdbcTemplate.jdbcTemplate.batchUpdate(insertStatement, rows)

            println("PATH -> $PATH")

            sequelize = Sequelize(ds, PATH)


        }
    }


    @Test
    fun testQueryWithParams() {
        val expectedResult: ArrayList<Map<String, Any>> =
            arrayListOf(mapOf("product_code" to "P1234", "product_name" to "SOAP"))

        val results: ArrayList<Map<String, Any>> = sequelize.fetchResults {
            queryName = QueryName.PRODUCT.queryName
            params = mapOf("productCode" to "P1234")
        }
        Assert.assertEquals(expectedResult, results.map { it.filterKeys { key -> keys.contains(key.toLowerCase()) } })
    }

    @Test
    fun testQueryWithParams2() {
        val expectedResult = mapOf<String, Any>(
            "product_code" to "P7890",
            "product_name" to "LIGHTER"
        )

        val result = sequelize.fetchResults {
            queryName = QueryName.PRODUCT_WITH_ARG.queryName
            params = mapOf("productCode" to "P7890")
        }[0]



        Assert.assertEquals(expectedResult, result.filterKeys { keys.contains(it.toLowerCase()) })

    }

    @Test
    fun testInQueryBehaviourWithListParam() {
        val result = sequelize.fetchResults {
            queryName = QueryName.PRODUCT_IN_QUERY.queryName
            params = mapOf("productCodes" to listOf("P1214", "P8901"))
        }

        val expectedResult = arrayListOf<Map<String, Any>>(
            mapOf("product_code" to "P1214", "product_name" to "BRUSH"),
            mapOf("product_code" to "P8901", "product_name" to "TOOTH PASTE")
        )

        Assert.assertEquals(expectedResult, result.map { row -> row.filterKeys { keys.contains(it.toLowerCase()) } })
    }


}