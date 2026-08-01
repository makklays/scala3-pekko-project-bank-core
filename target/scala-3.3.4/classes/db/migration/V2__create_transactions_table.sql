-- V2__create_transactions_table.sql

-- Создание таблицы транзакций (движение денежных средств)
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,

    sender_account_id UUID NOT NULL,
    recipient_account_id UUID NOT NULL,

    amount NUMERIC(18, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, COMPLETED, FAILED, REVERSED
    description VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Внешние ключи для обеспечения целостности данных на уровне БД
    CONSTRAINT fk_sender_account FOREIGN KEY (sender_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT fk_recipient_account FOREIGN KEY (recipient_account_id) REFERENCES accounts(id) ON DELETE RESTRICT
);

-- Индексы для быстрой фильтрации истории транзакций по конкретному счету
CREATE INDEX IF NOT EXISTS idx_transactions_sender_account ON transactions(sender_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_recipient_account ON transactions(recipient_account_id);

