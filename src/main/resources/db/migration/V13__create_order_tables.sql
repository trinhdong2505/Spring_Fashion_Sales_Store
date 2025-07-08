-- Order
CREATE TABLE orders (
            id BIGSERIAL PRIMARY KEY,

            order_date DATE NOT NULL,
            total_price DOUBLE PRECISION NOT NULL,
            order_status VARCHAR(50) NOT NULL,

            updated_at DATE,

            address_id BIGINT,
            user_id BIGINT,

            CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
            CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- Index
CREATE INDEX idx_order_user_id ON orders (user_id);


-- Order Item
CREATE TABLE order_items (
            id BIGSERIAL PRIMARY KEY,

            quantity INTEGER,
            unit_price DOUBLE PRECISION,

            product_variant_id BIGINT NOT NULL,
            order_id BIGINT,

            CONSTRAINT fk_order_item_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants(id),
            CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Index
CREATE INDEX idx_order_item_order_id ON order_items (order_id);