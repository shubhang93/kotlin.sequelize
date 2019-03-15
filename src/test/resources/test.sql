-- name: productsInQuery
select *
from product
where PRODUCT_CODE in (:productCodes);

