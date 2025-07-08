-- File
CREATE TABLE files (
         id BIGSERIAL PRIMARY KEY,
         public_id VARCHAR(255),
         file_name VARCHAR(255),
         image_url VARCHAR(1000),
         file_type VARCHAR(20),

        created_by VARCHAR(255),
         created_at DATE
);
