/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class DocumentOntkoppelGegevens {
  constructor(
    public zaakUUID: string,
    public documentUUID: string,
    public reden: string,
  ) {}
}
