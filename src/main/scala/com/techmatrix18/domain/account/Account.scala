package com.techmatrix18.domain.account

import java.util.UUID
import java.time.Instant

/**
 * Account -
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 29.07.2026
 */
case class Account(
  id: UUID,
  phoneNumber: String,
  balance: Money,
  status: AccountStatus,
  createdAt: Instant,
  updatedAt: Instant
):

  // Бизнес-метод: Зачисление денег
  def deposit(amount: Money, now: Instant): Either[String, Account] =
    if (status == AccountStatus.Frozen)
      Left("Error: Account is frozen")
    else
      (this.balance + amount).map { newBalance =>
        this.copy(balance = newBalance, updatedAt = now)
      }

  // Бизнес-метод: Списание денег
  def withdraw(amount: Money, now: Instant): Either[String, Account] =
    if (status == AccountStatus.Frozen)
      Left("Error: Account is frozen")
    else
      (this.balance - amount).map { newBalance =>
        this.copy(balance = newBalance, updatedAt = now)
      }

  // Бизнес-метод: Заморозка счета (например, при подозрении на фрод)
  def frozen(now: Instant): Account =
    this.copy(status = AccountStatus.Frozen, updatedAt = now)


// Фабрика сущности (Companion Object)
object Account:

  // Создание нового аккаунта (бизнес-правила при старте)
  def create(
    phoneNumber: String,
    currency: String,
    now: Instant
  ): Either[String, Account] =
    // Простая валидация номера телефона (например, длина)
    if (phoneNumber.isBlank || phoneNumber.length < 9)
      Left("Error: Invalid phone number")
    else
      // Инициализируем аккаунт с нулевым балансом
      Money(BigDecimal(0.00), currency).map { initBalance =>
        Account(
          id = AccountId.generate(),
          phoneNumber = phoneNumber,
          balance = initBalance,
          status = AccountStatus.Active,
          createdAt = now,
          updatedAt = now
        )
      }

