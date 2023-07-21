/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { FileFormat } from "./file-format";
import { User } from "../../identity/model/user";
import { DocumentRechten } from "../../policy/model/document-rechten";
import { EnkelvoudigInformatieobjectOndertekening } from "./enkelvoudig-informatieobject-ondertekening";
import { InformatieobjectIndicatie } from "../../shared/indicaties/informatie-object-indicaties/informatie-object-indicaties.component";
import { InformatieobjectStatus } from "./informatieobject-status.enum";

export class EnkelvoudigInformatieobject {
  uuid: string;
  identificatie: string;
  titel: string;
  beschrijving: string;
  creatiedatum: string;
  registratiedatumTijd: string;
  ontvangstdatum: string;
  verzenddatum: string;
  bronorganisatie: string;
  vertrouwelijkheidaanduiding: string;
  auteur: string;
  status: InformatieobjectStatus;
  formaat: FileFormat;
  taal: string;
  versie: number;
  informatieobjectTypeUUID: string;
  informatieobjectTypeOmschrijving: string;
  bestandsnaam: string;
  bestandsomvang: number;
  link: string;
  ondertekening: EnkelvoudigInformatieobjectOndertekening;
  indicatieGebruiksrecht: boolean;
  gelockedDoor: User;
  indicaties: InformatieobjectIndicatie[];
  rechten: DocumentRechten;
  isBesluitDocument: boolean;
}
