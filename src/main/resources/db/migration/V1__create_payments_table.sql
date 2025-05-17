CREATE TABLE payments (
          id BIGSERIAL PRIMARY KEY,
          external_id VARCHAR(50) UNIQUE NOT NULL,
          amount DECIMAL(19,2) NOT NULL,
          credit_card_number VARCHAR(19) NOT NULL,
          order_id VARCHAR(50) NOT NULL,
          callback_url VARCHAR(255) NOT NULL,
          status VARCHAR(20) NOT NULL,
          message TEXT,
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_payments_external_id ON payments(external_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);