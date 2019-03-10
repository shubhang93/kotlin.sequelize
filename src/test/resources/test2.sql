-- name: product
select product_code, id
from product
limit 1;

-- name: productWithArg
select *
from product
where product_code = :productCode