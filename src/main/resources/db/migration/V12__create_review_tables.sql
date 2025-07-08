-- Review
CREATE TABLE reviews (
        id BIGSERIAL PRIMARY KEY,
        rating DOUBLE PRECISION,
        comment TEXT NOT NULL,
        image_urls JSONB,

        created_at DATE,
        updated_at DATE,

        user_id BIGINT NOT NULL,
        product_id BIGINT NOT NULL,

        CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id),
        CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Index
CREATE INDEX idx_review_user_id ON reviews (user_id);
CREATE INDEX idx_review_product_id ON reviews (product_id);