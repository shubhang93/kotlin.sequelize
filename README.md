# kotlin.sequelize 
A Library that lets you run SQL queries directly from .sql files

[![Build Status](https://travis-ci.com/shubhang93/kotlin.sequelize.svg?branch=master)](https://travis-ci.com/shubhang93/kotlin.sequelize)

## Installation
 Maven
 ```
 <dependency>
  <groupId>org.sequelize</groupId>
  <artifactId>sequelize</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>

 ```
 Gradle
 ```
 repositories {
    jcenter()
    maven {
        url "https://dl.bintray.com/shubhang93/kotlin"
    }
}
 
 compile 'org.sequelize:sequelize:1.0.0'
 
 ```
 


## Motivation
* Heavily Inspired by yesql, a Clojure library :heart: :heart: .
* SQL in itself is an extremely matured DSL and providing wrappers around SQL to query data can sometimes be a cumbersome process. The real beauty of sql lies in joins which feels very fluent when written as SQL queries.
* Most of the common REST API(s) return a stringified list of maps, which in my SQL can be viewed as a bunch records.
* Testability, your queries can be written in .sql files with full syntax highlighting support and can be tested on live database instances without having to run your complete backend system.
* Many modern IDE(s) come with a database extension, which allow you to directly run queries from your IDE(s).

## What this library does not offer
* Type checking of your result set, everything returned by this library is a list of maps.
* There are no DAOs, Domain classes, results returned by this library are plain list of maps.

## Dependencies
* This library uses Spring NamedJDBCTemplate to pass named arguments, this will enable us to add more features in future.
* We plan to add a very thin layer of abstraction to save data.
* We also want to add some Async API(s).



## How to Use
* Formatting your .sql files.
* Please add a name comment to all your queries, the name is what we will refer our query with in our kotlin code.
```sql
-- name: product
SELECT * FROM product where product_code=:productCode;
-- name: bestRatedProducts
SELECT * FROM product where product_rating = 5000;
-- name: productInJune
SELECT * FROM product where product_purchase_month = :monthNum
-- name: productInMayOrApril
SELECT * FROM product where product_purchase_month =:monthNums

```

```kotlin
    fun main(){
        val dataSource = MySqlDataSource()
        val sqz = Sequelize(dataSource,"/path/to/where/all/your/.sql/query/folder")
        /*Use the fetch API to execute the query and retrieve results*/
        //Returns single row
        val product1 = sqz.fetchOne{queryName="product";params = mapOf("productCode" to ""AXCN9008")}
        /*OR IF YOU PREFER TO CALL IT LIKE A FUNCTION*/
        val product2 = sqz.fetchOne(queryName="product", params = mapOf("productCode" to "CNN90877"))
        /*Return Multiple rows*/
        val productsInJune = sqz.fetch(queryName="productsBeforeJune", params=mapOf("monthNum" to 6))
        /*Pass List of args to in-queries*/
        val productsInMayOrApril = sqz.fetch(queryName="productsBeforeJune", params=mapOf("monthNums" to listOf(4,5)))
     
    }
    
```
## How to run the tests
* Please clone the repository and replace the paths.
* Run the tests using ./gradlew test command.

## PRs are welcome





