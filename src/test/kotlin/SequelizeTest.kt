import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import sequelize.Sequelize
import sequelize.extractQueryMap
import sequelize.params
import sequelize.query

class SequelizeTest {
    private lateinit var sequelize: Sequelize


    @Test
    fun itShouldReturnQueryMap() {
        val qm = extractQueryMap("/Users/shubhangb/kotlin.sequelize/src/sqlTest")
        Assert.assertTrue(qm.size > 1)
    }

    @Before
    fun setupDB() {
        val ds = MysqlDataSource()
        ds.setPassword("password")
        ds.user = "root"
        ds.setUrl("jdbc:mysql://localhost:3306/unza_mt")

        sequelize = Sequelize(ds, "/Users/shubhangb/kotlin.sequelize/src/sqlTest")

    }


    @Test
    fun itShouldReturnAnArrayListOfMaps() {
        val expectedResult: ArrayList<Map<String, Any>> =
            arrayListOf(mapOf("retailer_code" to "1012", "id" to 11.toLong()))

        val results: ArrayList<Map<String, Any>> = sequelize.query {
            name = "retailer"
        }
        Assert.assertEquals(expectedResult, results)
    }

    @Test
    fun itShouldReturnAProductWithProductCode1412097() {
        val expectedResult = mapOf<String, Any>(
            "product_code" to "1412097",
            "id" to 1.toLong()
        )

        val result = sequelize.query {
            name = "product"
            params { "productCode" to "1412097" }
        }[0]

        Assert.assertEquals(expectedResult, result)

    }
}