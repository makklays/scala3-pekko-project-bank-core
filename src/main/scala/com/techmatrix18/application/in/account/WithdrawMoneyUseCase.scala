package com.techmatrix18.application.in.account

import com.techmatrix18.application.ApplicationError

import scala.concurrent.Future
import com.techmatrix18.application.account.ApplicationError

/**
 * WithdrawMoneyUseCase - Списание денег
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
trait WithdrawMoneyUseCase:
  def execute(command: WithdrawMoneyCommand): Future[Either[ApplicationError, Unit]]

