package com.techmatrix18.application.out.account

import scala.concurrent.Future
import java.util.UUID
import com.techmatrix18.domain.account.Account

/**
 * AccountRepository
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 29.07.2026
 */
trait AccountRepository:

  // Находит аккаунт по его уникальному идентификатору (UUID).
  def findById(id: UUID): Future[Option[Account]]

  // Находит аккаунт по номеру телефона (критично для мгновенных P2P-переводов Bizum).
  def findByPhoneNumber(phoneNumber: String): Future[Option[Account]]

  // Сохраняет новый аккаунт или обновляет состояние существующего (Upsert логика).
  def save(account: Account): Future[Unit]



