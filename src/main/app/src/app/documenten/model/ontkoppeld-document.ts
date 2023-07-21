/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { User } from "../../identity/model/user";

export class OntkoppeldDocument {
  id: number;
  documentUUID: string;
  documentID: string;
  zaakID: string;
  creatiedatum: string;
  titel: string;
  bestandsnaam: string;
  ontkoppeldDoor: User;
  ontkoppeldOp: string;
  reden: string;
}
