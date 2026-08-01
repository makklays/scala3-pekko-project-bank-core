-- V3__create_transaction_saga_states_table.sql

-- Создание таблицы состояний Саги для компонента transfer
CREATE TABLE IF NOT EXISTS transaction_saga_states (
    saga_id UUID PRIMARY KEY,

    transaction_id UUID NOT NULL,
    current_step VARCHAR(30) NOT NULL,  -- STARTED, SENDER_DEBITED, COMPENSATING, FAILED, COMPLETED
    payload JSONB NOT NULL,             -- Сериализованные данные платежа для восстановления из памяти

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Связь с таблицей транзакций
    CONSTRAINT fk_saga_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
);

-- Индекс для быстрой выборки незавершенных саг при перезапуске ноды/сервера
CREATE INDEX IF NOT EXISTS idx_transaction_saga_incomplete ON transaction_saga_states(current_step)
WHERE current_step NOT IN ('COMPLETED', 'FAILED');

