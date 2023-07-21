/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * Maps to OpcodeEnum.java
 *
 * Note that the CREATED value is not available for subscription (reason: new objectIds will be unknown client side ;-)
 */
export enum Opcode {
  UPDATED = "UPDATED",
  DELETED = "DELETED",
  ANY = "ANY",
}
