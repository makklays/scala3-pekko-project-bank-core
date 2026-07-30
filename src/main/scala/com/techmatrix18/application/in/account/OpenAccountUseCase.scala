package com.techmatrix18.application.in.account

import com.techmatrix18.application.ApplicationError

import scala.concurrent.Future
import com.techmatrix18.application.account.ApplicationError

/**
 * OpenAccountUseCase - Открытие счета
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
trait OpenAccountUseCase:
  // Открывает новый банковский счет
  def execute(command: OpenAccountCommand): Future[Either[ApplicationError, UUID]]

