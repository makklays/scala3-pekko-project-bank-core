package com.techmatrix18.application.in.transaction

import java.util.UUID
import scala.concurrent.Future
import com.techmatrix18.application.ApplicationError

/**
 * CreateTransferUseCase - Входящий порт для создания нового перевода денежных средств.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
trait CreateTransactionUseCase:
  def execute(command: CreateTransactionCommand): Future[Either[ApplicationError, UUID]]

