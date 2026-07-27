package com.techmatrix18.domain.transfer

/**
 * Luhn Algorithm
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 28.07.2026
 */
object LuhnAlgorithm:

  /**
   * Проверяет валидность номера банковской карты по алгоритму Луна.
   * @param cardNumber строка с номером карты (может содержать пробелы)
   * @return true, если номер карты валиден, иначе false
   */
  def isValid(cardNumber: String): Boolean =
    // 1. Очищаем строку от пробелов и дефисов
    val cleanNumber = cardNumber.replaceAll("\\s+", "")

    // 2. Базовая проверка: номер должен состоять только из цифр и быть длиннее 13 знаков
    if (cleanNumber.length < 13 || !cleanNumber.forall(_.isDigit)) then
      false
    else
      // 3. Функциональный обход цифр с конца (справа налево)
      val digits = cleanNumber.map(_.asDigit).reverse

      val checksum = digits.zipWithIndex.map { case (digit, index) =>
        // Каждую вторую цифру (нечетные индексы, т.к. начали с 0) умножаем на 2
        if (index % 2 == 1) then
          val doubled = digit * 2
          if (doubled > 9) doubled - 9 else doubled
        else
          digit
      }.sum

      // 4. Если сумма делится на 10 без остатка — карта валидна
      checksum % 10 == 0

