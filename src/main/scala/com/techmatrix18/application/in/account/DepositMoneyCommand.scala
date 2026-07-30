package com.techmatrix18.application.in.account

import java.util.UUID

/**
 * DepositMoneyCommand
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
case class DepositMoneyCommand(
  accountId: UUID,
  amount: BigDecimal,
  currency: String
)

