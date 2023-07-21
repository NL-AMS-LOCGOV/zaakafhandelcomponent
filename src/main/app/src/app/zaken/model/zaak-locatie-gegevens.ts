/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Geometry } from "./geometry";

export class ZaakLocatieGegevens {
  constructor(
    public geometrie: Geometry,
    public reden: string,
  ) {}
}
