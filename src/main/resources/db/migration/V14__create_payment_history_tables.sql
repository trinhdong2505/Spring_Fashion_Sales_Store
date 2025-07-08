-- Payment History
CREATE TABLE payment_history (
             id BIGSERIAL PRIMARY KEY,

             is_primary BOOLEAN,

             payment_method VARCHAR(50) NOT NULL,
             total_amount DOUBLE PRECISION NOT NULL,
             payment_status VARCHAR(50) NOT NULL,

             payment_time TIMESTAMP,

             order_id BIGINT,
             CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Index
CREATE INDEX idx_payment_history_order_id ON payment_history (order_id);