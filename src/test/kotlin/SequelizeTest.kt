import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.sequelize.Sequelize
import org.sequelize.dsl.fetchResults
import org.sequelize.util.extractQueryMap
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

const val PATH = "/Users/shubhangb/kotlin.sequelize/src/test/resources"

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
        val keys = listOf<String>("product_code", "product_code")
        @BeforeClass
        @JvmStatic
        fun setUpDB() {
            val ds = JdbcDataSource()
            ds.password = ""
            ds.user = "sa"
            ds.url = "jdbc:h2:~/kotlin.sequelize/src/test/resources/test;IGNORECASE=TRUE;MODE=MYSQL"


            val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(ds)


            val products = arrayOf<Map<String, Any>>(
                mutableMapOf("PRODUCT_NAME" to "SOAP", "PRODUCT_CODE" to "P1234"),
                mutableMapOf("PRODUCT_NAME" to "SHAMPOO", "PRODUCT_CODE" to "P5678"),
                mutableMapOf("PRODUCT_NAME" to "LIGHTER", "PRODUCT_CODE" to "P7890"),
                mutableMapOf("PRODUCT_NAME" to "KLEENX", "PRODUCT_CODE" to "P9871"),
                mutableMapOf("PRODUCT_NAME" to "CUP", "PRODUCT_CODE" to "P9451"),
                mutableMapOf("PRODUCT_NAME" to "BRUSH", "PRODUCT_CODE" to "P1214"),
                mutableMapOf("PRODUCT_NAME" to "TOOTH PASTE", "PRODUCT_CODE" to "P8901")
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

            sequelize = Sequelize(ds, PATH)


        }
    }


    @Test
    fun testQueryWithParams() {
        val expectedResult: ArrayList<Map<String, Any>> =
            arrayListOf(mapOf("PRODUCT_CODE" to "P1234", "PRODUCT_NAME" to "SOAP"))

        val results: ArrayList<Map<String, Any>> = sequelize.fetchResults {
            queryName = QueryName.PRODUCT.queryName
            params = mapOf("productCode" to "P1234")
        }
        Assert.assertEquals(expectedResult, results.map { it.filterKeys { key -> keys.contains(key) } })
    }

    @Test
    fun testQueryWithParams2() {
        val expectedResult = mapOf<String, Any>(
            "PRODUCT_CODE" to "P7890",
            "PRODUCT_NAME" to "LIGHTER"
        )

        val result = sequelize.fetchResults {
            queryName = QueryName.PRODUCT_WITH_ARG.queryName
            params = mapOf("productCode" to "P7890")
        }[0]



        Assert.assertEquals(expectedResult, result.filterKeys { keys.contains(it) })

    }

    @Test
    fun testInQueryBehaviourWithListParam() {
        val result = sequelize.fetchResults {
            queryName = QueryName.PRODUCT_IN_QUERY.queryName
            params = mapOf("productCodes" to listOf("P1214", "P8901"))
        }

        val expectedResult = arrayListOf<Map<String, Any>>(
            mapOf("PRODUCT_CODE" to "P1214", "PRODUCT_NAME" to "BRUSH"),
            mapOf("PRODUCT_CODE" to "P8901", "PRODUCT_NAME" to "TOOTH PASTE")
        )

        Assert.assertEquals(expectedResult, result.map { it.filterKeys { keys.contains(it) } })
    }


}