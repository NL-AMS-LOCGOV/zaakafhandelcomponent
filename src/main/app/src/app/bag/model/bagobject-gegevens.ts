/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObject } from "./bagobject";

export class BAGObjectGegevens {
  constructor(
    public zaakUuid: string,
    public zaakobject: BAGObject,
    public uuid?: string,
    public redenWijzigen?: string,
  ) {}
}
