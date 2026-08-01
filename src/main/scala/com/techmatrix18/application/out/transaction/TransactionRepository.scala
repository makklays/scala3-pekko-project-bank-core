package com.techmatrix18.application.out.transaction

import scala.concurrent.Future
import java.util.UUID
import com.techmatrix18.domain.transfer.{Transaction, TransactionStatus}

/**
 * TransactionRepository - Исходящий порт (интерфейс) для управления персистентностью транзакций.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
trait TransactionRepository:

  /**
   * Найти конкретную транзакцию по её уникальному идентификатору.
   * Нужно для проверки статуса платежа или при восстановлении Саги.
   */
  def findById(id: UUID): Future[Option[Transaction]]

  /**
   * Получить список транзакций (историю) по конкретному аккаунту с поддержкой пагинации.
   * Ищет записи, где аккаунт является либо отправителем, либо получателем.
   */
  def findByAccountId(accountId: UUID, limit: Int, offset: Int): Future[List[Transaction]]

  /**
   * Сохранить новую транзакцию или обновить существующую (например, изменить статус с PENDING на COMPLETED).
   */
  def save(transaction: Transaction): Future[Unit]
