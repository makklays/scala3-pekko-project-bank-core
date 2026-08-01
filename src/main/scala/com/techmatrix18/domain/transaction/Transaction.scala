package com.techmatrix18.domain.transaction

import java.util.UUID
import java.time.Instant
import com.techmatrix18.domain.account.Money

/**
 * Transaction - Доменная сущность проводки (движения денежных средств)
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */

// TransactionStatus - Строго типизированные статусы жизненного цикла транзакции
enum TransactionStatus:
  case Pending    // Платеж создан, ожидает проверки антифрода или списания
  case Completed  // Деньги успешно списаны у отправителя и зачислены получателю
  case Failed     // Ошибка (нет денег, счет заблокирован и т.д.)
  case Reversed   // Деньги были списаны, но возвращены назад (Rollback Саги)

// Transaction - Доменная сущность проводки (движения денежных средств)
case class Transaction (
  id: UUID,
  senderAccountId: UUID,
  recipientAccountId: UUID,
  money: Money,               // Объединяет в себе сумму (BigDecimal) и валюту (String)
  status: TransactionStatus,  // Защищено через Enum
  description: String,
  createdAt: Instant,
  updatedAt: Instant
):

  // Пример доменного метода: проверка, завершена ли транзакция окончательно
  def isFinalized: Boolean =
    status == TransactionStatus.Completed ||
      status == TransactionStatus.Failed ||
      status == TransactionStatus.Reversed

