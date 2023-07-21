/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { BAGObjecttype } from "./bagobjecttype";

export abstract class BAGObject {
  url: string;
  identificatie: string;
  bagObjectType: BAGObjecttype;
  documentdatum: string;
  documentnummer: string;
  geconstateerd: boolean;
  status: string;
  omschrijving: string;
}
