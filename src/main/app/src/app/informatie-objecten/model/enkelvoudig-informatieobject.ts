/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {FileFormat} from './file-format';

export class EnkelvoudigInformatieobject {
    uuid: string;
    titel: string;
    beschrijving: string;
    identificatie: string;
    creatiedatum: string;
    bronorganisatie: string;
    vertrouwelijkheidaanduiding: string;
    auteur: string;
    status: string;
    formaat: FileFormat;
    taal: string;
    versie: number;
    informatieobjectTypeUUID: string;
    informatieobjectTypeOmschrijving: string;
    registratiedatumTijd: string;
    bestandsnaam: string;
    bestandsomvang: number;
    link: string;
    ontvangstdatum: string;
    verzenddatum: string;
    indicatieGebruiksrecht: boolean;
    ondertekening: string;
    locked: boolean;
    inhoudUrl: string;
    taakObject: boolean;
}
