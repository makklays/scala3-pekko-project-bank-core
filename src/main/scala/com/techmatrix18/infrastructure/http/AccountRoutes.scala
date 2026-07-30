package com.techmatrix18.infrastructure.http

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import java.util.UUID
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext

// Импорты наших портов и ошибок из слоя Application
import com.techmatrix18.application.ApplicationError
import com.techmatrix18.application.ports.in._

/**
 * AccountRoutes
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */

final case class CreateAccountRequest(phoneNumber: String, currency: String)

// Протокол для автоматической конвертации JSON в Case Classes
trait AccountJsonSupport extends DefaultJsonProtocol with SprayJsonSupport:
  implicit val createAccountFormat = jsonFormat2(CreateAccountRequest.apply)
  // Нам также нужен формат для UUID, чтобы вернуть его в качестве текста/JSON
  implicit val uuidFormat: spray.json.RootJsonFormat[UUID] =
    new spray.json.RootJsonFormat[UUID]:
      def write(uuid: UUID) = spray.json.JsString(uuid.toString)
      def read(value: spray.json.JsValue) = UUID.fromString(value.convertTo[String])


class AccountRoutes(
  openAccountUseCase: OpenAccountUseCase,
  depositMoneyUseCase: DepositMoneyUseCase,
  withdrawMoneyUseCase: WithdrawMoneyUseCase
)(using ec: ExecutionContext) extends AccountJsonSupport: // Подключаем JSON

  // Главный роут для эндпоинтов сущности Account
  val routes: Route =
    pathPrefix("api" / "v1") {
      concat(
        // Конкретная точка входа: api/v1/hello
        path("hello") {
          // Метод запроса обязательно GET
          get {
            // Возвращаем статус 200 OK и строку текста
            complete(StatusCodes.OK, "Привет! Это простой ответ от Bank Core API.")
          }
        },

        // Конкретная точка входа: api/v1/accounts
        path("accounts") {
          concat(
            // POST /api/v1/accounts - Открытие счета
            post {
              entity(as[CreateAccountRequest]) { request =>
                val command = OpenAccountCommand(request.phoneNumber, request.currency)

                onComplete(openAccountUseCase.execute(command)) {
                  case Success(Right(accountId)) =>
                    complete(StatusCodes.Created, accountId)
                  case Success(Left(appError)) =>
                    handleApplicationError(appError)
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError, s"Server error: ${ex.getMessage}")
                }
              }
            }
          )
        }
      )
    }

  // Метод для обработки ошибок приложения и перевода их в HTTP статусы
  private def handleApplicationError(error: ApplicationError): Route =
    error match
      case ApplicationError.AccountNotFound(id) =>
        complete(StatusCodes.NotFound, s"Account $id not found")
      case ApplicationError.DomainValidationFailed(domainError) =>
        complete(StatusCodes.BadRequest, s"Validation failed: $domainError")
      case ApplicationError.SystemFailure(cause) =>
        complete(StatusCodes.InternalServerError, s"System failure: ${cause.getMessage}")

