-- V4__create_fraud_logs_table.sql

-- Создание таблицы логов безопасности для компонента fraud
CREATE TABLE IF NOT EXISTS fraud_logs (
    id BIGSERIAL PRIMARY KEY,

    account_id UUID NOT NULL,
    transaction_id UUID,           -- Может быть NULL, если заблокирован сам аккаунт до транзакции
    reason VARCHAR(100) NOT NULL,  -- VELOCITY_LIMIT_EXCEEDED, SUSPICIOUS_AMOUNT, BLACKLISTED_PHONE

    risk_score INT NOT NULL,       -- Оценка риска от 0 до 100
    triggered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Внешние ключи
    CONSTRAINT fk_fraud_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT
);

-- Индекс для быстрого поиска истории фрод-активности по конкретному аккаунту
CREATE INDEX IF NOT EXISTS idx_fraud_logs_account ON fraud_logs(account_id);

