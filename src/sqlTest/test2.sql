-- name: distributor
select *
from distributor;


-- name: product
select product_code, id
from product
where product_code = :productCode;