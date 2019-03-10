#kotlin.sequelize
A Library that lets you run SQL queries directly from .sql files

## Motivation
* SQL in itself is an extremely matured DSL and providing wrappers around SQL to query data can sometimes be a cumbersome process.The real beauty of sql lies in joins which feels very fluent when written as a query.
* Most of the common REST API(s) return a stringified list of map, which in my SQL can be viewed as a bunch records.
* Testability, your queries can be writing in .sql files with full syntax highlighting support and can be tested on live database instances.
* Many modern IDE(s) come with a database extension, which allow you to directly run queries from your IDE.

## What this library does not offer
* Type checking of your result set, everything returned by this library is a list of maps.
* Would not recommend this library to perform transactional Database operations.
* This library is mostly meant to read data from your database with full SQL support, any sort of write operations might not work as expected.



## How to Use
* Formatting your .sql files.
* Please add a name comment to all your queries, the name is what we will refer our query with in our kotlin code.
```sql
-- name: product
SELECT * FROM product where product_code=:productCode;
-- name: bestRatedProducts
SELECT * FROM product where product_rating = 5000
```

```kotlin
    fun main(){
        val dataSource = MySqlDataSource()
        val sequelize = Sequelize(dataSource,"/path/to/where/all/your/.sql/query/folder")
        /*Use the query DSL to execute the query and retrieve results*/
        val bestRatedProducts = sequelize.query{
            name="bestRatedProducts"
        }
        
        val product = sequelize.query{
            name="product"
            params{
                "productCode" to "P5678"
            }
        }
    }
    
```
## How to run the tests
* Please clone the repository and replace the paths.
* Run the tests using ./gradlew test command.





