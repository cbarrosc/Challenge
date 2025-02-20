CREATE TABLE request_history (
                                 id SERIAL PRIMARY KEY,
                                 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 endpoint VARCHAR(255),
                                 parameters TEXT,
                                 response TEXT,
                                 error TEXT,
                                 success BOOLEAN
);