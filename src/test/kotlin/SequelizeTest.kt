import org.h2.jdbcx.JdbcDataSource
import org.junit.BeforeClass
import org.junit.Test
import org.sequelize.Sequelize
import org.sequelize.entity.Entity
import org.sequelize.entity.Table
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

const val PATH = "/Users/shubhangb/kotlin.sequelize/src/test/resources"

enum class QueryName(val queryName: String) {
    PRODUCT("product"),
    PRODUCT_WITH_ARG("productWithArg"),
    PRODUCT_IN_QUERY("productsInQuery")
}


class SequelizeTest {

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
            ds.url = "jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_UPPER=FALSE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1"


            val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(ds)
            entityRelationMapping = org.sequelize.entity.entityRelationMapping {
                table { name = "product";primaryKey = "id" }
            }
            entity = Entity(dataSource = ds, entityRelationMapping = entityRelationMapping)

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

            namedParameterJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS product(id int auto_increment primary key,product_code varchar(255),product_name varchar(255));") { it.execute() }

            namedParameterJdbcTemplate.execute("TRUNCATE TABLE product;") { it.execute() }

            val insertStatement = "INSERT INTO product ($columnNames) VALUES($placeholders)".toString()

            namedParameterJdbcTemplate.jdbcTemplate.batchUpdate(insertStatement, rows)

            sequelize = Sequelize(ds, PATH)

        }
    }


    @Test
    fun testSingleRecordSaving() {
        val row = mapOf<String, Any>("PRODUCT_CODE" to "P45678", "PRODUCT_NAME" to "OIL")
        entity.saveOrUpdateOne("product", row)
    }

}