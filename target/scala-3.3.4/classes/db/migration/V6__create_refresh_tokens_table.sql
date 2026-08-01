-- V6__create_refresh_tokens_table.sql

-- Таблица для долгоживущих refresh-токенов (Безопасность / Сессии)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,

    user_id UUID NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL, -- Сам токен (или его хэш)
    device_info VARCHAR(255),          -- Для безопасности (например: "Chrome on MacOS", "React App")
    ip_address VARCHAR(45),            -- IP адрес, с которого вошли

    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Связь с пользователем. Если пользователя удалят — его сессии сотрутся
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Индекс для мгновенной валидации токена при обновлении сессии
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_value ON refresh_tokens(token);

