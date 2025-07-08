-- Category
CREATE TABLE categories (
          id BIGSERIAL PRIMARY KEY,
          name VARCHAR(255) NOT NULL UNIQUE,
          description TEXT,
          status VARCHAR(50) NOT NULL,
          created_by VARCHAR(255),
          updated_by VARCHAR(255),
          created_at DATE,
          updated_at DATE
);
