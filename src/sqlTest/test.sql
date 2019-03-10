-- name:        retailer
select retailer_code, id
from retailer
where retailer_code = '1012';


-- name: stockInHand


select *
from party_product_level_stock;