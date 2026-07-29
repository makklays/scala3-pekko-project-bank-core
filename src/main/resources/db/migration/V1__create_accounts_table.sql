-- V1__create_accounts_table.sql

-- Создание таблицы аккаунтов
CREATE TABLE IF NOT EXISTS accounts (
    -- UUID в качестве первичного ключа идеально подходит для распределенных реактивных систем
    id UUID PRIMARY KEY,

    -- Номер телефона (формат Bizum / Испания обычно +34... или просто 9 цифр, храним с запасом в VARCHAR)
    phone_number VARCHAR(20) NOT NULL UNIQUE,

    -- Баланс: ВСЕГДА NUMERIC/DECIMAL для денег. Никаких REAL/FLOAT во избежание ошибок округления!
    -- 15 знаков до запятой, 2 знака после (хватит для миллиардных сумм)
    balance NUMERIC(17, 2) NOT NULL DEFAULT 0.00,

    -- Валюта: ISO-4217 код (например, 'EUR', 'USD', USDT, etc). Ограничение в 8 символов так как крипта длиннее.
    currency VARCHAR(8) NOT NULL DEFAULT 'EUR',

    -- Статус аккаунта (будет мапиться на Enum в Scala: ACTIVE, FROZEN)
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    -- Временные метки для аудита и синхронизации
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(), -- время передает Scala
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()  -- время передает Scala
);

-- Индекс для быстрого поиска аккаунта по номеру телефона (критично для мгновенных P2P переводов)
CREATE INDEX IF NOT EXISTS idx_accounts_phone_number ON accounts(phone_number);

