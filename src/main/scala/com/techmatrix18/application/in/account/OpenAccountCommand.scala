package com.techmatrix18.application.in.account

/**
 * OpenAccountCommand
 *
 * Данные, необходимые бизнесу для открытия счета (из REST API)
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
case class OpenAccountCommand(
  phoneNumber: String,
  currency: String
)

