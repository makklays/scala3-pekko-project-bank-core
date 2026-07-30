package com.techmatrix18.application

import com.techmatrix18.domain.account.DomainError

/**
 * ApplicationError
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
enum ApplicationError:
  case AccountNotFound(id: java.util.UUID)
  case DomainValidationFailed(error: DomainError)
  case SystemFailure(cause: Throwable)

