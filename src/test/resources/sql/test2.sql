-- name: product
select product_code, id, product_name
from product
limit 1;

-- name: productWithArg
select *
from product
where product_code = :productCode;

-- name: allProducts
select *
from product
