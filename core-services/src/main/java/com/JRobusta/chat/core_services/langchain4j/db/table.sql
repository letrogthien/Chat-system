
-- =======================================================
-- KNOWLEDGE BASE: GAMES
-- =======================================================
CREATE TABLE IF NOT EXISTS games (
    game_id VARCHAR(36) PRIMARY KEY,
    
    -- Thông tin cơ bản
    title VARCHAR(255) NOT NULL,
    price DECIMAL(15, 2) DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'VND',
    
    -- Thông tin phát hành
    developer VARCHAR(255),
    publisher VARCHAR(255),
    release_date DATE,
    
    -- Phân loại và nội dung
    genre_tags TEXT,
    review_summary VARCHAR(255),
    
    -- Dữ liệu cốt lõi cho AI (RAG)
    description TEXT,                -- AI sẽ đọc trường này để lấy ngữ cảnh
    system_requirements TEXT,        -- AI đọc cấu hình để tư vấn
    
    -- Metadata
    image_url VARCHAR(2083),
    source_url VARCHAR(2083),
    
    -- Trạng thái đồng bộ Vector
    is_vectorized TINYINT(1) DEFAULT 0, 
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Index hỗ trợ tìm kiếm và worker
CREATE INDEX idx_games_title ON games (title);
CREATE INDEX idx_games_is_vectorized ON games (is_vectorized);

