-- name: testInQueryWithListArgs
select *
from product
where product_code in (:productCodes);


