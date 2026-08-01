package com.techmatrix18.infrastructure.http

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import java.util.UUID
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext

// Импорты входящих портов (Use Cases) и ошибок приложения
import com.techmatrix18.application.ApplicationError
import com.techmatrix18.application.ports.in.transfer._

/**
 * TransactionRoutes - HTTP контроллер для управления переводами и выписками.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */

// Request DTO для JSON-тела при создании перевода
final case class CreateTransferRequest(
  senderAccountId: UUID,
  recipientCardOrNumber: String,
  amount: BigDecimal,
  currency: String,
  description: Option[String]
)

// Протокол автоматической сериализации JSON для трансферов
trait TransactionJsonSupport extends DefaultJsonProtocol with SprayJsonSupport:
  implicit val createTransferFormat = jsonFormat5(CreateTransferRequest.apply)

  // Формат для UUID, чтобы отдавать ID созданной транзакции
  implicit val uuidJsonFormat: spray.json.RootJsonFormat[UUID] =
    new spray.json.RootJsonFormat[UUID]:
      def write(uuid: UUID) = spray.json.JsString(uuid.toString)
      def read(value: spray.json.JsValue) = UUID.fromString(value.convertTo[String])


/**
 * TransactionRoutes - HTTP контроллер для управления переводами и выписками.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 01.08.2026
 */
class TransactionRoutes(
  createTransferUseCase: CreateTransferUseCase,
  getTransferHistoryUseCase: GetTransferHistoryUseCase
)(using ec: ExecutionContext) extends TransactionJsonSupport:

  val routes: Route =
    pathPrefix("api" / "v1") {
      concat(
        // Hello info :-)
        path("hello-info") {
          get {
            // Возвращаем статус 200 OK и строку текста
            complete(StatusCodes.OK, "Привет! Это простой ответ от Bank Core API - секция Транзакций.")
          }
        },

        // Эндпоинт создания перевода: POST /api/v1/transfers
        path("transfers") {
          post {
            entity(as[CreateTransferRequest]) { request =>
              val command = CreateTransferCommand(
                senderAccountId = request.senderAccountId,
                recipientCardOrNumber = request.recipientCardOrNumber,
                amount = request.amount,
                currency = request.currency,
                description = request.description
              )

              onComplete(createTransferUseCase.execute(command)) {
                case Success(Right(transactionId)) =>
                  complete(StatusCodes.Accepted, transactionId) // 202 Accepted, так как процесс асинхронный (Сага)
                case Success(Left(appError)) =>
                  handleApplicationError(appError)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError, s"Server error: ${ex.getMessage}")
              }
            }
          }
        },

        // Эндпоинт истории: GET /api/v1/accounts/{accountId}/transfers?limit=20&offset=0
        path("accounts" / Segment / "transfers") { accountIdStr =>
          get {
            // Безопасно парсим UUID из строки пути
            val accountId = UUID.fromString(accountIdStr)

            // Извлекаем Query-параметры пагинации из URL с дефолтными значениями
            parameters("limit".as[Int].?, "offset".as[Int].?) { (limitOpt, offsetOpt) =>

              // Для чтения истории мы не отдаем доменные сущности напрямую,
              // но в рамках MVP выведем их строковое представление или простой JSON-маппинг текста
              val query = GetTransferHistoryQuery(
                accountId = accountId,
                limit = limitOpt.getOrElse(20),
                offset = offsetOpt.getOrElse(0)
              )

              onComplete(getTransferHistoryUseCase.execute(query)) {
                case Success(transactionsList) =>
                  // Для простоты пока преобразуем список транзакций в читаемый текст/лог,
                  // так как полноценный JSON-маршалер для всего дерева транзакции добавим в доменном слое.
                  val responseText = transactionsList.map(t =>
                    s"ID: ${t.id}, From: ${t.senderAccountId}, To: ${t.recipientAccountId}, Amount: ${t.money.amount} ${t.money.currency}, Status: ${t.status}"
                  ).mkString("\n")

                  complete(StatusCodes.OK, responseText)
                case Failure(ex) =>
                  complete(StatusCodes.InternalServerError, s"Server error: ${ex.getMessage}")
              }
            }
          }
        }
      )
    }

  // Метод перевода ошибок нашего бизнес-ядра в понятные фронтенду HTTP-статусы
  private def handleApplicationError(error: ApplicationError): Route =
    error match
      case ApplicationError.AccountNotFound(id) =>
        complete(StatusCodes.NotFound, s"Account $id not found")
      case ApplicationError.DomainValidationFailed(msg) =>
        complete(StatusCodes.BadRequest, s"Business rule validation failed: $msg")
      case ApplicationError.SystemFailure(cause) =>
        complete(StatusCodes.InternalServerError, s"Infrastructure failure: ${cause.getMessage}")

