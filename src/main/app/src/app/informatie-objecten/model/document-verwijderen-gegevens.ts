/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

export class DocumentVerwijderenGegevens {
  constructor(
    public zaakUuid: string,
    public reden: string,
  ) {}
}
