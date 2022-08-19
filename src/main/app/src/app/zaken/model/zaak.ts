/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Zaaktype} from './zaaktype';
import {Group} from '../../identity/model/group';
import {ZaakStatus} from './zaak-status';
import {ZaakResultaat} from './zaak-resultaat';
import {GerelateerdeZaak} from './gerelateerde-zaak';
import {ZaakKenmerk} from './zaak-kenmerk';
import {ZaakEigenschap} from './zaak-eigenschap';
import {User} from '../../identity/model/user';
import {Geometry} from './geometry';
import {Communicatiekanaal} from './communicatiekanaal';
import {ZaakActies} from './zaak-acties';
import {IdentificatieType} from '../../klanten/model/klanten/identificatieType';

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
    archiefActiedatum: string;
    archiefNominatie: string;
    communicatiekanaal: Communicatiekanaal;
    vertrouwelijkheidaanduiding: string;
    zaakgeometrie: Geometry;
    isOpgeschort: boolean;
    redenOpschorting: string;
    isVerlengd: boolean;
    redenVerlenging: string;
    duurVerlenging: string;
    groep: Group;
    behandelaar: User;
    gerelateerdeZaken: GerelateerdeZaak[];
    kenmerken: ZaakKenmerk[];
    initiatorIdentificatieType: IdentificatieType;
    initiatorIdentificatie: string;
    isHeropend: boolean;
    isHoofdzaak: boolean;
    isDeelzaak: boolean;
    acties: ZaakActies;
}
