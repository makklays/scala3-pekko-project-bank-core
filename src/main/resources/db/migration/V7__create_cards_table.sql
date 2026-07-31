-- V7__create_cards_table.sql

-- Создание таблицы банковских карт
CREATE TABLE IF NOT EXISTS cards (
    id UUID PRIMARY KEY,

    account_id UUID NOT NULL,                   -- К какому счету привязана карта
    card_number VARCHAR(19) UNIQUE NOT NULL,    -- Номер карты (16-19 знаков), который будем проверять по Луну
    cardholder_name VARCHAR(100) NOT NULL,
    expiration_date VARCHAR(5) NOT NULL,        -- Формат MM/YY
    cvv_encrypted VARCHAR(255) NOT NULL,        -- CVV код в зашифрованном виде
    status VARCHAR(20) NOT NULL,                -- ACTIVE, BLOCKED, EXPIRED
    is_virtual BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Индекс для авторизации транзакций по номеру карты (когда клиент платит в терминале)
CREATE INDEX IF NOT EXISTS idx_cards_number ON cards(card_number);

