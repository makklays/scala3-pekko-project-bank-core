package com.techmatrix18.infrastructure.db

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, ExecutionContext}
import java.util.UUID
import java.time.Instant

import com.techmatrix18.domain.account.Money
import com.techmatrix18.domain.transfer.{Transaction, TransactionStatus}
import com.techmatrix18.application.ports.out.transfer.TransactionRepository

/**
 * PostgresTransactionRepository - Реализация инфраструктурного порта для транзакций на Slick.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */

// SLICK MAPPING (Описание схемы таблицы transactions)

class TransactionsTable(tag: Tag) extends Table[TransactionRow](tag, "transactions") {
  def id = column[UUID]("id", O.PrimaryKey)
  def senderAccountId = column[UUID]("sender_account_id")
  def recipientAccountId = column[UUID]("recipient_account_id")
  def amount = column[BigDecimal]("amount")
  def currency = column[String]("currency")
  def status = column[String]("status")
  def description = column[String]("description")
  def createdAt = column[Instant]("created_at")
  def updatedAt = column[Instant]("updated_at")

  // Сборка строки таблицы в промежуточный DTO (Row)
  def * = (id, senderAccountId, recipientAccountId, amount, currency, status, description, createdAt, updatedAt).mapTo[TransactionRow]
}

// ПРОМЕЖУТОЧНЫЙ DTO (Буфер между БД и Доменом)

final case class TransactionRow(
  id: UUID,
  senderAccountId: UUID,
  recipientAccountId: UUID,
  amount: BigDecimal,
  currency: String,
  status: String,
  description: String,
  createdAt: Instant,
  updatedAt: Instant
) {
  // Конвертация из плоской строки БД в богатую доменную модель Transaction
  def toDomain: Transaction =
    Transaction(
      id = id,
      senderAccountId = senderAccountId,
      recipientAccountId = recipientAccountId,
      money = Money(amount, currency).getOrElse(throw new IllegalStateException("Invalid money format in transactions DB")),
      status = TransactionStatus.valueOf(status.toLowerCase.capitalize), // PENDING -> Pending
      description = description,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
}

object TransactionRow {
  // Конвертация из доменной модели в плоскую строку БД для сохранения
  def fromDomain(domain: Transaction): TransactionRow =
    TransactionRow(
      id = domain.id,
      senderAccountId = domain.senderAccountId,
      recipientAccountId = domain.recipientAccountId,
      amount = domain.money.amount,
      currency = domain.money.currency,
      status = domain.status.toString.toUpperCase, // Pending -> PENDING
      description = domain.description,
      createdAt = domain.createdAt,
      updatedAt = domain.updatedAt
    )
}

//  ИСХОДЯЩИЙ АДАПТЕР (Реализация нашего нового интерфейса/порта)

/**
 * PostgresTransactionRepository - Реализация инфраструктурного порта для транзакций на Slick.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
class PostgresTransactionRepository(db: Database)(implicit ec: ExecutionContext) extends TransactionRepository {

  // Централизованная точка запросов к таблице transactions
  private val transactions = TableQuery[TransactionsTable]

  override def findById(id: UUID): Future[Option[Transaction]] = {
    val query = transactions.filter(_.id === id).result.headOption
    db.run(query).map(_.map(_.toDomain))
  }

  override def findByAccountId(accountId: UUID, limit: Int, offset: Int): Future[List[Transaction]] = {
    // Ищем транзакции, где данный аккаунт выступает ЛИБО отправителем, ЛИБО получателем
    val query = transactions
      .filter(t => t.senderAccountId === accountId || t.recipientAccountId === accountId)
      .sortBy(_.createdAt.desc) // Свежие транзакции выводим первыми
      .drop(offset)
      .take(limit)
      .result

    db.run(query).map(_.map(_.toDomain).toList)
  }

  override def save(transaction: Transaction): Future[Unit] = {
    val row = TransactionRow.fromDomain(transaction)

    // insertOrUpdate выполняет Upsert (вставит новую запись или обновит статус существующей)
    val action = transactions.insertOrUpdate(row)
    db.run(action).map(_ => ())
  }
}

