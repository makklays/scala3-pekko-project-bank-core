-- V9__create_exchange_rates_table.sql

-- Создаем таблицу курсов обмена с жесткой привязкой к справочнику currencies
CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGSERIAL PRIMARY KEY,

    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate NUMERIC(10, 6) NOT NULL,       -- Курс обмена (например, 1.085000)

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Ограничения целостности
    CONSTRAINT fk_rate_from_currency FOREIGN KEY (from_currency) REFERENCES currencies(code) ON DELETE RESTRICT,
    CONSTRAINT fk_rate_to_currency FOREIGN KEY (to_currency) REFERENCES currencies(code) ON DELETE RESTRICT,

    -- Уникальный индекс, чтобы для одной пары валют была только одна актуальная строчка
    CONSTRAINT uq_currency_pair UNIQUE (from_currency, to_currency)
);

