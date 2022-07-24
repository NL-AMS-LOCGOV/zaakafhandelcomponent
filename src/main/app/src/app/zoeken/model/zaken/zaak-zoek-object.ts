/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {ZoekObject} from '../zoek-object';

export class ZaakZoekObject implements ZoekObject {
    id: string;
    type: string;
    identificatie: string;
    omschrijving: string;
    toelichting: string;
    registratiedatum: string;
    startdatum: string;
    einddatumGepland: string;
    einddatum: string;
    uiterlijkeEinddatumAfdoening: string;
    publicatiedatum: string;
    communicatiekanaal: string;
    vertrouwelijkheidaanduiding: string;
    afgehandeld: boolean;
    groepNaam: string;
    behandelaarNaam: string;
    behandelaarGebruikersnaam: string;
    initiatorIdentificatie: string;
    locatie: string;
    indicatieVerlenging: boolean;
    duurVerlenging: string;
    redenVerlenging: string;
    redenOpschorting: string;
    zaaktypeUuid: string;
    zaaktypeOmschrijving: string;
    resultaattypeOmschrijving: string;
    resultaatToelichting: string;
    statustypeOmschrijving: string;
}
