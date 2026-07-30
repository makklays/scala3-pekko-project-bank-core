package com.techmatrix18.application.in.account

import java.util.UUID

/**
 * WithdrawMoneyCommand - Списание денег
 *
 * Данные, необходимые для выполнения списания со счета
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
case class WithdrawMoneyCommand(
  accountId: UUID,
  amount: BigDecimal,
  currency: String
)

