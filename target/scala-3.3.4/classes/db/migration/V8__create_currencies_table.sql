-- V8__create_currencies_table.sql

-- Создание справочника валют ISO 4217 и таблицы курсов обмена
CREATE TABLE IF NOT EXISTS currencies (
    code VARCHAR(7) PRIMARY KEY,               -- Буквенный код по ISO 4217 (например: EUR, USD, USDT)

    numeric_code INT UNIQUE NOT NULL,          -- Официальный цифровой код (например: 978, 840)
    name VARCHAR(50) NOT NULL,                 -- Полное название (Euro, US Dollar)
    symbol VARCHAR(5) NOT NULL,                -- Символ валюты (€, $, £)
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Наполняем справочник базовыми валютами с реальными кодами ISO 4217
INSERT INTO currencies (code, numeric_code, name, symbol) VALUES
('EUR', 978, 'Euro', '€'),
('USD', 840, 'US Dollar', '$'),
('GBP', 826, 'British Pound', '£'),
('PLN', 985, 'Polish Zloty', 'zł')
ON CONFLICT (code) DO NOTHING;

