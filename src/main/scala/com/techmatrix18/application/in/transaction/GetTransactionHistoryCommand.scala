package com.techmatrix18.application.in.transaction

import java.util.UUID

/**
 * В CQRS (разделении команд и запросов) для чтения обычно используют слово Query вместо Command
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
case class GetTransactionHistoryCommand(
  accountId: UUID,
  limit: Int = 20,
  offset: Int = 0
)

