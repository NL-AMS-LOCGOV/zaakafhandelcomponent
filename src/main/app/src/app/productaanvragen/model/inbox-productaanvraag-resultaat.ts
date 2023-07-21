/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Resultaat } from "../../shared/model/resultaat";
import { InboxProductaanvraag } from "./inbox-productaanvraag";

export class InboxProductaanvraagResultaat extends Resultaat<InboxProductaanvraag> {
  filterType: string[];
}
