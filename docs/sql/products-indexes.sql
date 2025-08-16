-- products-indexes.sql
-- Purpose: Indexes for brand-filtered sorts (likes_desc, price_asc, latest)
-- Engine: MySQL 8.0+

-- Create Indexes
CREATE INDEX idx_products_brand_status_like
  ON products(brand_id, status, like_count DESC, id);

CREATE INDEX idx_products_like_desc
  ON products (like_count DESC, id);

-- Drop Indexes (rollback)
DROP INDEX idx_products_brand_status_like ON products;
DROP INDEX idx_products_like_desc ON products;

SHOW INDEX FROM products;
