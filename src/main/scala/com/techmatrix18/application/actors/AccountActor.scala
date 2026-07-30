package com.techmatrix18.application.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.{Behaviors, ActorContext}
import java.util.UUID
import java.time.Instant
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext

import com.techmatrix18.domain.account.{Account, Money}
import com.techmatrix18.application.ApplicationError
import com.techmatrix18.application.ports.out.AccountRepository

/**
 * AccountActor
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 29.07.2026
 */
object AccountActor:

  //--- ПРОТОКОЛ СООБЩЕНИЙ (Команды / Commands) ---

  sealed trait Command

  // Команда на создание нового аккаунта в памяти/БД
  final case class CreateAccount(
    phoneNumber: String,
    currency: String,
    replyTo: ActorRef[Either[ApplicationError, UUID]]
  ) extends Command

  // Команда на зачисление денег
  final case class DepositMoney(
    money: Money,
    replyTo: ActorRef[Either[ApplicationError, Unit]]
  ) extends Command

  //
  final case class WithdrawMoney(
    money: Money,
    replyTo: ActorRef[Either[ApplicationError, Unit]]
  ) extends Command

  // Внутренние адаптеры для обработки ответов из Асинхронного Репозитория (Future -> Command)
  private final case class WrappedAccountResponse(maybeAccount: Option[Account]) extends Command
  private final case class WrappedSaveResponse(result: scala.util.Try[Unit]) extends Command

  //--- ИНИЦИАЛИЗАЦИЯ И ПОВЕДЕНИЕ (Behaviors) ---

  // Точка входа для запуска актора конкретного аккаунта
  def apply(accountId: UUID, repository: AccountRepository): Behavior[Command] =
    Behaviors.setup { context =>
      implicit val ec: ExecutionContext = context.executionContext

      // При старте актор асинхронно запрашивает свое состояние из БД через исходящий порт
      context.pipeToSelf(repository.findById(accountId)) {
        case Success(maybeAccount) => WrappedAccountResponse(maybeAccount)
        case Failure(ex) => WrappedAccountResponse(None) // Или логирование ошибки
      }

      // Переходим в состояние ожидания загрузки данных
      loading(accountId, repository)
    }

  // Стейт 1: Актор запущен, но еще не вычитал данные из базы данных
  private def loading(accountId: UUID, repository: AccountRepository): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match
        case WrappedAccountResponse(Some(account)) =>
          // Данные успешно загружены, переходим в рабочее состояние с актуальным доменным стейтом
          active(account, repository)

        case WrappedAccountResponse(None) =>
          // Аккаунта нет в БД, ожидаем команду явного создания (CreateAccount)
          uninitialized(accountId, repository)

        case _ =>
          // Игнорируем или складываем в буфер другие команды, пока не загрузились
          Behaviors.unhandled
    }

  // Стейт 2: Аккаунт еще не существует в физической природе
  private def uninitialized(accountId: UUID, repository: AccountRepository): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      implicit val ec: ExecutionContext = context.executionContext
      message match
        case CreateAccount(phone, currency, replyTo) =>
          // Вызываем фабрику Доменного слоя для валидации и сборки сущности
          Account.create(phone, currency, Instant.now()) match
            case Left(domainError) =>
              replyTo ! Left(ApplicationError.DomainValidationFailed(domainError))
              Behaviors.same
            case Right(newAccount) =>
              // Подменяем ID на тот, с которым был запущен Актор
              val accountToSave = newAccount.copy(id = accountId)

              // Сохраняем в БД через исходящий порт
              context.pipeToSelf(repository.save(accountToSave))(WrappedSaveResponse(_))

              // Отвечаем успехом создателю
              replyTo ! Right(accountId)

              // Переводим актор в активный режим
              active(accountToSave, repository)

        case _ =>
          Behaviors.unhandled
    }

  // Стейт 3: Аккаунт активен, загружен и готов обрабатывать транзакции последовательно
  private def active(account: Account, repository: AccountRepository): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      implicit val ec: ExecutionContext = context.executionContext

      message match
        case DepositMoney(money, replyTo) =>
          // Вызываем чистый доменный метод мутации
          account.deposit(money, Instant.now()) match
            case Left(domainError) =>
              replyTo ! Left(ApplicationError.DomainValidationFailed(domainError))
              Behaviors.same
            case Right(updatedAccount) =>
              // Асинхронно сохраняем новое состояние в репозиторий
              repository.save(updatedAccount)
              replyTo ! Right(())
              // Рекурсивно меняем состояние актора на новое
              active(updatedAccount, repository)

        case WithdrawMoney(money, replyTo) =>
          // Вызываем чистый доменный метод мутации баланса
          account.withdraw(money, Instant.now()) match
            case Left(domainError) =>
              replyTo ! Left(ApplicationError.DomainValidationFailed(domainError))
              Behaviors.same
            case Right(updatedAccount) =>
              repository.save(updatedAccount)
              replyTo ! Right(())
              active(updatedAccount, repository)

        case _ =>
          Behaviors.unhandled
    }