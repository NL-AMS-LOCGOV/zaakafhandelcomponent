/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { EnkelvoudigInformatieobject } from "./enkelvoudig-informatieobject";

export class GekoppeldeZaakEnkelvoudigInformatieobject extends EnkelvoudigInformatieobject {
  relatieType?: string;
  zaakIdentificatie?: string;
  zaakUUID?: string;
}
