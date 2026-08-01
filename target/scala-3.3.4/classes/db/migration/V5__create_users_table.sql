-- V5__create_users_table.sql

-- Создание таблицы пользователей (клиентов банка)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,                -- ACTIVE, BLOCKED, PENDING_VERIFICATION

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Теперь свяжем наши существующие аккаунты с пользователями.
-- Для этого добавим колонку user_id в таблицу accounts (мягкая миграция)
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS user_id UUID;

-- Добавляем внешний ключ (если в базе уже есть тестовые данные, FK лучше делать nullable или с дефолтом)
ALTER TABLE accounts ADD CONSTRAINT fk_account_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

-- Индекс для быстрого поиска всех счетов конкретного пользователя
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);

