-- name: testSingleRowFetch
select product_code, id, product_name
from product
limit 1;

-- name: testWithArgs
select *
from product
where product_code = :productCode;

-- name: simpleTest
select *
from product
