/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Resultaat } from "../../shared/model/resultaat";
import { OntkoppeldDocument } from "./ontkoppeld-document";
import { User } from "../../identity/model/user";

export class OntkoppeldeDocumentenResultaat extends Resultaat<OntkoppeldDocument> {
  filterOntkoppeldDoor: User[];
}
