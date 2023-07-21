/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { MailtemplateVariabele } from "./mailtemplate-variabele";

export class Mailtemplate {
  id: number;
  mailTemplateNaam: string;
  onderwerp: string;
  body: string;
  mail: string;
  variabelen: MailtemplateVariabele[];
  defaultMailtemplate: boolean;
}
