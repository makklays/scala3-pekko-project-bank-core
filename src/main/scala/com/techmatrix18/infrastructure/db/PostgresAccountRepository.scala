package com.techmatrix18.infrastructure.db

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, ExecutionContext}
import java.util.UUID
import java.time.Instant

import com.techmatrix18.domain.account.{Account, Money, AccountStatus, AccountId}
import com.techmatrix18.application.ports.out.AccountRepository

/**
 * PostgresAccountRepository
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */

//--- SLICK MAPPING (Описание схемы таблицы accounts для базы данных) ---

class AccountsTable(tag: Tag) extends Table[AccountRow](tag, "accounts") {
  def id = column[UUID]("id", O.PrimaryKey)
  def phoneNumber = column[String]("phone_number")
  def balance = column[BigDecimal]("balance")
  def currency = column[String]("currency")
  def status = column[String]("status")
  def createdAt = column[Instant]("created_at")
  def updatedAt = column[Instant]("updated_at")

  // Сборка строки таблицы в промежуточный DTO (Row) для удобства маппинга
  def * = (id, phoneNumber, balance, currency, status, createdAt, updatedAt).mapTo[AccountRow]
}

// Промежуточный кейс-класс, соответствующий плоской структуре таблицы в БД
final case class AccountRow(
  id: UUID,
  phoneNumber: String,
  balance: BigDecimal,
  currency: String,
  status: String,
  createdAt: Instant,
  updatedAt: Instant
) {
  // Метод конвертации инфраструктурной строки в чистую Доменную сущность
  def toDomain: Account =
  // Так как из БД данные гарантированно валидные, мы используем небезопасный .get или кастомную сборку
    Account(
      id = AccountId.fromUUID(id),
      phoneNumber = phoneNumber,
      balance = Money(balance, currency).getOrElse(throw new IllegalStateException("Invalid money in DB")),
      status = AccountStatus.valueOf(status.toLowerCase.capitalize), // ACTIVE -> Active
      createdAt = createdAt,
      updatedAt = updatedAt
    )
}

object AccountRow {
  // Метод конвертации Доменной сущности в плоскую строку БД для сохранения
  def fromDomain(domain: Account): AccountRow =
    AccountRow(
      id = domain.id.asInstanceOf[UUID], // распаковка opaque type
      phoneNumber = domain.phoneNumber,
      balance = domain.balance.amount,
      currency = domain.balance.currency,
      status = domain.status.toString.toUpperCase, // Active -> ACTIVE
      createdAt = domain.createdAt,
      updatedAt = domain.updatedAt
    )
}

//--- ИСПРАВНЫЙ ИСХОДЯЩИЙ АДАПТЕР (Реализация Порта) ---

// PostgresAccountRepository - Реализация инфраструктурного порта на Slick.
class PostgresAccountRepository(db: Database)(implicit ec: ExecutionContext) extends AccountRepository {

  // Централизованная точка запросов к таблице accounts
  private val accounts = TableQuery[AccountsTable]

  override def findById(id: UUID): Future[Option[Account]] = {
    val query = accounts.filter(_.id === id).result.headOption
    db.run(query).map(_.map(_.toDomain))
  }

  override def findByPhoneNumber(phoneNumber: String): Future[Option[Account]] = {
    val query = accounts.filter(_.phoneNumber === phoneNumber).result.headOption
    db.run(query).map(_.map(_.toDomain))
  }

  override def save(account: Account): Future[Unit] = {
    val row = AccountRow.fromDomain(account)

    // Используем insertOrUpdate (Upsert) для сохранения новых записей и обновления существующих
    val action = accounts.insertOrUpdate(row)
    db.run(action).map(_ => ())
  }
}

