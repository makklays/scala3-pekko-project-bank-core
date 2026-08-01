package com.techmatrix18.application.in.transaction

import java.util.UUID

/**
 * CreateTransactionCommand
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */

// Команда, которая приходит из HTTP контроллера
case class CreateTransactionCommand(
  senderAccountId: UUID,
  recipientCardOrNumber: String,
  amount: BigDecimal,
  currency: String,
  description: Option[String]
)

