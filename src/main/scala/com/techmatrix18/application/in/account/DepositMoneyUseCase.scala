package com.techmatrix18.application.in.account

import com.techmatrix18.application.ApplicationError

import scala.concurrent.Future
import com.techmatrix18.application.account.ApplicationError

/**
 * DepositMoneyUseCase - Зачисление денег
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
trait DepositMoneyUseCase:
  def execute(command: DepositMoneyCommand): Future[Either[ApplicationError, Unit]]

