/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from './zaaktype';
import {Groep} from '../../identity/model/groep';
import {ZaakStatus} from './zaak-status';
import {ZaakResultaat} from './zaak-resultaat';
import {GerelateerdeZaak} from './gerelateerde-zaak';
import {ZaakKenmerk} from './zaak-kenmerk';
import {ZaakEigenschap} from './zaak-eigenschap';
import {Medewerker} from '../../identity/model/medewerker';
import {Geometry} from './geometry';
import {Communicatiekanaal} from './communicatiekanaal';
import {ZaakRechten} from './zaak-rechten';

export class Zaak {
    uuid: string;
    identificatie: string;
    omschrijving: string;
    toelichting: string;
    zaaktype: Zaaktype;
    status: ZaakStatus;
    resultaat: ZaakResultaat;
    bronorganisatie: string;
    verantwoordelijkeOrganisatie: string;
    registratiedatum: string;
    startdatum: string;
    einddatumGepland: string;
    einddatum: string;
    uiterlijkeEinddatumAfdoening: string;
    publicatiedatum: string;
    communicatiekanaal: Communicatiekanaal;
    vertrouwelijkheidaanduiding: string;
    zaakgeometrie: Geometry;
    indicatieOpschorting: boolean;
    redenOpschorting: string;
    indicatieVerlenging: boolean;
    redenVerlenging: string;
    duurVerlenging: string;
    groep: Groep;
    behandelaar: Medewerker;
    gerelateerdeZaken: GerelateerdeZaak[];
    kenmerken: ZaakKenmerk[];
    eigenschappen: ZaakEigenschap[];
    rechten: ZaakRechten;
    initiatorIdentificatie: string; // BSN or Vestigingsnummer
}
