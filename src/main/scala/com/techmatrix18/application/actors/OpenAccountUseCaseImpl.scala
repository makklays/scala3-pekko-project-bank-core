package com.techmatrix18.application.actors

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.util.Timeout
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import java.util.UUID

import com.techmatrix18.application.ApplicationError
import com.techmatrix18.application.ports.in.{OpenAccountUseCase, OpenAccountCommand}
import com.techmatrix18.application.ports.out.AccountRepository

/**
 * OpenAccountUseCaseImpl - Реализация бизнес-сценария открытия счета.
 * Управляет созданием и вызовом акторов для выполнения операции.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
class OpenAccountUseCaseImpl(
  repository: AccountRepository,
  system: ActorSystem[_] // Система акторов нужна для динамического спавна AccountActor
)(using ec: ExecutionContext) extends OpenAccountUseCase:

  // Таймаут для ожидания ответа от актора
  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val scheduler = system.scheduler

  override def execute(command: OpenAccountCommand): Future[Either[ApplicationError, UUID]] =
    // Генерируем новый уникальный ID для открываемого счета
    val newAccountId = UUID.randomUUID()

    // В реальной системе здесь происходит обращение к менеджеру акторов (Cluster Sharding)
    // Для нашего локального ядра мы создаем (спавним) живой актор для этого аккаунта прямо в системе
    val accountActor = system.systemActorOf(
      AccountActor(newAccountId, repository),
      s"account-$newAccountId"
    )

    // Отправляем в только что созданный актор команду инициализации через Ask Pattern (?)
    accountActor ? (replyTo => AccountActor.CreateAccount(command.phoneNumber, command.currency, replyTo))

