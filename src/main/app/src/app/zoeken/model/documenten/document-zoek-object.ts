/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZoekObject, ZoekObjectType} from '../zoek-object';
import {DocumentRechten} from '../../../policy/model/document-rechten';

export class DocumentZoekObject implements ZoekObject {
    id: string;
    type: ZoekObjectType;
    identificatie: string;
    titel: string;
    beschrijving: string;
    zaaktypeUuid: string;
    zaaktypeIdentificatie: string;
    zaaktypeOmschrijving: string;
    zaakIdentificatie: string;
    zaakUuid: string;
    zaakRelatie: string;
    creatiedatum: string;
    registratiedatum: string;
    ontvangstdatum: string;
    verzenddatum: string;
    ondertekeningDatum: string;
    ondertekeningSoort: string;
    vertrouwelijkheidaanduiding: string;
    auteur: string;
    status: string;
    formaat: string;
    versie: number;
    bestandsnaam: string;
    bestandsomvang: number;
    documentType: string;

    indicatieOndertekend: boolean;
    inhoudUrl: string;
    indicatieVergrendeld: boolean;
    vergrendeldDoor: string;
    rechten: DocumentRechten;
}
