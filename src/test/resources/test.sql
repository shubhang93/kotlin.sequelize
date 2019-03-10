-- name: productsInQuery
select *
from product
where product_code in (:productCodes);