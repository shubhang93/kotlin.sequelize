import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sequelize.Sequelize
import org.sequelize.dsl.params
import org.sequelize.dsl.query
import org.sequelize.extractQueryMap
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class SequelizeTest {
    private lateinit var sequelize: Sequelize


    @Test
    fun itShouldReturnQueryMap() {
        val qm = extractQueryMap("/Users/shubhangb/kotlin.sequelize/src/test/resources")
        Assert.assertTrue(qm.size > 1)
    }

    @Before
    fun setupDB() {
        val ds = JdbcDataSource()
        ds.password = ""
        ds.user = "sa"
        ds.url = "jdbc:h2:~/kotlin.sequelize/src/test/resources/test"

        val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(ds)


        val products = arrayOf<Map<String, Any>>(
            mutableMapOf("id" to 1, "product_code" to "P1234"),
            mutableMapOf("id" to 2, "product_code" to "P5678"),
            mutableMapOf("id" to 4, "product_code" to "P7890")
        )

        val rows = products.map {
            it.values.toTypedArray()
        }.toMutableList()


        val placeholders = "?".repeat(rows.first().size).split("").filter { it != "" }.joinToString(",")
        val columnNames = products.first().keys.toList().joinToString(",")

        namedParameterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product(id int,product_code varchar(255));") { it.execute() }

        namedParameterJdbcTemplate.execute("TRUNCATE TABLE product;") { it.execute() }

        val insertStatement = "INSERT INTO product ($columnNames) VALUES($placeholders)".toString()


        namedParameterJdbcTemplate.jdbcTemplate.batchUpdate(insertStatement, rows)


        sequelize = Sequelize(ds, "/Users/shubhangb/kotlin.sequelize/src/test/resources")

    }


    @Test
    fun itShouldReturnAProductWithCodeP1234() {
        val expectedResult: ArrayList<Map<String, Any>> =
            arrayListOf(mapOf("PRODUCT_CODE" to "P1234", "ID" to 1))

        val results: ArrayList<Map<String, Any>> = sequelize.query {
            name = "product"
        }
        Assert.assertEquals(expectedResult, results)
    }

    @Test
    fun itShouldReturnAProductWithProductCodeP7890() {
        val expectedResult = mapOf<String, Any>(
            "PRODUCT_CODE" to "P7890",
            "ID" to 4
        )

        val result = sequelize.query {
            name = "productWithArg"
            params { "productCode" to "P7890" }
        }[0]

        Assert.assertEquals(expectedResult, result)

    }
}