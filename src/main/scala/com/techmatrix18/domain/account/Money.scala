package com.techmatrix18.domain.account

import java.math.RoundingMode

/**
 * Money actor
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 28.07.2026
 */

// 1. Строгий тип для Валюты с использованием Opaque Types в Scala 3
opaque type Currency <: String = String
object Currency:
  val EUR: Currency = "EUR"

  // Метод безопасного создания валюты (пока работаем только с EUR для рынка Испании)
  def fromString(value: String): Either[String, Currency] =
    if (value.toUpperCase == "EUR") Right(value.toUpperCase)
    else Left(s"Unsupported currency: $value. Only EUR is allowed for Bizum/Revolut-ES operations.")

// 2. Объект-значение (Value Object) для представления денег
case class Money private (amount: BigDecimal, currency: Currency):

  // Бизнес-логика: Сложение денег с проверкой валюты
  def +(that: Money): Either[String, Money] =
    if (this.currency != that.currency)
      Left(s"Cannot add different currencies: ${this.currency} and ${that.currency}")
    else
      Right(Money(this.amount + that.amount, this.currency))

  // Бизнес-логика: Вычитание денег с проверкой валюты
  def -(that: Money): Either[String, Money] =
    if (this.currency != that.currency)
      Left(s"Cannot subtract different currencies: ${this.currency} and ${that.currency}")
    else
      Right(Money(this.amount - that.amount, this.currency))

  // Проверки для бизнес-правил банка
  def isLessThan(that: Money): Boolean = this.amount < that.amount
  def isNegative: Boolean = this.amount < 0
  def isZero: Boolean = this.amount == 0

object Money:
  // Фабричный метод, гарантирующий банковское округление (2 знака после запятой)
  def apply(amount: BigDecimal, currency: Currency): Money =
    val scaledAmount = amount.setScale(2, RoundingMode.HALF_EVEN)
    new Money(scaledAmount, currency)

  // Удобный хелпер для быстрого создания сумм в Евро
  def euro(amount: BigDecimal): Money = Money(amount, Currency.EUR)

