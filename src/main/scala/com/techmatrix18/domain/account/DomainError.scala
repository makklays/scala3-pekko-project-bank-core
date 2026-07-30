package com.techmatrix18.domain.account

/**
 * DomainError
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
enum DomainError:
  case NegativeAmount
  case CurrencyMismatch(expected: String, actual: String)
  case InsufficientFunds
  case AccountIsFrozen
  case InvalidPhoneNumber

