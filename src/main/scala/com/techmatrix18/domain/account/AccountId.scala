package com.techmatrix18.domain.account

import java.util.UUID

/**
 * AccountId
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 30.07.2026
 */
opaque type AccountId = UUID
object AccountId:
  def generate(): AccountId = UUID.randomUUID()
  def fromUUID(uuid: UUID): AccountId = uuid

