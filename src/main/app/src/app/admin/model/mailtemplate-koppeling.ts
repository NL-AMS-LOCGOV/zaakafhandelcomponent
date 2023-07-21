/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Mailtemplate } from "./mailtemplate";
import { ZaakafhandelParameters } from "./zaakafhandel-parameters";

export class MailtemplateKoppeling {
  id: number;
  zaakafhandelParameters: ZaakafhandelParameters;
  mailtemplate: Mailtemplate;
}
