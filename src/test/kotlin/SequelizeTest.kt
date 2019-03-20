import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.sequelize.Sequelize
import org.sequelize.dsl.fetchResults
import org.sequelize.entity.Entity
import org.sequelize.entity.Table
import org.sequelize.util.extractQueryMap
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

const val PATH = "/Users/shubhangb/kotlin.sequelize/src/test/resources"
const val CONN_STRING = "jdbc:h2:mem:test_db;IGNORECASE=TRUE;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE"

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
        private lateinit var entity: Entity
        private lateinit var entityRelationMapping: Map<String, Table>
        val keys = listOf<String>("PRODUCT_CODE", "PRODUCT_NAME")
        @BeforeClass
        @JvmStatic
        fun setUpDB() {
            val ds = JdbcDataSource()
            ds.password = ""
            ds.user = "sa"
            ds.url = CONN_STRING


            val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(ds)
            entity = Entity(dataSource = ds, entityRelationMapping = entityRelationMapping)

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

            namedParameterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product(id int auto_increment primary key,product_code varchar(255),product_name varchar(255));") { it.execute() }

            namedParameterJdbcTemplate.execute("TRUNCATE TABLE product;") { it.execute() }

            val insertStatement = "INSERT INTO product ($columnNames) VALUES($placeholders)".toString()

            namedParameterJdbcTemplate.jdbcTemplate.batchUpdate(insertStatement, rows)

            sequelize = Sequelize(ds, PATH)


        }
    }

    @Test
    fun testEntityRelationMappingDSL() {

        val result = entityRelationMapping["product"]
        val expectedResult =
            Table(
                "product",
                "id",
                null, null, null, null
            )

        Assert.assertEquals(expectedResult, result)

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


    private fun insertNewRecordsUsingEntityClass() {
        val data = listOf<Map<String, Any>>(
            mapOf("PRODUCT_CODE" to "P9099", "PRODUCT_NAME" to "SCRUBBER"),
            mapOf("PRODUCT_CODE" to "P8078", "PRODUCT_NAME" to "LIQUIFIED GAS")
        )
        val result = entity.saveBatch("product", data)
    }

    @Test
    fun testIfNewRecordsAreSavedUsingEntityClass() {
        insertNewRecordsUsingEntityClass()
        val expectedResult = listOf<Map<String, Any>>(
            mapOf("PRODUCT_CODE" to "P9099", "PRODUCT_NAME" to "SCRUBBER"),
            mapOf("PRODUCT_CODE" to "P8078", "PRODUCT_NAME" to "LIQUIFIED GAS")
        )
        val result = sequelize.fetchResults {
            queryName = "productsInQuery"
            params = mapOf("productCodes" to listOf("P8078", "P9099"))
        }

        Assert.assertEquals(expectedResult, result.map { it.filterKeys { keys.contains(it) } })
    }

    @Test
    fun testTransactionalBehaviourOfSaveBatch() {
        val data = listOf<Map<String, Any>>(
            mapOf("PRODUCT_CODE" to "P4532", "PRODUCT_NAME" to "BREAD"),
            mapOf("PRODUCT_CODE" to "5691", "PRODUCT_NAE" to "KNIFE")
        )

        val rowCount = entity.saveBatch("product", data)
        val results = sequelize.fetchResults {
            queryName = QueryName.PRODUCT_WITH_ARG.queryName
            params = mapOf("productCode" to "P4532")
        }

        val expectedResult = arrayListOf<Map<String, Any>>()
        Assert.assertEquals(expectedResult, results)
    }

    @Test
    fun testSingleRecordSaving() {
        val row = mapOf<String, Any>("PRODUCT_CODE" to "P45678", "PRODUCT_NAME" to "OIL")
        entity.saveOrUpdateOne("product", row)
    }

}