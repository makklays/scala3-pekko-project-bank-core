package com.techmatrix18.application.in.transaction

import scala.concurrent.Future
import com.techmatrix18.domain.transaction.Transaction

/**
 * GetTransferHistoryUseCase - Входящий порт для получения выписки по счету.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
trait GetTransactionHistoryUseCase:
  def execute(query: GetTransactionHistoryCommand): Future[List[Transaction]]

